part of backend_v1_api;

/** System configuration request */
class BackendRpcBackendConfig {

  core.int cancelQuote;

  core.int editQuote;

  core.int make_quote;

  core.int order_range;

  core.String report_email;

  /** Create new BackendRpcBackendConfig from JSON data */
  BackendRpcBackendConfig.fromJson(core.Map json) {
    if (json.containsKey("cancelQuote")) {
      cancelQuote = (json["cancelQuote"] is core.String) ? core.int.parse(json["cancelQuote"]) : json["cancelQuote"];
    }
    if (json.containsKey("editQuote")) {
      editQuote = (json["editQuote"] is core.String) ? core.int.parse(json["editQuote"]) : json["editQuote"];
    }
    if (json.containsKey("make_quote")) {
      make_quote = (json["make_quote"] is core.String) ? core.int.parse(json["make_quote"]) : json["make_quote"];
    }
    if (json.containsKey("order_range")) {
      order_range = (json["order_range"] is core.String) ? core.int.parse(json["order_range"]) : json["order_range"];
    }
    if (json.containsKey("report_email")) {
      report_email = json["report_email"];
    }
  }

  /** Create JSON Object for BackendRpcBackendConfig */
  core.Map toJson() {
    var output = new core.Map();

    if (cancelQuote != null) {
      output["cancelQuote"] = cancelQuote;
    }
    if (editQuote != null) {
      output["editQuote"] = editQuote;
    }
    if (make_quote != null) {
      output["make_quote"] = make_quote;
    }
    if (order_range != null) {
      output["order_range"] = order_range;
    }
    if (report_email != null) {
      output["report_email"] = report_email;
    }

    return output;
  }

  /** Return String representation of BackendRpcBackendConfig */
  core.String toString() => JSON.encode(this.toJson());

}

/** Customer group request */
class BackendRpcBackendCustomerGroupRequest {

  core.String director_display_name;

  core.String director_id;

  core.String director_login_name;

  core.String director_password;

  core.String id;

  core.String name_cn;

  core.String name_en;

  /** Create new BackendRpcBackendCustomerGroupRequest from JSON data */
  BackendRpcBackendCustomerGroupRequest.fromJson(core.Map json) {
    if (json.containsKey("director_display_name")) {
      director_display_name = json["director_display_name"];
    }
    if (json.containsKey("director_id")) {
      director_id = json["director_id"];
    }
    if (json.containsKey("director_login_name")) {
      director_login_name = json["director_login_name"];
    }
    if (json.containsKey("director_password")) {
      director_password = json["director_password"];
    }
    if (json.containsKey("id")) {
      id = json["id"];
    }
    if (json.containsKey("name_cn")) {
      name_cn = json["name_cn"];
    }
    if (json.containsKey("name_en")) {
      name_en = json["name_en"];
    }
  }

  /** Create JSON Object for BackendRpcBackendCustomerGroupRequest */
  core.Map toJson() {
    var output = new core.Map();

    if (director_display_name != null) {
      output["director_display_name"] = director_display_name;
    }
    if (director_id != null) {
      output["director_id"] = director_id;
    }
    if (director_login_name != null) {
      output["director_login_name"] = director_login_name;
    }
    if (director_password != null) {
      output["director_password"] = director_password;
    }
    if (id != null) {
      output["id"] = id;
    }
    if (name_cn != null) {
      output["name_cn"] = name_cn;
    }
    if (name_en != null) {
      output["name_en"] = name_en;
    }

    return output;
  }

  /** Return String representation of BackendRpcBackendCustomerGroupRequest */
  core.String toString() => JSON.encode(this.toJson());

}

/** Backend customer group */
class BackendRpcBackendCustomerGroupResponse {

  /** " Backend customer */
  core.List<BackendRpcBackendCustomerResponse> customers;

  /** User */
  BackendRpcBackendUser director;

  core.String id;

  core.String name_cn;

  core.String name_en;

