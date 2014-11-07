import 'dart:html';
import 'package:polymer/polymer.dart';
import 'package:paper_elements/paper_dialog.dart';
import 'package:paper_elements/paper_toast.dart';
import 'package:google_backend_v1_api/backend_v1_api_browser.dart';
import 'package:google_backend_v1_api/backend_v1_api_client.dart';

@CustomTag('customer-subpage')
class CustomerSubpage extends PolymerElement with ChangeNotifier  {
  List<BackendRpcBackendCustomerGroupResponse> customerGroups = toObservable([]);
  
  @reflectable @observable String get displayName => __$displayName; String __$displayName; @reflectable set displayName(String value) { __$displayName = notifyPropertyChange(#displayName, __$displayName, value); }
  @reflectable @observable String get loginName => __$loginName; String __$loginName; @reflectable set loginName(String value) { __$loginName = notifyPropertyChange(#loginName, __$loginName, value); }
  @reflectable @observable String get password => __$password; String __$password; @reflectable set password(String value) { __$password = notifyPropertyChange(#password, __$password, value); }
  @reflectable @observable bool get creareOrder => __$creareOrder; bool __$creareOrder; @reflectable set creareOrder(bool value) { __$creareOrder = notifyPropertyChange(#creareOrder, __$creareOrder, value); }
  @reflectable @observable bool get editOrder => __$editOrder; bool __$editOrder; @reflectable set editOrder(bool value) { __$editOrder = notifyPropertyChange(#editOrder, __$editOrder, value); }
  @reflectable @observable bool get cancelOrder => __$cancelOrder; bool __$cancelOrder; @reflectable set cancelOrder(bool value) { __$cancelOrder = notifyPropertyChange(#cancelOrder, __$cancelOrder, value); }
  @reflectable @observable bool get receiveOrder => __$receiveOrder; bool __$receiveOrder; @reflectable set receiveOrder(bool value) { __$receiveOrder = notifyPropertyChange(#receiveOrder, __$receiveOrder, value); }
  @reflectable @observable bool get changeStorage => __$changeStorage; bool __$changeStorage; @reflectable set changeStorage(bool value) { __$changeStorage = notifyPropertyChange(#changeStorage, __$changeStorage, value); }
  @reflectable @observable bool get editTemplate => __$editTemplate; bool __$editTemplate; @reflectable set editTemplate(bool value) { __$editTemplate = notifyPropertyChange(#editTemplate, __$editTemplate, value); }
  
  @reflectable @observable String get customerNameCn => __$customerNameCn; String __$customerNameCn; @reflectable set customerNameCn(String value) { __$customerNameCn = notifyPropertyChange(#customerNameCn, __$customerNameCn, value); }
  @reflectable @observable String get customerNameEn => __$customerNameEn; String __$customerNameEn; @reflectable set customerNameEn(String value) { __$customerNameEn = notifyPropertyChange(#customerNameEn, __$customerNameEn, value); }
  @reflectable @observable String get customerHuphendId => __$customerHuphendId; String __$customerHuphendId; @reflectable set customerHuphendId(String value) { __$customerHuphendId = notifyPropertyChange(#customerHuphendId, __$customerHuphendId, value); }
  @reflectable @observable String get directorDispalyName => __$directorDispalyName; String __$directorDispalyName; @reflectable set directorDispalyName(String value) { __$directorDispalyName = notifyPropertyChange(#directorDispalyName, __$directorDispalyName, value); }
  @reflectable @observable String get directorLoginName => __$directorLoginName; String __$directorLoginName; @reflectable set directorLoginName(String value) { __$directorLoginName = notifyPropertyChange(#directorLoginName, __$directorLoginName, value); }
  @reflectable @observable String get directorPassword => __$directorPassword; String __$directorPassword; @reflectable set directorPassword(String value) { __$directorPassword = notifyPropertyChange(#directorPassword, __$directorPassword, value); }
  
  @reflectable @observable int get panelSelected => __$panelSelected; int __$panelSelected; @reflectable set panelSelected(int value) { __$panelSelected = notifyPropertyChange(#panelSelected, __$panelSelected, value); }
  
  @reflectable @observable bool get actioning => __$actioning; bool __$actioning = false; @reflectable set actioning(bool value) { __$actioning = notifyPropertyChange(#actioning, __$actioning, value); }
  
  final String SELECT_MODE_GROUP = 'GROUP';
  final String SELECT_MODE_CUSTOMER = 'CUSTOMER';
  final String SELECT_MODE_USER = 'USER';
  
  String currentMode;
  BackendRpcBackendCustomerGroupResponse selectGroup;
  BackendRpcBackendCustomerResponse selectCustomer;
  BackendRpcBackendUser selectUser;
  
  Backend backend;
  PaperDialog deleteDialog;
  PaperToast paperToast;
  Element deleteBtn;
  Element deleteMessage;
  Element directorPanel;
  Element huphengIdElement;
  
  Element groupMenu;
  
  CustomerSubpage.created() : super.created() {
    backend = new Backend();
    backend.rootUrl = 'https://ruiqi-test.appspot.com/';
  }
  
  @override
  void attached() {
    super.attached();
    
    deleteBtn = $['delete-btn'];
    deleteDialog = $['delete-dialog'];
    deleteMessage = $['dialog-message'];
    directorPanel = $['director-panel'];
    paperToast = $['toast-view'];
    huphengIdElement = $['hupheng-id'];
    
    panelSelected = 0;
    currentMode = SELECT_MODE_GROUP;
    
    syncCustomer();
  }
  
  @override
  void detached() {
    super.detached();
  }
  
  void onSelectCustomerGroup(Event event, var detail, Node target) {
    if (actioning) {
      return;
    }
    
    bool isselect = detail['isSelected'];
    if (isselect) {
      Element selectItem = detail['item'];
      int groupIndex = int.parse(selectItem.attributes['data-group']);
      if (groupIndex >= 0) {
        selectGroup =  customerGroups[groupIndex];
        customerNameCn = selectGroup.name_cn;
        customerNameEn = selectGroup.name_en;
        if (selectGroup.director != null) {
          directorDispalyName = selectGroup.director.display_name;
          directorLoginName = selectGroup.director.login_name;
          directorPassword = selectGroup.director.password;
        }
        deleteBtn.hidden = false;
      } else {
        resetEmptyGroup();
      }

      currentMode = SELECT_MODE_GROUP;
      selectCustomer = null;
      selectUser = null;
      panelSelected = 0;
      huphengIdElement.hidden = true;
      directorPanel.hidden = false;
    }
  }
  
  void resetEmptyGroup() {
    selectGroup = null;
    customerNameCn = '';
    customerNameEn = '';
    directorDispalyName = '';
    directorLoginName = '';
    directorPassword = '';
    deleteBtn.hidden = true;
  }
  
  void onSelectCustomer(Event event, var detail, Node target) {
    if (actioning) {
      return;
    }
    
    bool isselect = detail['isSelected'];
    if (isselect) {
      Element selectItem = detail['item'];
      int customerIndex = int.parse(selectItem.attributes['data-customer']);
      if (customerIndex >= 0) {
        selectCustomer = selectGroup.customers[customerIndex];
        customerNameCn = selectCustomer.name_cn;
        customerNameEn = selectCustomer.name_en;
        customerHuphendId = selectCustomer.hupheng_id;
        deleteBtn.hidden = false;
      } else {
        resetEmptyCustomer();
      }
      
      currentMode = SELECT_MODE_CUSTOMER;
      selectUser = null;
      directorDispalyName = '';
      directorLoginName = '';
      directorPassword = '';
      panelSelected = 0;
      huphengIdElement.hidden = false;
      directorPanel.hidden = true;
    }
  }
  
  void resetEmptyCustomer() {
    selectCustomer = null;
    customerNameCn = '';
    customerNameEn = '';
    customerHuphendId = '';
    deleteBtn.hidden = true;
  }
  
  void onSelectUser(Event event, var detail, Node target) {
    if (actioning) {
      return;
    }
    
    bool isselect = detail['isSelected'];
    if (isselect) {
      Element selectItem = detail['item'];
      int userIndex = int.parse(selectItem.attributes['data-user']);
      if (userIndex >= 0) {
        selectUser = selectCustomer.users[userIndex];
        displayName = selectUser.display_name;
        loginName = selectUser.login_name;
        password = selectUser.password;
        creareOrder = selectUser.can_create_order;
        editOrder = selectUser.can_edit_order;
        cancelOrder = selectUser.can_cancel_order;
        receiveOrder = selectUser.can_receive_order;
        changeStorage = selectUser.can_change_storage;
        editTemplate = selectUser.can_edit_template;
        deleteBtn.hidden = false;
      } else {
        resetEmptyUser();
      }
      
      currentMode = SELECT_MODE_USER;
      panelSelected = 1;
    }
  }
  
  void resetEmptyUser() {
    selectUser = null;
    displayName = '';
    loginName = '';
    password = '';
    deleteBtn.hidden = true;
    
    creareOrder = true;
    editOrder = true;
    cancelOrder = true;
    receiveOrder = true;
    changeStorage = true;
    editTemplate = true;
  }
  
  void syncCustomer() {
    backend.listCustomer().then((BackendRpcCustomerGroupResponseCollection value) {
      for (BackendRpcBackendCustomerGroupResponse group in value.items) {
        group.customers = toObservable(group.customers);
        if (group.customers != null) {
          for (BackendRpcBackendCustomerResponse customer in group.customers) {
            customer.users = toObservable(customer.users);
          }
        }
      }
      customerGroups.addAll(value.items);
    });
  }
  
  void onDeleteClicked(Event event, var detail, Node target) {
    String deleteName = null;
    if (selectUser != null) {
      deleteName = selectUser.display_name;
    } else if (selectCustomer != null) {
      deleteName = selectCustomer.name_cn;
    } else if (selectGroup != null) {
      deleteName = selectGroup.name_cn;
    }
    
    if (deleteName != null) {
      deleteMessage.text = '确认删除' + deleteName;
      deleteDialog.toggle();
    }
  }
  
  void onDeleteConfirm(Event event, var detail, Node target) {
    var dataMap = new Map();
    if (selectUser != null) {
      dataMap['id'] = selectUser.id;
    } else if (selectCustomer != null) {
      dataMap['id'] = selectCustomer.id;
    } else if (selectGroup != null) {
      dataMap['id'] = selectGroup.id;
    }
    
    actioning = true;
    paperToast.text = '正在删除..';
    paperToast.show();
    
    BackendRpcDeleteRequest request = new BackendRpcDeleteRequest.fromJson(dataMap);
    backend.deleteItem(request).then((BackendRpcSimpleResponse value) {
      actioning = false;
      paperToast.text = value.message;
      paperToast.show();
              
      if (value.code == 200) {
        if (selectUser != null) {
          selectCustomer.users.remove(selectUser);
          resetEmptyUser();
        } else if (selectCustomer != null) {
          selectGroup.customers.remove(selectCustomer);
          resetEmptyCustomer();
          selectUser = null;
        } else if (selectGroup != null) {
          customerGroups.remove(selectGroup);
          resetEmptyGroup();
          selectCustomer = null;
          selectUser = null;
        }
      }
    }).catchError((exception) {
      actioning = false;
      paperToast.text = '删除失败';
      paperToast.show();
    });
  }
  
  void onSubmitClicked(Event event, var detail, Node target) {
    if (currentMode == SELECT_MODE_GROUP) {
      if (customerNameCn == null || customerNameCn.isEmpty) {
        paperToast.text = '客户中文名不可为空';
        paperToast.show();
        return;
      }
      if (customerNameEn == null || customerNameEn.isEmpty) {
        paperToast.text = '客户英文名不可为空';
        paperToast.show();
        return;
      }
      if (directorDispalyName == null || directorDispalyName.isEmpty) {
        paperToast.text = '经理昵称不可为空';
        paperToast.show();
        return;
      }
      if (directorLoginName == null || directorLoginName.isEmpty) {
        paperToast.text = '经理用户名不可为空';
        paperToast.show();
        return;
      }
      if (directorPassword == null || directorPassword.isEmpty) {
        paperToast.text = '经理密码不可为空';
        paperToast.show();
        return;
      }
      
      var mapData = new Map();
      mapData['name_cn'] = customerNameCn;
      mapData['name_en'] = customerNameEn;
      mapData['director_display_name'] = directorDispalyName;
      mapData['director_login_name'] = directorLoginName;
      mapData['director_password'] = directorPassword;
      if (selectGroup != null) {
        mapData['director_id'] = selectGroup.director.id;
        mapData['id'] = selectGroup.id;
      }
      
      actioning = true;
      paperToast.text = '正在提交..';
      paperToast.show();
      
      BackendRpcBackendCustomerGroupRequest request = new BackendRpcBackendCustomerGroupRequest.fromJson(mapData);
      backend.changeCustomerGroup(request).then((BackendRpcSingleBackendCustomerGroupResponse value) {
        actioning = false;
        if (value.code == 200) {
          if (selectGroup == null) {
            value.item.customers = toObservable([]);
            customerGroups.add(value.item);
          } else {
            value.item.customers = selectGroup.customers;
            int index = customerGroups.indexOf(selectGroup);
            customerGroups[index] = value.item;
            selectGroup = value.item;
          }
          paperToast.text = '修改成功';
          paperToast.show();
        } else {
          paperToast.text = value.message;
          paperToast.show();
        }
      }).catchError((value){
        actioning = false;
        paperToast.text = '修改失败';
        paperToast.show();
      });
    } else if (currentMode == SELECT_MODE_CUSTOMER) {
      if (customerHuphendId == null || customerHuphendId.isEmpty) {
        paperToast.text = '客户ID不可为空';
        paperToast.show();
        return;
      }
      if (customerNameCn == null || customerNameCn.isEmpty) {
        paperToast.text = '客户中文名不可为空';
        paperToast.show();
        return;
      }
      if (customerNameEn == null || customerNameEn.isEmpty) {
        paperToast.text = '客户英文名不可为空';
        paperToast.show();
        return;
      }
      
      var mapData = new Map();
      mapData['hupheng_id'] = customerHuphendId;
      mapData['name_cn'] = customerNameCn;
      mapData['name_en'] = customerNameEn;
      mapData['parent_id'] = selectGroup.id;
      if (selectCustomer != null) {
        mapData['id'] = selectCustomer.id;
      }
      
      actioning = true;
      paperToast.text = '正在提交..';
      paperToast.show();
      
      BackendRpcBackendCustomerRequest request = new BackendRpcBackendCustomerRequest.fromJson(mapData);
      backend.changeCustomer(request).then((BackendRpcSingleBackendCustomerResponse value) {
        actioning = false;
        if (value.code == 200) {
          if (selectCustomer == null) {
            value.item.users = toObservable([]);
            selectGroup.customers.add(value.item);
          } else {
            value.item.users = selectCustomer.users;
            int index = selectGroup.customers.indexOf(selectCustomer);
            selectGroup.customers[index] = value.item;
            selectCustomer = value.item;
          }
          paperToast.text = '修改成功';
          paperToast.show();
        } else {
          paperToast.text = value.message;
          paperToast.show();
        }
      }).catchError((value) {
        actioning = false;
        paperToast.text = '修改失败';
        paperToast.show();
      });
    } else if (currentMode == SELECT_MODE_USER) {
      if (displayName == null || displayName.isEmpty) {
        paperToast.text = '用户昵称不可为空';
        paperToast.show();
        return;
      }
      if (loginName == null || loginName.isEmpty) {
        paperToast.text = '用户登录名不可为空';
        paperToast.show();
        return;
      }
      if (password == null || password.isEmpty) {
        paperToast.text = '用户密码不可为空';
        paperToast.show();
        return;
      }
      
      var mapData = new Map();
      mapData['role'] = 'Customer';
      mapData['display_name'] = displayName;
      mapData['login_name'] = loginName;
      mapData['password'] = password;
      mapData['parent_id'] = selectCustomer.id;
      mapData['is_manager'] = false;
      mapData['can_create_order'] = creareOrder;
      mapData['can_edit_order'] = editOrder;
      mapData['can_cancel_order'] = cancelOrder;
      mapData['can_receive_order'] = receiveOrder;
      mapData['can_change_storage'] = changeStorage;
      mapData['can_edit_template'] = editTemplate;
      mapData['can_deliver_order'] = false;
      mapData['can_view_history'] = true;
      mapData['can_confirm_order'] = false;
      mapData['can_view_statistic'] = false;
      if (selectUser != null) {
        mapData['id'] = selectUser.id;
      }
      
      actioning = true;
      paperToast.text = '正在提交..';
      paperToast.show();
      
      BackendRpcBackendUser request = new BackendRpcBackendUser.fromJson(mapData);
      backend.changeUser(request).then((BackendRpcUserResponse value) {
        actioning = false;
        if (value.code == 200) {
          if (selectUser == null) {
            selectCustomer.users.add(value.item);
          } else {
            int index = selectCustomer.users.indexOf(selectUser);
            selectCustomer.users[index] = value.item;
            selectUser = value.item;
          }
          paperToast.text = '修改成功';
          paperToast.show();
        } else {
          paperToast.text = value.message;
          paperToast.show();
        }
      }).catchError((value) {
        actioning = false;
        paperToast.text = '修改失败';
        paperToast.show();
      });
    }
  }
}