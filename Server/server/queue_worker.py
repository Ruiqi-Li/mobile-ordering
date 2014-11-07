import webapp2
import time

from datetime import datetime, timedelta

from google.appengine.api import taskqueue
from google.appengine.ext import ndb
from gcm import GCM
from apns import APNs, Frame, Payload
import logging

from ndb_models import DBUser
from ndb_models import DBSupplier
from ndb_models import DBOrder
from ndb_models import DBStorage
from ndb_models import DBOrderOperate
from ndb_models import DBOrderOperateItem
from ndb_models import getNextChangeId

from local_static import OPERATE_ACTION_SYSTEM_AMEND
from local_static import OPERATE_ACTION_CONFIRM
from local_static import OPERATE_ACTION_RECEIVE
from local_static import HUPHENG_SUPPLIER_ID
from local_static import ORDER_STATE_PENDING
from local_static import ORDER_STATE_CONFIRMED
from local_static import ORDER_STATE_DELIVERED
from local_static import ORDER_STATE_RECEIVED
from local_static import SYSTEM_AMEND_DESCRIPTION
from local_static import SYSTEM_WARING_DESCRIPTION
from local_static import SYSTEM_RECEIVE_DESCRIPTION

SERVER_API_KEY = 'AIzaSyBawvZjxMUilii3N5v77Fi17lOQ0Ujs4Vc'

CLIENT_MSG_ACTION_NOTIFY_LOGOUT = 'NOTIFY_LOGOUT'
CLIENT_MSG_ACTION_ORDER_CHANGE = 'ORDER_CHANGE'
CLIENT_MSG_ACTION_PRODUCT_CHANGE = 'PRODUCT_CHANGE'
CLIENT_MSG_ACTION_CUSTOMER_CHANGE = 'CUSTOMER_CHANGE'
CLIENT_MSG_ACTION_USER_CONFIG_CHANGE = 'USER_CONFIG_CHANGE'
CLIENT_MSG_ACTION_CUSTOMER_STORE_CHANGE = 'CUSTOMER_STORE_CHANGE'
CLIENT_MSG_ACTION_SUPPLIER_STORE_CHANGE = 'SUPPLIER_STORE_CHANGE'

SERVER_NOTIFY_SUPPLIER_UPDATE_CUSTOMER = 'SERVER_UPDATE_CUSTOMER'

PARAMS_ACTION = 'action'
PARAMS_ID = 'id'
PARAMS_ALERT = 'alert'
PARAMS_NOTIFY_SUPPLIER = 'notify_supplier'
PARAMS_DEVICE_TYPE = 'type'
PARAMS_DEVICE_TOKEN = 'token'

def enqueueOrderAutoProcessCheck():
    taskqueue.add(url='/worker/order_process',
                  params={})

""" Send Interface """

def gcm_send(android, message):
    gcm = GCM(SERVER_API_KEY)
    gcm.json_request(registration_ids=android, data=message)

def apns_send(tokens, alert, message):
    apns = APNs(cert_file='cert.pem', key_file='key.pem')
    payload = Payload(alert=alert, custom=message)
    frame = Frame()
    identifier = 1
    expiry = time.time()+3600
    priority = 10
    [frame.add_item(token, payload, identifier, expiry, priority) for token in tokens if len(token) > 10]
    try:
        apns.gateway_server.send_notification_multiple(frame)
    except:
        pass

""" Notify """

def enqueueNotifyLogout(device_type, device_token):
    taskqueue.add(url='/worker/notification',
                  params={PARAMS_ACTION: CLIENT_MSG_ACTION_NOTIFY_LOGOUT,
                          PARAMS_DEVICE_TYPE: device_type,
                          PARAMS_DEVICE_TOKEN: device_token})

def enqueueNotifyUserConfigChange(device_type, device_token):
    taskqueue.add(url='/worker/notification',
                  params={PARAMS_ACTION: CLIENT_MSG_ACTION_USER_CONFIG_CHANGE,
                          PARAMS_DEVICE_TYPE: device_type,
                          PARAMS_DEVICE_TOKEN: device_token})