  /** Create new BackendRpcBackendCustomerGroupResponse from JSON data */
  BackendRpcBackendCustomerGroupResponse.fromJson(core.Map json) {
    if (json.containsKey("customers")) {
      customers = json["customers"].map((customersItem) => new BackendRpcBackendCustomerResponse.fromJson(customersItem)).toList();
    }
    if (json.containsKey("director")) {
      director = new BackendRpcBackendUser.fromJson(json["director"]);
    }
    if (json.containsKey("id")) {
      id = json["id"];
    }
    if (json.containsKey("name_cn")) {
      name_cn = json["name_cn"];
    }
    if (json.containsKey("name_en")) {
      name_en = json["name_en"];
    }
  }

  /** Create JSON Object for BackendRpcBackendCustomerGroupResponse */
  core.Map toJson() {
    var output = new core.Map();

    if (customers != null) {
      output["customers"] = customers.map((customersItem) => customersItem.toJson()).toList();
    }
    if (director != null) {
      output["director"] = director.toJson();
    }
    if (id != null) {
      output["id"] = id;
    }
    if (name_cn != null) {
      output["name_cn"] = name_cn;
    }
    if (name_en != null) {
      output["name_en"] = name_en;
    }

    return output;
  }

  /** Return String representation of BackendRpcBackendCustomerGroupResponse */
  core.String toString() => JSON.encode(this.toJson());

}

/** " Customer request */
class BackendRpcBackendCustomerRequest {

  core.String hupheng_id;

  core.String id;

  core.String name_cn;

  core.String name_en;

  core.String parent_id;

  /** Create new BackendRpcBackendCustomerRequest from JSON data */
  BackendRpcBackendCustomerRequest.fromJson(core.Map json) {
    if (json.containsKey("hupheng_id")) {
      hupheng_id = json["hupheng_id"];
    }
    if (json.containsKey("id")) {
      id = json["id"];
    }
    if (json.containsKey("name_cn")) {
      name_cn = json["name_cn"];
    }
    if (json.containsKey("name_en")) {
      name_en = json["name_en"];
    }
    if (json.containsKey("parent_id")) {
      parent_id = json["parent_id"];
    }
  }

  /** Create JSON Object for BackendRpcBackendCustomerRequest */
  core.Map toJson() {
    var output = new core.Map();

    if (hupheng_id != null) {
      output["hupheng_id"] = hupheng_id;
    }
    if (id != null) {
      output["id"] = id;
    }
    if (name_cn != null) {
      output["name_cn"] = name_cn;
    }
    if (name_en != null) {
      output["name_en"] = name_en;
    }
    if (parent_id != null) {
      output["parent_id"] = parent_id;
    }

    return output;
  }

  /** Return String representation of BackendRpcBackendCustomerRequest */
  core.String toString() => JSON.encode(this.toJson());

}

/** " Backend customer */
class BackendRpcBackendCustomerResponse {

  core.String hupheng_id;

  core.String id;

  core.String name_cn;

  core.String name_en;

  /** User */
  core.List<BackendRpcBackendUser> users;

  /** Create new BackendRpcBackendCustomerResponse from JSON data */
  BackendRpcBackendCustomerResponse.fromJson(core.Map json) {
    if (json.containsKey("hupheng_id")) {
      hupheng_id = json["hupheng_id"];
    }
    if (json.containsKey("id")) {
      id = json["id"];
    }
    if (json.containsKey("name_cn")) {
      name_cn = json["name_cn"];
    }
    if (json.containsKey("name_en")) {
      name_en = json["name_en"];
    }
    if (json.containsKey("users")) {
      users = json["users"].map((usersItem) => new BackendRpcBackendUser.fromJson(usersItem)).toList();
    }
  }

  /** Create JSON Object for BackendRpcBackendCustomerResponse */
  core.Map toJson() {
    var output = new core.Map();

    if (hupheng_id != null) {
      output["hupheng_id"] = hupheng_id;
    }
    if (id != null) {
      output["id"] = id;
    }
    if (name_cn != null) {
      output["name_cn"] = name_cn;
    }
    if (name_en != null) {
      output["name_en"] = name_en;
    }
    if (users != null) {
      output["users"] = users.map((usersItem) => usersItem.toJson()).toList();
    }

    return output;
  }

  /** Return String representation of BackendRpcBackendCustomerResponse */
  core.String toString() => JSON.encode(this.toJson());

}

