package sg.com.smartmediasoft.storeclient.ui.main;

import android.os.Bundle;

public interface DrawerActionCallback {
	public static String ACTION_LOGOUT = "ACTION_LOGOUT";
	public static String ACTION_SELECT_CUSTOMER = "ACTION_SELECT_CUSTOMER";
	public static String ACTION_SELECT_PRODUCT_MANAGE = "ACTION_SELECT_PRODUCT_MANAGE";
	public static String ACTION_SELECT_ORDER_HISTORY = "ACTION_SELECT_ORDER_HISTORY";
	public static String ACTION_SELECT_ORDER_TEMPLATE = "ACTION_SELECT_ORDER_TEMPLATE";
	public static String ACTION_SELECT_LOGIN_USER = "ACTION_SELECT_LOGIN_USER";
	
	public void onDrawerActionStart(String action, Bundle data);
}