def enqueueNotifySupplierStorageChange():
    taskqueue.add(url='/worker/notification',
                  params={PARAMS_ACTION: CLIENT_MSG_ACTION_SUPPLIER_STORE_CHANGE})

def enqueueNotifyCustomerStorageChange(customer_id):
    taskqueue.add(url='/worker/notification',
                  params={PARAMS_ACTION: CLIENT_MSG_ACTION_CUSTOMER_STORE_CHANGE,
                          PARAMS_ID: customer_id})

def enqueueNotifyCustomerChange(customer_id):
    taskqueue.add(url='/worker/notification',
                  params={PARAMS_ACTION: CLIENT_MSG_ACTION_CUSTOMER_CHANGE,
                          PARAMS_ID: customer_id})

def enqueueNotifyOrderChange(alert, notify_supplier, customer_id):
    taskqueue.add(url='/worker/notification',
                  params={PARAMS_ACTION: CLIENT_MSG_ACTION_ORDER_CHANGE,
                          PARAMS_ID: customer_id,
                          PARAMS_ALERT: alert,
                          PARAMS_NOTIFY_SUPPLIER: notify_supplier})

def enqueueNotifyProductChange():
    taskqueue.add(url='/worker/notification',
                  params={PARAMS_ACTION: CLIENT_MSG_ACTION_PRODUCT_CHANGE})

def enqueueNotifySupplierUpdateCustomer():
    taskqueue.add(url='/worker/notification',
                  params={PARAMS_ACTION: SERVER_NOTIFY_SUPPLIER_UPDATE_CUSTOMER})

class OrderProcessWorker(webapp2.RequestHandler):
    def post(self):
        # oldDeliverOrderDate = datetime.now() - timedelta(days=7)
        # old_deliver_order = DBOrder.query(ndb.AND(DBOrder.state == ORDER_STATE_DELIVERED, DBOrder.deliver_date < oldDeliverOrderDate))
        # if old_deliver_order:
        #     for order in old_deliver_order:
        #         if not order.descriptions:
        #             order.descriptions = []
        #         order.descriptions.append(SYSTEM_RECEIVE_DESCRIPTION)
        #         order.operates.append(DBOrderOperate(operator='System',
        #                        action=OPERATE_ACTION_RECEIVE,
        #                        date=datetime.now(),
        #                        description=SYSTEM_RECEIVE_DESCRIPTION))
        #         order.state = ORDER_STATE_RECEIVED
        #         order.finish_date = datetime.now()
        #         order.change_id = getNextChangeId()
        #         order.put()
        #         enqueueNotifyOrderChange(order.key.parent().urlsafe())

        pending_orders = DBOrder.query(DBOrder.state == ORDER_STATE_PENDING).fetch()
        if not pending_orders:
            return
        storage = DBStorage.query(ancestor=ndb.Key(DBSupplier, HUPHENG_SUPPLIER_ID)).get()

        for pending_order in pending_orders:
            operate_items = []
            description = SYSTEM_AMEND_DESCRIPTION
            new_order_items = []
            storageChange = False

            ignore_order = False
            for order_item in pending_order.order_items:
                storage_item = findStorageByProductId(storage, order_item.product_id)
                if storage_item:
                    if storage_item.amount >= order_item.amount:
                        storageChange = True
                        storage_item.amount = round(storage_item.amount - order_item.amount, 1)
                        new_order_items.append(order_item)
                    else:
                        if hasSystemAmend(pending_order):
                            ignore_order = True
                            break
                        if storage_item.amount < 0:
                            storage_item.amount = 0
                        operate_items.append(DBOrderOperateItem(image_url=order_item.image_url,
                                                                 unit_name=order_item.unit_name,
                                                                 name_cn=order_item.name_cn,
                                                                 name_en=order_item.name_en,
                                                                 value_before=order_item.amount,
                                                                 value_after=storage_item.amount))
                        if storage_item.amount > 0:
                            storageChange = True
                            order_item.amount = storage_item.amount
                            new_order_items.append(order_item)
                        storage_item.amount = 0
                else:
                    new_order_items.append(order_item)

            if ignore_order:
                continue

            if new_order_items:
                pending_order.order_items = new_order_items
                now = datetime.now()
                notify = 'FALSE'
                if operate_items:
                    notify = 'TRUE'
                    pending_order.operates.append(DBOrderOperate(operator='System',
                                                                action=OPERATE_ACTION_SYSTEM_AMEND,
                                                                date=datetime(now.year, now.month, now.day, now.hour, now.minute, now.second, now.microsecond),
                                                                description=description,
                                                                operate_items=operate_items))
                pending_order.operates.append(DBOrderOperate(operator='System',
                                                            action=OPERATE_ACTION_CONFIRM,
                                                            date=datetime(now.year, now.month, now.day, now.hour, now.minute, now.second + 1, now.microsecond),
                                                            operate_items=[]))
                pending_order.state = ORDER_STATE_CONFIRMED
                pending_order.confirm_date = datetime.now()
                pending_order.change_id = getNextChangeId()
                pending_order.put()

                if storageChange:
                    storage.change_id = getNextChangeId()
                    storage.put()
                    enqueueNotifySupplierStorageChange()

                enqueueNotifyOrderChange('Order Confirmed: ' + pending_order.human_id, notify, pending_order.key.parent().urlsafe())
            else:
                if not findSystemAmendOperate(pending_order.operates):
                    pending_order.operates.append(DBOrderOperate(operator='System',
                                                                action=OPERATE_ACTION_SYSTEM_AMEND,
                                                                date=datetime.now(),
                                                                description=SYSTEM_WARING_DESCRIPTION))
                    pending_order.change_id = getNextChangeId()
                    pending_order.put()

                    enqueueNotifyOrderChange('NEW ORDER: ' + pending_order.human_id, 'TRUE', pending_order.key.parent().urlsafe())