/** Product */
class BackendRpcBackendProduct {

  core.bool descimal_unit;

  core.String hupheng_id;

  core.String id;

  core.String image_url;

  core.String name_cn;

  core.String name_en;

  core.String type;

  core.String unit_name;

  /** Create new BackendRpcBackendProduct from JSON data */
  BackendRpcBackendProduct.fromJson(core.Map json) {
    if (json.containsKey("descimal_unit")) {
      descimal_unit = json["descimal_unit"];
    }
    if (json.containsKey("hupheng_id")) {
      hupheng_id = json["hupheng_id"];
    }
    if (json.containsKey("id")) {
      id = json["id"];
    }
    if (json.containsKey("image_url")) {
      image_url = json["image_url"];
    }
    if (json.containsKey("name_cn")) {
      name_cn = json["name_cn"];
    }
    if (json.containsKey("name_en")) {
      name_en = json["name_en"];
    }
    if (json.containsKey("type")) {
      type = json["type"];
    }
    if (json.containsKey("unit_name")) {
      unit_name = json["unit_name"];
    }
  }

  /** Create JSON Object for BackendRpcBackendProduct */
  core.Map toJson() {
    var output = new core.Map();

    if (descimal_unit != null) {
      output["descimal_unit"] = descimal_unit;
    }
    if (hupheng_id != null) {
      output["hupheng_id"] = hupheng_id;
    }
    if (id != null) {
      output["id"] = id;
    }
    if (image_url != null) {
      output["image_url"] = image_url;
    }
    if (name_cn != null) {
      output["name_cn"] = name_cn;
    }
    if (name_en != null) {
      output["name_en"] = name_en;
    }
    if (type != null) {
      output["type"] = type;
    }
    if (unit_name != null) {
      output["unit_name"] = unit_name;
    }

    return output;
  }

  /** Return String representation of BackendRpcBackendProduct */
  core.String toString() => JSON.encode(this.toJson());

}

/** User */
class BackendRpcBackendUser {

  core.bool can_cancel_order;

  core.bool can_change_storage;

  core.bool can_confirm_order;

  core.bool can_create_order;

  core.bool can_deliver_order;

  core.bool can_edit_order;

  core.bool can_edit_template;

  core.bool can_receive_order;

  core.bool can_view_history;

  core.bool can_view_statistic;

  core.String display_name;

  core.String id;

  core.bool is_manager;

  core.String login_name;

  core.String parent_id;

  core.String password;

  core.String role;

  /** Create new BackendRpcBackendUser from JSON data */
  BackendRpcBackendUser.fromJson(core.Map json) {
    if (json.containsKey("can_cancel_order")) {
      can_cancel_order = json["can_cancel_order"];
    }
    if (json.containsKey("can_change_storage")) {
      can_change_storage = json["can_change_storage"];
    }
    if (json.containsKey("can_confirm_order")) {
      can_confirm_order = json["can_confirm_order"];
    }
    if (json.containsKey("can_create_order")) {
      can_create_order = json["can_create_order"];
    }
    if (json.containsKey("can_deliver_order")) {
      can_deliver_order = json["can_deliver_order"];
    }
    if (json.containsKey("can_edit_order")) {
      can_edit_order = json["can_edit_order"];
    }
    if (json.containsKey("can_edit_template")) {
      can_edit_template = json["can_edit_template"];
    }
    if (json.containsKey("can_receive_order")) {
      can_receive_order = json["can_receive_order"];
    }
    if (json.containsKey("can_view_history")) {
      can_view_history = json["can_view_history"];
    }
    if (json.containsKey("can_view_statistic")) {
      can_view_statistic = json["can_view_statistic"];
    }
    if (json.containsKey("display_name")) {
      display_name = json["display_name"];
    }
    if (json.containsKey("id")) {
      id = json["id"];
    }
    if (json.containsKey("is_manager")) {
      is_manager = json["is_manager"];
    }
    if (json.containsKey("login_name")) {
      login_name = json["login_name"];
    }
    if (json.containsKey("parent_id")) {
      parent_id = json["parent_id"];
    }
    if (json.containsKey("password")) {
      password = json["password"];
    }
    if (json.containsKey("role")) {
      role = json["role"];
    }
  }

