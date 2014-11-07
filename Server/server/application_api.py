import endpoints
from frontend_api import FrontendApi
from backend_api import BackendApi

APPLICATION = endpoints.api_server([FrontendApi, BackendApi])