package sg.com.smartmediasoft.storeclient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sg.com.smartmediasoft.storeclient.database.LocalOrderItem;
import sg.com.smartmediasoft.storeclient.database.LocalOrderTemplate;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

public class LocalUtils {
	public static final long HOURS_IN_MILLIS_SENCODE = 60 * 60 * 1000;

	public static final int DRAWER_LIST_LOADER = 1;
	public static final int PRODUCT_LIST_VEGETABLE_LOADER = 2;
	public static final int PRODUCT_LIST_FRUIT_LOADER = 3;
	public static final int MAIN_ORDER_LOADER = 4;

	public static final int CODE_SUCCESS 									= 200;
	public static final int CODE_FAILED_DATA_ALREADY_CHANGE 				= 303;
	public static final int CODE_ERROR_DATA_FOUND 							= 304;
	public static final int CODE_ERROR_LOGIN_USERNAME_OR_PASSWORD_INVALIED 	= 409;

	public static final int CODE_FAILED_UNKNOW = 500;

	public static final String PRODUCT_TYPE_VEGETABLE = "Vegetable";
	public static final String PRODUCT_TYPE_FRUIT = "Fruit";

	public static final String RECORD_CHECK_MODE_ITEM = "item";
	public static final String RECORD_CHECK_MODE_RANGE = "range";

	public static final String USER_ROLE_MONITOR = "Monitor";
	public static final String USER_ROLE_CUSTOMER = "Customer";
	public static final String USER_ROLE_SUPPLIER = "Supplier";

	public static final String ORDER_STATE_PENDING = "Pending";
	public static final String ORDER_STATE_CONFIRMED = "Confirmed";
	public static final String ORDER_STATE_DELIVERED = "Delivered";
	public static final String ORDER_STATE_RECEIVED = "Received";
	public static final String ORDER_STATE_CANCELED = "Canceled";

	public static final String OPERATE_ACTION_CONFIRM = "CONFIRM";
	public static final String OPERATE_ACTION_DELIVER = "DELIVER";
	public static final String OPERATE_ACTION_RECEIVE = "RECEIVE";
	public static final String OPERATE_ACTION_SYSTEM_AMEND = "SYSTEM_AMEND";
	public static final String OPERATE_ACTION_CUSTOMER_AMEND = "CUSTOMER_AMEND";
	public static final String OPERATE_ACTION_SUPPLIER_AMEND = "SUPPLIER_AMEND";
	public static final String OPERATE_ACTION_CUSTOMER_NEW = "CUSTOMER_NEW";
	public static final String OPERATE_ACTION_SUPPLIER_NEW = "SUPPLIER_NEW";
	public static final String OPERATE_ACTION_CUSTOMER_CANCEL = "CUSTOMER_CANCEL";
	public static final String OPERATE_ACTION_SUPPLIER_CANCEL = "SUPPLIER_CANCEL";

	public static final String OPERATE_ITEM_TARGET_TIME_OPERATE_ID = "TARGET_TIME_OPERATE";
	public static final String OPERATE_ITEM_CONFIRM_OPERATE_ID = "CONFIRM_OPERATE_ID";
	public static final String OPERATE_ITEM_DLIVER_OPERATE_ID = "DLIVER_OPERATE_ID";

	public static final String NOTIFY_ACTION_NOTIFY_LOGOUT = "NOTIFY_LOGOUT";
	public static final String NOTIFY_ACTION_ORDER_CHANGE = "ORDER_CHANGE";
	public static final String NOTIFY_ACTION_CUSTOMER_CHANGE = "CUSTOMER_CHANGE";
	public static final String NOTIFY_ACTION_PRODUCT_CHANGE = "PRODUCT_CHANGE";
	public static final String NOTIFY_ACTION_CUSTOMER_STORE_CHANGE = "CUSTOMER_STORE_CHANGE";
	public static final String NOTIFY_ACTION_SUPPLIER_STORE_CHANGE = "SUPPLIER_STORE_CHANGE";
	public static final String NOTIFY_ACTION_USER_CONFIG_CHANGE = "USER_CONFIG_CHANGE";
	
