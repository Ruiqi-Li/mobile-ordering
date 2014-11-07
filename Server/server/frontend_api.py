import endpoints
import time
import cgi
import urllib
import string
import logging
import copy

from local_static import *
from ndb_models import *
from frontend_rpc import *
from xlsx_generator import generateReport
from xlsx_generator import generateSystemReportHead
from xlsx_generator import generateSystemReportDetail

from queue_worker import enqueueNotifyLogout
from queue_worker import enqueueOrderAutoProcessCheck
from queue_worker import enqueueNotifyCustomerStorageChange
from queue_worker import enqueueNotifySupplierStorageChange
from queue_worker import enqueueNotifyCustomerChange
from queue_worker import enqueueNotifyOrderChange

from google.appengine.ext import ndb
from google.appengine.api import mail
from protorpc import remote
from protorpc import message_types
from datetime import timedelta, datetime, date

package='frontend'

def findCustomerByKey(customers, target_key):
    for customer in customers:
        if customer.key == target_key:
            return customer

def getOrderSummeryInternal(start_date, end_date, customer_ids, customers):
    query = DBOrder.query(ndb.AND(DBOrder.target_date >= start_date,
                                  DBOrder.target_date < end_date))
    query = query.filter(DBOrder.customer_id.IN(customer_ids))
    query = query.filter(DBOrder.state.IN([ORDER_STATE_CONFIRMED, ORDER_STATE_DELIVERED, ORDER_STATE_RECEIVED]))
    db_orders = query.fetch()

    order_item_map = dict()
    customer_order_map = dict()
    date_map_for_systemreport = dict()
    if db_orders:
        for db_order in db_orders:
            order_date = date(db_order.target_date.year, db_order.target_date.month, db_order.target_date.day)
            if order_date not in date_map_for_systemreport:
                date_map_for_systemreport[order_date] = dict()

            customer_map = date_map_for_systemreport[order_date]
            customer = findCustomerByKey(customers, db_order.key.parent())
            if customer.hupheng_id not in customer_map:
                customer_map[customer.hupheng_id] = dict()

            customer_orders = customer_map[customer.hupheng_id]

            for order_item in db_order.order_items:
                if order_item.product_id in order_item_map:
                    item = order_item_map[order_item.product_id]
                    item.amount = item.amount + order_item.amount
                else:
                    order_item_map[order_item.product_id] = order_item

                customer_key = db_order.key.parent().urlsafe()
                if customer_key not in customer_order_map:
                    customer_order_map[customer_key] = dict()

                order_map = customer_order_map[customer_key]

                if order_item.product_id in order_map:
                    order_map[order_item.product_id] = order_map[order_item.product_id] + order_item.amount
                else:
                    order_map[order_item.product_id] = order_item.amount

                if order_item.product_id in customer_orders:
                    customer_order = customer_orders[order_item.product_id]
                    customer_order.amount += order_item.amount
                else:
                    customer_orders[order_item.product_id] = copy.deepcopy(order_item)

    result_items = []
    for key in order_item_map:
        item = order_item_map[key]
        result_items.append(dbOrderItemToFrontendOrderItem(item))
    return result_items, customer_order_map, date_map_for_systemreport

@endpoints.api(name='frontend', version='v1',
               allowed_client_ids=[WEB_CLIENT_ID, ANDROID_CLIENT_ID, IOS_CLIENT_ID, endpoints.API_EXPLORER_CLIENT_ID],
               audiences=[ANDROID_AUDIENCE],
               scopes=[endpoints.EMAIL_SCOPE])
