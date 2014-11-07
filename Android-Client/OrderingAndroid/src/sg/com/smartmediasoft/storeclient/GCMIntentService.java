package sg.com.smartmediasoft.storeclient;

import sg.com.smartmediasoft.storeclient.database.LocalCustomer;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.ui.main.MainActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * This class is started up as a service of the Android application. It listens
 * for Google Cloud Messaging (GCM) messages directed to this device.
 * 
 * When the device is successfully registered for GCM, a message is sent to the
 * App Engine backend via Cloud Endpoints, indicating that it wants to receive
 * broadcast messages from the it.
 * 
 * Before registering for GCM, you have to create a project in Google's Cloud
 * Console (https://code.google.com/apis/console). In this project, you'll have
 * to enable the "Google Cloud Messaging for Android" Service.
 * 
 * Once you have set up a project and enabled GCM, you'll have to set the
 * PROJECT_NUMBER field to the project number mentioned in the "Overview" page.
 * 
 * See the documentation at
 * http://developers.google.com/eclipse/docs/cloud_endpoints for more
 * information.
 */
public class GCMIntentService extends GCMBaseIntentService {

	/**
	 * Register the device for GCM.
	 * 
	 * @param mContext
	 *            the activity's context.
	 */
	public static void register(Context mContext) {
		GCMRegistrar.checkDevice(mContext);
		GCMRegistrar.checkManifest(mContext);
		GCMRegistrar.register(mContext, ProjectConst.PROJECT_NUMBER);
	}

	/**
	 * Unregister the device from the GCM service.
	 * 
	 * @param mContext
	 *            the activity's context.
	 */
	public static void unregister(Context mContext) {
		GCMRegistrar.unregister(mContext);
	}

	public GCMIntentService() {
		super(ProjectConst.PROJECT_NUMBER);
	}

	/**
	 * Called on registration error. This is called in the context of a Service
	 * - no dialog or UI.
	 * 
	 * @param context
	 *            the Context
	 * @param errorId
	 *            an error message
	 */
	@Override
	public void onError(Context context, String errorId) {
		Log.e(GCMIntentService.class.getSimpleName(), "errorId: " + errorId);
	}

	/**
	 * Called when a cloud message has been received.
	 */
	@Override
	public void onMessage(Context context, Intent intent) {
		UserController userController = new UserController(context);
		String[] user = userController.getLoginUser();
		if (user == null) {
			return;
		}

		Bundle data = intent.getExtras();
		String action = data.getString("action");

		if (LocalUtils.NOTIFY_ACTION_ORDER_CHANGE.equals(action)) {
			String customerId = data.getString("customer_id");
			LocalCustomer customer = LocalCustomer.getCustomerById(context,
					customerId);
			if (customer == null) {
				return;
			}

			BackendService.syncOrder(context, customerId, true);
		} else if (LocalUtils.NOTIFY_ACTION_CUSTOMER_STORE_CHANGE
				.equals(action)) {
			String customerId = data.getString("customer_id");
			LocalCustomer customer = LocalCustomer.getCustomerById(context,
					customerId);
			if (customer == null) {
				return;
			}

			BackendService.syncCustomerStore(context, customer.mUuid, 0,
					customer.mStoreChangeId);
		} else if (LocalUtils.NOTIFY_ACTION_SUPPLIER_STORE_CHANGE
				.equals(action)) {
			if (userController.userIsSupplier()) {
				BackendService.syncSupplierStore(this, 0,
						userController.getSupplierStoreChanhgeId());
			}
		} else if (LocalUtils.NOTIFY_ACTION_PRODUCT_CHANGE.equals(action)) {
			BackendService.syncProducts(context, true);
		} else if (LocalUtils.NOTIFY_ACTION_NOTIFY_LOGOUT.equals(action)) {
			BackendService.logout(context,
					getString(R.string.dialog_auto_logout_message), false);
		} else if (LocalUtils.NOTIFY_ACTION_CUSTOMER_CHANGE.equals(action)) {
			BackendService.syncCustomer(context, true);
		} else if (LocalUtils.NOTIFY_ACTION_USER_CONFIG_CHANGE.equals(action)) {
			BackendService.reLogin(context, user[0], user[1],
					userController.getDeviceToken());
		}
	}

	/**
	 * Called back when a registration token has been received from the Google
	 * Cloud Messaging service.
	 * 
	 * @param context
	 *            the Context
	 */
	@Override
	public void onRegistered(Context context, String registration) {
		UserController controller = new UserController(context);
		controller.registed(registration);
		String[] user = controller.getLoginUser();
		if (user != null) {
			BackendService.login(context, user[0], user[1], registration);
		}

		SMSBroadcastManager.broadcastAction(context,
				SMSBroadcastManager.ACTION_DEVICE_REGIST_FINISHED,
				LocalUtils.CODE_SUCCESS, null);
	}

	/**
	 * Called back when the Google Cloud Messaging service has unregistered the
	 * device.
	 * 
	 * @param context
	 *            the Context
	 */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		UserController controller = new UserController(context);
		controller.unregistered(registrationId);
	}
}
