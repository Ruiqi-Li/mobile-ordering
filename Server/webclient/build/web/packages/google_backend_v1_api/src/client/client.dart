part of backend_v1_api;

abstract class Client extends ClientBase {
  core.String basePath = "/_ah/api/backend/v1/backend/";
  core.String rootUrl = "https://ruiqi-test.appspot.com/";


  //
  // Parameters
  //

  /**
   * Data format for the response.
   * Added as queryParameter for each request.
   */
  core.String get alt => params["alt"];
  set alt(core.String value) => params["alt"] = value;

  /**
   * Selector specifying which fields to include in a partial response.
   * Added as queryParameter for each request.
   */
  core.String get fields => params["fields"];
  set fields(core.String value) => params["fields"] = value;

  /**
   * API key. Your API key identifies your project and provides you with API access, quota, and reports. Required unless you provide an OAuth 2.0 token.
   * Added as queryParameter for each request.
   */
  core.String get key => params["key"];
  set key(core.String value) => params["key"] = value;

  /**
   * OAuth 2.0 token for the current user.
   * Added as queryParameter for each request.
   */
  core.String get oauth_token => params["oauth_token"];
  set oauth_token(core.String value) => params["oauth_token"] = value;

  /**
   * Returns response with indentations and line breaks.
   * Added as queryParameter for each request.
   */
  core.bool get prettyPrint => params["prettyPrint"];
  set prettyPrint(core.bool value) => params["prettyPrint"] = value;

  /**
   * Available to use for quota purposes for server-side applications. Can be any arbitrary string assigned to a user, but should not exceed 40 characters. Overrides userIp if both are provided.
   * Added as queryParameter for each request.
   */
  core.String get quotaUser => params["quotaUser"];
  set quotaUser(core.String value) => params["quotaUser"] = value;

  /**
   * IP address of the site where the request originates. Use this if you want to enforce per-user limits.
   * Added as queryParameter for each request.
   */
  core.String get userIp => params["userIp"];
  set userIp(core.String value) => params["userIp"] = value;

  //
  // Methods
  //

  /**
   *
   * [request] - BackendRpcBackendCustomerRequest to send in this request
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcSingleBackendCustomerResponse> changeCustomer(BackendRpcBackendCustomerRequest request, {core.Map optParams}) {
    var url = "change_customer";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", body: request.toString(), urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcSingleBackendCustomerResponse.fromJson(data));
  }

  /**
   *
   * [request] - BackendRpcBackendCustomerGroupRequest to send in this request
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcSingleBackendCustomerGroupResponse> changeCustomerGroup(BackendRpcBackendCustomerGroupRequest request, {core.Map optParams}) {
    var url = "changeCustomerGroup";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", body: request.toString(), urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcSingleBackendCustomerGroupResponse.fromJson(data));
  }

  /**
   *
   * [request] - BackendRpcBackendProduct to send in this request
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcProductResponse> changeProduct(BackendRpcBackendProduct request, {core.Map optParams}) {
    var url = "changeProduct";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", body: request.toString(), urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcProductResponse.fromJson(data));
  }

  /**
   *
   * [request] - BackendRpcBackendUser to send in this request
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcUserResponse> changeUser(BackendRpcBackendUser request, {core.Map optParams}) {
    var url = "change_user";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", body: request.toString(), urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcUserResponse.fromJson(data));
  }

  /**
   *
   * [request] - BackendRpcLoginRequest to send in this request
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcSimpleResponse> clear_data(BackendRpcLoginRequest request, {core.Map optParams}) {
    var url = "clear_data";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", body: request.toString(), urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcSimpleResponse.fromJson(data));
  }

  /**
   *
   * [request] - BackendRpcBackendConfig to send in this request
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcSimpleResponse> configSystem(BackendRpcBackendConfig request, {core.Map optParams}) {
    var url = "configSystem";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", body: request.toString(), urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcSimpleResponse.fromJson(data));
  }

  /**
   *
   * [request] - BackendRpcDeleteRequest to send in this request
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcSimpleResponse> deleteItem(BackendRpcDeleteRequest request, {core.Map optParams}) {
    var url = "deleteItem";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", body: request.toString(), urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcSimpleResponse.fromJson(data));
  }

  /**
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcBackendConfig> getSystemConfig({core.Map optParams}) {
    var url = "get_system_config";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcBackendConfig.fromJson(data));
  }

  /**
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcCustomerGroupResponseCollection> listCustomer({core.Map optParams}) {
    var url = "listCustomer";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcCustomerGroupResponseCollection.fromJson(data));
  }

  /**
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcUserCollectionResponse> listEmployee({core.Map optParams}) {
    var url = "listEmployee";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcUserCollectionResponse.fromJson(data));
  }

  /**
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcProductCollectionResponse> listProduct({core.Map optParams}) {
    var url = "listProduct";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcProductCollectionResponse.fromJson(data));
  }

  /**
   *
   * [request] - BackendRpcLoginRequest to send in this request
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcSimpleResponse> login(BackendRpcLoginRequest request, {core.Map optParams}) {
    var url = "login";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", body: request.toString(), urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcSimpleResponse.fromJson(data));
  }

  /**
   *
   * [request] - BackendRpcLoginRequest to send in this request
   *
   * [optParams] - Additional query parameters
   */
  async.Future<BackendRpcSimpleResponse> password_find(BackendRpcLoginRequest request, {core.Map optParams}) {
    var url = "password_find";
    var urlParams = new core.Map();
    var queryParams = new core.Map();

    var paramErrors = new core.List();
    if (optParams != null) {
      optParams.forEach((key, value) {
        if (value != null && queryParams[key] == null) {
          queryParams[key] = value;
        }
      });
    }

    if (!paramErrors.isEmpty) {
      throw new core.ArgumentError(paramErrors.join(" / "));
    }

    var response;
    response = this.request(url, "POST", body: request.toString(), urlParams: urlParams, queryParams: queryParams);
    return response
      .then((data) => new BackendRpcSimpleResponse.fromJson(data));
  }
}