class FrontendApi(remote.Service):
    """Mobile ordering system frontend API v1"""

    @endpoints.method(ReportRequest, SimpleResponse,
                      path='frontend/generate_report', http_method='POST',
                      name='generateReport')
    def generateReport(self, request):
        if len(request.dates) == 1:
            date = request.dates[0]
            start_date = datetime(date.year, date.month, date.day)
            end_date = datetime(date.year, date.month, date.day, 23, 59, 59, 999999)
            title = start_date.strftime('%d, %B %Y')
        elif len(request.dates) == 2:
            date1 = request.dates[0]
            date2 = request.dates[1]
            start_date = datetime(date1.year, date1.month, date1.day)
            end_date = datetime(date2.year, date2.month, date2.day, 23, 59, 59, 999999)
            title = start_date.strftime('%d-%B-%Y') + '~' + end_date.strftime('%d-%B-%Y')
        else:
            return ReportResponse(code=ERROR_INVIALID_PARAMETER)

        mail_address = DBSystem.get_config().report_email
        if mail_address:
            customers = ndb.get_multi([ndb.Key(urlsafe=id) for id in request.customer_ids])
            result_items, customer_order_map, date_map_for_systemreport = getOrderSummeryInternal(start_date, end_date, request.customer_ids, customers)

            products = ndb.get_multi([ndb.Key(urlsafe=item.product_id) for item in result_items])
            product_map = dict()
            for product in products:
                product_map[product.key.urlsafe()] = product

            message = mail.EmailMessage(sender="Mobile Ordering Server <smartmediasoft.sg@gmail.com>", subject="Report")
            message.to = mail_address
            message.body = "Mobile ordering system report"
            message.attachments=[("report(" + title + ").xlsx", generateReport(title, result_items, customer_order_map, customers)),
                                 ("do_head(" + title + ").xlsx", generateSystemReportHead(date_map_for_systemreport)),
                                 ("do_detail(" + title + ").xlsx", generateSystemReportDetail(product_map, date_map_for_systemreport))]
            message.send()

            return SimpleResponse(code=SUCCESS)

    @endpoints.method(ReportRequest, ReportResponse,
                      path='front/get_order_summery', http_method='POST',
                      name='getOrderSummery')
    def getOrderSummery(self, request):
        if len(request.dates) == 1:
            date = request.dates[0]
            start_date = datetime(date.year, date.month, date.day)
            end_date = datetime(date.year, date.month, date.day, 23, 59, 59, 999999)
        elif len(request.dates) == 2:
            date1 = request.dates[0]
            date2 = request.dates[1]
            start_date = datetime(date1.year, date1.month, date1.day)
            end_date = datetime(date2.year, date2.month, date2.day, 23, 59, 59, 999999)
        else:
            return ReportResponse(code=ERROR_INVIALID_PARAMETER)

        customers = ndb.get_multi([ndb.Key(urlsafe=id) for id in request.customer_ids])
        result_items, customer_order_map, date_map_for_systemreport = getOrderSummeryInternal(start_date, end_date, request.customer_ids, customers)

        return ReportResponse(code=SUCCESS, items=result_items)

    @endpoints.method(LoginRequest, UserResponse,
                      path='front/login', http_method='POST',
                      name='login')
    def login(self, request):
        if request.platform != 'ANDROID' and request.platform != 'IOS':
            return UserResponse(code=ERROR_INVIALID_PARAMETER)

        user = DBUser.query(ndb.AND(DBUser.login_name == request.username, DBUser.password == request.password)).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED)
            return UserResponse(code=ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED,
                                       message_cn=message_cn,
                                       message_en=message_en)

        if user.role == USER_ROLE_CUSTOMER or user.role == USER_ROLE_MONITOR:
            customer = user.key.parent().get()
            company_name_en = customer.name_en
            company_name_cn = customer.name_cn
        elif user.role == USER_ROLE_SUPPLIER:
            company_name_en = 'HUPHENG'
            company_name_cn = 'HUPHENG'

        if (user.device_type != request.platform) or (user.device_token != request.token):
            if user.device_type and user.device_token:
                enqueueNotifyLogout(user.device_type, user.device_token)
            user.device_token = request.token
            user.device_type = request.platform
            user.put()

        return UserResponse(code=SUCCESS, user=dbUserToFrontendUser(user,
                    user.key.parent().urlsafe(), company_name_cn, company_name_en))


    @endpoints.method(LogoutRequest, SimpleResponse,
                      path='front/logout', http_method='POST',
                      name='logout')
    def logout(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()

        if user:
            user.device_type = ""
            user.device_token = ""
            user.put()

        return SimpleResponse(code=SUCCESS)

    @endpoints.method(message_types.VoidMessage, OrderDaysResponse,
                      path='front/get_order_days', http_method='POST',
                      name='getOrderDays')
    def getOrderDays(self, request):
        return OrderDaysResponse(days=DBSystem.get_config().order_range)


    @endpoints.method(ChangeStorageRequest, StorageResponse,
                      path='front/change_storage', http_method='POST',
                      name='changeStorage')
    def changeStorage(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return StorageResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)
        supplier_storage = False;
        if not user.can_change_storage:
            message_en, message_cn = codeToMessage(ERROR_USER_DO_NOT_HAS_AUTHORITY)
            return StorageResponse(code=ERROR_USER_DO_NOT_HAS_AUTHORITY,
                        message_cn=message_cn, message_en=message_en)

        if user.role == USER_ROLE_SUPPLIER and request.customer_id:
            customer = ndb.Key(urlsafe=request.customer_id).get()
            if not customer or not isinstance(customer, DBCustomer):
                return StorageResponse(code=ERROR_INVIALID_PARAMETER)

            storage = DBStorage.query(ancestor=customer.key).get()
            if not storage:
                storage = DBStorage(parent=customer.key)
        else:
            if user.role == USER_ROLE_SUPPLIER:
                supplier_storage = True
            storage = DBStorage.query(ancestor=user.key.parent()).get()
            if not storage:
                storage = DBStorage(parent=user.key.parent())

        storage_item = findStorageItem(storage, request.product_id)
        if request.reset:
            if storage_item:
                storage.items.remove(storage_item)
                storage.put()

                if supplier_storage:
                    enqueueOrderAutoProcessCheck()
        else:
            if storage_item:
                if request.change_from != storage_item.amount:
                    return StorageResponse(code=ERROR_ORDER_DATA_ALREADY_CHANGED,
                                           item=dbStorageToFrontendStorage(storage))
                new_value = request.change_from + request.change_value
            else:
                new_value = request.change_value

            if new_value < 0:
                new_value = 0
            else:
                new_value = round(new_value, 1)

            if supplier_storage or new_value:
                if storage_item:
                    storage_item.amount = new_value
                else:
                    if not storage.items:
                        storage.items = []
                    storage.items.append(DBStoreItem(id=request.product_id,
                                                     amount=new_value))
                storage.change_id = getNextChangeId()
                storage.put()

                if supplier_storage and new_value:
                    enqueueOrderAutoProcessCheck()
            elif storage_item:
                storage.items.remove(storage_item)
                storage.change_id = getNextChangeId()
                storage.put()

        if supplier_storage:
            enqueueNotifySupplierStorageChange()
        else:
            enqueueNotifyCustomerStorageChange(user.key.parent().urlsafe())

        return StorageResponse(code=SUCCESS, item=dbStorageToFrontendStorage(storage))


    @endpoints.method(GetStorageRequest, StorageResponse,
                      path='front/get_storage', http_method='POST',
                      name='getStorage')
    def getStorage(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return StorageResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)

        if (user.role == USER_ROLE_SUPPLIER or user.role == USER_ROLE_MONITOR) and request.customer_id:
            query = DBStorage.query(ancestor=ndb.Key(urlsafe=request.customer_id))
        else:
            query = DBStorage.query(ancestor=user.key.parent())

        storage = query.filter(DBStorage.change_id > request.change_id).get()

        return StorageResponse(code=SUCCESS, item=dbStorageToFrontendStorage(storage))

    """ Product Api """

    @endpoints.method(SimpleRequest, ProductCollectionResponse,
                      path='front/list_product', http_method='POST',
                      name='listProduct')
    def listProduct(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return ProductCollectionResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)
        db_products = DBProduct.query(DBProduct.change_id > request.change_id).fetch()
        result_products = []
        for db_product in db_products:
            result_products.append(dbProductToFrontendProduct(db_product))

        return ProductCollectionResponse(code=SUCCESS, items=result_products)

    """ Order Api """

    @endpoints.method(MakeOrderRequest, OrderResponse,
                      path='front/make_order', http_method='POST',
                      name='makeOrder')
    def makeOrder(self, request):
        if (sum(n <= 0 for n in request.product_amounts) > 0):
            return OrderResponse(code=ERROR_INVIALID_PARAMETER)

        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return OrderResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)
        if not user.can_create_order:
            message_en, message_cn = codeToMessage(ERROR_USER_DO_NOT_HAS_AUTHORITY)
            return OrderResponse(code=ERROR_USER_DO_NOT_HAS_AUTHORITY,
                                       message_cn=message_cn,
                                       message_en=message_en)

        target_date = datetime(request.target_date.year,
                               request.target_date.month,
                               request.target_date.day)

        if user.role != USER_ROLE_SUPPLIER:
            qutoa_date = datetime(target_date.year, target_date.month, target_date.day, DBSystem.get_config().make_quote) - timedelta(days=1)
            now = datetime.now() + timedelta(hours=8)
            if now > qutoa_date:
                message_en, message_cn = codeToMessage(ERROR_ORDER_MAKE_TIME_EXPIRED)
                return OrderResponse(code=ERROR_ORDER_MAKE_TIME_EXPIRED,
                                            message_cn=message_cn,
                                            message_en=message_en)

        if user.role == USER_ROLE_SUPPLIER and request.customer_id:
            customer = ndb.Key(urlsafe=request.customer_id).get()
        else:
            customer = user.key.parent().get()

        if not customer or not isinstance(customer, DBCustomer):
            return OrderResponse(code=ERROR_INVIALID_PARAMETER)

        key_list = []
        for product_id in request.product_ids:
            key_list.append(ndb.Key(urlsafe=product_id))
        products = ndb.get_multi(key_list)

        change_id = getNextChangeId()
        human_id = getNextHumanId(customer.key)
        descriptions = []
        if request.description:
            descriptions.append(request.description)
        if user.role == USER_ROLE_SUPPLIER:
            operate_action = OPERATE_ACTION_SUPPLIER_NEW
        else:
            operate_action = OPERATE_ACTION_CUSTOMER_NEW

        operate_time = DBOrderTimeOperate(time_to=target_date)
        operate_items, order_items = getOperateAndOrderItemsNew(products,
                            request.product_ids, request.product_amounts)
        operates = [DBOrderOperate(operator=user.display_name,
                                   action=operate_action,
                                   date=datetime.now(),
                                   description=request.description,
                                   operate_items=operate_items,
                                   operate_time=operate_time)]

        new_order = DBOrder(parent=customer.key,
                            customer_id=customer.key.urlsafe(),
                            change_id=change_id,
                            human_id=human_id,
                            state=ORDER_STATE_PENDING,
                            target_date=target_date,
                            create_date=datetime.now(),
                            descriptions=descriptions,
                            operates=operates,
                            order_items=order_items)
        new_order.put()

        enqueueNotifyOrderChange('New Order: ' + new_order.human_id, 'FALSE', customer.key.urlsafe())
        enqueueOrderAutoProcessCheck()

        return OrderResponse(code=SUCCESS, item=dbOrderToFrontendOrder(new_order))


    @endpoints.method(AmendOrderRequest, OrderResponse,
                      path='front/amend_order', http_method='POST',
                      name='amendOrder')
    def amendOrder(self, request):
        if (sum(n <= 0 for n in request.product_amounts) > 0):
            return OrderResponse(code=ERROR_INVIALID_PARAMETER)

        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return OrderResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)
        if not user.can_edit_order:
            message_en, message_cn = codeToMessage(ERROR_USER_DO_NOT_HAS_AUTHORITY)
            return OrderResponse(code=ERROR_USER_DO_NOT_HAS_AUTHORITY,
                                       message_cn=message_cn,
                                       message_en=message_en)
        order = ndb.Key(urlsafe=request.order_id).get()
        if not order or not isinstance(order, DBOrder):
            return OrderResponse(code=ERROR_INVIALID_PARAMETER)

        if order.change_id != request.change_id:
            return OrderResponse(code=ERROR_ORDER_DATA_ALREADY_CHANGED,
                                 item=dbOrderToFrontendOrder(order))

        if user.role != USER_ROLE_SUPPLIER:
            if order.state != ORDER_STATE_PENDING and order.state != ORDER_STATE_CONFIRMED:
                message_en, message_cn = codeToMessage(ERROR_ORDER_ALREADY_SHIPED_CANNOT_CHANGE)
                return OrderResponse(code=ERROR_ORDER_ALREADY_SHIPED_CANNOT_CHANGE,
                                            message_cn=message_cn,
                                            message_en=message_en)
            qutoa_date = datetime(order.target_date.year, order.target_date.month, order.target_date.day, DBSystem.get_config().editQuote) - timedelta(days=1)
            now = datetime.now() + timedelta(hours=8)
            if now > qutoa_date:
                message_en, message_cn = codeToMessage(ERROR_ORDER_EDIT_TIME_EXPIRED)
                return OrderResponse(code=ERROR_ORDER_EDIT_TIME_EXPIRED,
                                            message_cn=message_cn,
                                            message_en=message_en)

        key_list = []
        for product_id in request.product_ids:
            key_list.append(ndb.Key(urlsafe=product_id))
        products = ndb.get_multi(key_list)

        order.change_id = getNextChangeId()
        if request.description:
            if not order.descriptions:
                order.descriptions = []
            order.descriptions.append(request.description)
        if user.role == USER_ROLE_SUPPLIER:
            operate_action = OPERATE_ACTION_SUPPLIER_AMEND
        else:
            operate_action = OPERATE_ACTION_CUSTOMER_AMEND

        target_date = datetime(request.target_date.year,
                               request.target_date.month,
                               request.target_date.day)
        operate_time = None
        if order.target_date != target_date:
            operate_time = DBOrderTimeOperate(time_from=order.target_date,
                                              time_to=target_date)
            order.target_date = target_date

        operate_items, order_items, back_to_pending = getOperateAndOrderItemsAmend(products, order,
                            request.product_ids, request.product_amounts)
        order.operates.append(DBOrderOperate(operator=user.display_name,
                                   action=operate_action,
                                   date=datetime.now(),
                                   description=request.description,
                                   operate_items=operate_items,
                                   operate_time=operate_time))
        if back_to_pending:
            order.state = ORDER_STATE_PENDING
            order.confirm_date = None
            order.operates.append(DBOrderOperate(operator='System',
                                   action=OPERATE_ACTION_SYSTEM_AMEND,
                                   date=datetime.now() + timedelta(seconds=1),
                                   description=SYSTEM_BACK_TO_PENDING_DESCRIPTION))
        order.order_items = order_items
        order.put()

        notify = 'FALSE'
        if hasSystemAmend(order):
            notify = 'TRUE'

        enqueueNotifyOrderChange('Order Amend: ' + order.human_id, notify, order.key.parent().urlsafe())
        enqueueOrderAutoProcessCheck()

        return OrderResponse(code=SUCCESS, item=dbOrderToFrontendOrder(order))


    @endpoints.method(ChangeOrderStateRequest, OrderResponse,
                      path='front/cancel_order', http_method='POST',
                      name='cancelOrder')
    def cancelOrder(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return OrderResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)
        if not user.can_cancel_order:
            message_en, message_cn = codeToMessage(ERROR_USER_DO_NOT_HAS_AUTHORITY)
            return OrderResponse(code=ERROR_USER_DO_NOT_HAS_AUTHORITY,
                                       message_cn=message_cn,
                                       message_en=message_en)
        order = ndb.Key(urlsafe=request.order_id).get()
        if not order or (user.role != USER_ROLE_SUPPLIER and order.key.parent() != user.key.parent()):
            return OrderResponse(code=ERROR_INVIALID_PARAMETER)

        if order.state == ORDER_STATE_CANCELED or order.state == ORDER_STATE_RECEIVED:
            message_en, message_cn = codeToMessage(ERROR_ORDER_REQUIRED_STATE_NOT_CORRECT)
            return OrderResponse(code=ERROR_ORDER_REQUIRED_STATE_NOT_CORRECT,
                                message_cn=message_cn,
                                message_en=message_en)

        if user.role != USER_ROLE_SUPPLIER:
            if order.state != ORDER_STATE_PENDING and order.state != ORDER_STATE_CONFIRMED:
                message_en, message_cn = codeToMessage(ERROR_ORDER_ALREADY_SHIPED_CANNOT_CANCEL)
                return OrderResponse(code=ERROR_ORDER_ALREADY_SHIPED_CANNOT_CANCEL,
                                            message_cn=message_cn,
                                            message_en=message_en)
            qutoa_date = datetime(order.target_date.year, order.target_date.month, order.target_date.day, DBSystem.get_config().cancelQuote) - timedelta(days=1)
            now = datetime.now() + timedelta(hours=8)
            if now > qutoa_date:
                message_en, message_cn = codeToMessage(ERROR_ORDER_EDIT_TIME_EXPIRED)
                return OrderResponse(code=ERROR_ORDER_EDIT_TIME_EXPIRED,
                                            message_cn=message_cn,
                                            message_en=message_en)

        if user.role == USER_ROLE_SUPPLIER:
            operate_action = OPERATE_ACTION_SUPPLIER_CANCEL
        else:
            operate_action = OPERATE_ACTION_CUSTOMER_CANCEL
        orderAddSimpleAction(order, user, operate_action, request.description)
        order.state = ORDER_STATE_CANCELED
        order.finish_date = datetime.now()
        order.change_id = getNextChangeId()
        order.put()

        if order.state != ORDER_STATE_PENDING:
            supplier_store = DBStorage.query(ancestor=ndb.Key(DBSupplier, HUPHENG_SUPPLIER_ID)).get()
            if supplier_store and supplier_store.items:
                changes = False
                for order_item in order.order_items:
                    find = [store_item for store_item in supplier_store.items if store_item.id == order_item.product_id]
                    if find:
                        changes = True
                        find[0].amount += order_item.amount
                if changes:
                    supplier_store.change_id = getNextChangeId()
                    supplier_store.put()
                    enqueueNotifySupplierStorageChange()

        enqueueNotifyOrderChange('Order Canceled: ' + order.human_id, 'TRUE', order.key.parent().urlsafe())
        enqueueOrderAutoProcessCheck()

        return OrderResponse(code=SUCCESS, item=dbOrderToFrontendOrder(order))


    @endpoints.method(ChangeOrderStateRequest, OrderResponse,
                      path='front/confirm_order', http_method='POST',
                      name='confirmOrder')
    def confirmOrder(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return OrderResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)
        if user.role != USER_ROLE_SUPPLIER or not user.can_confirm_order:
            message_en, message_cn = codeToMessage(ERROR_USER_DO_NOT_HAS_AUTHORITY)
            return OrderResponse(code=ERROR_USER_DO_NOT_HAS_AUTHORITY,
                                       message_cn=message_cn,
                                       message_en=message_en)
        order = ndb.Key(urlsafe=request.order_id).get()
        if not order or not isinstance(order, DBOrder):
            return OrderResponse(code=ERROR_INVIALID_PARAMETER)

        if order.state != ORDER_STATE_PENDING:
            message_en, message_cn = codeToMessage(ERROR_ORDER_REQUIRED_STATE_NOT_CORRECT)
            return OrderResponse(code=ERROR_ORDER_REQUIRED_STATE_NOT_CORRECT,
                                message_cn=message_cn,
                                message_en=message_en)

        if order.change_id != request.change_id:
            return OrderResponse(code=ERROR_ORDER_DATA_ALREADY_CHANGED,
                                 item=dbOrderToFrontendOrder(order))

        orderAddSimpleAction(order, user, OPERATE_ACTION_CONFIRM, request.description)
        order.state = ORDER_STATE_CONFIRMED
        order.confirm_date = datetime.now()
        order.change_id = getNextChangeId()
        order.put()

        supplier_store = DBStorage.query(ancestor=user.key.parent()).get()
        if supplier_store and supplier_store.items:
            change = False
            for order_item in order.order_items:
                find = [store_item for store_item in supplier_store.items if store_item.id == order_item.product_id]
                if find and find[0].amount > 0:
                    change = True
                    find[0].amount = find[0].amount - order_item.amount
                    if find[0].amount < 0:
                        find[0].amount = 0
                    else:
                        find[0].amount = round(find[0].amount, 1)
            if change:
                supplier_store.change_id = getNextChangeId()
                supplier_store.put()
                enqueueNotifySupplierStorageChange()

        enqueueNotifyOrderChange('Order Confirmed: ' + order.human_id, 'FALSE', order.key.parent().urlsafe())

        return OrderResponse(code=SUCCESS, item=dbOrderToFrontendOrder(order))

    @endpoints.method(ChangeOrderStateRequest, OrderResponse,
                      path='front/deliver_order', http_method='POST',
                      name='deliverOrder')
    def deliverOrder(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return OrderResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)
        if user.role != USER_ROLE_SUPPLIER or not user.can_deliver_order:
            message_en, message_cn = codeToMessage(ERROR_USER_DO_NOT_HAS_AUTHORITY)
            return OrderResponse(code=ERROR_USER_DO_NOT_HAS_AUTHORITY,
                                       message_cn=message_cn,
                                       message_en=message_en)
        order = ndb.Key(urlsafe=request.order_id).get()
        if not order or not isinstance(order, DBOrder):
            return OrderResponse(code=ERROR_INVIALID_PARAMETER)

        if order.state != ORDER_STATE_CONFIRMED:
            message_en, message_cn = codeToMessage(ERROR_ORDER_REQUIRED_STATE_NOT_CORRECT)
            return OrderResponse(code=ERROR_ORDER_REQUIRED_STATE_NOT_CORRECT,
                                message_cn=message_cn,
                                message_en=message_en)

        if order.change_id != request.change_id:
            return OrderResponse(code=ERROR_ORDER_DATA_ALREADY_CHANGED,
                                 item=dbOrderToFrontendOrder(order))

        orderAddSimpleAction(order, user, OPERATE_ACTION_DELIVER, request.description)
        order.state = ORDER_STATE_DELIVERED
        order.deliver_date = datetime.now()
        order.change_id = getNextChangeId()
        order.put()

        enqueueNotifyOrderChange('Order Delivered: ' + order.human_id, 'FALSE', order.key.parent().urlsafe())

        return OrderResponse(code=SUCCESS, item=dbOrderToFrontendOrder(order))


    @endpoints.method(ChangeOrderStateRequest, OrderResponse,
                      path='front/receive_order', http_method='POST',
                      name='receiveOrder')
    def receiveOrder(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return OrderResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)
        if not user.can_receive_order:
            message_en, message_cn = codeToMessage(ERROR_USER_DO_NOT_HAS_AUTHORITY)
            return OrderResponse(code=ERROR_USER_DO_NOT_HAS_AUTHORITY,
                                       message_cn=message_cn,
                                       message_en=message_en)
        order = ndb.Key(urlsafe=request.order_id).get()
        if not order or not isinstance(order, DBOrder):
            return OrderResponse(code=ERROR_INVIALID_PARAMETER)

        if order.state != ORDER_STATE_DELIVERED:
            message_en, message_cn = codeToMessage(ERROR_ORDER_REQUIRED_STATE_NOT_CORRECT)
            return OrderResponse(code=ERROR_ORDER_REQUIRED_STATE_NOT_CORRECT,
                                message_cn=message_cn,
                                message_en=message_en)

        if order.change_id != request.change_id:
            return OrderResponse(code=ERROR_ORDER_DATA_ALREADY_CHANGED,
                                 item=dbOrderToFrontendOrder(order))

        orderAddSimpleAction(order, user, OPERATE_ACTION_RECEIVE, request.description)
        order.state = ORDER_STATE_RECEIVED
        order.finish_date = datetime.now()
        order.change_id = getNextChangeId()
        order.put()

        enqueueNotifyOrderChange('Order Received: ' + order.human_id, 'FALSE', order.key.parent().urlsafe())

        return OrderResponse(code=SUCCESS, item=dbOrderToFrontendOrder(order))


    @endpoints.method(SimpleRequest, OrderCollectionResponse,
                      path='front/list_order', http_method='POST',
                      name='listOrder')
    def listOrder(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return OrderCollectionResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)

        if user.role == USER_ROLE_CUSTOMER:
            query = DBOrder.query(ancestor=user.key.parent())
        elif user.role == USER_ROLE_MONITOR:
            query = DBOrder.query(ancestor=user.key.parent())
        elif user.role == USER_ROLE_SUPPLIER:
            query = DBOrder.query()
        query = query.filter(DBOrder.state.IN([ORDER_STATE_PENDING, ORDER_STATE_CONFIRMED, ORDER_STATE_DELIVERED]))

        all_ongoing_order = query.fetch(projection=[DBOrder.human_id])
        ongoing_human_ids = [order.human_id for order in all_ongoing_order]

        query = query.filter(DBOrder.change_id > request.change_id)
        db_orders = query.order(-DBOrder.change_id).fetch()
        result_orders = []
        if db_orders:
            for db_order in db_orders:
                result_orders.append(dbOrderToFrontendOrder(db_order))

        return OrderCollectionResponse(code=SUCCESS, items=result_orders,
                                       ongoing_order_humanids=ongoing_human_ids)


    @endpoints.method(RefreshHistoryOrderRequest, OrderHistoryResponse,
                      path='front/refersh_history_order', http_method='POST',
                      name='refreshHistoryOrder')
    def refreshHistoryOrder(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()

        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return OrderHistoryResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)

        if user.role == USER_ROLE_MONITOR or user.role == USER_ROLE_SUPPLIER:
            query = DBOrder.query(ancestor=ndb.Key(urlsafe=request.customer_id))
        else:
            query = DBOrder.query(ancestor=user.key.parent())
        query = query.filter(DBOrder.change_id > request.start_id)
        query = query.filter(ndb.OR(DBOrder.state == ORDER_STATE_RECEIVED, DBOrder.state == ORDER_STATE_CANCELED))
        db_orders = query.order(-DBOrder.change_id).fetch(25)

        result_items = []
        start_id = request.start_id
        end_id = None
        if db_orders:
            start_id = db_orders[0].change_id
            if len(db_orders) == 25:
                end_id = db_orders[-1].change_id

            for db_order in db_orders:
                result_items.append(dbOrderToFrontendOrder(db_order))

        return OrderHistoryResponse(code=SUCCESS, start_id=start_id, end_id=end_id, items=result_items)


    @endpoints.method(MoreHistoryOrderRequest, OrderHistoryResponse,
                      path='front/more_history_order', http_method='POST',
                      name='moreHistoryOrder')
    def moreHistoryOrder(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()

        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return OrderHistoryResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)

        if user.role == USER_ROLE_SUPPLIER and request.customer_id:
            query = DBOrder.query(ancestor=ndb.Key(urlsafe=request.customer_id))
        else:
            query = DBOrder.query(ancestor=user.key.parent())
        query = query.filter(DBOrder.change_id < request.end_id)
        query = query.filter(ndb.OR(DBOrder.state == ORDER_STATE_RECEIVED, DBOrder.state == ORDER_STATE_CANCELED))
        db_orders = query.order(-DBOrder.change_id).fetch(25)

        end_id = None
        result_items = []
        if db_orders:
            if len(db_orders) == 25:
                end_id = db_orders[-1].change_id
            for db_order in db_orders:
                result_items.append(dbOrderToFrontendOrder(db_order))

        return OrderHistoryResponse(code=SUCCESS, end_id=end_id, items=result_items)


    @endpoints.method(SearchHistoryOrderRequest, OrderCollectionResponse,
                      path='front/search_history_order', http_method='POST',
                      name='searchHistoryOrder')
    def searchHistoryOrder(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return OrderCollectionResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)
        if not user.can_view_history:
            message_en, message_cn = codeToMessage(ERROR_USER_DO_NOT_HAS_AUTHORITY)
            return OrderCollectionResponse(code=ERROR_USER_DO_NOT_HAS_AUTHORITY,
                                       message_cn=message_cn,
                                       message_en=message_en)

        if user.role == USER_ROLE_SUPPLIER:
            query = DBOrder.query()
        else:
            query = DBOrder.query(ancestor=user.key.parent())

        query = query.filter(ndb.OR(DBOrder.state == ORDER_STATE_CANCELED, DBOrder.state == ORDER_STATE_RECEIVED))
        query = query.filter(DBOrder.human_id.IN(request.query_ids))
        db_orders = query.fetch()
        result_items = []
        if db_orders:
            for db_order in db_orders:
                result_items.append(dbOrderToFrontendOrder(db_order))

        return OrderCollectionResponse(code=SUCCESS, items=result_items)

    """ Customer Api """

    @endpoints.method(SimpleRequest, CustomerCollectionResponse,
                      path='front/list_customer', http_method='POST',
                      name='listCustomer')
    def listCustomer(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return CustomerCollectionResponse(code=ERROR_LOGIN_KICK_OFF,
                                       message_cn=message_cn,
                                       message_en=message_en)
        result_items = []
        if user.role == USER_ROLE_SUPPLIER:
            db_customer_all = DBCustomer.query(DBCustomer.deleted == False).fetch(projection=[DBCustomer.customer_code])
            db_customer_all = [db_customer.key.urlsafe() for db_customer in db_customer_all]
            db_customers = DBCustomer.query(DBCustomer.change_id > request.change_id).fetch()
            customer_group_keys = []
            for db_customer in db_customers:
                customer_group_keys.append(db_customer.key.parent())

            if customer_group_keys:
                db_customer_groups = ndb.get_multi(customer_group_keys)

            for db_customer in db_customers:
                db_customer_group = findCustomerGroupByKey(db_customer_groups, db_customer.key.parent())
                if db_customer_group:
                    result_items.append(dbCustomerToFrontendCustomer(db_customer_group, db_customer))
        elif user.role == USER_ROLE_CUSTOMER:
            db_customer = user.key.parent().get()
            db_customer_all = [db_customer.key.urlsafe()]
            if db_customer.change_id > request.change_id:
                db_customer_group = db_customer.key.parent().get()
                result_items.append(dbCustomerToFrontendCustomer(db_customer_group, db_customer))
        elif user.role == USER_ROLE_MONITOR:
            db_customer_group = user.key.parent().get()
            query = DBCustomer.query(ancestor=db_customer_group.key)
            query = query.filter(DBCustomer.deleted == False)
            db_customer_all = query.fetch(projection=[DBCustomer.customer_code])
            db_customer_all = [db_customer.key.urlsafe() for db_customer in db_customer_all]
            db_customers = query.filter(DBCustomer.change_id > request.change_id).fetch()

            for db_customer in db_customers:
                result_items.append(dbCustomerToFrontendCustomer(db_customer_group, db_customer))

        return CustomerCollectionResponse(code=SUCCESS, items=result_items, customer_set=db_customer_all)

    """ Template Api """

    @endpoints.method(ChangeTemplateRequest, TemplateResponse,
                      path='front/change_template', http_method='POST',
                      name='changeTemplate')
    def changeTemplate(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return TemplateResponse(code=ERROR_LOGIN_KICK_OFF,
                        message_cn=message_cn, message_en=message_en)

        customer = user.key.parent().get()
        if not customer or not isinstance(customer, DBCustomer):
            return TemplateResponse(code=ERROR_INVIALID_PARAMETER)

        template = findTemplateByName(customer, request.template_name)
        if not template:
            if not customer.templates:
                customer.templates = []
            template = DBTemplate(name=request.template_name)
            customer.templates.append(template)

        key_list = [ndb.Key(urlsafe=product_id) for product_id in request.product_ids]
        products = ndb.get_multi(key_list)

        description_cn = [(str(request.product_amounts[index]) + product.unit_name + ' ' + product.name_cn) \
                          for index, product in enumerate(products)]
        description_en = [(str(request.product_amounts[index]) + product.unit_name + ' ' + product.name_en) \
                          for index, product in enumerate(products)]

        description_cn = ','.join(description_cn)
        description_en = ','.join(description_en)

        template.product_ids = request.product_ids
        template.product_amounts = request.product_amounts
        template.summery_cn = description_cn
        template.summery_en = description_en
        customer.change_id = getNextChangeId()
        customer.put()

        enqueueNotifyCustomerChange(customer.key.urlsafe())

        return TemplateResponse(code=SUCCESS, item=dbTemplateToFrontendTemplate(template))


    @endpoints.method(DeleteTemplateRequest, SimpleResponse,
                      path='front/delete_template', http_method='POST',
                      name='deleteTemplate')
    def deleteTemplate(self, request):
        user = DBUser.query(DBUser.device_token == request.token).get()
        if not user:
            message_en, message_cn = codeToMessage(ERROR_LOGIN_KICK_OFF)
            return SimpleResponse(code=ERROR_LOGIN_KICK_OFF,
                        message_cn=message_cn, message_en=message_en)

        customer = user.key.parent().get()
        template = findTemplateByName(customer, request.template_name)
        if not template:
            return SimpleResponse(code=ERROR_DATA_NOT_FOUND)

        customer.templates.remove(template)
        customer.change_id = getNextChangeId()
        customer.put()

        enqueueNotifyCustomerChange(customer.key.urlsafe())

        return SimpleResponse(code=SUCCESS)

def getOperateAndOrderItemsAmend(products, order, ids, amounts):
    old_order_items = order.order_items
    operate_items = []
    order_items = []
    storage = DBStorage.query(ancestor=ndb.Key(DBSupplier, HUPHENG_SUPPLIER_ID)).get()
    backToPending = False;

    for idx, id in enumerate(ids):
        product = findProductById(products, id)
        old_item = findOrderItemByProductId(old_order_items, product.key.urlsafe())
        if old_item:
            value_before = old_item.amount
        else:
            value_before = 0;
        value_after = amounts[idx];

        addition = value_after - value_before;
        if order.state == ORDER_STATE_CONFIRMED and addition > 0:
            storage_item = findStorageByProductId(storage, id)
            if storage_item and storage_item.amount < addition:
                backToPending = True

        if value_before != value_after:
            operate_items.append(DBOrderOperateItem(image_url=product.image_url,
                                                    unit_name=product.unit_name,
                                                    name_cn=product.name_cn,
                                                    name_en=product.name_en,
                                                    value_before=value_before,
                                                    value_after=value_after))
        order_items.append(DBOrderItem(product_id=id,
                                       amount=amounts[idx],
                                       unit_name=product.unit_name,
                                       descimal_unit=product.descimal_unit,
                                       name_cn=product.name_cn,
                                       name_en=product.name_en,
                                       image_url=product.image_url))
    for old_item in old_order_items:
        if old_item.product_id not in ids:
            operate_items.append(DBOrderOperateItem(image_url=old_item.image_url,
                                                     unit_name=old_item.unit_name,
                                                     name_cn=old_item.name_cn,
                                                     name_en=old_item.name_en,
                                                     value_before=old_item.amount,
                                                     value_after=0))

    if order.state == ORDER_STATE_CONFIRMED:
        if backToPending:
            for old_item in old_order_items:
                store_item = findStorageByProductId(storage, old_item.product_id)
                if store_item:
                    store_item.amount = store_item.amount + old_item.amount
        else:
            for old_item in old_order_items:
                store_item = findStorageByProductId(storage, old_item.product_id)
                if store_item:
                    store_item.amount = store_item.amount + old_item.amount
            for new_item in order_items:
                store_item = findStorageByProductId(storage, new_item.product_id)
                if store_item:
                    store_item.amount = store_item.amount - new_item.amount
        storage.change_id = getNextChangeId()
        storage.put()
        enqueueNotifySupplierStorageChange()

    return operate_items, order_items, backToPending

def findStorageByProductId(storage, product_id):
    if storage:
        for storage_item in storage.items:
            if storage_item.id == product_id:
                return storage_item

def hasSystemAmend(order):
    for operate_item in order.operates:
        if operate_item.action == OPERATE_ACTION_SYSTEM_AMEND:
            return True