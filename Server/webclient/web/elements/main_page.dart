import 'dart:html';
import 'package:polymer/polymer.dart';
import 'package:paper_elements/paper_tabs.dart';
import 'package:paper_elements/paper_toast.dart';
import 'package:google_backend_v1_api/backend_v1_api_browser.dart';
import 'package:google_backend_v1_api/backend_v1_api_client.dart';

/**
 * A Polymer click counter element.
 */
@CustomTag('main-page')
class MainPage extends PolymerElement {
  @published int tabSelected = 0;
  
  @observable int launcherSelect = 0;
  @observable String loginName;
  @observable String password;
  @observable bool logining = false;
  @observable String loginBtnText = '登陆';
  
  Element titleTabs;
  Element subpageContainer;
  var titleChangeListener;
  Element systemSubPage;
  
  PaperToast toastElement;
  Backend backend;

  MainPage.created() : super.created() {
    backend = new Backend();
    backend.rootUrl = 'https://ruiqi-test.appspot.com/';
  }

  @override
  void attached() {
    super.attached();
    
    toastElement = $['bottom-toast'];
    
    titleTabs = $['main-title-tab'];
    subpageContainer = $['subpage-container'];
    titleChangeListener = titleTabs.onClick.listen((event) {
      num newIndex = (event.currentTarget as PaperTabs).selectedIndex;
      print(tabSelected);
      switch(newIndex) {
        case 10:
          if (systemSubPage == null) {
            systemSubPage = new Element.tag('system-subpage');
          } else if (systemSubPage.parent == subpageContainer) {
            return;
          }
          
          subpageContainer.children.clear();
          subpageContainer.children.add(systemSubPage);
          break;
      }
    });
  }
  
  @override
  void detached() {
    super.detached();
    
    titleChangeListener.cancel();
  }
  
  void onLoginClicked() {
    if (loginName == null || loginName.isEmpty) {
      toastElement.text = '用户名不能为空';
      toastElement.toggle();
      return;
    }
    
    if (password == null || password.isEmpty) {
      toastElement.text = '密码不能为空';
      toastElement.toggle();
      return;
    }
    
    loginBtnText = '正在登陆..';
    logining = true;
    
    var dataMap = new Map();
    dataMap['username'] = loginName;
    dataMap['password'] = password;
    BackendRpcLoginRequest request = new BackendRpcLoginRequest.fromJson(dataMap);
    backend.login(request).then((BackendRpcSimpleResponse response) {
      if (response.code == 200) {
        launcherSelect = 1;
      } else {
        logining = false;
        loginBtnText = '登陆';
        toastElement.text = response.message;
        toastElement.toggle();
      }
    });
  }
}

