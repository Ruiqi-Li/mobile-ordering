from google.appengine.ext import ndb
from google.appengine.api import memcache
from datetime import date

class DBSystem(ndb.Model):
    """System basic object """
    customer_codes = ndb.StringProperty(repeated=True, indexed=False)
    change_id = ndb.IntegerProperty(indexed=False)
    username = ndb.StringProperty(indexed=False)
    password = ndb.StringProperty(indexed=False)
    report_email = ndb.StringProperty(indexed=False)
    order_range = ndb.IntegerProperty(indexed=False)
    make_quote = ndb.IntegerProperty(indexed=False)
    editQuote = ndb.IntegerProperty(indexed=False)
    cancelQuote = ndb.IntegerProperty(indexed=False)

    @classmethod
    def get_config(cls):
        return cls.query().get()

    @classmethod
    def get_next_customercode(cls):
        system_config = cls.query().get()
        return system_config.customer_codes.pop(0)

class DBOrderOperateItem(ndb.Model):
    """Order operate item: a single operate item """
    image_url = ndb.StringProperty(required=True)
    unit_name = ndb.StringProperty(required=True)
    name_cn = ndb.StringProperty(required=True)
    name_en = ndb.StringProperty(required=True)
    value_before = ndb.FloatProperty(required=True)
    value_after = ndb.FloatProperty(required=True)

class DBOrderTimeOperate(ndb.Model):
    time_from = ndb.DateTimeProperty()
    time_to = ndb.DateTimeProperty(required=True)

class DBOrderOperate(ndb.Model):
    """Order operate: can include a list of operate items """
    operator = ndb.StringProperty(required=True)
    action = ndb.StringProperty(required=True)
    date = ndb.DateTimeProperty(auto_now_add=True)
    description = ndb.StringProperty()
    operate_items = ndb.LocalStructuredProperty(DBOrderOperateItem, repeated=True)
    operate_time = ndb.LocalStructuredProperty(DBOrderTimeOperate)

class DBOrderItem(ndb.Model):
    """Order item: use both in order and order template"""
    product_id = ndb.StringProperty(required=True)
    amount = ndb.FloatProperty(required=True)
    unit_name = ndb.StringProperty(required=True)
    descimal_unit = ndb.BooleanProperty(required=True)
    name_cn = ndb.StringProperty(required=True)
    name_en = ndb.StringProperty(required=True)
    image_url = ndb.StringProperty(required=True)

class DBOrder(ndb.Model):
    """Order"""
    customer_id = ndb.StringProperty(required=True)
    change_id = ndb.IntegerProperty(required=True)
    human_id = ndb.StringProperty(required=True)
    state = ndb.StringProperty(required=True)
    target_date = ndb.DateTimeProperty(required=True)
    create_date = ndb.DateTimeProperty(auto_now_add=True, indexed=False)
    confirm_date = ndb.DateTimeProperty(indexed=False)
    deliver_date = ndb.DateTimeProperty()
    finish_date = ndb.DateTimeProperty(indexed=False)
    descriptions = ndb.StringProperty(repeated=True, indexed=False)
    operates = ndb.LocalStructuredProperty(DBOrderOperate, repeated=True)
    order_items = ndb.LocalStructuredProperty(DBOrderItem, repeated=True)

class DBCustomerGroup(ndb.Model):
    deleted = ndb.BooleanProperty(required=True)
    name_cn = ndb.StringProperty(required=True, indexed=False)
    name_en = ndb.StringProperty(required=True, indexed=False)

    @classmethod
    def _pre_delete_hook(cls, key):
        ndb.delete_multi(
            DBCustomer.query(ancestor=key).fetch(keys_only=True)
        )
        ndb.delete_multi(
            DBUser.query(ancestor=key).fetch(keys_only=True)
        )

class DBTemplate(ndb.Model):
    """Order template"""
    name = ndb.StringProperty(required=True)
    summery_cn = ndb.StringProperty(required=True)
    summery_en = ndb.StringProperty(required=True)
    product_ids = ndb.StringProperty(repeated=True)
    product_amounts = ndb.FloatProperty(repeated=True)

class DBCustomer(ndb.Model):
    """Custoemr"""
    deleted = ndb.BooleanProperty(required=True)
    change_id = ndb.IntegerProperty(required=True)
    hupheng_id = ndb.StringProperty(required=True, indexed=False)
    customer_code = ndb.StringProperty(required=True)
    name_cn = ndb.StringProperty(required=True, indexed=False)
    name_en = ndb.StringProperty(required=True, indexed=False)
    latest_order_date = ndb.DateProperty(indexed=False)
    daily_order_count = ndb.IntegerProperty(indexed=False)
    templates = ndb.LocalStructuredProperty(DBTemplate, repeated=True)

    @classmethod
    def _pre_delete_hook(cls, key):
        storage = DBStorage.query(ancestor=key).get(keys_only=True)
        if storage:
            storage.delete()
        ndb.delete_multi(
            DBOrder.query(ancestor=key).fetch(keys_only=True)
        )
        ndb.delete_multi(
            DBUser.query(ancestor=key).fetch(keys_only=True)
        )