def hasSystemAmend(order):
    for operate_item in order.operates:
        if operate_item.action == OPERATE_ACTION_SYSTEM_AMEND:
            return True

def findSystemAmendOperate(operates):
    for operate in operates:
        if operate.action == OPERATE_ACTION_SYSTEM_AMEND:
            return True

def findStorageByProductId(storage, product_id):
    if storage:
        for storage_item in storage.items:
            if storage_item.id == product_id:
                return storage_item

class NotificationWorker(webapp2.RequestHandler):
    def post(self): # should run at most 1/s due to entity group limit
        action = self.request.get(PARAMS_ACTION)
        android_tokens = None
        ios_tokens = None
        ios_supplier = None
        alert = ''
        ios_notify_supplier = False

        if action == CLIENT_MSG_ACTION_NOTIFY_LOGOUT or action == CLIENT_MSG_ACTION_USER_CONFIG_CHANGE:
            message = {'action': action}
            device_type = self.request.get(PARAMS_DEVICE_TYPE)
            token = self.request.get(PARAMS_DEVICE_TOKEN)
            if device_type == 'ANDROID':
                android_tokens = [token]
            elif device_type == 'IOS':
                ios_tokens = [token]
        elif action == CLIENT_MSG_ACTION_SUPPLIER_STORE_CHANGE:
            message = {'action': CLIENT_MSG_ACTION_SUPPLIER_STORE_CHANGE}
            supplier_users = DBUser.query(ancestor=ndb.Key(DBSupplier, HUPHENG_SUPPLIER_ID))\
                                .fetch(projection=[DBUser.device_type, DBUser.device_token])
            android_tokens = [user.device_token for user in supplier_users if user.device_type == 'ANDROID' and user.device_token]
            ios_tokens = [user.device_token for user in supplier_users if user.device_type == 'IOS' and user.device_token]
        elif action == CLIENT_MSG_ACTION_CUSTOMER_STORE_CHANGE or action == CLIENT_MSG_ACTION_CUSTOMER_CHANGE or \
                action == CLIENT_MSG_ACTION_ORDER_CHANGE:
            customer_id = self.request.get(PARAMS_ID)
            if action == CLIENT_MSG_ACTION_CUSTOMER_CHANGE:
                message = {'action': CLIENT_MSG_ACTION_CUSTOMER_CHANGE,
                           'customer_id': customer_id}
            elif action == CLIENT_MSG_ACTION_CUSTOMER_STORE_CHANGE:
                message = {'action': CLIENT_MSG_ACTION_CUSTOMER_STORE_CHANGE,
                           'customer_id': customer_id}
            else:
                ios_notify_supplier = (self.request.get(PARAMS_NOTIFY_SUPPLIER) == 'TRUE')
                alert = self.request.get(PARAMS_ALERT)
                message = {'action': CLIENT_MSG_ACTION_ORDER_CHANGE,
                           'customer_id': customer_id}

            try:
                customer_key = ndb.Key(urlsafe=customer_id)
            except Exception, e:
                return

            customer_users = DBUser.query(ancestor=customer_key).fetch(projection=[DBUser.device_type, DBUser.device_token])
            android_tokens = [user.device_token for user in customer_users if user.device_type == 'ANDROID' and user.device_token]
            ios_tokens = [user.device_token for user in customer_users if user.device_type == 'IOS' and user.device_token]

            supplier_users = DBUser.query(ancestor=ndb.Key(DBSupplier, HUPHENG_SUPPLIER_ID)).fetch(projection=[DBUser.device_type, DBUser.device_token])
            android_tokens = android_tokens + [user.device_token for user in supplier_users if user.device_type == 'ANDROID' and user.device_token]
            ios_supplier = [user.device_token for user in supplier_users if user.device_type == 'IOS' and user.device_token]

            monitor = DBUser.query(ancestor=customer_key.parent()).get()
            if monitor and monitor.device_type and monitor.device_token:
                if monitor.device_type == 'ANDROID':
                    android_tokens.append(monitor.device_token)
                elif monitor.device_type == 'IOS':
                    ios_tokens.append(monitor.device_token)
        elif action == CLIENT_MSG_ACTION_PRODUCT_CHANGE:
            message = {'action': action}
            users = DBUser.query().fetch(projection=[DBUser.device_token, DBUser.device_type])
            android_tokens = [user.device_token for user in users if user.device_type == 'ANDROID' and user.device_token]
            ios_tokens = [user.device_token for user in users if user.device_type == 'IOS' and user.device_token]
        elif action == SERVER_NOTIFY_SUPPLIER_UPDATE_CUSTOMER:
            message = {'action': CLIENT_MSG_ACTION_CUSTOMER_CHANGE}
            supplier_users = DBUser.query(ancestor=ndb.Key(DBSupplier, HUPHENG_SUPPLIER_ID)).fetch(projection=[DBUser.device_type, DBUser.device_token])
            android_tokens = [user.device_token for user in supplier_users if user.device_type == 'ANDROID' and user.device_token]
            ios_tokens = [user.device_token for user in supplier_users if user.device_type == 'IOS' and user.device_token]

        if android_tokens:
            gcm_send(android_tokens, message)
        logging.info('---IOS START----------------------------------------------')
        if ios_tokens:
            logging.info('SEND MESSAGE: ' + str(message))
            logging.info('SEND ALERT: ' + str(alert))
            logging.info('SEND TO: ' + str(ios_tokens))
            apns_send(ios_tokens, alert, message)
        logging.info('~~~~~~~~')
        if ios_supplier:
            logging.info('SEND MESSAGE: ' + str(message))
            if ios_notify_supplier:
                logging.info('SEND ALERT: ' + str(alert))
                apns_send(ios_supplier, alert, message)
            else:
                logging.info('SEND ALERT: ')
                apns_send(ios_supplier, '', message)
            logging.info('SEND TO: ' + str(ios_supplier))


APP = webapp2.WSGIApplication(
    [
        ('/worker/notification', NotificationWorker),
        ('/worker/order_process', OrderProcessWorker)
    ], debug=True)