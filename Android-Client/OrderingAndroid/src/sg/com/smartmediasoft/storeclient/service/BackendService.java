package sg.com.smartmediasoft.storeclient.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.AppPreference;
import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.UserController;
import sg.com.smartmediasoft.storeclient.database.DatabaseHelper;
import sg.com.smartmediasoft.storeclient.database.LocalCustomer;
import sg.com.smartmediasoft.storeclient.database.LocalOperate;
import sg.com.smartmediasoft.storeclient.database.LocalOperateItem;
import sg.com.smartmediasoft.storeclient.database.LocalOrder;
import sg.com.smartmediasoft.storeclient.database.LocalOrderItem;
import sg.com.smartmediasoft.storeclient.database.LocalOrderTemplate;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import sg.com.smartmediasoft.storeclient.database.StoreProvider;
import sg.com.smartmediasoft.storeclient.ui.orderdetail.OrderDetailActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.appspot.ruiqi_test.frontend.Frontend;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcAmendOrderRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcChangeOrderStateRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcChangeStorageRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcChangeTemplateRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcCustomerCollectionResponse;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcDeleteTemplateRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcGetStorageRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcLoginRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcLogoutRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcMakeOrderRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcMoreHistoryOrderRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcOrderCollectionResponse;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcOrderDaysResponse;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcOrderHistoryResponse;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcOrderResponse;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcProductCollectionResponse;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcRefreshHistoryOrderRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcReportRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcReportResponse;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSearchHistoryOrderRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSimpleRequest;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSimpleResponse;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSmsCustomer;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSmsOperate;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSmsOperateItem;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSmsOrder;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSmsOrderItem;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSmsOrderTemplate;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSmsProduct;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSmsStorage;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSmsStoreItem;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSmsTimeOperate;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcSmsUser;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcStorageResponse;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcTemplateResponse;
import com.appspot.ruiqi_test.frontend.model.FrontendRpcUserResponse;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.pinyin4android.PinyinUtil;

public class BackendService extends LocalIntentService {
	private static final String GROUP_KEY_MOS_MESSAGES = "group_key_mos_messages";

	private static final String ACTION_LOGIN = "sg.com.smartmediasoft.storeclient.service.LOGIN";
	private static final String ACTION_LOGOUT = "sg.com.smartmediasoft.storeclient.service.LOGOUT";

	private static final String ACTION_SYNC_PRODUCTS = "sg.com.smartmediasoft.storeclient.service.SYNC_PRODUCTS";
	private static final String ACTION_SYNC_CUSTOMERS = "sg.com.smartmediasoft.storeclient.service.SYNC_CUSTOMERS";
	private static final String ACTION_SYNC_ORDERS = "sg.com.smartmediasoft.storeclient.service.SYNC_ORDERS";
	private static final String ACTION_SYNC_ORDER_RECORDS = "sg.com.smartmediasoft.storeclient.service.SYNC_ORDER_RECORDS";
	private static final String ACTION_SYNC_CUSTOMER_STORE = "sg.com.smartmediasoft.storeclient.service.SYNC_CUSTOMER_STORE";
	private static final String ACTION_SYNC_SUPPLIER_STORE = "sg.com.smartmediasoft.storeclient.service.SYNC_SUPPLIER_STORE";

	private static final String ACTION_GET_ORDER_DAYS = "sg.com.smartmediasoft.storeclient.service.GET_ORDER_DAYS";
	private static final String ACTION_MAKE_ORDER = "sg.com.smartmediasoft.storeclient.service.MAKE_ORDER";
	private static final String ACTION_AMEND_ORDER = "sg.com.smartmediasoft.storeclient.service.AMEND_ORDER";
	private static final String ACTION_CONFIRM_ORDER = "sg.com.smartmediasoft.storeclient.service.CONFIRM_ORDER";
	private static final String ACTION_DELIVER_ORDER = "sg.com.smartmediasoft.storeclient.service.DELIVER_ORDER";
	private static final String ACTION_RECEIVE_ORDER = "sg.com.smartmediasoft.storeclient.service.RECEIVE_ORDER";
	private static final String ACTION_CANCLE_ORDER = "sg.com.smartmediasoft.storeclient.service.CANCLE_ORDER";

	private static final String ACTION_UPDATE_STORE = "sg.com.smartmediasoft.storeclient.service.UPDATE_STORE";
	private static final String ACTION_GENERATE_REPORT = "sg.com.smartmediasoft.storeclient.service.GENERATE_REPORT";

	private static final String ACTION_UPDATE_ORDER_TEMPLATE = "sg.com.smartmediasoft.storeclient.service.UPDATE_ORDER_TEMPLATE";
	private static final String ACTION_DELETE_ORDER_TEMPLATE = "sg.com.smartmediasoft.storeclient.service.DELETE_ORDER_TEMPLATE";

	private static final String ACTION_REFRESH_HISTORY_ORDER = "sg.com.smartmediasoft.storeclient.service.REFRESH_HISTORY_ORDER";
	private static final String ACTION_MORE_HISTORY_ORDER = "sg.com.smartmediasoft.storeclient.service.MORE_HISTORY_ORDER";
	private static final String ACTION_SEARCH_HISTORY_ORDER = "sg.com.smartmediasoft.storeclient.service.ACTION_SEARCH_HISTORY_ORDER";

	private static final String EXTRA_USERNAME = "username";
	private static final String EXTRA_PASSWORD = "password";
	private static final String EXTRA_DEVICETOKEN = "device_token";
	private static final String EXTRA_CUSTOMER_ID = "customer_id";
	private static final String EXTRA_START_ID = "start_id";
	private static final String EXTRA_END_ID = "end_id";
	private static final String EXTRA_QUERY_ID = "query_id";
	private static final String EXTRA_PRODUCTS = "products";
	private static final String EXTRA_AMOUNDS = "amounds";
	private static final String EXTRA_TARGET_TIME = "targetTime";
	private static final String EXTRA_ORDER_ID = "orderId";
	private static final String EXTRA_HUMAN_ID = "humanId";
	private static final String EXTRA_CHANGE_ID = "change_id";
	private static final String EXTRA_PRODUCT_ID = "product_id";
	private static final String EXTRA_CHANGE_FROM = "change_from";
	private static final String EXTRA_CHANGE_VALUE = "change_value";
	private static final String EXTRA_DESCRIPTION = "description";
	private static final String EXTRA_TEMPLATE_NAME = "template_name";
	private static final String EXTRA_CHECK_DATES = "check_dates";
	private static final String EXTRA_CUSTOMER_IDS = "customer_ids";
	private static final String EXTRA_STORE_CHANGE_ID = "store_change_id";
	private static final String EXTRA_RESET_VALUE = "reset";
	private static final String EXTRA_FIRST_TIME_LOGIN = "first_time_login";
	private static final String EXTRA_MESSAGE = "message";
	private static final String EXTRA_UNREGIST_SERVER = "unregist_server";

	private static final HttpTransport HTTP_TRANSPORT = AndroidHttp
			.newCompatibleTransport();
	private static final JsonFactory JSON_FACTORY = new GsonFactory();

