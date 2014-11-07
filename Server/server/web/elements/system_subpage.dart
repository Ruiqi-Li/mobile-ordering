import 'package:polymer/polymer.dart';
import 'package:paper_elements/paper_toast.dart';
import 'package:google_backend_v1_api/backend_v1_api_browser.dart';
import 'package:google_backend_v1_api/backend_v1_api_client.dart';

@CustomTag('system-subpage')
class SystemSubPage extends PolymerElement with ChangeNotifier  {
  @reflectable @observable dynamic get orderAdvenceDay => __$orderAdvenceDay; dynamic __$orderAdvenceDay; @reflectable set orderAdvenceDay(dynamic value) { __$orderAdvenceDay = notifyPropertyChange(#orderAdvenceDay, __$orderAdvenceDay, value); }
  @reflectable @observable dynamic get makeQuota => __$makeQuota; dynamic __$makeQuota; @reflectable set makeQuota(dynamic value) { __$makeQuota = notifyPropertyChange(#makeQuota, __$makeQuota, value); }
  @reflectable @observable dynamic get editQuota => __$editQuota; dynamic __$editQuota; @reflectable set editQuota(dynamic value) { __$editQuota = notifyPropertyChange(#editQuota, __$editQuota, value); }
  @reflectable @observable dynamic get cancelQuota => __$cancelQuota; dynamic __$cancelQuota; @reflectable set cancelQuota(dynamic value) { __$cancelQuota = notifyPropertyChange(#cancelQuota, __$cancelQuota, value); }
  @reflectable @observable dynamic get email => __$email; dynamic __$email; @reflectable set email(dynamic value) { __$email = notifyPropertyChange(#email, __$email, value); }
  
  @reflectable @observable bool get actioning => __$actioning; bool __$actioning = false; @reflectable set actioning(bool value) { __$actioning = notifyPropertyChange(#actioning, __$actioning, value); }
  
  @reflectable @observable bool get emailInvalid => __$emailInvalid; bool __$emailInvalid = true; @reflectable set emailInvalid(bool value) { __$emailInvalid = notifyPropertyChange(#emailInvalid, __$emailInvalid, value); }
  @reflectable @observable bool get orderDayInvalid => __$orderDayInvalid; bool __$orderDayInvalid = true; @reflectable set orderDayInvalid(bool value) { __$orderDayInvalid = notifyPropertyChange(#orderDayInvalid, __$orderDayInvalid, value); }
  @reflectable @observable bool get makeQuotaInvalid => __$makeQuotaInvalid; bool __$makeQuotaInvalid = true; @reflectable set makeQuotaInvalid(bool value) { __$makeQuotaInvalid = notifyPropertyChange(#makeQuotaInvalid, __$makeQuotaInvalid, value); }
  @reflectable @observable bool get editQuotaInvalid => __$editQuotaInvalid; bool __$editQuotaInvalid = true; @reflectable set editQuotaInvalid(bool value) { __$editQuotaInvalid = notifyPropertyChange(#editQuotaInvalid, __$editQuotaInvalid, value); }
  @reflectable @observable bool get cancelQuotaInvalid => __$cancelQuotaInvalid; bool __$cancelQuotaInvalid = true; @reflectable set cancelQuotaInvalid(bool value) { __$cancelQuotaInvalid = notifyPropertyChange(#cancelQuotaInvalid, __$cancelQuotaInvalid, value); }
  
  PaperToast toast;
  
  Backend backend;
  
  SystemSubPage.created() : super.created() {
    backend = new Backend();
    backend.rootUrl = 'https://smsorderingserver.appspot.com/';
  }
  
  @override
  void attached() {
    super.attached();
    
    toast = $['toast-view'];
    
    backend.getSystemConfig().then((BackendRpcBackendConfig config) {
      editQuota = config.editQuote;
      cancelQuota = config.cancelQuote;
      email = config.report_email;
      makeQuota = config.make_quote;
      orderAdvenceDay = config.order_range;
    });
  }
  
  void onSubmitClicked() {
    if (emailInvalid || email == null || email.isEmpty) {
      toast.text = "请填写正确的邮箱地址";
      toast.show();
      return;
    } else if (orderDayInvalid) {
      toast.text = "请填写正确的天数 1 ~ 100";
      toast.show();
      return;
    } else if (makeQuotaInvalid || editQuotaInvalid || cancelQuotaInvalid) {
      toast.text = "请填写正确的时间 0 ~ 23";
      toast.show();
      return;
    }
    
    var dataMap = new Map();
    dataMap['editQuote'] = editQuota;
    dataMap['cancelQuote'] = cancelQuota;
    dataMap['report_email'] = email;
    dataMap['make_quote'] = makeQuota;
    dataMap['order_range'] = orderAdvenceDay;
    
    actioning = true;
    toast.text = "正在提交..";
    toast.show();
    
    BackendRpcBackendConfig request = new BackendRpcBackendConfig.fromJson(dataMap);
    backend.configSystem(request).then((BackendRpcSimpleResponse value) {
      actioning = false;
      toast.text = "操作成功";
      toast.show();
    }).catchError((value) {
      actioning = false;
      toast.text = "操作失败";
      toast.show();
    });
  }
}