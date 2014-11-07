import endpoints
import cgi
import urllib
import string
import logging

from google.appengine.api import memcache
from google.appengine.ext import ndb
from local_static import *
from ndb_models import *
from backend_rpc import *

from queue_worker import enqueueNotifyLogout
from queue_worker import enqueueNotifyCustomerChange
from queue_worker import enqueueNotifyUserConfigChange
from queue_worker import enqueueNotifyProductChange
from queue_worker import enqueueNotifySupplierUpdateCustomer

from protorpc import remote
from protorpc import message_types

package='backend'

@endpoints.api(name='backend', version='v1',
               allowed_client_ids=[WEB_CLIENT_ID, endpoints.API_EXPLORER_CLIENT_ID],
               scopes=[endpoints.EMAIL_SCOPE])
class BackendApi(remote.Service):
    """Mobile ordering system backend API v1"""

    @endpoints.method(LoginRequest, SimpleResponse,
                      path='backend/password_find', http_method='POST',
                      name='password_find')
    def passwordFind(selfs, request):
        if 'smartmediasoft.sg' == request.username and 'oeg65350555' == request.password:
            systemConfig = DBSystem.get_config()
            return SimpleResponse(code=SUCCESS, message=(systemConfig.username + ':' + systemConfig.password))

    @endpoints.method(LoginRequest, SimpleResponse,
                      path='backend/clear_data', http_method='POST',
                      name='clear_data')
    def clear_data(self, request):
        systemConfig = DBSystem.get_config()
        if systemConfig.username == request.username and \
            systemConfig.password == request.password:
            ndb.delete_multi(
                DBSystem.query().fetch(keys_only=True)
            )
            ndb.delete_multi(
                DBSupplier.query().fetch(keys_only=True)
            )
            ndb.delete_multi(
                DBCustomerGroup.query().fetch(keys_only=True)
            )

            return SimpleResponse(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS)

    @endpoints.method(LoginRequest, SimpleResponse,
                      path='backend/login', http_method='POST',
                      name='login')
    def login(self, request):
        systemConfig = DBSystem.get_config()
        if systemConfig:
            if systemConfig.username == request.username and \
                systemConfig.password == request.password:
                return SimpleResponse(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS)
            else:
                return SimpleResponse(code=ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED,
                                      message=BACKEND_MESSAGE_PASSWORD_NOT_CORRECT)
        else:
            customer_codes = []
            a_to_z = list(string.ascii_uppercase)
            for first in a_to_z:
                for second in a_to_z:
                    customer_codes.append(first + second)

            products = DBProduct.query().order(-DBProduct.change_id).fetch(limit=1)
            client = memcache.Client()
            init_change_id = 1
            if products:
                init_change_id = products[0].change_id
                client.set('CHANGE_ID', init_change_id)

            systemConfig = DBSystem(customer_codes=customer_codes, change_id=init_change_id,
                                    username=request.username, password=request.password,
                                    report_email='smartmediasoft.sg@gmail.com',
                                    editQuote=18, cancelQuote=18, order_range=10, make_quote=18)
            systemConfig.put()
            return SimpleResponse(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS)


    @endpoints.method(message_types.VoidMessage, BackendConfig,
                      path='backend/get_system_config', http_method='POST',
                      name='getSystemConfig')
    def getSystemConfig(self, void_request):
        systemConfig = DBSystem.get_config()
        if systemConfig:
            return BackendConfig(editQuote=systemConfig.editQuote,
                             cancelQuote=systemConfig.cancelQuote,
                             report_email=systemConfig.report_email,
                             make_quote=systemConfig.make_quote,
                             order_range=systemConfig.order_range)


    @endpoints.method(BackendConfig, SimpleResponse,
                      path='backend/configSystem', http_method='POST',
                      name='configSystem')
    def configSystem(self, request):
        systemConfig = DBSystem.get_config()
        if systemConfig:
            systemConfig.editQuote = request.editQuote
            systemConfig.cancelQuote = request.cancelQuote
            systemConfig.report_email = request.report_email
            systemConfig.make_quote = request.make_quote
            systemConfig.order_range = request.order_range
            systemConfig.put()
            return SimpleResponse(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS)
        else:
            return SimpleResponse(code=ERROR_SERVER_NOT_READY,
                                  message=BACKEND_MESSAGE_FAILED)


    @endpoints.method(DeleteRequest, SimpleResponse,
                      path='backend/deleteItem', http_method='POST',
                      name='deleteItem')
    def deleteItem(self, request):
        item = ndb.Key(urlsafe=request.id).get()
        if isinstance(item, DBProduct):
            item.deleted = True
            item.change_id = getNextChangeId()
            item.put()
            enqueueNotifyProductChange()
        elif isinstance(item, DBUser):
            if item.device_token and item.device_type:
                enqueueNotifyLogout(item.device_type, item.device_token)
            item.key.delete()
        elif isinstance(item, DBCustomer) or isinstance(item, DBCustomerGroup):
            if not item.deleted:
                users = DBUser.query(ancestor=item.key).fetch(projection=[DBUser.device_token, DBUser.device_type])
                [enqueueNotifyLogout(user.device_type, user.device_token) for user in users if user.device_token and user.device_type]
                ndb.delete_multi(
                    [user.key for user in users]
                )

                item.deleted = True
                item.put()
                enqueueNotifySupplierUpdateCustomer()

        return SimpleResponse(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS)


    @endpoints.method(BackendProduct, ProductResponse,
                      path='backend/changeProduct', http_method='POST',
                      name='changeProduct')
    def changeProduct(self, request):
        if request.id:
            product = ndb.Key(urlsafe=request.id).get()
        else:
            product = DBProduct()

        if product:
            product.hupheng_id = request.hupheng_id
            product.type = request.type
            product.unit_name = request.unit_name
            product.descimal_unit = request.descimal_unit
            product.name_en = request.name_en
            product.name_cn = request.name_cn
            product.image_url = request.image_url
            product.deleted = False
            product.change_id = getNextChangeId()
            product.put()

            enqueueNotifyProductChange()

            return ProductResponse(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS, item=dbProductToBackendProduct(product))
        else:
            return ProductResponse(code=ERROR_INVIALID_PARAMETER, message=BACKEND_MESSAGE_FAILED)


    @endpoints.method(BackendUser, UserResponse,
                      path='backend/change_user', http_method='POST',
                      name='changeUser')
    def changeUer(self, request):
        user = None
        if request.id:
            user = ndb.Key(urlsafe=request.id).get()
            if not user or not isinstance(user, DBUser):
                return UserResponse(code=ERROR_INVIALID_PARAMETER)

            if user.login_name != request.login_name:
                if DBUser.query(DBUser.login_name == request.login_name).get():
                    return UserResponse(code=ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED,
                                          message=BACKEND_MESSAGE_DUPLICATE_USER)
            if (user.password != request.password or user.login_name != request.login_name) and user.device_token and user.device_type:
                enqueueNotifyLogout(user.device_type, user.device_token)
                user.device_type = ''
                user.device_token = ''
        else:
            if DBUser.query(DBUser.login_name == request.login_name).get():
                return UserResponse(code=ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED,
                                      message=BACKEND_MESSAGE_DUPLICATE_USER)

            if request.role == USER_ROLE_MONITOR:
                customer_group = DBCustomerGroup.get_by_id(request.parent_id)
                if customer_group and isinstance(customer_group, DBCustomerGroup):
                    user = DBUser(parent=customer_group.key)
                else:
                    return UserResponse(code=ERROR_INVIALID_PARAMETER)
            elif request.role == USER_ROLE_CUSTOMER:
                customer = ndb.Key(urlsafe=request.parent_id).get()
                if customer and isinstance(customer, DBCustomer):
                    user = DBUser(parent=customer.key)
                else:
                    return UserResponse(code=ERROR_INVIALID_PARAMETER)
            elif request.role == USER_ROLE_SUPPLIER:
                supplier = ndb.Key(DBSupplier, HUPHENG_SUPPLIER_ID).get()
                if supplier:
                    user = DBUser(parent=supplier.key)
                else:
                    return UserResponse(code=ERROR_INVIALID_PARAMETER)
            else:
                return UserResponse(code=ERROR_INVIALID_PARAMETER)

        if user:
            user.display_name = request.display_name
            user.login_name = request.login_name
            user.password = request.password
            user.role = request.role
            user.is_manager = request.is_manager
            user.can_create_order = request.can_create_order
            user.can_edit_order = request.can_edit_order
            user.can_cancel_order = request.can_cancel_order
            user.can_confirm_order = request.can_confirm_order
            user.can_deliver_order = request.can_deliver_order
            user.can_receive_order = request.can_receive_order
            user.can_view_history = request.can_view_history
            user.can_view_statistic = request.can_view_statistic
            user.can_change_storage = request.can_change_storage
            user.can_edit_template = request.can_edit_template
            user.put()

            if user.device_token and user.device_type:
                enqueueNotifyUserConfigChange(user.device_type, user.device_token)

            return UserResponse(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS, item=dbUserToBackendUser(user))
        else:
            return UserResponse(code=ERROR_INVIALID_PARAMETER, message=BACKEND_MESSAGE_FAILED)


    @endpoints.method(BackendCustomerRequest, SingleBackendCustomerResponse,
                      path='backend/change_customer', http_method='POST',
                      name='changeCustomer')
    def changeCustomer(self, request):
        if request.id:
            customer = ndb.Key(urlsafe=request.id).get()
        else:
            customer_group = ndb.Key(urlsafe=request.parent_id).get()
            if customer_group and isinstance(customer_group, DBCustomerGroup):
                customer = DBCustomer(parent=customer_group.key)
                customer.deleted = False
                customer.customer_code = DBSystem.get_next_customercode()

        if customer and (not customer.deleted):
            customer.change_id = getNextChangeId()
            customer.hupheng_id = request.hupheng_id
            customer.name_cn = request.name_cn
            customer.name_en = request.name_en
            customer.put()

            enqueueNotifyCustomerChange(customer.key.urlsafe())

            return SingleBackendCustomerResponse(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS, item=dbCustomerToBackendCustomer(customer))


    @endpoints.method(BackendCustomerGroupRequest, SingleBackendCustomerGroupResponse,
                      path='backend/changeCustomerGroup', http_method='POST',
                      name='changeCustomerGroup')
    def changeCustomerGroup(self, request):
        if request.id:
            customer_group = ndb.Key(urlsafe=request.id).get()
        else:
            customer_group = DBCustomerGroup()
            customer_group.deleted = False

        if customer_group and isinstance(customer_group, DBCustomerGroup) and not customer_group.deleted:
            if customer_group.key and (customer_group.name_cn != request.name_cn or customer_group.name_en != request.name_en):
                customers = DBCustomer.query(ancestor=customer_group.key).fetch()
                for customer in customers:
                    customer.change_id = getNextChangeId()
                    customer.put()
                    enqueueNotifyCustomerChange(customer.key.urlsafe())

            customer_group.name_cn = request.name_cn
            customer_group.name_en = request.name_en
            customer_group.put()

            if request.director_id:
                director = ndb.Key(urlsafe=request.director_id).get()
                if not director or not isinstance(director, DBUser):
                    return SingleBackendCustomerGroupResponse(code=ERROR_INVIALID_PARAMETER,
                                          message=BACKEND_MESSAGE_FAILED)
                if director.login_name != request.director_login_name:
                    logging.info('login_name: ' + director.login_name)
                    logging.info('director_login_name: ' + request.director_login_name)
                    if DBUser.query(DBUser.login_name == request.director_login_name).get():
                        return SingleBackendCustomerGroupResponse(code=ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED,
                                          message=BACKEND_MESSAGE_DUPLICATE_USER)
                if (director.password != request.director_password or director.login_name != request.director_login_name)\
                                    and director.device_token and director.device_type:
                    enqueueNotifyLogout(director.device_type, director.device_token)
                    director.device_type = ''
                    director.device_token = ''
            else:
                if DBUser.query(DBUser.login_name == request.director_login_name).get():
                    return SingleBackendCustomerGroupResponse(code=ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED,
                                          message=BACKEND_MESSAGE_DUPLICATE_USER)
                director = DBUser(parent=customer_group.key)

            director.display_name = request.director_display_name
            director.login_name = request.director_login_name
            director.password = request.director_password
            director.role = USER_ROLE_MONITOR
            director.is_manager = True
            director.can = False
            director.can_edit_order = False
            director.can_cancel_order = False
            director.can_confirm_order = False
            director.can_receive_order = False
            director.can_view_history = True
            director.can_view_statistic = False
            director.can_change_storage = False
            director.can_edit_template = False
            director.put()

            if director.device_token and director.device_type:
                enqueueNotifyUserConfigChange(director.device_type, director.device_token)
            return SingleBackendCustomerGroupResponse(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS,
                                                      item=dbCustomerGroupToBackendCustomerGroup(customer_group, dbUserToBackendUser(director)))


    @endpoints.method(message_types.VoidMessage, ProductCollectionResponse,
                      path='backend/listProduct', http_method='POST',
                      name='listProduct')
    def listProduct(self, request):
        db_products = DBProduct.query(DBProduct.deleted == False).order(DBProduct.name_en).fetch()
        product_results = []
        for product in db_products:
            product_results.append(dbProductToBackendProduct(product))
        return ProductCollectionResponse(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS, items=product_results)


    @endpoints.method(message_types.VoidMessage, UserCollectionResponse,
                      path='backend/listEmployee', http_method='POST',
                      name='listEmployee')
    def listEmployee(self, void_message):
        user_results = []
        supplier_key = ndb.Key(DBSupplier, HUPHENG_SUPPLIER_ID)
        supplier = supplier_key.get()
        if not supplier:
            supplier = DBSupplier(key=supplier_key,
                                  name_cn='HUPHENG FRESH',
                                  name_en='HUPHENG FRESH')
            supplier.put()
        db_users = DBUser.query(ancestor=supplier.key).fetch()
        for db_user in db_users:
            user_results.append(dbUserToBackendUser(db_user))
            
        return UserCollectionResponse(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS, items=user_results)

    @endpoints.method(message_types.VoidMessage, CustomerGroupResponseCollection,
                      path='backend/listCustomer', http_method='POST',
                      name='listCustomer')
    def listCustomer(self, void_message):
        db_customer_groups = DBCustomerGroup.query(DBCustomerGroup.deleted == False).fetch()
        db_customers = DBCustomer.query(DBCustomer.deleted == False).fetch()
        db_users = DBUser.query(DBUser.role != USER_ROLE_SUPPLIER).fetch()

        result_groups = []
        for db_group in db_customer_groups:
            group_customers = []
            director = None

            for db_user in db_users:
                if db_user.key.parent() == db_group.key:
                    director = dbUserToBackendUser(db_user)
                    break

            for db_customer in db_customers:
                if db_customer.key.parent() == db_group.key:
                    customer_users = []

                    for db_user in db_users:
                        if db_user.key.parent() == db_customer.key:
                            customer_users.append(dbUserToBackendUser(db_user))

                    group_customers.append(BackendCustomerResponse(id=db_customer.key.urlsafe(),
                                                        hupheng_id=db_customer.hupheng_id,
                                                        name_cn=db_customer.name_cn,
                                                        name_en=db_customer.name_en,
                                                        users=customer_users))

            result_groups.append(BackendCustomerGroupResponse(id=db_group.key.urlsafe(),
                                                       name_en=db_group.name_en,
                                                       name_cn=db_group.name_cn,
                                                       customers=group_customers,
                                                       director=director))

        return CustomerGroupResponseCollection(code=SUCCESS, message=BACKEND_MESSAGE_SUCCESS, items=result_groups)