  /** Create JSON Object for BackendRpcBackendUser */
  core.Map toJson() {
    var output = new core.Map();

    if (can_cancel_order != null) {
      output["can_cancel_order"] = can_cancel_order;
    }
    if (can_change_storage != null) {
      output["can_change_storage"] = can_change_storage;
    }
    if (can_confirm_order != null) {
      output["can_confirm_order"] = can_confirm_order;
    }
    if (can_create_order != null) {
      output["can_create_order"] = can_create_order;
    }
    if (can_deliver_order != null) {
      output["can_deliver_order"] = can_deliver_order;
    }
    if (can_edit_order != null) {
      output["can_edit_order"] = can_edit_order;
    }
    if (can_edit_template != null) {
      output["can_edit_template"] = can_edit_template;
    }
    if (can_receive_order != null) {
      output["can_receive_order"] = can_receive_order;
    }
    if (can_view_history != null) {
      output["can_view_history"] = can_view_history;
    }
    if (can_view_statistic != null) {
      output["can_view_statistic"] = can_view_statistic;
    }
    if (display_name != null) {
      output["display_name"] = display_name;
    }
    if (id != null) {
      output["id"] = id;
    }
    if (is_manager != null) {
      output["is_manager"] = is_manager;
    }
    if (login_name != null) {
      output["login_name"] = login_name;
    }
    if (parent_id != null) {
      output["parent_id"] = parent_id;
    }
    if (password != null) {
      output["password"] = password;
    }
    if (role != null) {
      output["role"] = role;
    }

    return output;
  }

  /** Return String representation of BackendRpcBackendUser */
  core.String toString() => JSON.encode(this.toJson());

}

/** Backend use only, get all datas */
class BackendRpcCustomerGroupResponseCollection {

  core.int code;

  /** Backend customer group */
  core.List<BackendRpcBackendCustomerGroupResponse> items;

  core.String message;

  /** Create new BackendRpcCustomerGroupResponseCollection from JSON data */
  BackendRpcCustomerGroupResponseCollection.fromJson(core.Map json) {
    if (json.containsKey("code")) {
      code = (json["code"] is core.String) ? core.int.parse(json["code"]) : json["code"];
    }
    if (json.containsKey("items")) {
      items = json["items"].map((itemsItem) => new BackendRpcBackendCustomerGroupResponse.fromJson(itemsItem)).toList();
    }
    if (json.containsKey("message")) {
      message = json["message"];
    }
  }

  /** Create JSON Object for BackendRpcCustomerGroupResponseCollection */
  core.Map toJson() {
    var output = new core.Map();

    if (code != null) {
      output["code"] = code;
    }
    if (items != null) {
      output["items"] = items.map((itemsItem) => itemsItem.toJson()).toList();
    }
    if (message != null) {
      output["message"] = message;
    }

    return output;
  }

  /** Return String representation of BackendRpcCustomerGroupResponseCollection */
  core.String toString() => JSON.encode(this.toJson());

}

/** Delete request */
class BackendRpcDeleteRequest {

  core.String id;

  /** Create new BackendRpcDeleteRequest from JSON data */
  BackendRpcDeleteRequest.fromJson(core.Map json) {
    if (json.containsKey("id")) {
      id = json["id"];
    }
  }

  /** Create JSON Object for BackendRpcDeleteRequest */
  core.Map toJson() {
    var output = new core.Map();

    if (id != null) {
      output["id"] = id;
    }

    return output;
  }

  /** Return String representation of BackendRpcDeleteRequest */
  core.String toString() => JSON.encode(this.toJson());

}

/** User for backend login */
class BackendRpcLoginRequest {

  core.String password;

  core.String username;

  /** Create new BackendRpcLoginRequest from JSON data */
  BackendRpcLoginRequest.fromJson(core.Map json) {
    if (json.containsKey("password")) {
      password = json["password"];
    }
    if (json.containsKey("username")) {
      username = json["username"];
    }
  }

