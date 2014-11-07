package sg.com.smartmediasoft.storeclient.ui.orderdetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.UserController;
import sg.com.smartmediasoft.storeclient.database.LocalOperate;
import sg.com.smartmediasoft.storeclient.database.LocalOrder;
import sg.com.smartmediasoft.storeclient.database.LocalOrderItem;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.ui.NotifyAwareActivity;
import sg.com.smartmediasoft.storeclient.ui.SlidingTabLayout;
import sg.com.smartmediasoft.storeclient.ui.main.MainActivity;
import sg.com.smartmediasoft.storeclient.ui.orderdetail.StateChangeConfirmDialog.StateActionConfirmCallback;
import sg.com.smartmediasoft.storeclient.ui.orderedit.EditOrderActivity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OrderDetailActivity extends NotifyAwareActivity implements
		LoaderCallbacks<Void>, OnClickListener, StateActionConfirmCallback {
	public static final String EXTRA_LOCAL_ORDER = "order";
	public static final String EXTRA_LOCAL_ORDER_ID = "order_id";
	public static final String EXTRA_LOCAL_ORDER_OPERATES = "order_operates";
	public static final String EXTRA_LOCAL_ORDER_ORDERITENS = "order_products";
	public static final String EXTRA_FROM_NOTIFICATION = "fromNotification";
	public static final String EXTRA_TOAST = "toast";

	private static final String CONFIRM_DIALOG_TAG = "CONFIRM_DIALOG_FRAGMENT";
	private static final int DETAIL_LOADER_ID = 1;
	private static final int EDIT_ORDER_RESULT = 10;

	private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;
	private Button mBottomActionBtn;
	private UserController mUserController;

	private LocalOrder mOrder;
	private ArrayList<LocalOrderItem> mOrderItems;
	private List<LocalOperate> mOperates;

	private ProgressDialog mProgressDialog;
	private boolean mNeedStartMainWhenFinished;
	private AlertDialog mConfirmDialog;

	@Override
	public void getListereningActions(Set<String> actions) {
		actions.add(SMSBroadcastManager.ACTION_CHANGE_ORDER_STATE_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_SYNC_ORDER_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_USER_CONFIG_CHANGED);
	}

	@Override
	public void onActionReveived(Context context, String action, Intent data) {
		if (SMSBroadcastManager.ACTION_CHANGE_ORDER_STATE_FINISHED
				.equals(action)) {
			mProgressDialog.hide();
			
			Bundle bundle = data.getExtras();
			if (mOrder.mHumanId.equals(bundle
					.getString(SMSBroadcastManager.DATA_EXRTA_ID))) {
				int resultCode = data.getIntExtra(
						SMSBroadcastManager.EXTRA_RESULT_CODE,
						LocalUtils.CODE_FAILED_UNKNOW);
				if (resultCode == LocalUtils.CODE_SUCCESS
						|| resultCode == LocalUtils.CODE_FAILED_DATA_ALREADY_CHANGE) {
                    if (mConfirmDialog != null) {
                        mConfirmDialog.dismiss();
                    }

                    Fragment dialogFragment = getFragmentManager()
                            .findFragmentByTag(CONFIRM_DIALOG_TAG);
                    if (dialogFragment != null) {
                        ((DialogFragment) dialogFragment).dismiss();
                    }

					Intent updateIntent = new Intent();
					updateIntent
							.putExtra(EXTRA_LOCAL_ORDER,
									bundle.getParcelable(EXTRA_LOCAL_ORDER));
					updateIntent
							.putParcelableArrayListExtra(EXTRA_LOCAL_ORDER_OPERATES,
									bundle.getParcelableArrayList(EXTRA_LOCAL_ORDER_OPERATES));
					updateIntent
							.putParcelableArrayListExtra(EXTRA_LOCAL_ORDER_ORDERITENS,
									bundle.getParcelableArrayList(EXTRA_LOCAL_ORDER_ORDERITENS));
					updateIntent.putExtra(EXTRA_FROM_NOTIFICATION, true);
					updateIntent.putExtra(EXTRA_LOCAL_ORDER_ID, mOrder.mUUID);
					updatePage(updateIntent);

					if (resultCode == LocalUtils.CODE_SUCCESS) {
						NotificationManagerCompat notificationManager = NotificationManagerCompat
								.from(this);
						notificationManager.cancel(LocalUtils
								.humanIdToNotificationId(mOrder.mHumanId));

                        if (bundle.getBoolean(EXTRA_TOAST, true)) {
                            Toast.makeText(context,
                                    R.string.toast_order_state_change_success,
                                    Toast.LENGTH_SHORT).show();
                        }
					} else {
						Toast.makeText(context,
								R.string.toast_order_already_changed,
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(context,
							bundle.getString(SMSBroadcastManager.DATA_MESSAGE),
							Toast.LENGTH_SHORT).show();
				}
			}
        } else if (SMSBroadcastManager.ACTION_SYNC_ORDER_FINISHED
                .equals(action)) {
			Intent updateIntent = new Intent();
			updateIntent.putExtra(EXTRA_LOCAL_ORDER_ID, mOrder.mUUID);
			updatePage(updateIntent);

			NotificationManagerCompat notificationManager = NotificationManagerCompat
					.from(this);
			notificationManager.cancel(LocalUtils
					.humanIdToNotificationId(mOrder.mHumanId));
		} else if (SMSBroadcastManager.ACTION_USER_CONFIG_CHANGED
				.equals(action)) {
			invalidateOptionsMenu();
			resetBottomBtn();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.detail_activity);

		mUserController = new UserController(this);

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog
				.setMessage(getString(R.string.dialog_edit_order_progress_message));
		mProgressDialog.setCancelable(false);

		mViewPager = (ViewPager) findViewById(R.id.detail_activity_viewpager);
		mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.detail_activity_sliding_tabs);
		mBottomActionBtn = (Button) findViewById(R.id.detail_activity_action_button);
		mBottomActionBtn.setOnClickListener(this);

		mNeedStartMainWhenFinished = getIntent().getBooleanExtra(
				EXTRA_FROM_NOTIFICATION, false);

		updatePage(getIntent());
		if (mOrder == null) {
			finish();
			return;
		}

		NotificationManagerCompat notificationManager = NotificationManagerCompat
				.from(this);
		notificationManager.cancel(LocalUtils
				.humanIdToNotificationId(mOrder.mHumanId));
		
		prepareConfirmDialog();
	}

    private void prepareConfirmDialog() {
        View rootView = LayoutInflater.from(this).inflate(
                R.layout.dialog_choose_amount_layout, null);
        final EditText editText = (EditText) rootView
                .findViewById(R.id.order_dialog_amount_edittext);
        editText.setHint(R.string.hint_cancel_order);

        mConfirmDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_confirm_order_cancel_title)
                .setView(rootView)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                String description = editText.getText()
                                        .toString();
                                boolean start = BackendService.cancelOrder(
                                        OrderDetailActivity.this,
                                        mOrder.mCustomerId, mOrder.mUUID, mOrder.mHumanId,
                                        description);
                                if (start) {
                                    mProgressDialog.show();
                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {

                            }
                        }).create();

        mConfirmDialog.getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (mProgressDialog.isShowing()) {
			return;
		}

		updatePage(intent);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();

		if (mNeedStartMainWhenFinished) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	private void updatePage(Intent intent) {
		LocalOrder newOrder = intent.getParcelableExtra(EXTRA_LOCAL_ORDER);
		ArrayList<LocalOperate> newOperates = intent
				.getParcelableArrayListExtra(EXTRA_LOCAL_ORDER_OPERATES);
		ArrayList<LocalOrderItem> newOrderItems = intent
				.getParcelableArrayListExtra(EXTRA_LOCAL_ORDER_ORDERITENS);
		if (newOrder != null && newOperates != null && newOrderItems != null) {
			Collections.sort(newOperates, new Comparator<LocalOperate>() {

				@Override
				public int compare(LocalOperate lhs, LocalOperate rhs) {
					if (lhs.mDate < rhs.mDate) {
						return 1;
					} else if (lhs.mDate > rhs.mDate) {
						return -1;
					} else {
						return 0;
					}
				}
			});

			Collections.sort(newOrderItems, new Comparator<LocalOrderItem>() {

				@Override
				public int compare(LocalOrderItem lhs, LocalOrderItem rhs) {
					if (lhs.mAmount < rhs.mAmount) {
						return 1;
					} else if (rhs.mAmount < lhs.mAmount) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			
			mOrder = newOrder;
			mOrderItems = newOrderItems;
			mOperates = newOperates;
			mViewPager.setAdapter(new OrderDetailPagerAdapter(this, mOrder,
					mOrderItems, mOperates));
			mSlidingTabLayout.setViewPager(mViewPager);
			LoaderManager laoderManager = getLoaderManager();
			if (laoderManager.getLoader(DETAIL_LOADER_ID) == null) {
				getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this)
						.forceLoad();
			} else {
				getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this)
						.forceLoad();
			}
		} else {
			String newOrderId = intent.getStringExtra(EXTRA_LOCAL_ORDER_ID);

			if (TextUtils.isEmpty(newOrderId)) {
				return;
			}

			LocalOrder newState = LocalOrder.getOrderById(this, newOrderId);
			if (newState == null) {
				return;
			}

			mOrder = newState;
			LoaderManager laoderManager = getLoaderManager();
			if (laoderManager.getLoader(DETAIL_LOADER_ID) == null) {
				getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this)
						.forceLoad();
			} else {
				getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this)
						.forceLoad();
			}
		}

		if (mOrder.mRead == 0) {
			ContentValues values = new ContentValues();
			values.put(LocalOrder.READ, 1);
			getContentResolver().update(LocalOrder.CONTENT_URI, values,
					LocalOrder.ITEM_SELECTION, new String[] { mOrder.mUUID });
		}

		setTitle(mOrder.mHumanId);
		invalidateOptionsMenu();
		resetBottomBtn();
	}

	@Override
	protected void onDestroy() {
		LoaderManager loaderManager = getLoaderManager();
		if (loaderManager.getLoader(DETAIL_LOADER_ID) != null) {
			getLoaderManager().destroyLoader(DETAIL_LOADER_ID);
		}

		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(EXTRA_LOCAL_ORDER_ID, mOrder.mUUID);
	}

	@Override
	public Loader<Void> onCreateLoader(int id, Bundle args) {
		return new OrderDetailLoader(this, mOrder.mUUID);
	}

	@Override
	public void onLoadFinished(Loader<Void> loader, Void data) {
		OrderDetailLoader detailLoader = (OrderDetailLoader) loader;
		ArrayList<LocalOrderItem> items = detailLoader.getOrderItems();
		if (items != null && !items.isEmpty()) {
			mOrderItems = items;
			mViewPager.setAdapter(new OrderDetailPagerAdapter(this, mOrder,
					mOrderItems, detailLoader.getOperates()));
			mSlidingTabLayout.setViewPager(mViewPager);	
		}
	}

	@Override
	public void onLoaderReset(Loader<Void> loader) {
		mViewPager.setAdapter(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		if (mUserController.userIsDirector() || mOrder == null) {
			return true;
		}

		if (!LocalUtils.ORDER_STATE_CANCELED.equals(mOrder.mState)
				&& !LocalUtils.ORDER_STATE_RECEIVED.equals(mOrder.mState)) {
			getMenuInflater().inflate(R.menu.activity_detail, menu);

			if (!mUserController.userIsSupplier()
					&& !LocalUtils.ORDER_STATE_PENDING.equals(mOrder.mState)
					&& !LocalUtils.ORDER_STATE_CONFIRMED.equals(mOrder.mState)) {
				menu.removeItem(R.id.action_edit_order);
				menu.removeItem(R.id.action_cancel_order);
			}

			if (!mUserController.getUserCanCancelOrder()) {
				menu.removeItem(R.id.action_cancel_order);
			}

			if (!mUserController.getUserCanCanEditOrder()) {
				menu.removeItem(R.id.action_edit_order);
			}

			if (!mUserController.getUserCanMakeOrder()) {
				menu.removeItem(R.id.action_copy_order);
			}
		}

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit_order: {
			Intent intent = new Intent(this, EditOrderActivity.class);
			intent.putExtra(EditOrderActivity.EXTRA_ORDER, (Parcelable) mOrder);

			intent.putParcelableArrayListExtra(
					EditOrderActivity.EXTRA_PRODUCT_LIST,
					orderItemsToProducts(mOrderItems));
			startActivityForResult(intent, EDIT_ORDER_RESULT);
			return true;
		}
		case R.id.action_copy_order: {
			Intent intent = new Intent(this, EditOrderActivity.class);
			intent.putExtra(EditOrderActivity.EXTRA_CUSTOMER_ID,
					mOrder.mCustomerId);
			intent.putParcelableArrayListExtra(
					EditOrderActivity.EXTRA_PRODUCT_LIST,
					orderItemsToProducts(mOrderItems));
			startActivity(intent);
			return true;
		}
		case R.id.action_cancel_order: {
			showCancelConfirmDialog();
			return true;
		}
		case android.R.id.home:
			finish();
			if (mNeedStartMainWhenFinished) {
				Intent intent = new Intent(this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDIT_ORDER_RESULT && resultCode == RESULT_OK) {
			updatePage(data);
		} else if (mOrder != null) {
			Intent intent = new Intent();
			intent.putExtra(OrderDetailActivity.EXTRA_LOCAL_ORDER_ID, mOrder.mUUID);
			updatePage(intent);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.detail_activity_action_button:
			if (LocalUtils.ORDER_STATE_PENDING.equals(mOrder.mState)
					|| LocalUtils.ORDER_STATE_CONFIRMED.equals(mOrder.mState)
					|| LocalUtils.ORDER_STATE_DELIVERED.equals(mOrder.mState)) {
				StateChangeConfirmDialog fragment = new StateChangeConfirmDialog();
				Bundle args = new Bundle();
				if (LocalUtils.ORDER_STATE_PENDING.equals(mOrder.mState)) {
					args.putString(StateChangeConfirmDialog.EXTRA_TARGET_STATE,
							LocalUtils.ORDER_STATE_CONFIRMED);
				} else if (LocalUtils.ORDER_STATE_CONFIRMED.equals(mOrder.mState)) {
					args.putString(StateChangeConfirmDialog.EXTRA_TARGET_STATE,
							LocalUtils.ORDER_STATE_DELIVERED);
				} else {
					args.putString(StateChangeConfirmDialog.EXTRA_TARGET_STATE,
							LocalUtils.ORDER_STATE_RECEIVED);
				}
				fragment.setArguments(args);
				fragment.show(getFragmentManager(), CONFIRM_DIALOG_TAG);
			}
			break;
		default:
			break;
		}
	}

	private void resetBottomBtn() {
		if (mUserController.userIsSupplier()) {
			if (LocalUtils.ORDER_STATE_DELIVERED.equals(mOrder.mState)
					|| LocalUtils.ORDER_STATE_RECEIVED.equals(mOrder.mState)
					|| LocalUtils.ORDER_STATE_CANCELED.equals(mOrder.mState)) {
				mBottomActionBtn.setVisibility(View.GONE);
			} else if (LocalUtils.ORDER_STATE_PENDING.equals(mOrder.mState) &&
					!mUserController.getUserCanConfirmOrder()) {
				mBottomActionBtn.setVisibility(View.GONE);
			} else if (LocalUtils.ORDER_STATE_CONFIRMED.equals(mOrder.mState) &&
					!mUserController.getUserCanDeliverOrder()) {
				mBottomActionBtn.setVisibility(View.GONE);
			} else {
				mBottomActionBtn.setVisibility(View.VISIBLE);
				mBottomActionBtn
						.setText(LocalUtils.getSuplierActionTextWithOrderState(
								this, mOrder.mState));
			}
		} else if (mUserController.userIsCustomer()) {
			if (LocalUtils.ORDER_STATE_DELIVERED.equals(mOrder.mState)
					&& mUserController.getUserCanReceiveOrder()) {
				mBottomActionBtn.setVisibility(View.VISIBLE);
				mBottomActionBtn
						.setText(LocalUtils.getSuplierActionTextWithOrderState(
								this, mOrder.mState));
			} else {
				mBottomActionBtn.setVisibility(View.GONE);
			}
		} else {
			mBottomActionBtn.setVisibility(View.GONE);
		}
	}

	@Override
	public void onOrderStateChangeConfirmed(String targetState) {
		if (LocalUtils.ORDER_STATE_CONFIRMED.equals(targetState)) {
			if (BackendService.confirmOrder(this, mOrder.mCustomerId,
					mOrder.mUUID, mOrder.mHumanId, mOrder.mChangeId)) {
				mProgressDialog.show();
			}
		} else if (LocalUtils.ORDER_STATE_DELIVERED.equals(targetState)) {
			if (BackendService.deliverOrder(this, mOrder.mCustomerId,
					mOrder.mUUID, mOrder.mHumanId, mOrder.mChangeId)) {
				mProgressDialog.show();
			}
		} else if (LocalUtils.ORDER_STATE_RECEIVED.equals(targetState)) {
			if (BackendService.receiveOrder(this, mOrder.mCustomerId,
					mOrder.mUUID, mOrder.mHumanId, mOrder.mChangeId)) {
				mProgressDialog.show();
			}
		}
	}

	private void showCancelConfirmDialog() {
	    if (!mConfirmDialog.isShowing()) {
	        mConfirmDialog.show();
	    }
	}

	private ArrayList<LocalProduct> orderItemsToProducts(List<LocalOrderItem> items) {
		ArrayList<LocalProduct> prodecus = new ArrayList<LocalProduct>();
		for (LocalOrderItem item : items) {
			prodecus.add(LocalUtils.orderItemToProductLocal(item));
		}
		
		return prodecus;
	}

}
