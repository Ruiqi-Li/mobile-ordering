import calendar
from protorpc import messages
from protorpc import message_types

from google.appengine.api import taskqueue

""" General Request """

class SimpleRequest(messages.Message):
    token = messages.StringField(1, required=True)
    change_id = messages.IntegerField(2, required=True)

class SimpleResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    message_cn = messages.StringField(2)
    message_en = messages.StringField(3)

class SmsOrderItem(messages.Message):
    product_id = messages.StringField(1, required=True)
    amount = messages.FloatField(2, required=True)
    unit_name = messages.StringField(3, required=True)
    descimal_unit = messages.BooleanField(4, required=True)
    name_cn = messages.StringField(5, required=True)
    name_en = messages.StringField(6, required=True)
    image_url = messages.StringField(7, required=True)

class OrderDaysResponse(messages.Message):
    days = messages.IntegerField(1, required=True)

""" Report """

class ReportRequest(messages.Message):
    token = messages.StringField(1, required=True)
    customer_ids = messages.StringField(2, repeated=True)
    dates = message_types.DateTimeField(3, repeated=True)

class ReportResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    message_cn = messages.StringField(2)
    message_en = messages.StringField(3)
    items = messages.MessageField(SmsOrderItem, 4, repeated=True)

""" User """

class SmsUser(messages.Message):
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
    company_name_cn = messages.StringField(18, required=True)
    company_name_en = messages.StringField(19, required=True)

class LoginRequest(messages.Message):
    username = messages.StringField(1, required=True)
    password = messages.StringField(2, required=True)
    platform = messages.StringField(3, required=True)
    token = messages.StringField(4, required=True)

class LogoutRequest(messages.Message):
    token = messages.StringField(1, required=True)

class UserResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    message_cn = messages.StringField(2)
    message_en = messages.StringField(3)
    user = messages.MessageField(SmsUser, 4)

""" Order """

class SmsOperateItem(messages.Message):
    image_url = messages.StringField(1, required=True)
    unit_name = messages.StringField(2, required=True)
    name_cn = messages.StringField(3, required=True)
    name_en = messages.StringField(4, required=True)
    value_before = messages.FloatField(5)
    value_after = messages.FloatField(6)

class SmsTimeOperate(messages.Message):
    time_from = messages.IntegerField(1)
    time_to = messages.IntegerField(2, required=True)

class SmsOperate(messages.Message):
    operator = messages.StringField(1, required=True)
    action = messages.StringField(2, required=True)
    date = messages.IntegerField(3, required=True)
    description = messages.StringField(4)
    operate_items = messages.MessageField(SmsOperateItem, 5, repeated=True)
    operate_time = messages.MessageField(SmsTimeOperate, 6)

class SmsOrder(messages.Message):
    id = messages.StringField(1, required=True)
    customer_id = messages.StringField(2, required=True)
    change_id = messages.IntegerField(3, required=True)
    human_id = messages.StringField(4, required=True)
    state = messages.StringField(5, required=True)
    target_date = messages.IntegerField(6, required=True)
    create_date = messages.IntegerField(7, required=True)
    confirm_date = messages.IntegerField(8)
    deliver_date = messages.IntegerField(9)
    finish_date = messages.IntegerField(10)
    descriptions = messages.StringField(11, repeated=True)
    operates = messages.MessageField(SmsOperate, 12, repeated=True)
    order_items = messages.MessageField(SmsOrderItem, 13, repeated=True)

class MakeOrderRequest(messages.Message):
    token = messages.StringField(1, required=True)
    product_ids = messages.StringField(2, repeated=True)
    product_amounts = messages.FloatField(3, repeated=True)
    target_date = message_types.DateTimeField(4, required=True)
    description = messages.StringField(5)
    customer_id = messages.StringField(6)

class AmendOrderRequest(messages.Message):
    token = messages.StringField(1, required=True)
    order_id = messages.StringField(2, required=True)
    change_id = messages.IntegerField(3, required=True)
    product_ids = messages.StringField(4, repeated=True)
    product_amounts = messages.FloatField(5, repeated=True)
    target_date = message_types.DateTimeField(6, required=True)
    description = messages.StringField(7)

class ChangeOrderStateRequest(messages.Message):
    token = messages.StringField(1, required=True)
    order_id = messages.StringField(2, required=True)
    change_id = messages.IntegerField(3, required=True)
    description = messages.StringField(4)

class SearchHistoryOrderRequest(messages.Message):
    token = messages.StringField(1, required=True)
    query_ids = messages.StringField(2, repeated=True)
    customer_id = messages.StringField(3)

class RefreshHistoryOrderRequest(messages.Message):
    token = messages.StringField(1, required=True)
    start_id = messages.IntegerField(2, required=True)
    customer_id = messages.StringField(3)

