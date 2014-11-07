#!/usr/bin/python
# -*- coding: utf-8 -*-

from ndb_models import DBSystem
from ndb_models import DBOrderOperate

from datetime import datetime

WEB_CLIENT_ID = '595277279942-46l9vco02l05nti5lohpqqqvv5dfb1tq.apps.googleusercontent.com'
ANDROID_CLIENT_ID = '595277279942-j6aeo087lq0fo3ttfofack3gt2hcql4j.apps.googleusercontent.com'
IOS_CLIENT_ID = '884928042849-5976bk568145mgvvfff0lecsl9b565qe.apps.googleusercontent.com'
ANDROID_AUDIENCE = WEB_CLIENT_ID

SUCCESS=200

ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED=400
ERROR_USER_DO_NOT_HAS_AUTHORITY= 401
ERROR_ORDER_ALREADY_SHIPED_CANNOT_CHANGE=402
ERROR_ORDER_ALREADY_SHIPED_CANNOT_CANCEL=403
ERROR_ORDER_ALREADY_IN_REQUIRED_STATE=404
ERROR_ORDER_NO_MORE_NEW_ORDER_TODAY=405
ERROR_ORDER_EDIT_TIME_EXPIRED=406
ERROR_ORDER_CANCEL_TIME_EXPIRED=407
ERROR_SIMULTANEOUSLY_AMEND=408
ERROR_LOGIN_KICK_OFF=409
ERROR_ORDER_MAKE_TIME_EXPIRED=410

ERROR_INVIALID_PARAMETER=300
ERROR_ORDER_REQUIRED_STATE_NOT_CORRECT=301
ERROR_ORDER_DATA_ALREADY_CHANGED=303
ERROR_DATA_NOT_FOUND=304

ERROR_SERVER_ERROR=500
ERROR_SERVER_NOT_READY=501

USER_ROLE_MONITOR = 'Monitor'
USER_ROLE_CUSTOMER = 'Customer'
USER_ROLE_SUPPLIER = 'Supplier'

ORDER_STATE_PENDING = 'Pending'
ORDER_STATE_CONFIRMED = 'Confirmed'
ORDER_STATE_DELIVERED = 'Delivered'
ORDER_STATE_RECEIVED = 'Received'
ORDER_STATE_CANCELED = 'Canceled'

OPERATE_ACTION_CONFIRM = "CONFIRM";
OPERATE_ACTION_DELIVER = "DELIVER";
OPERATE_ACTION_RECEIVE = "RECEIVE";
OPERATE_ACTION_CUSTOMER_AMEND = "CUSTOMER_AMEND";
OPERATE_ACTION_SUPPLIER_AMEND = "SUPPLIER_AMEND";
OPERATE_ACTION_SYSTEM_AMEND = "SYSTEM_AMEND";
OPERATE_ACTION_CUSTOMER_NEW = "CUSTOMER_NEW";
OPERATE_ACTION_SUPPLIER_NEW = "SUPPLIER_NEW";
OPERATE_ACTION_CUSTOMER_CANCEL = "CUSTOMER_CANCEL";
OPERATE_ACTION_SUPPLIER_CANCEL = "SUPPLIER_CANCEL";

HUPHENG_SUPPLIER_ID = 'HUPHENG'

SYSTEM_AMEND_DESCRIPTION = u'产品缺货，系统已做自调整，如有疑问，请联系供货商'
SYSTEM_WARING_DESCRIPTION = u'产品缺货，订单正在等待供货商手动处理，如有疑问，请联系供货商'
SYSTEM_RECEIVE_DESCRIPTION = u'产品发货超过1周未收货， 系统自动修改至收货，如有疑问，请联系供货商'
SYSTEM_BACK_TO_PENDING_DESCRIPTION = u'修改商品缺货，订单回退至‘待确认’，等待供货商手动确认，如有疑问，请联系供货商'

BACKEND_MESSAGE_DUPLICATE_USER = u'登录名不可使用，请更换登录名'
BACKEND_MESSAGE_PASSWORD_NOT_CORRECT = u'用户名或密码不正确'
BACKEND_MESSAGE_SUCCESS = u'操作成功'
BACKEND_MESSAGE_FAILED = u'操作失败'

def codeToMessage(code):
    if code == ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED:
        return 'The username or password you entered is incorrect', u'用户名或密码不正确'
    elif code == ERROR_USER_DO_NOT_HAS_AUTHORITY:
        return 'You don\'t have permission', u'没有权限'
    elif code == ERROR_ORDER_ALREADY_SHIPED_CANNOT_CHANGE:
        return 'Order has been shipped, can not make changes.',\
               u'供货商已发货，不可更改'
    elif code == ERROR_ORDER_ALREADY_SHIPED_CANNOT_CANCEL:
        return 'Order has  been shipped, can not cancel.',\
               u'供货商已发货，不可取消'
    elif code == ERROR_ORDER_EDIT_TIME_EXPIRED:
        return 'Order has been prepared, can not make changes',\
               u'供货商已备货，不可更改'
    elif code == ERROR_ORDER_CANCEL_TIME_EXPIRED:
        return 'Order has been prepared, can not cancel',\
               u'供货商已备货，不可取消'
    elif code == ERROR_SIMULTANEOUSLY_AMEND:
        return 'Processing another modification. Please try again later',\
               u'另一个修改操作正在进行中，请稍后再试'
    elif code == ERROR_LOGIN_KICK_OFF:
        return 'Account has been logged in by other device. You have been log out',\
               u'您的帐户已在另一台设备登陆，您被迫下线'
    elif code == ERROR_ORDER_REQUIRED_STATE_NOT_CORRECT:
        return 'Operation failed. Order\'s state has changed.',\
               u'修改失败，订单状态已改变'
    elif code == ERROR_ORDER_MAKE_TIME_EXPIRED:
        return 'Deadline exceeded. Cannot make order for the date. Please select other date or contact supplier.',\
                u'要求到货时间的下单时间已过，请预定其他到货时间，或联系供货商'

def findStorageItem(storage, item_id):
    if storage.items:
        for i in range(len(storage.items)):
            if storage.items[i].id == item_id:
                return storage.items[i]

def findTemplateByName(customer, find_anme):
    if customer.templates:
        for template in customer.templates:
            if template.name == find_anme:
                return template

def orderAddSimpleAction(order, user, action, description):
    if description:
        if not order.descriptions:
            order.descriptions = []
        order.descriptions.append(description)

    order.operates.append(DBOrderOperate(operator=user.display_name,
                               action=action,
                               date=datetime.now(),
                               description=description))