	public static void logout(Context context, String logoutMessage,
			boolean unregistServer) {
		UserController controller = new UserController(context);
		String[] user = controller.getLoginUser();
		
		if (user != null && LocalUtils.hasNetwork(context)) {
			NotificationManagerCompat notificationManager = NotificationManagerCompat
					.from(context);
			notificationManager.cancelAll();
			
			controller.resetUsernamePassword();
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_LOGOUT);
			intent.putExtra(EXTRA_MESSAGE, logoutMessage);
			intent.putExtra(EXTRA_UNREGIST_SERVER, unregistServer);
			context.startService(intent);
		}
	}
	
	public static boolean login(Context context, String username,
			String password, String deviceToken) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_LOGIN);
			intent.putExtra(EXTRA_FIRST_TIME_LOGIN, true);
			intent.putExtra(EXTRA_USERNAME, username);
			intent.putExtra(EXTRA_PASSWORD, password);
			intent.putExtra(EXTRA_DEVICETOKEN, deviceToken);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean reLogin(Context context, String username,
			String password, String deviceToken) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_LOGIN);
			intent.putExtra(EXTRA_FIRST_TIME_LOGIN, false);
			intent.putExtra(EXTRA_USERNAME, username);
			intent.putExtra(EXTRA_PASSWORD, password);
			intent.putExtra(EXTRA_DEVICETOKEN, deviceToken);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}
	
	public static boolean startGetOrderDays(Context context) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_GET_ORDER_DAYS);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean searchHistoryOrder(Context context,
			String customerId, String queryId) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_SEARCH_HISTORY_ORDER);
			intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
			intent.putExtra(EXTRA_QUERY_ID, queryId);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean refreshHistoryOrder(Context context,
			String customerId, long startId) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_REFRESH_HISTORY_ORDER);
			intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
			intent.putExtra(EXTRA_START_ID, startId);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean moreHistoryOrder(Context context, String customerId,
			long endId) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_MORE_HISTORY_ORDER);
			intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
			intent.putExtra(EXTRA_END_ID, endId);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean generateReport(Context context, long[] dates,
			ArrayList<String> customers) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_GENERATE_REPORT);
			intent.putExtra(EXTRA_CHECK_DATES, dates);
			intent.putStringArrayListExtra(EXTRA_CUSTOMER_IDS, customers);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean getOrderStatistic(Context context, long[] dates,
			ArrayList<String> customers) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_SYNC_ORDER_RECORDS);
			intent.putExtra(EXTRA_CHECK_DATES, dates);
			intent.putStringArrayListExtra(EXTRA_CUSTOMER_IDS, customers);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean updateOrderTemplate(Context context,
			ArrayList<String> products, double[] amounds, String templateName) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_UPDATE_ORDER_TEMPLATE);
			intent.putStringArrayListExtra(EXTRA_PRODUCTS, products);
			intent.putExtra(EXTRA_AMOUNDS, amounds);
			intent.putExtra(EXTRA_TEMPLATE_NAME, templateName);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean deleteOrderTemplate(Context context,
			String templateName) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_DELETE_ORDER_TEMPLATE);
			intent.putExtra(EXTRA_TEMPLATE_NAME, templateName);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean syncCustomerStore(Context context, String customerId,
			long lastCheckTime, long storeChangeId) {
		if (LocalUtils.hasNetwork(context)) {
			if (System.currentTimeMillis() - lastCheckTime > AppPreference.CHECK_PERIOD) {
				Intent intent = new Intent(context, BackendService.class);
				intent.setAction(ACTION_SYNC_CUSTOMER_STORE);
				intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
				intent.putExtra(EXTRA_STORE_CHANGE_ID, storeChangeId);
				context.startService(intent);
				return true;
			}
		} else {
			LocalUtils.toastNoneNetwork(context);
		}

		return false;
	}

	public static boolean syncSupplierStore(Context context,
			long lastCheckTime, long storeChangeId) {
		if (LocalUtils.hasNetwork(context)) {
			if (System.currentTimeMillis() - lastCheckTime > AppPreference.CHECK_PERIOD) {
				Intent intent = new Intent(context, BackendService.class);
				intent.setAction(ACTION_SYNC_SUPPLIER_STORE);
				intent.putExtra(EXTRA_STORE_CHANGE_ID, storeChangeId);
				context.startService(intent);
				return true;
			}
		} else {
			LocalUtils.toastNoneNetwork(context);
		}

		return false;
	}

	public static boolean updateStore(Context context, long storeChangeId,
			String productId, double changeFrom, double changeValue,
			boolean reset) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_UPDATE_STORE);
			intent.putExtra(EXTRA_STORE_CHANGE_ID, storeChangeId);
			intent.putExtra(EXTRA_PRODUCT_ID, productId);
			intent.putExtra(EXTRA_CHANGE_FROM, changeFrom);
			intent.putExtra(EXTRA_CHANGE_VALUE, changeValue);
			intent.putExtra(EXTRA_RESET_VALUE, reset);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean syncProducts(Context context, boolean force) {
		if (LocalUtils.hasNetwork(context)) {
			AppPreference preference = new AppPreference(context);

			if (force || preference.isPorductNeedSync()) {
				Intent intent = new Intent(context, BackendService.class);
				intent.setAction(ACTION_SYNC_PRODUCTS);
				context.startService(intent);
				return true;
			}
		} else {
			LocalUtils.toastNoneNetwork(context);
		}

		return false;
	}

	public static boolean syncCustomer(Context context, boolean force) {
		if (LocalUtils.hasNetwork(context)) {
			AppPreference preference = new AppPreference(context);

			if (force || preference.isCustomerNeedSync()) {
				Intent intent = new Intent(context, BackendService.class);
				intent.setAction(ACTION_SYNC_CUSTOMERS);
				context.startService(intent);
				return true;
			}
		} else {
			LocalUtils.toastNoneNetwork(context);
		}

		return false;
	}

	public static boolean syncOrder(Context context, String customerId,
			boolean force) {
		if (LocalUtils.hasNetwork(context)) {
			AppPreference preference = new AppPreference(context);

			if (force || preference.isOrderNeedSync(customerId)) {
				Intent intent = new Intent(context, BackendService.class);
				intent.setAction(ACTION_SYNC_ORDERS);
				intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
				context.startService(intent);

				return true;
			}
		} else {
			LocalUtils.toastNoneNetwork(context);
		}

		return false;
	}

	public static boolean makeOrder(Context context,
			ArrayList<String> products, double[] amounds, Date targetTime,
			String customerId, String description) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_MAKE_ORDER);
			intent.putStringArrayListExtra(EXTRA_PRODUCTS, products);
			intent.putExtra(EXTRA_AMOUNDS, amounds);
			intent.putExtra(EXTRA_TARGET_TIME, targetTime.getTime());
			intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
			intent.putExtra(EXTRA_DESCRIPTION, description);
			context.startService(intent);

			Toast.makeText(context, R.string.toast_make_new_order_start,
					Toast.LENGTH_SHORT).show();
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean cancelOrder(Context context, String customerId,
			String orderId, String humanId, String description) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_CANCLE_ORDER);
			intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
			intent.putExtra(EXTRA_ORDER_ID, orderId);
			intent.putExtra(EXTRA_HUMAN_ID, humanId);
			intent.putExtra(EXTRA_DESCRIPTION, description);
			context.startService(intent);

			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean amendOrder(Context context,
			ArrayList<String> products, double[] amounds, Date targetTime,
			String orderId, long changeId, String customerId, String description) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_AMEND_ORDER);
			intent.putStringArrayListExtra(EXTRA_PRODUCTS, products);
			intent.putExtra(EXTRA_AMOUNDS, amounds);
			intent.putExtra(EXTRA_TARGET_TIME, targetTime.getTime());
			intent.putExtra(EXTRA_ORDER_ID, orderId);
			intent.putExtra(EXTRA_CHANGE_ID, changeId);
			intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
			intent.putExtra(EXTRA_DESCRIPTION, description);
			context.startService(intent);

			Toast.makeText(context, R.string.toast_amend_order_start,
					Toast.LENGTH_SHORT).show();
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean confirmOrder(Context context, String customerId,
			String orderId, String humanId, long changeId) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_CONFIRM_ORDER);
			intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
			intent.putExtra(EXTRA_ORDER_ID, orderId);
			intent.putExtra(EXTRA_HUMAN_ID, humanId);
			intent.putExtra(EXTRA_CHANGE_ID, changeId);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean deliverOrder(Context context, String customerId,
			String orderId, String humanId, long changeId) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_DELIVER_ORDER);
			intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
			intent.putExtra(EXTRA_ORDER_ID, orderId);
			intent.putExtra(EXTRA_HUMAN_ID, humanId);
			intent.putExtra(EXTRA_CHANGE_ID, changeId);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}

	public static boolean receiveOrder(Context context, String customerId,
			String orderId, String humanId, long changeId) {
		if (LocalUtils.hasNetwork(context)) {
			Intent intent = new Intent(context, BackendService.class);
			intent.setAction(ACTION_RECEIVE_ORDER);
			intent.putExtra(EXTRA_CUSTOMER_ID, customerId);
			intent.putExtra(EXTRA_ORDER_ID, orderId);
			intent.putExtra(EXTRA_HUMAN_ID, humanId);
			intent.putExtra(EXTRA_CHANGE_ID, changeId);
			context.startService(intent);
			return true;
		} else {
			LocalUtils.toastNoneNetwork(context);
			return false;
		}
	}
	
	private Bitmap mLargeIcon;
	private final IBinder mBinder = new BackendServiceBinder();

	public BackendService() {
		super(BackendService.class.getSimpleName());
	}
	
	public class BackendServiceBinder extends Binder {
		
		public void clearServiceWorks() {
			BackendService.this.clearServiceWorks();
		}
		
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mLargeIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		AppPreference preference = new AppPreference(this);
		Frontend frontendEndpoint = getFrontentEndpoint();
		UserController userController = new UserController(this);

		if (ACTION_SYNC_ORDERS.equals(action)) {
			startSyncOrder(intent, preference, frontendEndpoint, userController);
		} else if (ACTION_SYNC_PRODUCTS.equals(action)) {
			startSyncProduct(intent, preference, frontendEndpoint,
					userController);
		} else if (ACTION_SYNC_CUSTOMERS.equals(action)) {
			startSyncCustomer(intent, preference, frontendEndpoint,
					userController);
		} else if (ACTION_MAKE_ORDER.equals(action)) {
			startMakeOrder(intent, preference, frontendEndpoint, userController);
		} else if (ACTION_AMEND_ORDER.equals(action)) {
			startAmendOrder(intent, preference, frontendEndpoint,
					userController);
		} else if (ACTION_CONFIRM_ORDER.equals(action)
				|| ACTION_DELIVER_ORDER.equals(action)
				|| ACTION_RECEIVE_ORDER.equals(action)
				|| ACTION_CANCLE_ORDER.equals(action)) {
			startChangeOrderState(intent, preference, frontendEndpoint,
					userController);
		} else if (ACTION_UPDATE_STORE.equals(action)) {
			startUpdateStore(intent, preference, frontendEndpoint,
					userController);
		} else if (ACTION_UPDATE_ORDER_TEMPLATE.equals(action)) {
			startUpdateOrderTemplate(intent, preference, frontendEndpoint,
					userController);
		} else if (ACTION_DELETE_ORDER_TEMPLATE.equals(action)) {
			startDeleteOrderTemplate(intent, preference, frontendEndpoint,
					userController);
		} else if (ACTION_LOGIN.equals(action)) {
			startLogin(intent, preference, frontendEndpoint, userController);
		} else if (ACTION_SYNC_ORDER_RECORDS.equals(action)) {
			startGetOrderStatistic(intent, preference, frontendEndpoint,
					userController, false);
		} else if (ACTION_GENERATE_REPORT.equals(action)) {
			startGetOrderStatistic(intent, preference, frontendEndpoint,
					userController, true);
		} else if (ACTION_SYNC_CUSTOMER_STORE.equals(action)) {
			startSyncCustomerStore(intent, preference, frontendEndpoint,
					userController);
		} else if (ACTION_SYNC_SUPPLIER_STORE.equals(action)) {
			startSyncSupplierStore(intent, preference, frontendEndpoint,
					userController);
		} else if (ACTION_LOGOUT.equals(action)) {
			startLogout(intent, preference, frontendEndpoint, userController);
		} else if (ACTION_REFRESH_HISTORY_ORDER.equals(action)) {
			startSyncSyncHistoryOrder(intent, preference, frontendEndpoint,
					userController, true);
		} else if (ACTION_MORE_HISTORY_ORDER.equals(action)) {
			startSyncSyncHistoryOrder(intent, preference, frontendEndpoint,
					userController, false);
		} else if (ACTION_SEARCH_HISTORY_ORDER.equals(action)) {
			startSearchHistoryOrder(intent, preference, frontendEndpoint,
					userController);
		} else if (ACTION_GET_ORDER_DAYS.equals(action)) {
			startGetOrderDays(frontendEndpoint);
		}
	}

	private void startGetOrderDays(Frontend frontendEndpoint) {
		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle resultData = new Bundle();
		try {
			FrontendRpcOrderDaysResponse result = frontendEndpoint
					.getOrderDays().execute();

			returnCode = LocalUtils.CODE_SUCCESS;
			resultData
					.putLong(SMSBroadcastManager.DATA_INT_1, result == null ? 1 : result.getDays());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_GET_ORDER_DAYS_FINISHED,
					(int) returnCode, resultData);
		}
	}

	private void startSearchHistoryOrder(Intent intent,
			AppPreference preference, Frontend frontendEndpoint,
			UserController userController) {
		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle resultData = new Bundle();
		try {
			String token = userController.getDeviceToken();
			String[] user = userController.getLoginUser();

			if (!userCheck(token, user)) {
				return;
			}

			String customerId = intent.getStringExtra(EXTRA_CUSTOMER_ID);
			String queryId = intent.getStringExtra(EXTRA_QUERY_ID);

			FrontendRpcSearchHistoryOrderRequest request = new FrontendRpcSearchHistoryOrderRequest();
			request.setCustomerId(customerId);
			request.setToken(token);
			request.setQueryIds(Arrays.asList(queryId));
			FrontendRpcOrderCollectionResponse orderResults = frontendEndpoint
					.searchHistoryOrder(request).execute();

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = orderResults.getCode();
			resultData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? orderResults.getMessageCn()
					: orderResults.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS) {
				resultData.putBoolean(SMSBroadcastManager.DATA_HAS_DATA, true);
				processRemoteOrder(userController, orderResults.getItems(), -1, false, true,
						false);
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_SEARCH_ORDER_HISTORY_FINISHED,
					(int) returnCode, resultData);
		}
	}

	private void startSyncSyncHistoryOrder(Intent intent,
			AppPreference preference, Frontend frontendEndpoint,
			UserController userController, boolean refresh) {
		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		String resultAction = null;
		Bundle resultData = new Bundle();
		try {
			String token = userController.getDeviceToken();
			String[] user = userController.getLoginUser();

			if (!userCheck(token, user)) {
				return;
			}

			String customerId = intent.getStringExtra(EXTRA_CUSTOMER_ID);
			long startId = intent.getLongExtra(EXTRA_START_ID, -1);
			long endId = intent.getLongExtra(EXTRA_END_ID, -1);

			FrontendRpcOrderHistoryResponse historyResponse = null;
			if (refresh) {
				FrontendRpcRefreshHistoryOrderRequest request = new FrontendRpcRefreshHistoryOrderRequest();
				request.setToken(token);
				request.setStartId(startId);
				request.setCustomerId(customerId);
				resultAction = SMSBroadcastManager.ACTION_REFRESH_ORDER_HISTORY_FINISHED;
				historyResponse = frontendEndpoint.refreshHistoryOrder(request)
						.execute();
			} else {
				FrontendRpcMoreHistoryOrderRequest request = new FrontendRpcMoreHistoryOrderRequest();
				request.setToken(token);
				request.setEndId(endId);
				request.setCustomerId(customerId);
				resultAction = SMSBroadcastManager.ACTION_MORE_ORDER_HISTORY_FINISHED;
				historyResponse = frontendEndpoint.moreHistoryOrder(request)
						.execute();
			}

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = historyResponse.getCode();
			resultData.putString(SMSBroadcastManager.DATA_EXRTA_ID, customerId);
			resultData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? historyResponse.getMessageCn()
					: historyResponse.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS) {
				Long newStartId = historyResponse.getStartId();
				Long newEndId = historyResponse.getEndId();
				if (newStartId != null) {
					resultData.putLong(SMSBroadcastManager.DATA_INT_1,
							newStartId);
				}
				if (newEndId != null) {
					resultData
							.putLong(SMSBroadcastManager.DATA_INT_2, newEndId);
				}

				List<FrontendRpcSmsOrder> orders = historyResponse.getItems();
				if (orders != null && !orders.isEmpty()) {
					resultData.putBoolean(SMSBroadcastManager.DATA_HAS_DATA,
							true);
					processRemoteOrder(userController, orders, -1, false, true, false);
				} else {
					resultData.putBoolean(SMSBroadcastManager.DATA_HAS_DATA,
							false);
				}
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this, resultAction,
					(int) returnCode, resultData);
		}
	}

	private void startSyncOrder(Intent intent, AppPreference preference,
			Frontend frontendEndpoint, UserController userController) {
		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle resultData = new Bundle();
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();

			if (!userCheck(token, user)) {
				return;
			}

			String customerId = intent.getStringExtra(EXTRA_CUSTOMER_ID);
			long oldChangeId = preference.getOrderSyncKey(customerId);

			FrontendRpcSimpleRequest request = new FrontendRpcSimpleRequest();
			request.setChangeId(oldChangeId);
			request.setToken(token);
			FrontendRpcOrderCollectionResponse ordersResult = frontendEndpoint
					.listOrder(request).execute();

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = ordersResult.getCode();
			resultData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? ordersResult.getMessageCn()
					: ordersResult.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS) {
				List<FrontendRpcSmsOrder> orders = ordersResult.getItems();
				List<String> currentHumanIds = ordersResult
						.getOngoingOrderHumanids();
				oldChangeId = processRemoteOrder(userController, orders, oldChangeId, true,
						true, true);
				processFinishedOrder(userController, frontendEndpoint, token, currentHumanIds);
			} else if (ordersResult.getCode() == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}

			preference.resetOrderSync(customerId, oldChangeId);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_SYNC_ORDER_FINISHED,
					(int) returnCode, resultData);
		}
	}

	private void startSyncProduct(Intent intent, AppPreference preference,
			Frontend orderingEndpoint, UserController userController) {
		long oldChangeId = preference.getProductSyncKey();
		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle resultData = new Bundle();
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();

			if (!userCheck(token, user)) {
				return;
			}

			FrontendRpcSimpleRequest request = new FrontendRpcSimpleRequest();
			request.setChangeId(oldChangeId);
			request.setToken(token);
			FrontendRpcProductCollectionResponse result = orderingEndpoint
					.listProduct(request).execute();

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = result.getCode();
			resultData.putString(SMSBroadcastManager.DATA_MESSAGE,
					LocalUtils.isDeveceInChinese() ? result.getMessageCn()
							: result.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS) {
				List<FrontendRpcSmsProduct> products = result.getItems();
				if (products != null && !products.isEmpty()) {
					oldChangeId = processProductResult(products, oldChangeId);
				}
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}

			preference.resetProductSync(oldChangeId);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_SYNC_PRODUCT_FINISHED,
					(int) returnCode, resultData);
		}
	}

	private void startSyncCustomer(Intent intent, AppPreference preference,
			Frontend orderingEndpoint, UserController userController) {
		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle resultData = new Bundle();
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();
			long oldChangeId = preference.getCustomerSyncKey();

			if (!userCheck(token, user)) {
				return;
			}

			FrontendRpcSimpleRequest request = new FrontendRpcSimpleRequest();
			request.setChangeId(oldChangeId);
			request.setToken(token);
			FrontendRpcCustomerCollectionResponse customersResult = orderingEndpoint
					.listCustomer(request).execute();

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = customersResult.getCode();
			resultData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? customersResult.getMessageCn()
					: customersResult.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS) {
				if (userController.userIsCustomer()) {
					List<FrontendRpcSmsCustomer> remoteList = customersResult
							.getItems();
					if (remoteList != null && !remoteList.isEmpty()) {
						FrontendRpcSmsCustomer customer = remoteList.get(0);
						userController.resetCustomerName(customer.getNameCn(),
								customer.getNameEn());
					}
				} else if (userController.userIsDirector()) {
					List<FrontendRpcSmsCustomer> remoteList = customersResult
							.getItems();
					if (remoteList != null && !remoteList.isEmpty()) {
						FrontendRpcSmsCustomer customer = remoteList.get(0);
						userController.resetCustomerName(
								customer.getGroupNameCn(),
								customer.getGroupNameEn());
					}
				}

				oldChangeId = processCustomerResult(customersResult.getItems(),
						customersResult.getCustomerSet(), oldChangeId,
						userController.getUserRole());
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}

			preference.resetCustomerSync(oldChangeId);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_SYNC_CUSTOMER_FINISHED,
					(int) returnCode, resultData);
		}
	}

	private void startMakeOrder(Intent intent, AppPreference preference,
			Frontend orderingEndpoint, UserController userController) {
		String customerId = intent.getStringExtra(EXTRA_CUSTOMER_ID);
		List<String> products = intent.getStringArrayListExtra(EXTRA_PRODUCTS);
		double[] amounds = intent.getDoubleArrayExtra(EXTRA_AMOUNDS);
		long targetTimestamp = intent.getLongExtra(EXTRA_TARGET_TIME, 0);
		String description = intent.getStringExtra(EXTRA_DESCRIPTION);

		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle resultData = new Bundle();
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();

			if (!userCheck(token, user)) {
				return;
			}

			FrontendRpcMakeOrderRequest request = new FrontendRpcMakeOrderRequest();
			request.setCustomerId(customerId);
			request.setDescription(description);
			request.setProductAmounts(LocalUtils.arrayToList(amounds));
			request.setProductIds(products);
			request.setTargetDate(new DateTime(targetTimestamp + 8 * 60 * 60 * 1000, 0));
			request.setToken(token);
			FrontendRpcOrderResponse orderResult = orderingEndpoint.makeOrder(
					request).execute();

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = orderResult.getCode();
			resultData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? orderResult.getMessageCn()
					: orderResult.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS) {
				FrontendRpcSmsOrder item = orderResult.getItem();
				processRemoteOrder(userController, Arrays.asList(item), -1, false, true, false);
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_MAKE_ORDER_FINISHED,
					(int) returnCode, resultData);
		}
	}

	private void startAmendOrder(Intent intent, AppPreference preference,
			Frontend orderingEndpoint, UserController userController) {
		List<String> products = intent.getStringArrayListExtra(EXTRA_PRODUCTS);
		double[] amounds = intent.getDoubleArrayExtra(EXTRA_AMOUNDS);
		long targetTimestamp = intent.getLongExtra(EXTRA_TARGET_TIME, 0);
		String description = intent.getStringExtra(EXTRA_DESCRIPTION);
		String orderId = intent.getStringExtra(EXTRA_ORDER_ID);
		long changeId = intent.getLongExtra(EXTRA_CHANGE_ID, -1);

		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle returnData = new Bundle();
		returnData.putString(SMSBroadcastManager.DATA_EXRTA_ID, orderId);
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();
			if (!userCheck(token, user)) {
				return;
			}

			FrontendRpcAmendOrderRequest request = new FrontendRpcAmendOrderRequest();
			request.setChangeId(changeId);
			request.setDescription(description);
			request.setOrderId(orderId);
			request.setProductAmounts(LocalUtils.arrayToList(amounds));
			request.setProductIds(products);
			request.setTargetDate(new DateTime(targetTimestamp + 8 * 60 * 60 * 1000, 0));
			request.setToken(token);

			FrontendRpcOrderResponse orderResult = orderingEndpoint.amendOrder(
					request).execute();

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = orderResult.getCode();
			returnData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? orderResult.getMessageCn()
					: orderResult.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS
					|| returnCode == LocalUtils.CODE_FAILED_DATA_ALREADY_CHANGE) {
				processRemoteOrder(userController, Arrays.asList(orderResult.getItem()), -1,
						false, true, false);
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_EDIT_ORDER_FINISHED,
					(int) returnCode, returnData);
		}
	}

	private void startChangeOrderState(Intent intent, AppPreference preference,
			Frontend orderingEndpoint, UserController userController) {
		String action = intent.getAction();
		long changeId = intent.getLongExtra(EXTRA_CHANGE_ID, 0);
		String orderId = intent.getStringExtra(EXTRA_ORDER_ID);
		String humanId = intent.getStringExtra(EXTRA_HUMAN_ID);
		String description = intent.getStringExtra(EXTRA_DESCRIPTION);

		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle returnData = new Bundle();
		returnData.putString(SMSBroadcastManager.DATA_EXRTA_ID, humanId);
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();
			if (!userCheck(token, user)) {
				return;
			}

			FrontendRpcChangeOrderStateRequest request = new FrontendRpcChangeOrderStateRequest();
			request.setChangeId(changeId);
			request.setDescription(description);
			request.setOrderId(orderId);
			request.setToken(token);

			FrontendRpcOrderResponse orderResult = null;
			if (ACTION_CONFIRM_ORDER.equals(action)) {
				orderResult = orderingEndpoint.confirmOrder(request).execute();
			} else if (ACTION_DELIVER_ORDER.equals(action)) {
				orderResult = orderingEndpoint.deliverOrder(request).execute();
			} else if (ACTION_RECEIVE_ORDER.equals(action)) {
				orderResult = orderingEndpoint.receiveOrder(request).execute();
			} else if (ACTION_CANCLE_ORDER.equals(action)) {
				orderResult = orderingEndpoint.cancelOrder(request).execute();
			}

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = orderResult.getCode();
			returnData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? orderResult.getMessageCn()
					: orderResult.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS
					|| returnCode == LocalUtils.CODE_FAILED_DATA_ALREADY_CHANGE) {
				if (ACTION_CANCLE_ORDER.equals(action)
						|| ACTION_RECEIVE_ORDER.equals(action)) {
					getContentResolver()
							.delete(LocalOrder.CONTENT_URI,
									LocalOrder.ITEM_SELECTION,
									new String[] { orderId });
					processRemoteOrder(userController, Arrays.asList(orderResult.getItem()),
							changeId, false, false, false);
				} else {
					processRemoteOrder(userController, Arrays.asList(orderResult.getItem()),
							changeId, false, true, false);
				}
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_CHANGE_ORDER_STATE_FINISHED,
					(int) returnCode, returnData);
		}
	}

	private void startUpdateStore(Intent intent, AppPreference preference,
			Frontend orderingEndpoint, UserController userController) {
		String productId = intent.getStringExtra(EXTRA_PRODUCT_ID);
		double changeFrom = intent.getDoubleExtra(EXTRA_CHANGE_FROM, 0);
		double changeValue = intent.getDoubleExtra(EXTRA_CHANGE_VALUE, 0);
		boolean reset = intent.getBooleanExtra(EXTRA_RESET_VALUE, false);
		String customerId = intent.getStringExtra(EXTRA_CUSTOMER_ID);

		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle returnData = new Bundle();
		returnData.putString(SMSBroadcastManager.DATA_EXRTA_ID, productId);
		returnData.putDouble(SMSBroadcastManager.DATA_EXTRA_VALUE, changeValue);
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();
			if (!userCheck(token, user)) {
				return;
			}

			FrontendRpcChangeStorageRequest request = new FrontendRpcChangeStorageRequest();
			request.setChangeFrom(changeFrom);
			request.setChangeValue(changeValue);
			request.setCustomerId(customerId);
			request.setProductId(productId);
			request.setReset(reset);
			request.setToken(token);

			FrontendRpcStorageResponse storeResult = orderingEndpoint
					.changeStorage(request).execute();

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = storeResult.getCode();
			returnData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? storeResult.getMessageCn()
					: storeResult.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS
					|| returnCode == LocalUtils.CODE_FAILED_DATA_ALREADY_CHANGE) {
				if (userController.userIsSupplier()) {
					processSupplierStoreUpdate(userController,
							storeResult.getItem());
				} else {
					processCustomerStoreUpdate(userController.getCustomerId(),
							storeResult.getItem());
				}
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_UPDATE_STORE_FINISHED,
					(int) returnCode, returnData);
		}
	}

	private void startUpdateOrderTemplate(Intent intent,
			AppPreference preference, Frontend orderingEndpoint,
			UserController userController) {
		String templateName = intent.getStringExtra(EXTRA_TEMPLATE_NAME);
		List<String> products = intent.getStringArrayListExtra(EXTRA_PRODUCTS);
		double[] amounds = intent.getDoubleArrayExtra(EXTRA_AMOUNDS);

		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle returnData = new Bundle();
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();
			if (!userCheck(token, user)) {
				return;
			}

			FrontendRpcChangeTemplateRequest request = new FrontendRpcChangeTemplateRequest();
			request.setProductAmounts(LocalUtils.arrayToList(amounds));
			request.setProductIds(products);
			request.setTemplateName(templateName);
			request.setToken(token);

			FrontendRpcTemplateResponse orderTemplateResult = orderingEndpoint
					.changeTemplate(request).execute();

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = orderTemplateResult.getCode();
			returnData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? orderTemplateResult.getMessageCn()
					: orderTemplateResult.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS) {
				FrontendRpcSmsOrderTemplate remoteTemplate = orderTemplateResult
						.getItem();
				processRemoteOrderTemplate(remoteTemplate);
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_UPDATE_ORDER_TEMPLATE_FINISHED,
					(int) returnCode, returnData);
		}
	}

	private void startDeleteOrderTemplate(Intent intent,
			AppPreference preference, Frontend orderingEndpoint,
			UserController userController) {
		String tempalteName = intent.getStringExtra(EXTRA_TEMPLATE_NAME);

		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle returnData = new Bundle();
		returnData.putString(SMSBroadcastManager.DATA_EXRTA_ID, tempalteName);
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();
			if (!userCheck(token, user)) {
				return;
			}

			FrontendRpcDeleteTemplateRequest request = new FrontendRpcDeleteTemplateRequest();
			request.setTemplateName(tempalteName);
			request.setToken(token);
			FrontendRpcSimpleResponse orderTemplateResult = orderingEndpoint
					.deleteTemplate(request).execute();

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = orderTemplateResult.getCode();
			returnData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? orderTemplateResult.getMessageCn()
					: orderTemplateResult.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS) {
				getContentResolver().delete(LocalOrderTemplate.CONTENT_URI,
						LocalOrderTemplate.ITEM_SELECTION,
						new String[] { tempalteName });
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_UPDATE_ORDER_TEMPLATE_FINISHED,
					(int) returnCode, returnData);
		}
	}

	private void startGetOrderStatistic(Intent intent,
			AppPreference preference, Frontend orderingEndpoint,
			UserController userController, boolean generateReport) {
		long[] dateValues = intent.getLongArrayExtra(EXTRA_CHECK_DATES);
		ArrayList<String> customerIds = intent
				.getStringArrayListExtra(EXTRA_CUSTOMER_IDS);

		String reportAction = null;
		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle retuenData = new Bundle();
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();
			if (!userCheck(token, user)) {
				return;
			}

			List<DateTime> dates = new ArrayList<DateTime>();
			for (long value : dateValues) {
				dates.add(new DateTime(value + 8 * 60 * 60 * 1000, 0));
			}

			FrontendRpcReportRequest request = new FrontendRpcReportRequest();
			request.setCustomerIds(customerIds);
			request.setDates(dates);
			request.setToken(token);

			if (generateReport) {
				reportAction = SMSBroadcastManager.ACTION_GENERATE_ORDER_REPORT_FINISHED;
				FrontendRpcSimpleResponse reportResult = orderingEndpoint
						.generateReport(request).execute();

				if (!loginAccountCheck(user, userController)) {
					return;
				}

				returnCode = reportResult.getCode();
				retuenData.putString(
						SMSBroadcastManager.DATA_MESSAGE,
						LocalUtils.isDeveceInChinese() ? reportResult
								.getMessageCn() : reportResult.getMessageEn());

				if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
					BackendService.logout(this, getString(R.string.text_login_exparted), false);
				}
			} else {
				reportAction = SMSBroadcastManager.ACTION_SYNC_ORDER_RECORD_FINISHED;
				FrontendRpcReportResponse summeryResult = orderingEndpoint
						.getOrderSummery(request).execute();

				if (!loginAccountCheck(user, userController)) {
					return;
				}

				returnCode = summeryResult.getCode();
				retuenData.putString(
						SMSBroadcastManager.DATA_MESSAGE,
						LocalUtils.isDeveceInChinese() ? summeryResult
								.getMessageCn() : summeryResult.getMessageEn());

				if (returnCode == LocalUtils.CODE_SUCCESS) {
					List<FrontendRpcSmsOrderItem> items = summeryResult
							.getItems();
					if (items != null) {
						ArrayList<LocalOrderItem> localOrderItems = new ArrayList<LocalOrderItem>();
						for (FrontendRpcSmsOrderItem item : items) {
							localOrderItems.add(orderItemRemoteToLocal(null,
									item));
						}

						retuenData.putParcelableArrayList(
								SMSBroadcastManager.DATA_PARCELABLE_LIST,
								localOrderItems);
					}
				} else if (summeryResult.getCode() == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
					BackendService.logout(this, getString(R.string.text_login_exparted), false);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this, reportAction,
					(int) returnCode, retuenData);
		}
	}

	private void startLogin(Intent intent, AppPreference preference,
			Frontend orderingEndpoint, UserController userController) {
		String username = intent.getStringExtra(EXTRA_USERNAME);
		String password = intent.getStringExtra(EXTRA_PASSWORD);
		String token = intent.getStringExtra(EXTRA_DEVICETOKEN);
		boolean firstTimeLogin = intent.getBooleanExtra(EXTRA_FIRST_TIME_LOGIN,
				false);

		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle returnData = new Bundle();
		try {
			FrontendRpcLoginRequest request = new FrontendRpcLoginRequest();
			request.setPassword(password);
			request.setPlatform("ANDROID");
			request.setUsername(username);
			request.setToken(token);
			FrontendRpcUserResponse userResult = orderingEndpoint
					.login(request).execute();

			returnCode = userResult.getCode();
			returnData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? userResult.getMessageCn()
					: userResult.getMessageEn());

			if (userResult.getCode() == LocalUtils.CODE_SUCCESS) {
				FrontendRpcSmsUser loginUser = userResult.getUser();
				userController.userLogin(loginUser.getRole(),
						loginUser.getDisplayName(), loginUser.getParentId(),
						loginUser.getCompanyNameEn(),
						loginUser.getCompanyNameCn(), loginUser.getLoginName(),
						loginUser.getPassword(), loginUser.getCanCancelOrder(),
						loginUser.getCanChangeStorage(),
						loginUser.getCanConfirmOrder(),
						loginUser.getCanDeliverOrder(),
						loginUser.getCanEditOrder(),
						loginUser.getCanEditTemplate(),
						loginUser.getCanCreateOrder(),
						loginUser.getCanReceiveOrder(),
						loginUser.getCanViewHistory(),
						loginUser.getCanViewStatistic());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					firstTimeLogin ? SMSBroadcastManager.ACTION_LOGIN_FINISHED
							: SMSBroadcastManager.ACTION_USER_CONFIG_CHANGED,
					(int) returnCode, returnData);
		}
	}

	private void startSyncCustomerStore(Intent intent,
			AppPreference preference, Frontend orderingEndpoint,
			UserController userController) {
		long storeChangeId = intent.getLongExtra(EXTRA_STORE_CHANGE_ID, -1);
		if (storeChangeId == 0) {
			storeChangeId = -1;
		}
		String customerId = intent.getStringExtra(EXTRA_CUSTOMER_ID);

		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle returnData = new Bundle();
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();

			if (!userCheck(token, user)) {
				return;
			}

			FrontendRpcGetStorageRequest request = new FrontendRpcGetStorageRequest();
			request.setChangeId(storeChangeId);
			request.setCustomerId(customerId);
			request.setToken(token);
			FrontendRpcStorageResponse storeResult = orderingEndpoint
					.getStorage(request).execute();

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = storeResult.getCode();
			returnData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? storeResult.getMessageCn()
					: storeResult.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS
					|| returnCode == LocalUtils.CODE_FAILED_DATA_ALREADY_CHANGE) {
				processCustomerStoreUpdate(customerId, storeResult.getItem());
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_SYNC_CUSTOMER_STORE_FINISHED,
					(int) returnCode, returnData);
		}
	}

	private void startSyncSupplierStore(Intent intent,
			AppPreference preference, Frontend orderingEndpoint,
			UserController userController) {
		long storeChangeId = intent.getLongExtra(EXTRA_STORE_CHANGE_ID, -1);
		if (storeChangeId == 0) {
			storeChangeId = -1;
		}

		long returnCode = LocalUtils.CODE_FAILED_UNKNOW;
		Bundle returnData = new Bundle();
		try {
			String[] user = userController.getLoginUser();
			String token = userController.getDeviceToken();
			if (!userCheck(token, user)) {
				return;
			}

			FrontendRpcGetStorageRequest request = new FrontendRpcGetStorageRequest();
			request.setChangeId(storeChangeId);
			request.setToken(token);
			FrontendRpcStorageResponse storeResult = orderingEndpoint
					.getStorage(request).execute();

			if (!loginAccountCheck(user, userController)) {
				return;
			}

			returnCode = storeResult.getCode();
			returnData.putString(SMSBroadcastManager.DATA_MESSAGE, LocalUtils
					.isDeveceInChinese() ? storeResult.getMessageCn()
					: storeResult.getMessageEn());

			if (returnCode == LocalUtils.CODE_SUCCESS
					|| returnCode == LocalUtils.CODE_FAILED_DATA_ALREADY_CHANGE) {
				processSupplierStoreUpdate(userController,
						storeResult.getItem());
			} else if (returnCode == LocalUtils.CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED) {
				BackendService.logout(this, getString(R.string.text_login_exparted), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager.broadcastAction(this,
					SMSBroadcastManager.ACTION_SYNC_SUPPLIER_STORE_FINISHED,
					(int) returnCode, returnData);
		}
	}

	private void startLogout(Intent intent, AppPreference preference,
			Frontend orderingEndpoint, UserController userController) {
		Bundle returnData = new Bundle();
		returnData.putString(SMSBroadcastManager.DATA_MESSAGE,
				intent.getStringExtra(EXTRA_MESSAGE));
		try {
			String token = userController.getDeviceToken();
			userController.clearAll();
			
			DatabaseHelper.SQLiteHelper helper = new DatabaseHelper.SQLiteHelper(
					this, StoreProvider.DATABASE_NAME);
			SQLiteDatabase database = helper.getWritableDatabase();
			database.delete(LocalOrderTemplate.TABLE_NAME, null, null);
			database.delete(LocalCustomer.TABLE_NAME, null, null);
			database.delete(LocalOperate.TABLE_NAME, null, null);
			database.delete(LocalOperateItem.TABLE_NAME, null, null);
			database.delete(LocalOrder.TABLE_NAME, null, null);
			database.delete(LocalProduct.TABLE_NAME, null, null);
			database.delete(LocalOrderItem.TABLE_NAME, null, null);
			
			if (intent.getBooleanExtra(EXTRA_UNREGIST_SERVER, false)) {
				FrontendRpcLogoutRequest request = new FrontendRpcLogoutRequest();
				request.setToken(token);
				orderingEndpoint.logout(request).execute();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			SMSBroadcastManager
					.broadcastAction(this,
							SMSBroadcastManager.ACTION_LOGOUT_FINISHED, 200,
							returnData);
		}
	}

	private void processSupplierStoreUpdate(UserController userController,
			FrontendRpcSmsStorage supplierStore) {
		if (supplierStore == null) {
			return;
		}

		userController.resetSupplierStorage(supplierStore.getChangeId(),
				storagesToString(supplierStore.getItems()));
	}

	private void processCustomerStoreUpdate(String customreId,
			FrontendRpcSmsStorage store) {
		if (store == null) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(LocalCustomer.STORE_LAST_CHECK_TIME,
				System.currentTimeMillis());
		values.put(LocalCustomer.STORE_CHANGE_ID,
				store == null ? -1 : store.getChangeId());
		values.put(LocalCustomer.STORE_STORAGE_MAP, (store == null || store.getItems() == null) ? null
				: storagesToString(store.getItems()));

		getContentResolver().update(LocalCustomer.CONTENT_URI, values,
				LocalCustomer.ITEM_SELECTION, new String[] { customreId });
	}

	private long processRemoteOrder(UserController userController, List<FrontendRpcSmsOrder> remoteOrders,
			long oldchangeId, boolean sendNotify, boolean perisit,
			boolean remoteChange) {
		boolean firstTimeSetup = userController.getIsFirstTimeSetup();
		ContentResolver resolver = getContentResolver();

		if (remoteOrders == null || remoteOrders.isEmpty()) {
			return oldchangeId;
		}

		for (FrontendRpcSmsOrder remoteOrder : remoteOrders) {
			if (remoteOrder.getChangeId() > oldchangeId) {
				oldchangeId = remoteOrder.getChangeId();
			}

			LocalOrder oldOrder = LocalOrder.getOrderById(this,
					remoteOrder.getId());

			if (oldOrder != null) {
				if (oldOrder.mChangeId >= remoteOrder.getChangeId()) {
					continue;
				}

				resolver.delete(LocalOrder.CONTENT_URI,
						LocalOrder.ITEM_SELECTION,
						new String[] { oldOrder.mUUID });
			}

			ArrayList<LocalOrderItem> newOrderOrderItems = new ArrayList<LocalOrderItem>();
			ArrayList<LocalOperate> newOrderOperates = new ArrayList<LocalOperate>();

			LocalOrder newOrder = orderRemoteToLocal(remoteOrder);
			if (remoteChange) {
				newOrder.mRead = 0;
			}

			String oldState = null;
			if (perisit) {
				ContentValues values = new ContentValues();
				newOrder.toContentValues(values);
				resolver.insert(LocalOrder.CONTENT_URI, values);
			}

			List<FrontendRpcSmsOrderItem> remoteRederItems = remoteOrder
					.getOrderItems();
			for (FrontendRpcSmsOrderItem remoteOrderItem : remoteRederItems) {
				LocalOrderItem orderItem = orderItemRemoteToLocal(
						newOrder.mUUID, remoteOrderItem);
				newOrderOrderItems.add(orderItem);

				if (perisit) {
					ContentValues values = new ContentValues();
					orderItem.toContentValues(values);
					resolver.insert(LocalOrderItem.CONTENT_URI, values);
				}
			}

			List<FrontendRpcSmsOperate> operates = remoteOrder.getOperates();
			for (FrontendRpcSmsOperate remote : operates) {
				LocalOperate localOperate = operateRemoteToLocal(
						newOrder.mUUID, remote);

				if (perisit) {
					ContentValues values = new ContentValues();
					localOperate.toContentValues(values);
					resolver.insert(LocalOperate.CONTENT_URI, values);
				}

				List<FrontendRpcSmsOperateItem> remoteOperateItems = remote
						.getOperateItems();
				if (remoteOperateItems != null) {
					localOperate.mItems = new ArrayList<LocalOperateItem>();

					for (FrontendRpcSmsOperateItem remoteItem : remoteOperateItems) {
						LocalOperateItem localItem = operateItemRemoteToLocal(newOrder.mUUID,
								localOperate.mUUID, remoteItem);
						localOperate.mItems.add(localItem);

						if (perisit) {
							ContentValues values = new ContentValues();
							localItem.toContentValues(values);
							resolver.insert(LocalOperateItem.CONTENT_URI,
									values);
						}
					}
				}

				newOrderOperates.add(localOperate);
			}
			
			boolean defaultNotify = true;
			if (userController.userIsSupplier() && !orderHasSystemAutoAmend(newOrderOperates)) {
				defaultNotify = false;
			}

			if (defaultNotify && needSend(firstTimeSetup, sendNotify)) {
				Intent intent = new Intent(this, OrderDetailActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(OrderDetailActivity.EXTRA_LOCAL_ORDER,
						(Parcelable) newOrder);
				intent.putParcelableArrayListExtra(
						OrderDetailActivity.EXTRA_LOCAL_ORDER_OPERATES,
						newOrderOperates);
				intent.putParcelableArrayListExtra(
						OrderDetailActivity.EXTRA_LOCAL_ORDER_ORDERITENS,
						newOrderOrderItems);
				intent.putExtra(OrderDetailActivity.EXTRA_FROM_NOTIFICATION,
						true);

				sendNotification(this, newOrder, newOrderOperates, oldState,
						intent);
			}

			if (LocalUtils.ORDER_STATE_CANCELED.equals(newOrder.mState)
					|| LocalUtils.ORDER_STATE_RECEIVED
							.equals(newOrder.mState)) {
				Bundle data = new Bundle();
				data.putString(SMSBroadcastManager.DATA_EXRTA_ID,
						newOrder.mHumanId);
				data.putInt(SMSBroadcastManager.EXTRA_RESULT_CODE,
						LocalUtils.CODE_SUCCESS);
				data.putParcelable(OrderDetailActivity.EXTRA_LOCAL_ORDER,
						(Parcelable) newOrder);
				data.putParcelableArrayList(
						OrderDetailActivity.EXTRA_LOCAL_ORDER_OPERATES,
						newOrderOperates);
				data.putParcelableArrayList(
						OrderDetailActivity.EXTRA_LOCAL_ORDER_ORDERITENS,
						newOrderOrderItems);
				data.putBoolean(OrderDetailActivity.EXTRA_TOAST, false);

				SMSBroadcastManager.broadcastAction(this,
						SMSBroadcastManager.ACTION_CHANGE_ORDER_STATE_FINISHED,
						LocalUtils.CODE_SUCCESS, data);
			}
		}
		
		userController.setupFinished();

		return oldchangeId;
	}
	
	private boolean orderHasSystemAutoAmend(ArrayList<LocalOperate> operates) {
		for (LocalOperate operate : operates) {
			if (LocalUtils.OPERATE_ACTION_SYSTEM_AMEND.equals(operate.mOperateAction)) {
				return true;
			}
		}
		
		return false;
	}

	private void processFinishedOrder(UserController userController, Frontend orderingEndpoint, String token,
			List<String> currentOrderIds) throws IOException {
		ContentResolver resolver = getContentResolver();
		Cursor cursor = null;
		try {
			cursor = resolver.query(
					LocalOrder.CONTENT_URI,
					new String[] { LocalOrder.HUMAN_ID },
					LocalOrder.STATE + " IN (?,?,?)",
					new String[] {LocalUtils.ORDER_STATE_PENDING,
								  LocalUtils.ORDER_STATE_CONFIRMED,
								  LocalUtils.ORDER_STATE_DELIVERED},
					null);
			if (cursor != null && cursor.moveToFirst()) {
				List<String> finishedIds = new ArrayList<String>();
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					String humanId = cursor.getString(0);
					if (!hasHumanId(currentOrderIds, humanId)) {
						resolver.delete(LocalOrder.CONTENT_URI,
								LocalOrder.HUMAN_ID_SELECTION,
								new String[] { humanId });
						finishedIds.add(humanId);
					}
				}

				if (!finishedIds.isEmpty()) {
					FrontendRpcSearchHistoryOrderRequest request = new FrontendRpcSearchHistoryOrderRequest();
					request.setQueryIds(finishedIds);
					request.setToken(token);
					FrontendRpcOrderCollectionResponse result = orderingEndpoint
							.searchHistoryOrder(request).execute();
					if (result != null
							&& result.getCode() == LocalUtils.CODE_SUCCESS) {
						processRemoteOrder(userController, result.getItems(), -1, true, false,
								false);
					}
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	private boolean needSend(boolean firstStarup, boolean sendNotify) {
		return !firstStarup && sendNotify;
	}

	private void sendNotification(Context context, LocalOrder notifyOrder,
			ArrayList<LocalOperate> orderOperates, String oldState,
			Intent intent) {
		String title = "";
		String message = "";
		
		if (orderOperates == null || orderOperates.isEmpty()) {
			return;
		}
		
		if (orderOperates.size() == 1) {
			LocalOperate operate = orderOperates.get(0);
			title = context.getString(R.string.notify_title_order_new,
					notifyOrder.mHumanId);
			message = getString(R.string.text_prefix_operator,
					operate.mOperatorName);
		} else {
			LocalOperate lastOperate = orderOperates
					.get(orderOperates.size() - 1);
			if (LocalUtils.OPERATE_ACTION_SUPPLIER_CANCEL
					.equals(lastOperate.mOperateAction)
					|| LocalUtils.OPERATE_ACTION_CUSTOMER_CANCEL
							.equals(lastOperate.mOperateAction)) {
				title = context.getString(R.string.notify_title_order_canceled,
						notifyOrder.mHumanId);
				message = getString(R.string.text_prefix_operator,
						lastOperate.mOperatorName);
			} else if (LocalUtils.OPERATE_ACTION_RECEIVE
					.equals(lastOperate.mOperateAction)) {
				title = context.getString(R.string.notify_title_order_received,
						notifyOrder.mHumanId);
				message = getString(R.string.text_prefix_operator,
						lastOperate.mOperatorName);
			} else if (LocalUtils.ORDER_STATE_DELIVERED
					.equals(lastOperate.mOperateAction)) {
				title = context.getString(
						R.string.notify_title_order_delivered,
						notifyOrder.mHumanId);
				message = getString(R.string.text_prefix_operator,
						lastOperate.mOperatorName);
			} else if (LocalUtils.OPERATE_ACTION_CONFIRM
					.equals(lastOperate.mOperateAction)) {
				title = context.getString(
						R.string.notify_title_order_confirmed,
						notifyOrder.mHumanId);
				message = getString(R.string.text_prefix_operator,
						lastOperate.mOperatorName);
			} else {
				title = context.getString(R.string.notify_title_order_amend,
						notifyOrder.mHumanId);
				message = getString(R.string.text_prefix_operator,
						lastOperate.mOperatorName);
			}
		}

		int notifyId = LocalUtils.humanIdToNotificationId(notifyOrder.mHumanId);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
				notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Build the notification, setting the group appropriately
		Notification notif = new NotificationCompat.Builder(context)
				.setAutoCancel(true)
				.setContentTitle(title)
				.setContentText(message)
				.setSmallIcon(android.R.drawable.ic_dialog_email)
				.setLargeIcon(mLargeIcon)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setContentIntent(resultPendingIntent).build();

		// Issue the notification
		NotificationManagerCompat notificationManager = NotificationManagerCompat
				.from(this);
		notificationManager.notify(notifyId, notif);
	}

	private LocalOperate findOperate(List<LocalOperate> operates, String action) {
		int size = operates.size();
		for (int i = size - 1; i >= 0; i--) {
			LocalOperate operate = operates.get(i);
			if (operate.mOperateAction.equals(action)) {
				return operate;
			}
		}

		return null;
	}

	private boolean hasHumanId(List<String> currentIds, String humanId) {
		if (currentIds == null) {
			return false;
		}

		for (String checkId : currentIds) {
			if (checkId.equals(humanId)) {
				return true;
			}
		}

		return false;
	}

	private long processProductResult(List<FrontendRpcSmsProduct> remoteList,
			long oldChangeId) {
		ContentResolver resolver = getContentResolver();

		for (FrontendRpcSmsProduct remoteProduct : remoteList) {
			if (oldChangeId < remoteProduct.getChangeId()) {
				oldChangeId = remoteProduct.getChangeId();
			}

			LocalProduct localProduct = productRemoteToLocal(remoteProduct);

			ContentValues values = new ContentValues();
			localProduct.toContentValues(values);

			int result = resolver.update(LocalProduct.CONTENT_URI, values,
					LocalProduct.ITEM_SELECTION,
					new String[] { localProduct.mUUID });
			if (result <= 0) {
				resolver.insert(LocalProduct.CONTENT_URI, values);
			}
		}

		return oldChangeId;
	}

	private LocalOrder orderRemoteToLocal(FrontendRpcSmsOrder remote) {
		LocalOrder newOrder = new LocalOrder();
		newOrder.mUUID = remote.getId();
		newOrder.mCustomerId = remote.getCustomerId();
		newOrder.mState = remote.getState();
		newOrder.mTargetDate = remote.getTargetDate();
		newOrder.mCreateDate = remote.getCreateDate();
		newOrder.mConfirmDate = remote.getConfirmDate() == null ? 0 : remote
				.getConfirmDate();
		newOrder.mDeliverDate = remote.getDeliverDate() == null ? 0 : remote
				.getDeliverDate();
		newOrder.mFinishDate = remote.getFinishDate() == null ? 0 : remote
				.getFinishDate();
		newOrder.mChangeId = remote.getChangeId();
		newOrder.mHumanId = remote.getHumanId();
		newOrder.mHighLight = checkHeight(remote);
		return newOrder;
	}

	private int checkHeight(FrontendRpcSmsOrder remote) {
		if (LocalUtils.ORDER_STATE_PENDING.equals(remote.getState())
				|| LocalUtils.ORDER_STATE_CONFIRMED.equals(remote.getState())) {
			for (FrontendRpcSmsOperate operate : remote.getOperates()) {
				if (LocalUtils.OPERATE_ACTION_SYSTEM_AMEND.equals(operate
						.getAction())) {
					return 1;
				}
			}
		}

		return 0;
	}

	private LocalOrderItem orderItemRemoteToLocal(String orderId,
			FrontendRpcSmsOrderItem remote) {
		LocalOrderItem local = new LocalOrderItem();
		local.mOrderId = orderId;
		local.mProductId = remote.getProductId();
		local.mAmount = remote.getAmount();
		local.mUnitName = remote.getUnitName();
		local.mDescimalUnit = remote.getDescimalUnit() ? 1 : 0;
		local.mNameCN = remote.getNameCn();
		local.mNameEN = remote.getNameEn();
		local.mImageUrl = remote.getImageUrl();
		return local;
	}

	private LocalOperate operateRemoteToLocal(String orderId,
			FrontendRpcSmsOperate remote) {
		LocalOperate local = new LocalOperate();
		local.mOrderUUID = orderId;
		local.mDate = remote.getDate();
		local.mDescription = remote.getDescription();
		local.mOperateAction = remote.getAction();
		local.mOperatorName = remote.getOperator();
		local.mUUID = remote.getDate();
		FrontendRpcSmsTimeOperate timeOperate = remote.getOperateTime();
		if (timeOperate != null) {
			local.mTargetTimeFrom = timeOperate.getTimeFrom() == null ? 0
					: timeOperate.getTimeFrom();
			local.mTargetTimeTo = timeOperate.getTimeTo();
		}

		return local;
	}

	private LocalOperateItem operateItemRemoteToLocal(String orderId, long operateId,
			FrontendRpcSmsOperateItem remote) {
		LocalOperateItem local = new LocalOperateItem();
		local.mOrderId = orderId;
		local.mOperateId = operateId;
		local.mUnitName = remote.getUnitName();
		local.mNameCN = remote.getNameCn();
		local.mNameEN = remote.getNameEn();
		local.mImageUrl = remote.getImageUrl();
		local.mValueBefore = remote.getValueBefore();
		local.mValueAfter = remote.getValueAfter();
		return local;
	}

	private LocalProduct productRemoteToLocal(FrontendRpcSmsProduct remote) {
		LocalProduct localProduct = new LocalProduct();
		localProduct.mUnitName = remote.getUnitName();
		localProduct.mDecimalUnit = remote.getDescimalUnit() ? 1 : 0;
		localProduct.mChineseName = remote.getNameCn();
		localProduct.mEnglishName = remote.getNameEn();
		localProduct.mChineseSortName = PinyinUtil.toPinyin(this,
				localProduct.mChineseName);
		if (localProduct.mEnglishName != null) {
			localProduct.mEnglishSortName = localProduct.mEnglishName.toUpperCase();
		}
		localProduct.mImageUrl = remote.getImageUrl();
		localProduct.mUUID = remote.getId();
		localProduct.mType = remote.getType();
		localProduct.mChangeId = remote.getChangeId();
		localProduct.mDeleted = remote.getDeleted() ? 1 : 0;

		return localProduct;
	}

	private long processCustomerResult(List<FrontendRpcSmsCustomer> remoteList,
			List<String> customerSet, long oldChangeId, String userRole) {
		ContentResolver resolver = getContentResolver();
		if (remoteList != null) {
			for (FrontendRpcSmsCustomer remoteCustomer : remoteList) {
				if (oldChangeId < remoteCustomer.getChangeId()) {
					oldChangeId = remoteCustomer.getChangeId();
				}

				storeCustomerToLocal(resolver, remoteCustomer);
			}
		}

		if (customerSet != null) {
			Set<String> remoteCustomerSet = new HashSet<String>(customerSet);

			Cursor cursor = null;
			try {
				cursor = getContentResolver().query(LocalCustomer.CONTENT_URI,
						new String[] { LocalCustomer.UUID }, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					for (; !cursor.isAfterLast(); cursor.moveToNext()) {
						String uuid = cursor.getString(0);
						if (!remoteCustomerSet.contains(uuid)) {
							getContentResolver().delete(LocalCustomer.CONTENT_URI,
									LocalCustomer.ITEM_SELECTION,
									new String[] { uuid });
						}
					}
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}

		return oldChangeId;
	}

	private void storeCustomerToLocal(ContentResolver resolver,
			FrontendRpcSmsCustomer remoteCustomer) {
		LocalCustomer localCustomer = new LocalCustomer();
		localCustomer.mUuid = remoteCustomer.getId();
		localCustomer.mGroupId = remoteCustomer.getGroupId();
		localCustomer.mChineseGroupName = remoteCustomer.getGroupNameCn();
		localCustomer.mEnglishGroupName = remoteCustomer.getGroupNameEn();
		localCustomer.mChineseName = remoteCustomer.getNameCn();
		localCustomer.mEnglishName = remoteCustomer.getNameEn();
		localCustomer.mChangeId = remoteCustomer.getChangeId();

		ContentValues values = new ContentValues();
		localCustomer.toContentValues(values);

		int result = resolver.update(LocalCustomer.CONTENT_URI, values,
				LocalCustomer.ITEM_SELECTION,
				new String[] { localCustomer.mUuid });
		if (result <= 0) {
			resolver.insert(LocalCustomer.CONTENT_URI, values);
		}

		resolver.delete(LocalOrderTemplate.CONTENT_URI, null, null);
		List<FrontendRpcSmsOrderTemplate> orderTemplates = remoteCustomer
				.getTemplates();
		if (orderTemplates != null && !orderTemplates.isEmpty()) {
			for (FrontendRpcSmsOrderTemplate template : orderTemplates) {
				processRemoteOrderTemplate(template);
			}
		}
	}

	private void processRemoteOrderTemplate(FrontendRpcSmsOrderTemplate template) {
		LocalOrderTemplate localTemplate = new LocalOrderTemplate();
		localTemplate.mName = template.getName();
		localTemplate.mSummeryChinese = template.getSummeryCn();
		localTemplate.mSummeryEnglish = template.getSummeryEn();
		localTemplate.mProductsMap = listToString(template.getProductIds(),
				template.getProductAmounts());

		ContentResolver resolver = getContentResolver();
		ContentValues values = new ContentValues();
		localTemplate.toContentValues(values);
		int result = resolver.update(LocalOrderTemplate.CONTENT_URI, values,
				LocalOrderTemplate.ITEM_SELECTION,
				new String[] { localTemplate.mName });
		if (result <= 0) {
			resolver.insert(LocalOrderTemplate.CONTENT_URI, values);
		}
	}

	private boolean loginAccountCheck(String[] previousUser,
			UserController controller) {
		String[] currentUser = controller.getLoginUser();
		if (currentUser == null || !currentUser[0].equals(previousUser[0])) {
			return false;
		} else {
			return true;
		}
	}

	private boolean userCheck(String token, String[] user) {
		return !TextUtils.isEmpty(token) && user != null && user.length == 2
				&& !TextUtils.isEmpty(user[0]) && !TextUtils.isEmpty(user[1]);
	}

	private static Frontend getFrontentEndpoint() {
		Frontend.Builder builder = new Frontend.Builder(HTTP_TRANSPORT,
				JSON_FACTORY, null);
		return builder.build();
	}

	private static String storagesToString(List<FrontendRpcSmsStoreItem> items) {
		StringBuilder stringBuilder = new StringBuilder();

		if (items != null) {
			for (FrontendRpcSmsStoreItem item : items) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append("&");
				}
				String value = String.valueOf(item.getAmount());
				try {
					stringBuilder.append((item.getId() != null ? URLEncoder
							.encode(item.getId(), "UTF-8") : ""));
					stringBuilder.append("=");
					stringBuilder.append(value != null ? URLEncoder.encode(
							value, "UTF-8") : "");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(
							"This method requires UTF-8 encoding support", e);
				}
			}
		}

		return stringBuilder.toString();
	}

	private static String listToString(List<String> idList,
			List<Double> amountList) {
		StringBuilder stringBuilder = new StringBuilder();

		if (idList != null) {
			for (int index = 0; index < idList.size(); index++) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append("&");
				}
				String value = String.valueOf(amountList.get(index));
				try {
					stringBuilder
							.append((idList.get(index) != null ? URLEncoder
									.encode(idList.get(index), "UTF-8") : ""));
					stringBuilder.append("=");
					stringBuilder.append(value != null ? URLEncoder.encode(
							value, "UTF-8") : "");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(
							"This method requires UTF-8 encoding support", e);
				}
			}
		}

		return stringBuilder.toString();
	}
}
