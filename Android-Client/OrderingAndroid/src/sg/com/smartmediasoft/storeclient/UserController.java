package sg.com.smartmediasoft.storeclient;

import android.content.Context;
import android.text.TextUtils;

public class UserController {

	private AppPreference mPreference;

	public UserController(Context context) {
		mPreference = new AppPreference(context);
	}

	public void clearAll() {
		mPreference.clearAll();
	}
	
	public void resetCustomerName(String chineseName, String englishName) {
		mPreference.resetCustomerName(chineseName, englishName);
	}

	public void resetSupplierStorage(long storeChangeId, String storageMap) {
		mPreference.resetSupplierStorage(storeChangeId, storageMap);
	}

	public long getSupplierStoreCheckTime() {
		return mPreference.getSupplierStoreCheckTime();
	}

	public long getSupplierStoreChanhgeId() {
		return mPreference.getSupplierStoreChanhgeId();
	}

	public String getSupplierStorageMap() {
		return mPreference.getSupplierStorageMap();
	}
	
	public boolean getIsFirstTimeSetup() {
		return mPreference.getIsFirstTimeSetup();
	}
	
	public void setupFinished() {
		mPreference.setupFinished();
	}

	public String registed(String token) {
		String oldToken = mPreference.getDeviceToken();
		if (!LocalUtils.isEqual(oldToken, token)) {
			mPreference.setDeviceToken(token);

			return oldToken;
		}

		return null;
	}

	public void unregistered(String token) {
		String oldToken = mPreference.getDeviceToken();
		if (!TextUtils.isEmpty(oldToken)) {
			mPreference.setDeviceToken(null);
		}
	}

	public void userLogin(String userRole, String displayName,
			String customerId, String englishCompanyName,
			String chineseCompanyName, String username, String password,
			boolean canCancelOrder, boolean canChangeStorage,
			boolean canConfirmOrder, boolean canDeliverOrder, boolean canEditOrder,
			boolean canEditOrderTemplate, boolean canMakeOrder,
			boolean canReceiveOrder, boolean canViewOrderHistory,
			boolean canViewOrderStatistic) {
		mPreference.setUserRole(userRole);
		mPreference.setDisplayName(displayName);
		mPreference.setLoginUser(username, password);
		mPreference.setCustomerId(customerId);
		mPreference.setCompanyName(englishCompanyName, chineseCompanyName);
		mPreference.setAuthorities(canCancelOrder, canChangeStorage,
				canConfirmOrder, canDeliverOrder, canEditOrder, canEditOrderTemplate,
				canMakeOrder, canReceiveOrder,
				canViewOrderHistory, canViewOrderStatistic);
	}

	public String getCustomerId() {
		return mPreference.getCustomerId();
	}

	public String getEnglishCompanyName() {
		return mPreference.getEnglishCompanyName();
	}

	public String getChineseCompanyName() {
		return mPreference.getChineseCompanyName();
	}

	public boolean hasLoginUser() {
		String[] nameAndPassword = mPreference.getLoginUser();
		if (nameAndPassword == null || TextUtils.isEmpty(nameAndPassword[0])
				|| TextUtils.isEmpty(nameAndPassword[1])) {
			return false;
		}

		return true;
	}

	public String getDeviceToken() {
		return mPreference.getDeviceToken();
	}

	public String[] getLoginUser() {
		return mPreference.getLoginUser();
	}
	
	public void resetUsernamePassword() {
		mPreference.resetUsernamePassword();
	}

	public String getDisplayName() {
		return mPreference.getDisplayName();
	}

	public String getUserRole() {
		return mPreference.getUserRole();
	}

	public boolean userIsCustomer() {
		String userRole = mPreference.getUserRole();
		return LocalUtils.USER_ROLE_CUSTOMER.equals(userRole);
	}

	public boolean userIsSupplier() {
		String userRole = mPreference.getUserRole();
		return LocalUtils.USER_ROLE_SUPPLIER.equals(userRole);
	}

	public boolean userIsDirector() {
		String userRole = mPreference.getUserRole();
		return LocalUtils.USER_ROLE_MONITOR.equals(userRole);
	}

	public boolean getUserCanCancelOrder() {
		return mPreference.getUserCanCancelOrder();
	}

	public boolean getUserCanChangeStorage() {
		return mPreference.getUserCanChangeStorage();
	}

	public boolean getUserCanConfirmOrder() {
		return mPreference.getUserCanConfirmOrder();
	}

	public boolean getUserCanDeliverOrder() {
		return mPreference.getUserCanDeliverOrder();
	}
	
	public boolean getUserCanCanEditOrder() {
		return mPreference.getUserCanCanEditOrder();
	}

	public boolean getUserCanMakeOrder() {
		return mPreference.getUserCanMakeOrder();
	}

	public boolean getUserCanReceiveOrder() {
		return mPreference.getUserCanReceiveOrder();
	}

	public boolean getUserCanEditOrderTemplate() {
		return mPreference.getUserCanEditOrderTemplate();
	}

	public boolean getUserCanViewOrderHistory() {
		return mPreference.getUserCanViewOrderHistory();
	}

	public boolean getUserCanViewOrderStatistic() {
		return mPreference.getUserCanViewOrderStatistic();
	}
}