	public static int humanIdToNotificationId(String orderHumanId) {
		return Integer.valueOf(orderHumanId.substring(4));
	}
	
	public static LocalProduct orderItemToProductLocal(LocalOrderItem item) {
		LocalProduct product = new LocalProduct();
		product.mAmount = item.mAmount;
		product.mChineseName = item.mNameCN;
		product.mEnglishName = item.mNameEN;
		product.mDecimalUnit = item.mDescimalUnit;
		product.mImageUrl = item.mImageUrl;
		product.mUnitName = item.mUnitName;
		product.mUUID = item.mProductId;
		return product;
	}

	public static ArrayList<LocalProduct> getProductListByStorageInfo(
			Context context, String storeMapText) {
		if (!TextUtils.isEmpty(storeMapText)) {
			Map<String, Double> storeMap = LocalUtils
					.stringToMapDouble(storeMapText);

			return productStringToList(context, storeMap);
		}

		return null;
	}

	public static ArrayList<LocalProduct> productStringToList(Context context,
			Map<String, Double> storeMap) {
		if (storeMap != null && !storeMap.isEmpty()) {
			StringBuilder selection = new StringBuilder();
			selection.append(LocalProduct.UUID).append(" IN (");
			for (String key : storeMap.keySet()) {
				selection.append("?,");
			}
			selection.deleteCharAt(selection.length() - 1);
			selection.append(")");

			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(
						LocalProduct.CONTENT_URI,
						LocalProduct.CONTENT_PROJECTION, selection.toString(),
						storeMap.keySet().toArray(new String[storeMap.size()]),
						null);
				if (cursor != null && cursor.moveToFirst()) {
					ArrayList<LocalProduct> products = new ArrayList<LocalProduct>();
					for (; !cursor.isAfterLast(); cursor.moveToNext()) {
						LocalProduct product = new LocalProduct(cursor);
						product.mAmount = storeMap.get(product.mUUID);
						products.add(product);
					}

					return products;
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}

		return null;
	}

	public static String mapToString(Map<String, Double> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (String key : map.keySet()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append("&");
			}
			String value = String.valueOf(map.get(key));
			try {
				stringBuilder.append((key != null ? URLEncoder.encode(key,
						"UTF-8") : ""));
				stringBuilder.append("=");
				stringBuilder.append(value != null ? URLEncoder.encode(value,
						"UTF-8") : "");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						"This method requires UTF-8 encoding support", e);
			}
		}

