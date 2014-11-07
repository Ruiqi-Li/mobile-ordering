package sg.com.smartmediasoft.storeclient.ui.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.UserController;
import sg.com.smartmediasoft.storeclient.database.LocalCustomer;
import sg.com.smartmediasoft.storeclient.database.LocalOrderTemplate;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.service.BackendService.BackendServiceBinder;
import sg.com.smartmediasoft.storeclient.ui.NotifyAwareActivity;
import sg.com.smartmediasoft.storeclient.ui.RefreshListener;
import sg.com.smartmediasoft.storeclient.ui.history.HistoryActivity;
import sg.com.smartmediasoft.storeclient.ui.login.LoginActivity;
import sg.com.smartmediasoft.storeclient.ui.orderedit.EditOrderActivity;
import sg.com.smartmediasoft.storeclient.ui.statistic.StatisticActivity;
import sg.com.smartmediasoft.storeclient.ui.store.StoreActivity;
import sg.com.smartmediasoft.storeclient.ui.tamplate.OrderTamplateActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends NotifyAwareActivity implements DrawerActionCallback,
		RefreshListener, DialogFragmentlogoutConfirm.onComfirmedListener {
	public static final String EXTRA_AUTO_LOGOUT = "auto_logout";
	public static final String EXTRA_AUTO_LOGOUT_MESSAGE = "auto_logout_msg";

	private static final String MAIN_VIEW_MODE_ORDER_LIST_NORMAL = "MAIN_VIEW_MODE_ORDER_LIST_NORMAL";

	private DrawerLayout mRootView;
	private ActionBarDrawerToggle mDrawerToggle;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	private UserController mUserController;
	private String mCustomerId;
	private String mCustomerName;
	private ProgressDialog mProgressDialog;
	
	private ServiceConnection mBackendServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mProgressDialog.dismiss();
			
			BackendServiceBinder binder = (BackendServiceBinder) service;
			binder.clearServiceWorks();
			
			BackendService.logout(MainActivity.this, null, true);
			MainActivity.this.unbindService(this);
		}
	};
	
	@Override
	public void getListereningActions(Set<String> actions) {
		actions.add(SMSBroadcastManager.ACTION_LOGOUT_FINISHED);
	}

	@Override
	public void onActionReveived(Context context, String action, Intent data) {
		if (SMSBroadcastManager.ACTION_LOGOUT_FINISHED.equals(action)) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.setAction(SMSBroadcastManager.ACTION_LOGOUT_FINISHED);
			intent.putExtra(EXTRA_AUTO_LOGOUT, data.getBooleanExtra(
					SMSBroadcastManager.DATA_BOOLEAN, false));
			intent.putExtra(EXTRA_AUTO_LOGOUT_MESSAGE,
					data.getStringExtra(SMSBroadcastManager.DATA_MESSAGE));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if (SMSBroadcastManager.ACTION_LOGOUT_FINISHED.equals(intent.getAction())) {
			Intent startLoginIntent = new Intent(this, LoginActivity.class);
			startLoginIntent.putExtra(EXTRA_AUTO_LOGOUT,
					intent.getBooleanExtra(SMSBroadcastManager.DATA_BOOLEAN, false));
			startLoginIntent.putExtra(EXTRA_AUTO_LOGOUT_MESSAGE,
					intent.getStringExtra(SMSBroadcastManager.DATA_MESSAGE));
			startActivity(startLoginIntent);
			finish();	
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		setContentView(R.layout.main_activity);

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_swiperefresh);
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.white,
				android.R.color.holo_orange_light, android.R.color.white,
				android.R.color.holo_orange_light);
		mSwipeRefreshLayout.setEnabled(false);

		mRootView = (DrawerLayout) findViewById(R.id.main_activity_drawer_root);
		// set a custom shadow that overlays the main content when the drawer
		// opens
		mRootView
				.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mRootView,
				R.drawable.ic_drawer, R.string.main_drawer_open,
				R.string.main_drawer_close) {

			@Override
			public void onDrawerClosed(View view) {

			}

			@Override
			public void onDrawerOpened(View drawerView) {

			}
		};
		mRootView.setDrawerListener(mDrawerToggle);
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog
				.setMessage(getString(R.string.dialog_edit_order_progress_message));
		mProgressDialog.setCancelable(false);

		mUserController = new UserController(this);
		BackendService.syncProducts(this, false);

		if (savedInstanceState == null) {
			Fragment drawerFragment = null;
			if (mUserController.userIsCustomer()) {
				drawerFragment = new CustomerDrawerFragment();
			} else if (mUserController.userIsSupplier()) {
				drawerFragment = new SupplierDrawerFragment();
			} else if (mUserController.userIsDirector()) {
				drawerFragment = new DirectorDrawerFragment();
			} else {
				return;
			}

			getFragmentManager().beginTransaction()
					.replace(R.id.main_activity_drawer_panel, drawerFragment)
					.commit();

			resetMainFragemnt(MAIN_VIEW_MODE_ORDER_LIST_NORMAL, null);
		}
	}

	@Override
	public void onLogoutConfirmed() {
		mProgressDialog.show();
		bindService(new Intent(this, BackendService.class),
				mBackendServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case R.id.action_order_records: {
			Fragment fragment = getFragmentManager().findFragmentById(
					R.id.main_activity_drawer_panel);
			if (fragment instanceof GetCustomerInterface) {
				ArrayList<LocalCustomer> customers = ((GetCustomerInterface) fragment)
						.getCustomerList();
				if (customers != null && !customers.isEmpty()) {
					Intent intent = new Intent(this, StatisticActivity.class);
					intent.putParcelableArrayListExtra(
							StatisticActivity.EXTRA_CUSTOMER_LIST, customers);
					startActivity(intent);
				}
			}
			return true;
		}
		case R.id.action_customer_store: {
			Intent intent = new Intent(this, StoreActivity.class);
			intent.putExtra(StoreActivity.EXTRA_SUPPLIER_STORAGE, false);
			intent.putExtra(StoreActivity.EXTRA_CUSTOMER_ID, mCustomerId);
			intent.putExtra(StoreActivity.EXTRA_CUSTOMER_NAME, mCustomerName);
			startActivity(intent);
			return true;
		}
		case R.id.action_history_order: {
			Intent intent = new Intent(this, HistoryActivity.class);
			intent.putExtra(HistoryActivity.EXTRA_CUSTOMER_ID, mCustomerId);
			intent.putExtra(HistoryActivity.EXTRA_CUSTOMER_NAME, mCustomerName);
			startActivity(intent);
			return true;
		}
		case R.id.action_new_order: {
			mRootView.closeDrawers();
			onNewOrder();
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onDrawerActionStart(String action, Bundle data) {
		mRootView.closeDrawers();

		if (DrawerActionCallback.ACTION_LOGOUT.equals(action)) {
			DialogFragment fragment = new DialogFragmentlogoutConfirm();
			fragment.show(getFragmentManager(), null);
		} else if (DrawerActionCallback.ACTION_SELECT_LOGIN_USER.equals(action)) {
			mCustomerId = null;
			mCustomerName = null;

			resetMainFragemnt(MAIN_VIEW_MODE_ORDER_LIST_NORMAL, null);
		} else if (DrawerActionCallback.ACTION_SELECT_PRODUCT_MANAGE
				.equals(action)) {
			UserController userController = new UserController(this);
			Intent intent = new Intent(this, StoreActivity.class);
			if (userController.userIsCustomer()) {
				intent.putExtra(StoreActivity.EXTRA_SUPPLIER_STORAGE, false);
				intent.putExtra(StoreActivity.EXTRA_CUSTOMER_ID,
						userController.getCustomerId());
				intent.putExtra(
						StoreActivity.EXTRA_CUSTOMER_NAME,
						LocalUtils.isDeveceInChinese() ? userController
								.getChineseCompanyName() : userController
								.getEnglishCompanyName());
			} else if (userController.userIsSupplier()) {
				intent.putExtra(
						StoreActivity.EXTRA_CUSTOMER_NAME,
						LocalUtils.isDeveceInChinese() ? userController
								.getChineseCompanyName() : userController
								.getEnglishCompanyName());
				intent.putExtra(StoreActivity.EXTRA_SUPPLIER_STORAGE, true);
			}
			startActivity(intent);
		} else if (DrawerActionCallback.ACTION_SELECT_CUSTOMER.equals(action)) {
			mCustomerId = data.getString(
					DirectorDrawerFragment.EXTRA_CUSTOMER_UUID, null);
			mCustomerName = data.getString(
					DirectorDrawerFragment.EXTRA_CUSTOMER_NAME, null);

			resetMainFragemnt(MAIN_VIEW_MODE_ORDER_LIST_NORMAL, null);
		} else if (DrawerActionCallback.ACTION_SELECT_ORDER_TEMPLATE
				.equals(action)) {
			Intent intent = new Intent(this, OrderTamplateActivity.class);
			if (data != null) {
				Parcelable orderTelplate = data
						.getParcelable(CustomerDrawerFragment.EXTRA_ORDER_TEMPLATE);
				if (orderTelplate != null) {
					intent.putExtra(OrderTamplateActivity.EXTRA_ORDER_TEMPLATE,
							orderTelplate);
				}
			}

			startActivityForResult(intent,
					CustomerDrawerFragment.TEMPLATE_ACTIVITY_RESULT_CODE);
		} else if (DrawerActionCallback.ACTION_SELECT_ORDER_HISTORY
				.equals(action)) {
			Intent intent = new Intent(this, HistoryActivity.class);
			intent.putExtra(StoreActivity.EXTRA_CUSTOMER_ID,
					mUserController.getCustomerId());
			startActivity(intent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CustomerDrawerFragment.TEMPLATE_ACTIVITY_RESULT_CODE
				&& resultCode == Activity.RESULT_OK) {
			Fragment fragment = getFragmentManager().findFragmentById(
					R.id.main_activity_drawer_panel);
			if (fragment instanceof CustomerDrawerFragment) {
				((CustomerDrawerFragment) fragment).restartLoader();
			}
		}
	}

	@Override
	public void onRefreshStateChange(boolean refreshing) {
		mSwipeRefreshLayout.setRefreshing(refreshing);
	}

	private void resetMainFragemnt(String viewMode, Bundle data) {
		resetTitle(viewMode);
		FragmentManager manager = getFragmentManager();
		if (MAIN_VIEW_MODE_ORDER_LIST_NORMAL.equals(viewMode)) {
			Fragment mainFragment = manager
					.findFragmentById(R.id.main_activity_main_panel);
			if (mainFragment == null
					|| !(mainFragment instanceof MainOrderListFragment)) {
				manager.beginTransaction()
						.replace(R.id.main_activity_main_panel,
								new MainOrderListFragment()).commit();
			} else {
				((MainOrderListFragment) mainFragment)
						.resetContent(mCustomerId);
			}
		}

		invalidateOptionsMenu();
	}

	private void onNewOrder() {
		UserController userController = new UserController(this);
		if (userController.userIsCustomer()) {
			Fragment fragment = getFragmentManager().findFragmentById(
					R.id.main_activity_drawer_panel);
			List<LocalOrderTemplate> datas = ((CustomerDrawerFragment) fragment)
					.getCustomerTemplates();

			if (datas == null || datas.isEmpty()) {
				Intent intent = new Intent(MainActivity.this,
						EditOrderActivity.class);
				intent.putExtra(EditOrderActivity.EXTRA_CUSTOMER_ID,
						mUserController.getCustomerId());
				startActivity(intent);
			} else {
				LocalOrderTemplate newTemplate = new LocalOrderTemplate();
				newTemplate.mName = getString(R.string.dialog_new_order_first_item);
				datas.add(0, newTemplate);
				final NewOrderSelectAdapter adapter = new NewOrderSelectAdapter(
						this, datas);

				AlertDialog.Builder builderSingle = new AlertDialog.Builder(
						this);
				builderSingle.setIcon(R.drawable.ic_launcher);
				builderSingle.setTitle(R.string.dialog_new_order_title);
				builderSingle.setAdapter(adapter, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(MainActivity.this,
								EditOrderActivity.class);
						intent.putExtra(EditOrderActivity.EXTRA_CUSTOMER_ID,
								mUserController.getCustomerId());
						if (which > 0) {
							LocalOrderTemplate item = adapter.getItem(which);
							ArrayList<LocalProduct> products = LocalUtils
									.extractProductFromTemplate(
											MainActivity.this, item);
							intent.putParcelableArrayListExtra(
									EditOrderActivity.EXTRA_PRODUCT_LIST,
									products);
						}
						startActivity(intent);
					}
				});
				builderSingle.show();
			}
		} else {
			Intent intent = new Intent(this, EditOrderActivity.class);
			intent.putExtra(EditOrderActivity.EXTRA_CUSTOMER_ID, mCustomerId);
			startActivity(intent);
		}
	}

	private void resetTitle(String viewMode) {
		if (mCustomerName == null) {
			setTitle(R.string.main_activity_title_all);
		} else {
			setTitle(mCustomerName);
		}
	}

}
