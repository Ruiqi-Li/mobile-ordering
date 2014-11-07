package sg.com.smartmediasoft.storeclient;

import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

public class SMSBroadcastManager {
	public static final String ACTION_DEVICE_REGIST_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_DEVICE_REGIST_FINISHED";
	public static final String ACTION_LOGIN_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_LOGIN_FINISHED";
	public static final String ACTION_LOGOUT_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_LOGOUT_FINISHED";
	public static final String ACTION_USER_CONFIG_CHANGED = "sg.com.smartmediasoft.storeclient.ACTION_USER_CONFIG_CHANGED";

	public static final String ACTION_MAKE_ORDER_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_MAKE_ORDER_FINISHED";
	public static final String ACTION_EDIT_ORDER_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_EDIT_ORDER_FINISHED";
	public static final String ACTION_CHANGE_ORDER_STATE_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_CHANGE_ORDER_STATE_FINISHED";
	public static final String ACTION_SYNC_ORDER_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_SYNC_ORDER_FINISHED";
	public static final String ACTION_REFRESH_ORDER_HISTORY_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_REFRESH_ORDER_HISTORY_FINISHED";
	public static final String ACTION_MORE_ORDER_HISTORY_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_MORE_ORDER_HISTORY_FINISHED";
	public static final String ACTION_SEARCH_ORDER_HISTORY_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_SEARCH_ORDER_HISTORY_FINISHED";
	public static final String ACTION_GET_ORDER_DAYS_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_GET_ORDER_DAYS_FINISHED";
	
	public static final String ACTION_UPDATE_ORDER_TEMPLATE_FINISHED = "sg.com.smartmediasoft.storeclient.UPDATE_ORDER_TEMPLATE_FINISHED";

	public static final String ACTION_SYNC_PRODUCT_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_SYNC_PRODUCT_FINISHED";
	public static final String ACTION_SYNC_CUSTOMER_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_SYNC_CUSTOMER_FINISHED";

	public static final String ACTION_UPDATE_STORE_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_UPDATE_STORE_FINISHED";
	public static final String ACTION_SYNC_CUSTOMER_STORE_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_SYNC_CUSTOMER_STORE_FINISHED";
	public static final String ACTION_SYNC_SUPPLIER_STORE_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_SYNC_SUPPLIER_STORE_FINISHED";

	public static final String ACTION_SYNC_ORDER_TEMPLATE_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_SYNC_ORDER_TEMPLATE_FINISHED";
	public static final String ACTION_SYNC_ORDER_RECORD_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_SYNC_ORDER_RECORD_FINISHED";
	public static final String ACTION_GENERATE_ORDER_REPORT_FINISHED = "sg.com.smartmediasoft.storeclient.ACTION_GENERATE_ORDER_REPORT_FINISHED";

	public static final String DATA_EXRTA_ID = "id";
	public static final String DATA_EXTRA_VALUE = "value";
	public static final String DATA_PARCELABLE_LIST = "parcelable_list";
	public static final String DATA_HAS_DATA = "has_data";
	public static final String DATA_MESSAGE = "message_array";
	public static final String DATA_PARCELABLE_DATA = "parcelable_data";
	public static final String DATA_INT_1 = "int_1";
	public static final String DATA_INT_2 = "int_2";
	public static final String DATA_BOOLEAN = "boolean_1";

	public static final String EXTRA_RESULT_CODE = "code";

	public static void broadcastAction(Context context, String action,
			int code, Bundle data) {
		Intent intent = new Intent(action);
		intent.putExtra(EXTRA_RESULT_CODE, code);
		if (data != null) {
			intent.putExtras(data);
		}
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	public static void registReceiver(Context context,
			BroadcastReceiver receiver, Set<String> actions) {
		LocalBroadcastManager manager = LocalBroadcastManager
				.getInstance(context);
		for (String action : actions) {
			manager.registerReceiver(receiver, new IntentFilter(action));
		}
	}

	public static void unregistReveiver(Context context,
			BroadcastReceiver receiver) {
		LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
	}

}
