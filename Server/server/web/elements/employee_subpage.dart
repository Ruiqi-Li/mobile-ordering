import 'dart:html';
import 'package:polymer/polymer.dart';
import 'package:paper_elements/paper_dialog.dart';
import 'package:paper_elements/paper_toast.dart';
import 'package:google_backend_v1_api/backend_v1_api_browser.dart';
import 'package:google_backend_v1_api/backend_v1_api_client.dart';

@CustomTag('employee-subpage')
class EmployeeSubpage extends PolymerElement with ChangeNotifier  {
  List employees = toObservable([]);
  
  @reflectable @observable String get displayName => __$displayName; String __$displayName; @reflectable set displayName(String value) { __$displayName = notifyPropertyChange(#displayName, __$displayName, value); }
  @reflectable @observable String get loginName => __$loginName; String __$loginName; @reflectable set loginName(String value) { __$loginName = notifyPropertyChange(#loginName, __$loginName, value); }
  @reflectable @observable String get password => __$password; String __$password; @reflectable set password(String value) { __$password = notifyPropertyChange(#password, __$password, value); }
  @reflectable @observable bool get creareOrder => __$creareOrder; bool __$creareOrder; @reflectable set creareOrder(bool value) { __$creareOrder = notifyPropertyChange(#creareOrder, __$creareOrder, value); }
  @reflectable @observable bool get editOrder => __$editOrder; bool __$editOrder; @reflectable set editOrder(bool value) { __$editOrder = notifyPropertyChange(#editOrder, __$editOrder, value); }
  @reflectable @observable bool get cancelOrder => __$cancelOrder; bool __$cancelOrder; @reflectable set cancelOrder(bool value) { __$cancelOrder = notifyPropertyChange(#cancelOrder, __$cancelOrder, value); }
  @reflectable @observable bool get confirmOrder => __$confirmOrder; bool __$confirmOrder; @reflectable set confirmOrder(bool value) { __$confirmOrder = notifyPropertyChange(#confirmOrder, __$confirmOrder, value); }
  @reflectable @observable bool get deliverOrder => __$deliverOrder; bool __$deliverOrder; @reflectable set deliverOrder(bool value) { __$deliverOrder = notifyPropertyChange(#deliverOrder, __$deliverOrder, value); }
  @reflectable @observable bool get changeStorage => __$changeStorage; bool __$changeStorage; @reflectable set changeStorage(bool value) { __$changeStorage = notifyPropertyChange(#changeStorage, __$changeStorage, value); }
  @reflectable @observable bool get viewHistory => __$viewHistory; bool __$viewHistory; @reflectable set viewHistory(bool value) { __$viewHistory = notifyPropertyChange(#viewHistory, __$viewHistory, value); }
  @reflectable @observable bool get viewStatistic => __$viewStatistic; bool __$viewStatistic; @reflectable set viewStatistic(bool value) { __$viewStatistic = notifyPropertyChange(#viewStatistic, __$viewStatistic, value); }
  
  @reflectable @observable bool get actioning => __$actioning; bool __$actioning = false; @reflectable set actioning(bool value) { __$actioning = notifyPropertyChange(#actioning, __$actioning, value); }
  
  Backend backend;
  BackendRpcBackendUser select;
  Element deleteBtn;
  PaperDialog deleteDialog;
  PaperToast paperToast;
  Element deleteMessage;
  
  EmployeeSubpage.created() : super.created() {
    backend = new Backend();
    backend.rootUrl = 'https://ruiqi-test.appspot.com/';
  }
  
  @override
  void attached() {
    super.attached();
    
    deleteBtn = $['delete-btn'];
    deleteDialog = $['delete-dialog'];
    deleteMessage = $['dialog-message'];
    paperToast = $['toast-view'];
    newEmployeeClicked();
    syncEmployee();
  }
  
  @override
  void detached() {
    super.detached();
  }
  
  void syncEmployee() {
    backend.listEmployee().then((BackendRpcUserCollectionResponse value) {
      employees.addAll(value.items);
    });
  }
  
  void newEmployeeClicked() {
    displayName = '';
    loginName = '';
    password = '';
    creareOrder = true;
    editOrder = true;
    cancelOrder = true;
    confirmOrder = true;
    deliverOrder = true;
    changeStorage = true;
    viewHistory = true;
    viewStatistic = true;
    deleteBtn.hidden = true;
    select = null;
  }
  
  void onListItemClicked(Event event, var detail, Node target) {
    int index = int.parse((target as Element).attributes['data-index']);
    BackendRpcBackendUser user = employees[index];
    displayName = user.display_name;
    loginName = user.login_name;
    password = user.password;
    creareOrder = user.can_create_order;
    editOrder = user.can_edit_order;
    cancelOrder = user.can_cancel_order;
    confirmOrder = user.can_confirm_order;
    deliverOrder = user.can_deliver_order;
    changeStorage = user.can_change_storage;
    viewHistory = user.can_view_history;
    viewStatistic = user.can_view_statistic;
    deleteBtn.hidden = false;
    select = user;
  }
  
  void onDeleteClicked(Event event, var detail, Node target) {
    deleteMessage.text = '确认删除' + select.display_name;
    deleteDialog.toggle();
  }
  
  void onDeleteConfirm(Event event, var detail, Node target) {
    var dataMap = new Map();
    dataMap['id'] = select.id;
    
    actioning = true;
    paperToast.text = '正在删除..';
    paperToast.show();
    
    BackendRpcDeleteRequest request = new BackendRpcDeleteRequest.fromJson(dataMap);
    backend.deleteItem(request).then((BackendRpcSimpleResponse value) {
      paperToast.text = value.message;
      paperToast.show();
      
      actioning = false;
              
      if (value.code == 200) {
        employees.remove(select);
        newEmployeeClicked();
        paperToast.text = '删除成功';
        paperToast.show();
      } else {
        paperToast.text = value.message;
        paperToast.show();
      }
    }).catchError((exception) {
      actioning = false;
      paperToast.text = '删除失败';
      paperToast.show();
    });
  }
  
  void onSubmitClicked(Event event, var detail, Node target) {
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
    mapData['role'] = 'Supplier';
    mapData['display_name'] = displayName;
    mapData['login_name'] = loginName;
    mapData['password'] = password;
    mapData['is_manager'] = false;
    mapData['can_create_order'] = creareOrder;
    mapData['can_edit_order'] = editOrder;
    mapData['can_cancel_order'] = cancelOrder;
    mapData['can_change_storage'] = changeStorage;
    mapData['can_view_history'] = viewHistory;
    mapData['can_confirm_order'] = confirmOrder;
    mapData['can_deliver_order'] = deliverOrder;
    mapData['can_view_statistic'] = viewStatistic;
    mapData['can_receive_order'] = false;
    mapData['can_edit_template'] = false;
    if (select != null) {
      mapData['id'] = select.id;
    }
    
    actioning = true;
    paperToast.text = '正在提交..';
    paperToast.show();
    
    BackendRpcBackendUser request = new BackendRpcBackendUser.fromJson(mapData);
    backend.changeUser(request).then((BackendRpcUserResponse value) {
      actioning = false;
      
      if (value.code == 200) {
        if (select == null) {
          employees.add(value.item);
        } else {
          int index = employees.indexOf(select);
          employees[index] = value.item;
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