class MoreHistoryOrderRequest(messages.Message):
    token = messages.StringField(1, required=True)
    end_id = messages.IntegerField(2, required=True)
    customer_id = messages.StringField(3)

class OrderResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    message_cn = messages.StringField(2)
    message_en = messages.StringField(3)
    item = messages.MessageField(SmsOrder, 4)

class OrderCollectionResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    message_cn = messages.StringField(2)
    message_en = messages.StringField(3)
    ongoing_order_humanids = messages.StringField(4, repeated=True)
    items = messages.MessageField(SmsOrder, 5, repeated=True)

class OrderHistoryResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    message_cn = messages.StringField(2)
    message_en = messages.StringField(3)
    start_id = messages.IntegerField(4)
    end_id = messages.IntegerField(5)
    items = messages.MessageField(SmsOrder, 6, repeated=True)

""" Product """

class SmsProduct(messages.Message):
    id = messages.StringField(1, required=True)
    type = messages.StringField(2, required=True)
    unit_name = messages.StringField(3, required=True)
    descimal_unit = messages.BooleanField(4, required=True)
    name_cn = messages.StringField(5, required=True)
    name_en = messages.StringField(6, required=True)
    image_url = messages.StringField(7, required=True)
    change_id = messages.IntegerField(8, required=True)
    deleted = messages.BooleanField(9, required=True)

class ProductCollectionResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    message_cn = messages.StringField(2)
    message_en = messages.StringField(3)
    items = messages.MessageField(SmsProduct, 4, repeated=True)


""" Storage """

class SmsStoreItem(messages.Message):
    id = messages.StringField(1, required=True)
    amount = messages.FloatField(2, required=True)

class SmsStorage(messages.Message):
    change_id = messages.IntegerField(1)
    items = messages.MessageField(SmsStoreItem, 2, repeated=True)

class ChangeStorageRequest(messages.Message):
    token = messages.StringField(1, required=True)
    product_id = messages.StringField(2, required=True)
    change_from = messages.FloatField(3, required=True)
    change_value = messages.FloatField(4, required=True)
    customer_id = messages.StringField(5)
    reset = messages.BooleanField(6)

class GetStorageRequest(messages.Message):
    token = messages.StringField(1, required=True)
    change_id = messages.IntegerField(2, required=True)
    customer_id = messages.StringField(3)

class StorageResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    message_cn = messages.StringField(2)
    message_en = messages.StringField(3)
    item = messages.MessageField(SmsStorage, 4)

""" Template """

class SmsOrderTemplate(messages.Message):
    name = messages.StringField(1, required=True)
    summery_cn = messages.StringField(2, required=True)
    summery_en = messages.StringField(3, required=True)
    product_ids = messages.StringField(4, repeated=True)
    product_amounts = messages.FloatField(5, repeated=True)

class DeleteTemplateRequest(messages.Message):
    token = messages.StringField(1, required=True)
    template_name = messages.StringField(2, required=True)

class ChangeTemplateRequest(messages.Message):
    token = messages.StringField(1, required=True)
    template_name = messages.StringField(2, required=True)
    product_ids = messages.StringField(3, repeated=True)
    product_amounts = messages.FloatField(4, repeated=True)

class TemplateResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    message_cn = messages.StringField(2)
    message_en = messages.StringField(3)
    item = messages.MessageField(SmsOrderTemplate, 4)

""" Customer """

class SmsCustomer(messages.Message):
    id = messages.StringField(1, required=True)
    change_id = messages.IntegerField(2, required=True)
    name_cn = messages.StringField(3, required=True)
    name_en = messages.StringField(4, required=True)
    group_id = messages.StringField(5, required=True)
    group_name_cn = messages.StringField(6, required=True)
    group_name_en = messages.StringField(7, required=True)
    templates = messages.MessageField(SmsOrderTemplate, 8, repeated=True)

class CustomerCollectionResponse(messages.Message):
    code = messages.IntegerField(1, required=True)
    message_cn = messages.StringField(2)
    message_en = messages.StringField(3)
    items = messages.MessageField(SmsCustomer, 4, repeated=True)
    customer_set = messages.StringField(5, repeated=True)

""" Helper method """

def dbUserToFrontendUser(db_user, parent_id, company_name_cn, company_name_en):
    return SmsUser(id=db_user.key.urlsafe(),
                   parent_id=parent_id,
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
                   can_edit_template=db_user.can_edit_template,
                   company_name_cn=company_name_cn,
                   company_name_en=company_name_en)

def dbProductToFrontendProduct(db_product):
    return SmsProduct(id=db_product.key.urlsafe(),
                      type=db_product.type,
                      unit_name=db_product.unit_name,
                      descimal_unit=db_product.descimal_unit,
                      name_cn=db_product.name_cn,
                      name_en=db_product.name_en,
                      image_url=db_product.image_url,
                      change_id=db_product.change_id,
                      deleted=db_product.deleted)

