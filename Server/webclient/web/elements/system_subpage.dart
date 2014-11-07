import 'package:polymer/polymer.dart';
import 'package:paper_elements/paper_toast.dart';
import 'package:google_backend_v1_api/backend_v1_api_browser.dart';
import 'package:google_backend_v1_api/backend_v1_api_client.dart';

@CustomTag('system-subpage')
class SystemSubPage extends PolymerElement {
  @observable var orderAdvenceDay;
  @observable var makeQuota;
  @observable var editQuota;
  @observable var cancelQuota;
  @observable var email;
  
  @observable bool actioning = false;
  
  @observable bool emailInvalid = true;
  @observable bool orderDayInvalid = true;
  @observable bool makeQuotaInvalid = true;
  @observable bool editQuotaInvalid = true;
  @observable bool cancelQuotaInvalid = true;
  
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