package sg.com.smartmediasoft.storeclient;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
	private static final String APP_PREFERRENCE_NAME = "APP_PREFERENCE";
	public static final long CHECK_PERIOD = 10 * 1000;

	private static final String PRODUCT_SYNC_TIME = "PRODUCT_SYNC_TIME";
	private static final String PRODUCT_SYNC_KEY = "PRODUCT_SYNC_KEY";

	private static final String ORDER_SYNC_TIME_PREFIX = "ORDER_SYNC_TIME_";
	private static final String ORDER_SYNC_KEY_PREFIX = "ORDER_SYNC_KEY_";

	private static final String CUSTOMER_SYNC_TIME = "CUSTOMER_SYNC_TIME";
	private static final String CUSTOMER_SYNC_KEY = "CUSTOMER_SYNC_KEY";

	private static final String KEY_DEVICE_TOKEN = "DEVICE_TOKEN";
	private static final String KEY_LOGINUSER_DISPLAYNAME = "KEY_LOGINUSER_DISPLAYNAME";
	private static final String KEY_LOGINUSER_ROLE = "KEY_LOGINUSER_ROLE";
	private static final String KEY_LOGINUSER_USERNAME = "KEY_LOGINUSER_USERNAME";
	private static final String KEY_LOGINUSER_PASSWORD = "KEY_LOGINUSER_PASSWORD";
	private static final String KEY_LOGINUSER_CUSTOMER_ID = "KEY_LOGINUSER_CUSTOMER_ID";
	private static final String KEY_LOGINUSER_ENGLISH_COMPANY_NAME = "KEY_LOGINUSER_ENGLISH_COMPANY_NAME";
	private static final String KEY_LOGINUSER_CHINESE_COMPANY_NAME = "KEY_LOGINUSER_CHINESE_COMPANY_NAME";

	private static final String KEY_LOGINUSER_CAN_CANCEL_ORDER = "KEY_LOGINUSER_CAN_CANCEL_ORDER";
	private static final String KEY_LOGINUSER_CAN_CHANGE_STORAGE = "KEY_LOGINUSER_CAN_CHANGE_STORAGE";
	private static final String KEY_LOGINUSER_CAN_CONFIRM_ORDER = "KEY_LOGINUSER_CAN_CONFIRM_ORDER";
	private static final String KEY_LOGINUSER_CAN_DELIVER_ORDER = "KEY_LOGINUSER_CAN_DELIVER_ORDER";
	private static final String KEY_LOGINUSER_CAN_CAN_EDIT_ORDER = "KEY_LOGINUSER_CAN_CAN_EDIT_ORDER";
	private static final String KEY_LOGINUSER_CAN_EDIT_ORDER_TEMPLATE = "KEY_LOGINUSER_CAN_EDIT_ORDER_TEMPLATE";
	private static final String KEY_LOGINUSER_CAN_MAKE_ORDER = "KEY_LOGINUSER_CAN_MAKE_ORDER";
	private static final String KEY_LOGINUSER_CAN_RECEIVE_ORDER = "KEY_LOGINUSER_CAN_RECEIVE_ORDER";
	private static final String KEY_LOGINUSER_CAN_VIEW_ORDER_HISTORY = "KEY_LOGINUSER_CAN_VIEW_ORDER_HISTORY";
	private static final String KEY_LOGINUSER_CAN_VIEW_ORDER_STATISTIC = "KEY_LOGINUSER_CAN_VIEW_ORDER_STATISTIC";

	private static final String KEY_STORE_CHECK_TIME = "KEY_STORE_CHECK_TIME";
	private static final String KEY_STORE_CHANGE_ID = "KEY_STORE_CHANGE_ID";
	private static final String KEY_STORE_STORAGE_MAP = "KEY_STORE_STORAGE_MAP";

	private static final String KEY_TEMPLATE_COUNT = "KEY_TEMPLATE_COUNT";
	private static final String KEY_FIRSTTIME_SETUP = "KEY_FIRSTTIME_SETUP";

	private SharedPreferences mPreferences;

	public AppPreference(Context context) {
		mPreferences = context.getApplicationContext().getSharedPreferences(
				APP_PREFERRENCE_NAME, Context.MODE_PRIVATE);
	}

	public void clearAll() {
		String token = mPreferences.getString(KEY_DEVICE_TOKEN, null);
		mPreferences.edit().clear().commit();
		if (token != null) {
			mPreferences.edit().putString(KEY_DEVICE_TOKEN, token).commit();
		}
	}

	public void setAuthorities(boolean canCancelOrder,
			boolean canChangeStorage, boolean canConfirmOrder,
			boolean canDeliverOrder,
			boolean canEditOrder, boolean canEditOrderTemplate,
			boolean canMakeOrder, boolean canReceiveOrder,
			boolean canViewOrderHistory, boolean canViewOrderStatistic) {
		mPreferences
				.edit()
				.putBoolean(KEY_LOGINUSER_CAN_CANCEL_ORDER, canCancelOrder)
				.putBoolean(KEY_LOGINUSER_CAN_CHANGE_STORAGE, canChangeStorage)
				.putBoolean(KEY_LOGINUSER_CAN_CONFIRM_ORDER, canConfirmOrder)
				.putBoolean(KEY_LOGINUSER_CAN_DELIVER_ORDER, canDeliverOrder)
				.putBoolean(KEY_LOGINUSER_CAN_CAN_EDIT_ORDER, canEditOrder)
				.putBoolean(KEY_LOGINUSER_CAN_EDIT_ORDER_TEMPLATE,
						canEditOrderTemplate)
				.putBoolean(KEY_LOGINUSER_CAN_MAKE_ORDER, canMakeOrder)
				.putBoolean(KEY_LOGINUSER_CAN_RECEIVE_ORDER, canReceiveOrder)
				.putBoolean(KEY_LOGINUSER_CAN_VIEW_ORDER_HISTORY,
						canViewOrderHistory)
				.putBoolean(KEY_LOGINUSER_CAN_VIEW_ORDER_STATISTIC,
						canViewOrderStatistic).commit();
	}
	
	public void resetCustomerName(String chineseName, String englishName) {
		mPreferences.edit()
				.putString(KEY_LOGINUSER_CHINESE_COMPANY_NAME, chineseName)
				.putString(KEY_LOGINUSER_ENGLISH_COMPANY_NAME, englishName)
				.commit();
	}

	public boolean getUserCanCancelOrder() {
		return mPreferences.getBoolean(KEY_LOGINUSER_CAN_CANCEL_ORDER, false);
	}

	public boolean getUserCanChangeStorage() {
		return mPreferences.getBoolean(KEY_LOGINUSER_CAN_CHANGE_STORAGE, false);
	}

	public boolean getUserCanConfirmOrder() {
		return mPreferences.getBoolean(KEY_LOGINUSER_CAN_CONFIRM_ORDER, false);
	}

	public boolean getUserCanDeliverOrder() {
		return mPreferences.getBoolean(KEY_LOGINUSER_CAN_DELIVER_ORDER, false);
	}
	
	public boolean getIsFirstTimeSetup() {
		return mPreferences.getBoolean(KEY_FIRSTTIME_SETUP, true);
	}
	
	public void setupFinished() {
		mPreferences.edit().putBoolean(KEY_FIRSTTIME_SETUP, false).commit();
	}
	
	public boolean getUserCanCanEditOrder() {
		return mPreferences.getBoolean(KEY_LOGINUSER_CAN_CAN_EDIT_ORDER, false);
	}

	public boolean getUserCanMakeOrder() {
		return mPreferences.getBoolean(KEY_LOGINUSER_CAN_MAKE_ORDER,
				false);
	}

	public boolean getUserCanReceiveOrder() {
		return mPreferences.getBoolean(KEY_LOGINUSER_CAN_RECEIVE_ORDER, false);
	}

	public boolean getUserCanEditOrderTemplate() {
		return mPreferences.getBoolean(KEY_LOGINUSER_CAN_EDIT_ORDER_TEMPLATE, false);
	}

	public boolean getUserCanViewOrderHistory() {
		return mPreferences.getBoolean(KEY_LOGINUSER_CAN_VIEW_ORDER_HISTORY,
				false);
	}

	public boolean getUserCanViewOrderStatistic() {
		return mPreferences.getBoolean(KEY_LOGINUSER_CAN_VIEW_ORDER_STATISTIC,
				false);
	}

	// Supplier online storage
	public void resetSupplierStorage(long storeChangeId, String storageMap) {
		mPreferences.edit().putLong(KEY_STORE_CHANGE_ID, storeChangeId)
				.putLong(KEY_STORE_CHECK_TIME, System.currentTimeMillis())
				.putString(KEY_STORE_STORAGE_MAP, storageMap).commit();
	}

	public long getSupplierStoreCheckTime() {
		return mPreferences.getLong(KEY_STORE_CHECK_TIME, -1);
	}

	public long getSupplierStoreChanhgeId() {
		return mPreferences.getLong(KEY_STORE_CHANGE_ID, -1);
	}

	public String getSupplierStorageMap() {
		return mPreferences.getString(KEY_STORE_STORAGE_MAP, null);
	}

	// Product Sync
	public boolean isPorductNeedSync() {
		return timeCheck(mPreferences.getLong(PRODUCT_SYNC_TIME, 0));
	}

	public long getProductSyncKey() {
		return mPreferences.getLong(PRODUCT_SYNC_KEY, 0);
	}

	public void resetProductSync(long newSyncKey) {
		mPreferences.edit()
				.putLong(PRODUCT_SYNC_TIME, System.currentTimeMillis())
				.putLong(PRODUCT_SYNC_KEY, newSyncKey).commit();
	}

	// Order Sync
	public boolean isOrderNeedSync(String customerId) {
		return timeCheck(mPreferences.getLong(ORDER_SYNC_TIME_PREFIX
				+ customerId, 0));
	}

	public long getOrderSyncKey(String customerId) {
		return mPreferences.getLong(ORDER_SYNC_KEY_PREFIX + customerId, 0);
	}

	public void resetOrderSync(String customerId, long newSyncKey) {
		mPreferences
				.edit()
				.putLong(ORDER_SYNC_TIME_PREFIX + customerId,
						System.currentTimeMillis())
				.putLong(ORDER_SYNC_KEY_PREFIX + customerId, newSyncKey)
				.commit();
	}

	// Customer Sync
	public boolean isCustomerNeedSync() {
		return timeCheck(mPreferences.getLong(CUSTOMER_SYNC_TIME, 0));
	}

	public long getCustomerSyncKey() {
		return mPreferences.getLong(CUSTOMER_SYNC_KEY, 0);
	}

	public void resetCustomerSync(long newSyncKey) {
		mPreferences.edit()
				.putLong(CUSTOMER_SYNC_TIME, System.currentTimeMillis())
				.putLong(CUSTOMER_SYNC_KEY, newSyncKey).commit();
	}

	private boolean timeCheck(long value) {
		return System.currentTimeMillis() - value > CHECK_PERIOD;
	}

	public int getTemplateCount() {
		return mPreferences.getInt(KEY_TEMPLATE_COUNT, 1);
	}

	public void increaceTemplateCount() {
		int count = getTemplateCount();
		count++;
		mPreferences.edit().putInt(KEY_TEMPLATE_COUNT, count).commit();
	}

	public String getCustomerId() {
		return mPreferences.getString(KEY_LOGINUSER_CUSTOMER_ID, null);
	}

	public void setCustomerId(String customerId) {
		mPreferences.edit().putString(KEY_LOGINUSER_CUSTOMER_ID, customerId)
				.commit();
	}

	public String getUserRole() {
		return mPreferences.getString(KEY_LOGINUSER_ROLE, null);
	}

	public void setUserRole(String userRole) {
		mPreferences.edit().putString(KEY_LOGINUSER_ROLE, userRole).commit();
	}

	public String getDisplayName() {
		return mPreferences.getString(KEY_LOGINUSER_DISPLAYNAME, null);
	}

	public void setDisplayName(String displayName) {
		mPreferences.edit().putString(KEY_LOGINUSER_DISPLAYNAME, displayName)
				.commit();
	}

	public String getEnglishCompanyName() {
		return mPreferences.getString(KEY_LOGINUSER_ENGLISH_COMPANY_NAME, null);
	}

	public String getChineseCompanyName() {
		return mPreferences.getString(KEY_LOGINUSER_CHINESE_COMPANY_NAME, null);
	}

	public void setCompanyName(String englishCompanyName,
			String chineseCompanyName) {
		mPreferences
				.edit()
				.putString(KEY_LOGINUSER_ENGLISH_COMPANY_NAME,
						englishCompanyName)
				.putString(KEY_LOGINUSER_CHINESE_COMPANY_NAME,
						chineseCompanyName).commit();
	}

	public String getDeviceToken() {
		return mPreferences.getString(KEY_DEVICE_TOKEN, null);
	}

	public void setDeviceToken(String deviceToken) {
		mPreferences.edit().putString(KEY_DEVICE_TOKEN, deviceToken).commit();
	}

	public void setLoginUser(String username, String password) {
		mPreferences.edit().putString(KEY_LOGINUSER_USERNAME, username)
				.putString(KEY_LOGINUSER_PASSWORD, password).commit();
	}

	public void resetUsernamePassword() {
		mPreferences.edit().putString(KEY_LOGINUSER_USERNAME, null)
				.putString(KEY_LOGINUSER_PASSWORD, null).commit();
	}

	public String[] getLoginUser() {
		String username = mPreferences.getString(KEY_LOGINUSER_USERNAME, null);
		String password = mPreferences.getString(KEY_LOGINUSER_PASSWORD, null);

		if (username == null || password == null) {
			return null;
		}

		return new String[] { username, password };
	}

}