		return stringBuilder.toString();
	}

	public static Map<String, Double> stringToMapDouble(String input) {
		Map<String, Double> map = new HashMap<String, Double>();

		String[] nameValuePairs = input.split("&");
		for (String nameValuePair : nameValuePairs) {
			String[] nameValue = nameValuePair.split("=");
			try {
				map.put(URLDecoder.decode(nameValue[0], "UTF-8"),
						nameValue.length > 1 ? Double.valueOf(URLDecoder
								.decode(nameValue[1], "UTF-8")) : 0D);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						"This method requires UTF-8 encoding support", e);
			}
		}

		return map;
	}

	public static Map<String, Integer> stringToMapInt(String input) {
		Map<String, Integer> map = new HashMap<String, Integer>();

		String[] nameValuePairs = input.split("&");
		for (String nameValuePair : nameValuePairs) {
			String[] nameValue = nameValuePair.split("=");
			try {
				map.put(URLDecoder.decode(nameValue[0], "UTF-8"),
						nameValue.length > 1 ? Integer.valueOf(URLDecoder
								.decode(nameValue[1], "UTF-8")) : 0);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						"This method requires UTF-8 encoding support", e);
			}
		}

		return map;
	}

	public static ArrayList<LocalProduct> extractProductFromTemplate(
			Context context, LocalOrderTemplate template) {
		ArrayList<LocalProduct> mProducts = new ArrayList<LocalProduct>();
		Map<String, Double> productMap = LocalUtils
				.stringToMapDouble(template.mProductsMap);
		if (productMap != null && !productMap.isEmpty()) {
			StringBuilder selection = new StringBuilder();
			List<String> selectionArgs = new ArrayList<String>();
			Map<String, Double> idAmountMap = new HashMap<String, Double>();

			selection.append(LocalProduct.UUID).append(" IN (");
			for (String idString : productMap.keySet()) {
				idAmountMap.put(idString, productMap.get(idString));
				selectionArgs.add(idString);
				selection.append("?,");
			}
			selection.deleteCharAt(selection.length() - 1);
			selection.append(")");

			Cursor cursor = null;
			try {
				cursor = context.getContentResolver()
						.query(LocalProduct.CONTENT_URI,
								LocalProduct.CONTENT_PROJECTION,
								selection.toString(),
								selectionArgs.toArray(new String[selectionArgs
										.size()]), null);
				if (cursor != null && cursor.moveToFirst()) {
					for (; !cursor.isAfterLast(); cursor.moveToNext()) {
						LocalProduct product = new LocalProduct(cursor);
						product.mAmount = idAmountMap.get(product.mUUID);
						mProducts.add(product);
					}
				}
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}

		return mProducts;
	}

	public static boolean orderAvailableCheck(Context context,
			long distinationTime, int hoursBefore) {
		return distinationTime - System.currentTimeMillis() > hoursBefore
				* HOURS_IN_MILLIS_SENCODE;
	}

	public static String[] stringToArray(String encodeArray) {
		return encodeArray.split(",");
	}

	public static String arrayToString(List<String> array) {
		StringBuilder builder = new StringBuilder();
		for (String item : array) {
			builder.append(item).append(",");
		}
		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}

	public static String calculateTimeSescription(Context context,
			long distinationTime, int hoursBefore) {
		if (distinationTime <= 0) {
			return "";
		}

		long timeDiff = distinationTime - System.currentTimeMillis();
		if (timeDiff > hoursBefore * HOURS_IN_MILLIS_SENCODE) {
			return "";
		} else {
			if (hoursBefore > 24) {
				return context.getString(
						R.string.product_list_item_description_days_prefix,
						hoursBefore / 24);
			} else {
				return context.getString(
						R.string.product_list_item_description_hours_prefix,
						hoursBefore);
			}
		}
	}

	public static String getDesplayUnitText(double amount, String unitName,
			boolean decimalUnit) {
		if (decimalUnit) {
			return amount + " " + unitName;
		} else {
			int intAmount = (int) amount;
			return intAmount + " " + unitName;
		}
	}

	public static String getDesplayUnitText(LocalProduct product) {
		if (product == null) {
			return "";
		}

		if (product.mDecimalUnit == 1) {
			return product.mAmount > 0 ? product.mAmount + " "
					+ product.mUnitName : "";
		} else {
			int intAmount = (int) product.mAmount;
			return intAmount > 0 ? intAmount + " " + product.mUnitName : "";
		}
	}

	public static boolean hasNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();

		return isConnected;
	}

	public static void toastNoneNetwork(Context context) {
		Toast.makeText(context, R.string.toast_none_network_connection,
				Toast.LENGTH_SHORT).show();
	}

	public static String getSuplierActionTextWithOrderState(Context context,
			String state) {
		if (ORDER_STATE_PENDING.equals(state)) {
			return context.getString(R.string.order_state_pennding_action);
		} else if (ORDER_STATE_CONFIRMED.equals(state)) {
			return context.getString(R.string.order_state_reveive_action);
		} else if (ORDER_STATE_DELIVERED.equals(state)) {
			return context.getString(R.string.order_state_shiped_action);
		} else {
			return null;
		}
	}

	public static boolean isSupplierAction(String action) {
		if (OPERATE_ACTION_CONFIRM.equals(action)
				|| OPERATE_ACTION_DELIVER.equals(action)
				|| OPERATE_ACTION_SUPPLIER_AMEND.equals(action)
				|| OPERATE_ACTION_SUPPLIER_NEW.equals(action)) {
			return true;
		}

		return false;
	}

	public static String operateActionToText(Context context, String action) {
		if (OPERATE_ACTION_CONFIRM.equals(action)) {
			return context.getString(R.string.order_operate_action_confirm);
		} else if (OPERATE_ACTION_DELIVER.equals(action)) {
			return context.getString(R.string.order_operate_action_ship);
		} else if (OPERATE_ACTION_RECEIVE.equals(action)) {
			return context.getString(R.string.order_operate_action_dliver);
		} else if (OPERATE_ACTION_CUSTOMER_AMEND.equals(action)) {
			return context
					.getString(R.string.order_operate_action_customer_amend);
		} else if (OPERATE_ACTION_SUPPLIER_AMEND.equals(action)) {
			return context
					.getString(R.string.order_operate_action_supplier_amend);
		} else if (OPERATE_ACTION_CUSTOMER_NEW.equals(action)) {
			return context
					.getString(R.string.order_operate_action_customer_new);
		} else if (OPERATE_ACTION_SUPPLIER_NEW.equals(action)) {
			return context
					.getString(R.string.order_operate_action_supplier_new);
		} else if (OPERATE_ACTION_CUSTOMER_CANCEL.equals(action)) {
			return context
					.getString(R.string.order_operate_action_customer_cancel);
		} else if (OPERATE_ACTION_SUPPLIER_CANCEL.equals(action)) {
			return context
					.getString(R.string.order_operate_action_supplier_cancel);
		} else if (OPERATE_ACTION_SYSTEM_AMEND.equals(action)) {
			return context
					.getString(R.string.order_operate_action_system_amend);
		} else {
			return "";
		}
	}

	public static boolean isDeveceInChinese() {
		String language = Locale.getDefault().toString();
		String standString = Locale.CHINESE.toString();
		return language.startsWith(standString);
	}

	public static String stateCodeToString(String code, Context context) {
		if (ORDER_STATE_PENDING.equalsIgnoreCase(code)) {
			return context.getString(R.string.order_state_pennding);
		} else if (ORDER_STATE_CONFIRMED.equalsIgnoreCase(code)) {
			return context.getString(R.string.order_state_reveive);
		} else if (ORDER_STATE_DELIVERED.equalsIgnoreCase(code)) {
			return context.getString(R.string.order_state_shiped);
		} else if (ORDER_STATE_RECEIVED.equalsIgnoreCase(code)) {
			return context.getString(R.string.order_state_deliver);
		} else if (ORDER_STATE_CANCELED.equalsIgnoreCase(code)) {
			return context.getString(R.string.order_state_cancel);
		}

		return null;
	}

	public static DateFormat getDateTimeFormater() {
		return DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.MEDIUM);
	}

	public static DateFormat getDateTimeFormaterShort() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT);
	}

	public static DateFormat getDateFormater() {
		return DateFormat.getDateInstance(DateFormat.LONG);
	}

	public static DateFormat getDateFormaterShort() {
		return DateFormat.getDateInstance(DateFormat.SHORT);
	}

	public static List<Double> arrayToList(double[] array) {
		List<Double> results = new ArrayList<Double>();
		for (double value : array) {
			results.add(value);
		}

		return results;
	}

	public static double[] listToArray(List<Double> array) {
		double[] results = new double[array.size()];
		for (int i = 0; i < results.length; i++) {
			results[i] = array.get(i);
		}

		return results;
	}

	public static void addFragmentToStack(FragmentManager fm,
			Fragment fragment, int viewId) {
		FragmentTransaction ft = fm.beginTransaction();
		// ft.setCustomAnimations(android.R.anim.slide_in_left,
		// android.R.anim.slide_out_right, android.R.anim.slide_in_left,
		// android.R.anim.slide_out_right);
		ft.replace(viewId, fragment);
		ft.addToBackStack(null);
		ft.commit();
	}

	public static boolean isEqual(String text1, String text2) {
		if (text1 == null && text2 == null) {
			return true;
		} else if (text1 != null && text2 != null) {
			if (text1.equals(text2)) {
				return true;
			}
		}

		return false;
	}

}
