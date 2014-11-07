package sg.com.smartmediasoft.storeclient.ui.orderedit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.database.LocalOrder;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.ui.NotifyAwareActivity;
import sg.com.smartmediasoft.storeclient.ui.orderdetail.OrderDetailActivity;
import sg.com.smartmediasoft.storeclient.ui.orderedit.DialogSendOrder.SendOrderDialogCallback;
import sg.com.smartmediasoft.storeclient.ui.orderedit.OrderPreviewFragment.EditOrderStep3Callback;
import sg.com.smartmediasoft.storeclient.ui.orderedit.ProductSelectFragment.EditOrderStep2Callback;
import sg.com.smartmediasoft.storeclient.ui.orderedit.DateSelectFragment.EditOrderStep1Callback;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

public class EditOrderActivity extends NotifyAwareActivity implements
		EditOrderActivityInterface, EditOrderStep1Callback,
		EditOrderStep2Callback, EditOrderStep3Callback, SendOrderDialogCallback {
	public static final String EXTRA_PRODUCT_LIST = "product_list";
	public static final String EXTRA_ORDER = "order";
	public static final String EXTRA_CUSTOMER_ID = "customer_id";

	private ViewPager mViewPager;
	private DateSelectFragment mFragmentStep1;
	private ProductSelectFragment mFragmentStep2;
	private OrderPreviewFragment mFragmentStep3;

	private LocalOrder mOrder;
	private ArrayList<LocalProduct> mProducts;
	private String mDescription;
	private int mOrderRange = 1;

	private ProgressDialog mProgressDialog;

	@Override
	public void getListereningActions(Set<String> actions) {
		actions.add(SMSBroadcastManager.ACTION_MAKE_ORDER_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_EDIT_ORDER_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_GET_ORDER_DAYS_FINISHED);
	}

	@Override
	public void onActionReveived(Context context, String action, Intent data) {
		if (SMSBroadcastManager.ACTION_MAKE_ORDER_FINISHED.equals(action)
				|| SMSBroadcastManager.ACTION_EDIT_ORDER_FINISHED
						.equals(action)) {
			int resultCode = data.getIntExtra(
					SMSBroadcastManager.EXTRA_RESULT_CODE,
					LocalUtils.CODE_FAILED_UNKNOW);
			if (resultCode == LocalUtils.CODE_SUCCESS) {
				if (mProgressDialog.isShowing()) {
					Toast.makeText(EditOrderActivity.this,
							R.string.toast_make_new_order_success,
							Toast.LENGTH_SHORT).show();

					mProgressDialog.hide();
					Intent intent = new Intent();
					intent.putExtra(OrderDetailActivity.EXTRA_LOCAL_ORDER_ID, mOrder.mUUID);
					setResult(RESULT_OK, intent);
					finish();
				}
			} else {
				mProgressDialog.hide();
				
				if (resultCode == LocalUtils.CODE_FAILED_DATA_ALREADY_CHANGE ||
						resultCode == LocalUtils.CODE_ERROR_DATA_FOUND) {
					Toast.makeText(EditOrderActivity.this,
							R.string.toast_order_already_changed, Toast.LENGTH_SHORT)
							.show();
					Intent intent = new Intent();
					intent.putExtra(OrderDetailActivity.EXTRA_LOCAL_ORDER_ID, mOrder.mUUID);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					String serverMessage = data
							.getStringExtra(SMSBroadcastManager.DATA_MESSAGE);
					String message = TextUtils.isEmpty(serverMessage) ? getString(R.string.toast_make_new_order_failed)
							: serverMessage;
					showErrorMessageDialog(message);
				}
			}
		} else if (SMSBroadcastManager.ACTION_GET_ORDER_DAYS_FINISHED.equals(action)) {
			mOrderRange = (int) data.getLongExtra(SMSBroadcastManager.DATA_INT_1, 1);
			mFragmentStep1.initCalendar(new Date(mOrder.mTargetDate), mOrderRange);
			mProgressDialog.dismiss();
		}
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof DateSelectFragment) {
			mFragmentStep1 = (DateSelectFragment) fragment;
			boolean start = BackendService.startGetOrderDays(this);
			if (start) {
				mProgressDialog.show();
			} else {
				finish();
				return;
			}
		} else if (fragment instanceof OrderPreviewFragment) {
			mFragmentStep3 = (OrderPreviewFragment) fragment;
		} else if (fragment instanceof ProductSelectFragment) {
			mFragmentStep2 = (ProductSelectFragment) fragment;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog
				.setMessage(getString(R.string.dialog_edit_order_progress_message));
		mProgressDialog.setCancelable(false);

		if (savedInstanceState == null) {
			mOrder = getIntent().getParcelableExtra(EXTRA_ORDER);
			mProducts = getIntent().getParcelableArrayListExtra(
					EXTRA_PRODUCT_LIST);

			if (mOrder == null) {
				String customerId = getIntent().getStringExtra(
						EXTRA_CUSTOMER_ID);
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_YEAR, 1);
				
				mOrder = new LocalOrder();
				mOrder.mTargetDate = calendar.getTimeInMillis();
				mOrder.mCustomerId = customerId;
			}
		} else {
			mOrder = savedInstanceState.getParcelable(EXTRA_ORDER);
		}

		setContentView(R.layout.order_activity);

		mViewPager = (ViewPager) findViewById(R.id.order_view_pager);
		mViewPager.setAdapter(new EditOrderPagerAdapter(getFragmentManager()));

		scrollToPosition(EditOrderPagerAdapter.EDITORDER_STEP1_PAGE);
		
		BackendService.syncProducts(this, false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable(EXTRA_ORDER, mOrder);
		outState.putParcelableArrayList(EXTRA_PRODUCT_LIST, mProducts);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		if (mViewPager.getCurrentItem() == EditOrderPagerAdapter.EDITORDER_STEP3_PAGE) {
			scrollToPosition(EditOrderPagerAdapter.EDITORDER_STEP2_PAGE);
			return;
		} else if (mViewPager.getCurrentItem() == EditOrderPagerAdapter.EDITORDER_STEP2_PAGE) {
			mFragmentStep2.closeSearchBoxIfNeeded();
			scrollToPosition(EditOrderPagerAdapter.EDITORDER_STEP1_PAGE);
			return;
		}

		super.onBackPressed();
	}

	@Override
	public void onStep1Finished(long targetDate) {
		mOrder.mTargetDate = targetDate;

		mFragmentStep2.notifyProductChange();
		scrollToPosition(EditOrderPagerAdapter.EDITORDER_STEP2_PAGE);
	}

	@Override
	public ArrayList<LocalProduct> step2GetInitialProductList() {
		return mProducts;
	}

	@Override
	public void onStep2BackToPreviousStep(ArrayList<LocalProduct> datas) {
		mProducts = datas;
		mFragmentStep1.initCalendar(new Date(mOrder.mTargetDate), mOrderRange);
		scrollToPosition(EditOrderPagerAdapter.EDITORDER_STEP1_PAGE);
	}

	@Override
	public void onStep2Finished(ArrayList<LocalProduct> datas) {
		mProducts = datas;
		mFragmentStep3.changeDate(datas);

		scrollToPosition(EditOrderPagerAdapter.EDITORDER_STEP3_PAGE);
	}

	@Override
	public void onStep3BackToPreviousStep() {
		scrollToPosition(EditOrderPagerAdapter.EDITORDER_STEP2_PAGE);
	}

	@Override
	public void onStep3SubmitOrder(String description) {
		mDescription = description;

		DialogSendOrder confirmDialog = new DialogSendOrder();
		confirmDialog.show(getFragmentManager(), "orderconfirm");
	}

	@Override
	public Date getSelectedOrderDate() {
		return new Date(mOrder.mTargetDate);
	}

	@Override
	public ArrayList<LocalProduct> getSelectedProductList() {
		return mProducts;
	}

	@Override
	public void onSendOrder() {
		ArrayList<String> productIds = new ArrayList<String>();
		ArrayList<Double> amoundsList = new ArrayList<Double>();

		for (LocalProduct product : mProducts) {
			productIds.add(product.mUUID);
			amoundsList.add(product.mAmount);
		}

		boolean started = false;

		if (mOrder.mUUID == null) {
			started = BackendService.makeOrder(this, productIds, LocalUtils
					.listToArray(amoundsList), new Date(mOrder.mTargetDate),
					mOrder.mCustomerId, mDescription);
		} else {
			started = BackendService.amendOrder(this, productIds, LocalUtils
					.listToArray(amoundsList), new Date(mOrder.mTargetDate),
					mOrder.mUUID, mOrder.mChangeId, mOrder.mCustomerId,
					mDescription);
		}

		if (started) {
			mProgressDialog.show();
		}
	}
	
	private void showErrorMessageDialog(String message) {
		new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_edit_order_field)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditOrderActivity.this.finish();
					}
				})
				.create().show();
	}

	private void scrollToPosition(int position) {
		mViewPager.setCurrentItem(position, true);
		switch (position) {
		case EditOrderPagerAdapter.EDITORDER_STEP1_PAGE:
			setTitle(R.string.editorder_step_one_fragment_title);
			break;
		case EditOrderPagerAdapter.EDITORDER_STEP2_PAGE:
			setTitle(R.string.editorder_step_two_fragment_title);
			break;
		case EditOrderPagerAdapter.EDITORDER_STEP3_PAGE:
			setTitle(R.string.editorder_step_three_fragment_title);
			break;
		default:
			break;
		}
	}

}
