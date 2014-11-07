import 'dart:html';
import 'dart:convert';
import 'package:crypto/crypto.dart';
import 'package:polymer/polymer.dart';
import 'package:paper_elements/paper_dialog.dart';
import 'package:paper_elements/paper_toast.dart';
import 'package:google_backend_v1_api/backend_v1_api_browser.dart';
import 'package:google_backend_v1_api/backend_v1_api_client.dart';

@CustomTag('product-subpage')
class ProductSubpage extends PolymerElement {
  List products = toObservable([]);
  
  @observable int productType = 0;
  @observable String productImageUrl;
  @observable String productNameCn;
  @observable String productNameEn;
  @observable String productId;
  @observable String productUnitName;
  @observable int productIsDescimal = 0;
  
  @observable bool actioning = false;
  
  final CLIENT_ID = "595277279942-46l9vco02l05nti5lohpqqqvv5dfb1tq.apps.googleusercontent.com";
  Backend backend;
  BackendRpcBackendProduct select;
  Element deleteBtn;
  Element dropZone;
  PaperDialog deleteDialog;
  PaperToast paperToast;
  Element dialogMessage;
  var selectFile;
  
  ProductSubpage.created() : super.created() {
    backend = new Backend();
    backend.rootUrl = 'https://ruiqi-test.appspot.com/';
  }
  
  @override
  void attached() {
    super.attached();
    
    deleteBtn = $['delete-btn'];
    dropZone = $['drop-zone'];
    deleteDialog = $['delete-dialog'];
    dialogMessage = $['dialog-message'];
    paperToast = $['toast-view'];
    dropZone.onDragOver.listen((event) {
      event.stopPropagation();
      event.preventDefault();
      event.dataTransfer.dropEffect = 'copy';
    });
    dropZone.onDrop.listen((event) {
      event.stopPropagation();
      event.preventDefault();
      selectFile = event.dataTransfer.files[0];

      FileReader reader = new FileReader();
      reader.onLoad.listen((event) {
        productImageUrl = event.target.result;
      });
      reader.readAsDataUrl(selectFile);
    });
    syncProducts();
  }
  
  @override
  void detached() {
    super.detached();
  }
  
  void syncProducts() {
    backend.listProduct().then((BackendRpcProductCollectionResponse value) {
      products.addAll(value.items);
    });
  }
  
  void newProductClicked() {
    productType = 0;
    productImageUrl = '';
    productNameCn = '';
    productNameEn = '';
    productId = '';
    productUnitName = '';
    productIsDescimal = 0;
    select = null;
    deleteBtn.hidden = true;
  }
  
  void onListItemClicked(Event event, var detail, Node target) {
    int index = int.parse((target as Element).attributes['data-index']);
    BackendRpcBackendProduct product = products[index];
    productType = product.type == 'Fruit' ? 2 : 0;
    productImageUrl = product.image_url;
    productNameCn = product.name_cn;
    productNameEn = product.name_en;
    productId = product.hupheng_id;
    productUnitName = product.unit_name;
    productIsDescimal = product.descimal_unit ? 2 :0;
    select = product;
    deleteBtn.hidden = false;
  }
  
  void onDeleteClicked(Event event, var detail, Node target) {
    dialogMessage.text = '确认删除' + select.name_cn;
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
      actioning = false;
              
      if (value.code == 200) {
        products.remove(select);
        newProductClicked();
        
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
  
  void onSubmitClicked() {
    if (productNameCn == null || productNameCn.isEmpty) {
      paperToast.text = '商品中文名不可为空';
      paperToast.show();
      return;
    }
    if (productNameEn == null || productNameEn.isEmpty) {
      paperToast.text = '商品英文名不可为空';
      paperToast.show();
      return;
    }
    if (productUnitName == null || productUnitName.isEmpty) {
      paperToast.text = '商品销售单位不可为空';
      paperToast.show();
      return;
    }
    if (productId == null || productId.isEmpty) {
      paperToast.text = '商品ID不可为空';
      paperToast.show();
      return;
    }
    if (selectFile != null) {
      uploadImage();
      return;
    }
    if (productImageUrl == null || productImageUrl.isEmpty) {
      paperToast.text = '请拖拽商品图片至虚线区域';
      paperToast.show();
      return;
    }
    
    var dataMap = new Map();
    dataMap['image_url'] = productImageUrl;
    dataMap['name_cn'] = productNameCn;
    dataMap['name_en'] = productNameEn;
    dataMap['unit_name'] = productUnitName;
    dataMap['hupheng_id'] = productId;
    dataMap['type'] = productType == 0 ? 'Vegetable' : 'Fruit';
    dataMap['descimal_unit'] = productIsDescimal == 0 ? false : true;
    if (select != null) {
      dataMap['id'] = select.id;
    }
    
    actioning = true;
    paperToast.text = '正在提交..';
    paperToast.show();
    
    BackendRpcBackendProduct request = new BackendRpcBackendProduct.fromJson(dataMap);
    backend.changeProduct(request).then((BackendRpcProductResponse value) {
      actioning = false;
      
      if (value.code == 200) {
        if (select == null) {
          products.add(value.item);
        } else {
          int index = products.indexOf(select);
          products[index] = value.item;
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
  
  void uploadImage() {
    FileReader reader = new FileReader();
    reader.onLoad.listen((event) {
      String boundary = '-------314159265358979323846';
      String delimiter = "\r\n--" + boundary + "\r\n";
      String close_delim = "\r\n--" + boundary + "--";
      var data = event.target.result;
      String contentType = selectFile.type;
      if (contentType == null) {
        contentType = 'application/octet-stream';
      }
      
      var metadata = {
        'name': new DateTime.now().millisecondsSinceEpoch,
        'mimeType': contentType
      };

      var base64Data = CryptoUtils.bytesToBase64(data);
      var multipartRequestBody =
        delimiter +
        'Content-Type: application/json\r\n\r\n' +
        JSON.encode(metadata) +
        delimiter +
        'Content-Type: ' + contentType + '\r\n' +
        'Content-Transfer-Encoding: base64\r\n' +
        '\r\n' +
        base64Data +
        close_delim;
      
      var request = new HttpRequest();
      request.open('POST', 'https://www.googleapis.com/upload/storage/v1/b/mos-products/o?uploadType=multipart');
      request.setRequestHeader('Content-Type', 'multipart/mixed; boundary="' + boundary + '"');
      request.onLoad.listen((event) {
        if (event.target.status == 200) {
          selectFile = null;
          var json = JSON.decode(event.target.responseText);
          productImageUrl = json['mediaLink'];
          onSubmitClicked();
        }
      });
      request.send(multipartRequestBody);
    });
   reader.readAsArrayBuffer(selectFile);
  }
}