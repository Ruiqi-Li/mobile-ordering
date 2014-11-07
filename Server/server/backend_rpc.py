from protorpc import messages

""" General """

class SimpleResponse(messages.Message):
    """Simple response only indicate request is success or failed"""
    code = messages.IntegerField(1, required=True)
    message = messages.StringField(2)

class DeleteRequest(messages.Message):
    """Delete request"""
    id = messages.StringField(1, required=True)

class LoginRequest(messages.Message):
    """User for backend login"""
    username = messages.StringField(1, required=True)
    password = messages.StringField(2, required=True)


""" Backend Configure """

class BackendConfig(messages.Message):
    """ System configuration request"""
    editQuote = messages.IntegerField(1)
    cancelQuote = messages.IntegerField(2)
    report_email = messages.StringField(3)
    make_quote = messages.IntegerField(4)
    order_range = messages.IntegerField(5)

""" Product """

class BackendProduct(messages.Message):
    """ Product """
    id = messages.StringField(1)
    name_cn = messages.StringField(2, required=True)
    name_en = messages.StringField(3, required=True)
    image_url = messages.StringField(4, required=True)
    type = messages.StringField(5, required=True)
    unit_name = messages.StringField(6, required=True)
    descimal_unit = messages.BooleanField(7, required=True)
    hupheng_id = messages.StringField(8, required=True)

class ProductCollectionResponse(messages.Message):
    """ Product response collection """
    code = messages.IntegerField(1, required=True)
    message = messages.StringField(2)
    items = messages.MessageField(BackendProduct, 3, repeated=True)

class ProductResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    item = messages.MessageField(BackendProduct, 2)
    message = messages.StringField(3)

""" User """

class BackendUser(messages.Message):
    """ User """
    id = messages.StringField(1)
    parent_id = messages.StringField(2)
    display_name = messages.StringField(3, required=True)
    login_name = messages.StringField(4, required=True)
    password = messages.StringField(5, required=True)
    role = messages.StringField(6, required=True)
    is_manager = messages.BooleanField(7, required=True)
    can_create_order = messages.BooleanField(8, required=True)
    can_edit_order = messages.BooleanField(9, required=True)
    can_cancel_order = messages.BooleanField(10, required=True)
    can_confirm_order = messages.BooleanField(11, required=True)
    can_deliver_order = messages.BooleanField(12, required=True)
    can_receive_order = messages.BooleanField(13, required=True)
    can_view_history = messages.BooleanField(14, required=True)
    can_view_statistic = messages.BooleanField(15, required=True)
    can_change_storage = messages.BooleanField(16, required=True)
    can_edit_template = messages.BooleanField(17, required=True)

class UserCollectionResponse(messages.Message):
    """ User collections """
    code = messages.IntegerField(1, required=True)
    message = messages.StringField(2)
    items = messages.MessageField(BackendUser, 3, repeated=True)

class UserResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    item = messages.MessageField(BackendUser, 2)
    message = messages.StringField(3)

""" Customer """

class BackendCustomerGroupRequest(messages.Message):
    """ Customer group request """
    id = messages.StringField(1)
    name_cn = messages.StringField(2, required=True)
    name_en = messages.StringField(3, required=True)
    director_id = messages.StringField(4)
    director_display_name = messages.StringField(5, required=True)
    director_login_name = messages.StringField(6, required=True)
    director_password = messages.StringField(7, required=True)

class BackendCustomerRequest(messages.Message):
    """" Customer request """
    id = messages.StringField(1)
    parent_id = messages.StringField(2, required=True)
    hupheng_id = messages.StringField(3, required=True)
    name_cn = messages.StringField(4, required=True)
    name_en = messages.StringField(5, required=True)

class BackendCustomerResponse(messages.Message):
    """" Backend customer """
    id = messages.StringField(1, required=True)
    hupheng_id = messages.StringField(2, required=True)
    name_cn = messages.StringField(3, required=True)
    name_en = messages.StringField(4, required=True)
    users = messages.MessageField(BackendUser, 5, repeated=True)

class SingleBackendCustomerResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    item = messages.MessageField(BackendCustomerResponse, 2)
    message = messages.StringField(3)

class BackendCustomerGroupResponse(messages.Message):
    """ Backend customer group """
    id = messages.StringField(1, required=True)
    name_en = messages.StringField(2, required=True)
    name_cn = messages.StringField(3, required=True)
    director = messages.MessageField(BackendUser, 4)
    customers = messages.MessageField(BackendCustomerResponse, 5, repeated=True)

class SingleBackendCustomerGroupResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    item = messages.MessageField(BackendCustomerGroupResponse, 2)
    message = messages.StringField(3)

class CustomerGroupResponseCollection(messages.Message):
    """ Backend use only, get all datas """
    code = messages.IntegerField(1, required=True)
    message = messages.StringField(2)
    items = messages.MessageField(BackendCustomerGroupResponse, 3, repeated=True)

""" Helper method"""

def dbCustomerGroupToBackendCustomerGroup(db_group, director):
    return BackendCustomerGroupResponse(id=db_group.key.urlsafe(),
                                        name_en=db_group.name_en,
                                        name_cn=db_group.name_cn,
                                        director=director)

def dbCustomerToBackendCustomer(db_customer):
    return BackendCustomerResponse(id=db_customer.key.urlsafe(),
                                    hupheng_id=db_customer.hupheng_id,
                                    name_cn=db_customer.name_cn,
                                    name_en=db_customer.name_en);

def dbUserToBackendUser(db_user):
    return BackendUser(id=db_user.key.urlsafe(),
                   display_name=db_user.display_name,
                   login_name=db_user.login_name,
                   password=db_user.password,
                   role=db_user.role,
                   is_manager=db_user.is_manager,
                   can_create_order=db_user.can_create_order,
                   can_edit_order=db_user.can_edit_order,
                   can_cancel_order=db_user.can_cancel_order,
                   can_confirm_order=db_user.can_confirm_order,
                   can_deliver_order=db_user.can_deliver_order,
                   can_receive_order=db_user.can_receive_order,
                   can_view_history=db_user.can_view_history,
                   can_view_statistic=db_user.can_view_statistic,
                   can_change_storage=db_user.can_change_storage,
                   can_edit_template=db_user.can_edit_template)

def dbProductToBackendProduct(db_product):
    return BackendProduct(id=db_product.key.urlsafe(),
                          name_cn=db_product.name_cn,
                          name_en=db_product.name_en,
                          image_url=db_product.image_url,
                          type=db_product.type,
                          unit_name=db_product.unit_name,
                          descimal_unit=db_product.descimal_unit,
                          hupheng_id=db_product.hupheng_id)