  /** Create JSON Object for BackendRpcLoginRequest */
  core.Map toJson() {
    var output = new core.Map();

    if (password != null) {
      output["password"] = password;
    }
    if (username != null) {
      output["username"] = username;
    }

    return output;
  }

  /** Return String representation of BackendRpcLoginRequest */
  core.String toString() => JSON.encode(this.toJson());

}

/** Product response collection */
class BackendRpcProductCollectionResponse {

  core.int code;

  /** Product */
  core.List<BackendRpcBackendProduct> items;

  core.String message;

  /** Create new BackendRpcProductCollectionResponse from JSON data */
  BackendRpcProductCollectionResponse.fromJson(core.Map json) {
    if (json.containsKey("code")) {
      code = (json["code"] is core.String) ? core.int.parse(json["code"]) : json["code"];
    }
    if (json.containsKey("items")) {
      items = json["items"].map((itemsItem) => new BackendRpcBackendProduct.fromJson(itemsItem)).toList();
    }
    if (json.containsKey("message")) {
      message = json["message"];
    }
  }

  /** Create JSON Object for BackendRpcProductCollectionResponse */
  core.Map toJson() {
    var output = new core.Map();

    if (code != null) {
      output["code"] = code;
    }
    if (items != null) {
      output["items"] = items.map((itemsItem) => itemsItem.toJson()).toList();
    }
    if (message != null) {
      output["message"] = message;
    }

    return output;
  }

  /** Return String representation of BackendRpcProductCollectionResponse */
  core.String toString() => JSON.encode(this.toJson());

}

class BackendRpcProductResponse {

  core.int code;

  /** Product */
  BackendRpcBackendProduct item;

  core.String message;

  /** Create new BackendRpcProductResponse from JSON data */
  BackendRpcProductResponse.fromJson(core.Map json) {
    if (json.containsKey("code")) {
      code = (json["code"] is core.String) ? core.int.parse(json["code"]) : json["code"];
    }
    if (json.containsKey("item")) {
      item = new BackendRpcBackendProduct.fromJson(json["item"]);
    }
    if (json.containsKey("message")) {
      message = json["message"];
    }
  }

  /** Create JSON Object for BackendRpcProductResponse */
  core.Map toJson() {
    var output = new core.Map();

    if (code != null) {
      output["code"] = code;
    }
    if (item != null) {
      output["item"] = item.toJson();
    }
    if (message != null) {
      output["message"] = message;
    }

    return output;
  }

  /** Return String representation of BackendRpcProductResponse */
  core.String toString() => JSON.encode(this.toJson());

}

/** Simple response only indicate request is success or failed */
class BackendRpcSimpleResponse {

  core.int code;

  core.String message;

  /** Create new BackendRpcSimpleResponse from JSON data */
  BackendRpcSimpleResponse.fromJson(core.Map json) {
    if (json.containsKey("code")) {
      code = (json["code"] is core.String) ? core.int.parse(json["code"]) : json["code"];
    }
    if (json.containsKey("message")) {
      message = json["message"];
    }
  }

  /** Create JSON Object for BackendRpcSimpleResponse */
  core.Map toJson() {
    var output = new core.Map();

    if (code != null) {
      output["code"] = code;
    }
    if (message != null) {
      output["message"] = message;
    }

    return output;
  }

  /** Return String representation of BackendRpcSimpleResponse */
  core.String toString() => JSON.encode(this.toJson());

}

class BackendRpcSingleBackendCustomerGroupResponse {

  core.int code;

  /** Backend customer group */
  BackendRpcBackendCustomerGroupResponse item;

  core.String message;

  /** Create new BackendRpcSingleBackendCustomerGroupResponse from JSON data */
  BackendRpcSingleBackendCustomerGroupResponse.fromJson(core.Map json) {
    if (json.containsKey("code")) {
      code = (json["code"] is core.String) ? core.int.parse(json["code"]) : json["code"];
    }
    if (json.containsKey("item")) {
      item = new BackendRpcBackendCustomerGroupResponse.fromJson(json["item"]);
    }
    if (json.containsKey("message")) {
      message = json["message"];
    }
  }