class DBSupplier(ndb.Model):
    """ Spuulier """
    name_en = ndb.StringProperty(required=True, indexed=False)
    name_cn = ndb.StringProperty(required=True, indexed=False)

    @classmethod
    def _pre_delete_hook(cls, key):
        storage = DBStorage.query(ancestor=key).get(keys_only=True)
        if storage:
            storage.delete()
        ndb.delete_multi(
            DBUser.query(ancestor=key).fetch(keys_only=True)
        )

class DBStoreItem(ndb.Model):
    """Storage item, used in customer and supplier"""
    id = ndb.StringProperty(required=True)
    amount = ndb.FloatProperty(required=True)

class DBStorage(ndb.Model):
    change_id = ndb.IntegerProperty(required=True)
    items = ndb.LocalStructuredProperty(DBStoreItem, repeated=True)

class DBUser(ndb.Model):
    """User: use by custoemr , supplier and customer group manager"""
    display_name = ndb.StringProperty(required=True, indexed=False)
    login_name = ndb.StringProperty(required=True)
    password = ndb.StringProperty(required=True)
    role = ndb.StringProperty(required=True)
    device_type = ndb.StringProperty()
    device_token = ndb.StringProperty()
    is_manager = ndb.BooleanProperty(required=True, indexed=False, default=False)
    can_create_order = ndb.BooleanProperty(required=True, indexed=False, default=False)
    can_edit_order = ndb.BooleanProperty(required=True, indexed=False, default=False)
    can_cancel_order = ndb.BooleanProperty(required=True, indexed=False, default=False)
    can_confirm_order = ndb.BooleanProperty(required=True, indexed=False, default=False)
    can_deliver_order = ndb.BooleanProperty(required=True, indexed=False, default=False)
    can_receive_order = ndb.BooleanProperty(required=True, indexed=False, default=False)
    can_change_storage = ndb.BooleanProperty(required=True, indexed=False, default=False)
    can_view_history = ndb.BooleanProperty(required=True, indexed=False, default=False)
    can_view_statistic = ndb.BooleanProperty(required=True, indexed=False, default=False)
    can_edit_template = ndb.BooleanProperty(required=True, indexed=False, default=False)

class DBProduct(ndb.Model):
    """Product"""
    change_id = ndb.IntegerProperty(required=True)
    hupheng_id = ndb.StringProperty(required=True, indexed=False)
    type = ndb.StringProperty(required=True, indexed=False)
    unit_name = ndb.StringProperty(required=True, indexed=False)
    descimal_unit = ndb.BooleanProperty(required=True, indexed=False)
    name_en = ndb.StringProperty(required=True)
    name_cn = ndb.StringProperty(required=True, indexed=False)
    image_url = ndb.StringProperty(required=True, indexed=False)
    deleted = ndb.BooleanProperty(required=True)

""" Helper methods """

@ndb.transactional
def getNextHumanId(key):
    customer = key.get()
    today = date.today()
    if customer.latest_order_date and customer.latest_order_date == today:
        customer.daily_order_count+=1
    else:
        customer.latest_order_date = today
        customer.daily_order_count = 1

    month = customer.latest_order_date.month
    if month < 10:
        month = '0' + str(month)
    else:
        month = str(month)

    day = customer.latest_order_date.day
    if day < 10:
        day = '0' + str(day)
    else:
        day = str(day)

    index = customer.daily_order_count
    if index < 10:
        index = '00' + str(index)
    elif index < 100:
        index = '0' + str(index)
    else:
        index = str(index)

    customer.put()

    return customer.customer_code + str(customer.latest_order_date.year) + month + day + index

def findCustomerGroupByKey(customer_groups, key):
    for customer_group in customer_groups:
        if customer_group and customer_group.key == key:
            return customer_group

def findProductById(products, id):
    for product in products:
        if product.key.urlsafe() == id:
            return product

def findOrderItemByProductId(order_items, product_id):
    for order_item in order_items:
        if order_item.product_id == product_id:
            return order_item

def getOperateAndOrderItemsNew(products, ids, amounts):
    operate_items = []
    order_items = []
    for idx, id in enumerate(ids):
        product = findProductById(products, id)
        if not product:
            continue
        operate_items.append(DBOrderOperateItem(image_url=product.image_url,
                                                 unit_name=product.unit_name,
                                                 name_cn=product.name_cn,
                                                 name_en=product.name_en,
                                                 value_before=0,
                                                 value_after=amounts[idx]))
        order_items.append(DBOrderItem(product_id=id,
                                       amount=amounts[idx],
                                       unit_name=product.unit_name,
                                       descimal_unit=product.descimal_unit,
                                       name_cn=product.name_cn,
                                       name_en=product.name_en,
                                       image_url=product.image_url))
    return operate_items, order_items

def getNextChangeId():
    client = memcache.Client()
    retry = 0
    while retry < 10: # Retry loop
        retry +=1
        changeId = client.gets('CHANGE_ID')
        if changeId is None:
            break;
        changeId +=1
        if client.cas('CHANGE_ID', changeId):
            system_config = DBSystem.query().get()
            system_config.change_id = changeId;
            system_config.put()
            return changeId

    system_config = DBSystem.query().get()
    new_change_id = system_config.change_id + 1
    memcache.set(key="CHANGE_ID", value=new_change_id)
    return new_change_id