def dbCustomerToFrontendCustomer(db_customer_group, db_customer):
    templates = []
    if db_customer.templates:
        for db_template in db_customer.templates:
            if not db_template.product_ids or not db_template.product_amounts:
                continue
            templates.append(dbTemplateToFrontendTemplate(db_template))

    return SmsCustomer(id=db_customer.key.urlsafe(),
                       change_id = db_customer.change_id,
                       name_cn=db_customer.name_cn,
                       name_en=db_customer.name_en,
                       group_id=db_customer_group.key.urlsafe(),
                       group_name_cn=db_customer_group.name_cn,
                       group_name_en=db_customer_group.name_en,
                       templates=templates)

def dbTemplateToFrontendTemplate(db_template):
    return SmsOrderTemplate(name=db_template.name,
                            summery_cn=db_template.summery_cn,
                            summery_en=db_template.summery_en,
                            product_ids=db_template.product_ids,
                            product_amounts=db_template.product_amounts)

def dbOrderToFrontendOrder(db_order):
    operates = []
    for db_operate in db_order.operates:
        operates.append(dbOperateToFrontendOperate(db_operate))
    order_items = []
    for db_order_item in db_order.order_items:
        order_items.append(dbOrderItemToFrontendOrderItem(db_order_item))

    confirm_date = None
    deliver_date = None
    finish_date = None
    if db_order.confirm_date:
        confirm_date = calendar.timegm(db_order.confirm_date.utctimetuple()) * 1000 + (db_order.confirm_date.microsecond / 1000)
    if db_order.finish_date:
        finish_date = calendar.timegm(db_order.finish_date.utctimetuple()) * 1000 + (db_order.finish_date.microsecond / 1000)
    if db_order.deliver_date:
        deliver_date = calendar.timegm(db_order.deliver_date.utctimetuple()) * 1000 + (db_order.deliver_date.microsecond / 1000)

    return SmsOrder(id=db_order.key.urlsafe(),
                    customer_id=db_order.customer_id,
                    change_id=db_order.change_id,
                    human_id=db_order.human_id,
                    state=db_order.state,
                    target_date=calendar.timegm(db_order.target_date.utctimetuple()) * 1000 + (db_order.target_date.microsecond / 1000),
                    create_date=calendar.timegm(db_order.create_date.utctimetuple()) * 1000 + (db_order.create_date.microsecond / 1000),
                    confirm_date=confirm_date,
                    deliver_date=deliver_date,
                    finish_date=finish_date,
                    descriptions=db_order.descriptions,
                    operates=operates,
                    order_items=order_items)

def dbOperateToFrontendOperate(db_operate):
    operate_time = None
    if db_operate.operate_time:
        time_from = None
        time_to = None
        if db_operate.operate_time:
            if db_operate.operate_time.time_from:
                time_from=calendar.timegm(db_operate.operate_time.time_from.utctimetuple()) * 1000\
                                            + (db_operate.operate_time.time_from.microsecond / 1000)
            if db_operate.operate_time.time_to:
                time_to=calendar.timegm(db_operate.operate_time.time_to.utctimetuple()) * 1000\
                                            + (db_operate.operate_time.time_to.microsecond / 1000)
        operate_time = SmsTimeOperate(time_from=time_from,
                                      time_to=time_to)

    operate_items = []
    if db_operate.operate_items:
        for operate_item in db_operate.operate_items:
            operate_items.append(SmsOperateItem(image_url=operate_item.image_url,
                                                unit_name=operate_item.unit_name,
                                                name_cn=operate_item.name_cn,
                                                name_en=operate_item.name_en,
                                                value_before=operate_item.value_before,
                                                value_after=operate_item.value_after))
    return SmsOperate(operator=db_operate.operator,
                      action=db_operate.action,
                      date=calendar.timegm(db_operate.date.utctimetuple()) * 1000\
                                            + (db_operate.date.microsecond / 1000),
                      description=db_operate.description,
                      operate_items=operate_items,
                      operate_time=operate_time)

def dbOrderItemToFrontendOrderItem(db_order_item):
    return SmsOrderItem(product_id=db_order_item.product_id,
                        amount=db_order_item.amount,
                        unit_name=db_order_item.unit_name,
                        descimal_unit=db_order_item.descimal_unit,
                        name_cn=db_order_item.name_cn,
                        name_en=db_order_item.name_en,
                        image_url=db_order_item.image_url)

def dbStorageToFrontendStorage(db_storage):
    if not db_storage:
        return None

    storage_items = []
    for item in db_storage.items:
        storage_items.append(SmsStoreItem(id=item.id, amount=item.amount))
    return SmsStorage(change_id=db_storage.change_id,
                      items=storage_items)