  /** Create JSON Object for BackendRpcSingleBackendCustomerGroupResponse */
  core.Map toJson() {
    var output = new core.Map();

    if (code != null) {
      output["code"] = code;
    }
    if (item != null) {
      output["item"] = item.toJson();
    }
    if (message != null) {
      output["message"] = message;
    }

    return output;
  }

  /** Return String representation of BackendRpcSingleBackendCustomerGroupResponse */
  core.String toString() => JSON.encode(this.toJson());

}

class BackendRpcSingleBackendCustomerResponse {

  core.int code;

  /** " Backend customer */
  BackendRpcBackendCustomerResponse item;

  core.String message;

  /** Create new BackendRpcSingleBackendCustomerResponse from JSON data */
  BackendRpcSingleBackendCustomerResponse.fromJson(core.Map json) {
    if (json.containsKey("code")) {
      code = (json["code"] is core.String) ? core.int.parse(json["code"]) : json["code"];
    }
    if (json.containsKey("item")) {
      item = new BackendRpcBackendCustomerResponse.fromJson(json["item"]);
    }
    if (json.containsKey("message")) {
      message = json["message"];
    }
  }

  /** Create JSON Object for BackendRpcSingleBackendCustomerResponse */
  core.Map toJson() {
    var output = new core.Map();

    if (code != null) {
      output["code"] = code;
    }
    if (item != null) {
      output["item"] = item.toJson();
    }
    if (message != null) {
      output["message"] = message;
    }

    return output;
  }

  /** Return String representation of BackendRpcSingleBackendCustomerResponse */
  core.String toString() => JSON.encode(this.toJson());

}

/** User collections */
class BackendRpcUserCollectionResponse {

  core.int code;

  /** User */
  core.List<BackendRpcBackendUser> items;

  core.String message;

  /** Create new BackendRpcUserCollectionResponse from JSON data */
  BackendRpcUserCollectionResponse.fromJson(core.Map json) {
    if (json.containsKey("code")) {
      code = (json["code"] is core.String) ? core.int.parse(json["code"]) : json["code"];
    }
    if (json.containsKey("items")) {
      items = json["items"].map((itemsItem) => new BackendRpcBackendUser.fromJson(itemsItem)).toList();
    }
    if (json.containsKey("message")) {
      message = json["message"];
    }
  }

  /** Create JSON Object for BackendRpcUserCollectionResponse */
  core.Map toJson() {
    var output = new core.Map();

    if (code != null) {
      output["code"] = code;
    }
    if (items != null) {
      output["items"] = items.map((itemsItem) => itemsItem.toJson()).toList();
    }
    if (message != null) {
      output["message"] = message;
    }

    return output;
  }

  /** Return String representation of BackendRpcUserCollectionResponse */
  core.String toString() => JSON.encode(this.toJson());

}

class BackendRpcUserResponse {

  core.int code;

  /** User */
  BackendRpcBackendUser item;

  core.String message;

  /** Create new BackendRpcUserResponse from JSON data */
  BackendRpcUserResponse.fromJson(core.Map json) {
    if (json.containsKey("code")) {
      code = (json["code"] is core.String) ? core.int.parse(json["code"]) : json["code"];
    }
    if (json.containsKey("item")) {
      item = new BackendRpcBackendUser.fromJson(json["item"]);
    }
    if (json.containsKey("message")) {
      message = json["message"];
    }
  }

  /** Create JSON Object for BackendRpcUserResponse */
  core.Map toJson() {
    var output = new core.Map();

    if (code != null) {
      output["code"] = code;
    }
    if (item != null) {
      output["item"] = item.toJson();
    }
    if (message != null) {
      output["message"] = message;
    }

    return output;
  }

  /** Return String representation of BackendRpcUserResponse */
  core.String toString() => JSON.encode(this.toJson());

}

core.Map _mapMap(core.Map source, [core.Object convert(core.Object source) = null]) {
  assert(source != null);
  var result = new dart_collection.LinkedHashMap();
  source.forEach((core.String key, value) {
    assert(key != null);
    if(convert == null) {
      result[key] = value;
    } else {
      result[key] = convert(value);
    }
  });
  return result;
}
