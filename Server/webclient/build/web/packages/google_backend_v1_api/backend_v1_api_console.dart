library backend_v1_api.console;

import "package:google_oauth2_client/google_oauth2_console.dart" as oauth2;

import 'package:google_backend_v1_api/src/console_client.dart';

import "package:google_backend_v1_api/backend_v1_api_client.dart";

/** Mobile ordering system backend API v1 */
class Backend extends Client with ConsoleClient {

  /** OAuth Scope2: View your email address */
  static const String USERINFO_EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";

  final oauth2.OAuth2Console auth;

  Backend([oauth2.OAuth2Console this.auth]);
}
