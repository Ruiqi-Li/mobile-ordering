package sg.com.smartmediasoft.storeclient.ui.statistic;

import java.util.List;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.UserController;
import sg.com.smartmediasoft.storeclient.database.LocalOrderItem;
import sg.com.smartmediasoft.storeclient.ui.NotifyAwareFragment;
import sg.com.smartmediasoft.storeclient.ui.RefreshListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class StatisticProductFragment extends NotifyAwareFragment
		implements OnClickListener {

	private RefreshListener mRefreshListener;
	private StatisticActionInterface mStatisticActionInterface;
	
	private StatisticProductAdapter mAdapter;
	private ProgressDialog mProgressDialog;

	public static interface StatisticActionInterface {
		public void onStartGenerateReport();
	}

	public void clearSelectProduct() {
		mAdapter.changeDatas(null);
	}

	@Override
	public void getListereningActions(Set<String> actions) {
		actions.add(SMSBroadcastManager.ACTION_SYNC_ORDER_RECORD_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_GENERATE_ORDER_REPORT_FINISHED);
	}

	@Override
	public void onActionReveived(Context context, String action, Intent data) {
		if (SMSBroadcastManager.ACTION_SYNC_ORDER_RECORD_FINISHED
				.equals(action)) {
			mRefreshListener.onRefreshStateChange(false);

			int resultCode = data.getIntExtra(
					SMSBroadcastManager.EXTRA_RESULT_CODE,
					LocalUtils.CODE_FAILED_UNKNOW);
			if (resultCode == LocalUtils.CODE_SUCCESS) {
				Bundle datas = data.getExtras();
				List<LocalOrderItem> orderItems = datas
						.getParcelableArrayList(SMSBroadcastManager.DATA_PARCELABLE_LIST);
				if (orderItems == null || orderItems.isEmpty()) {
					Toast.makeText(getActivity(),
							R.string.toast_statistic_order_is_empty,
							Toast.LENGTH_SHORT).show();
				} else {
					mAdapter.changeDatas(orderItems);
				}
			}
		} else if (SMSBroadcastManager.ACTION_GENERATE_ORDER_REPORT_FINISHED
				.equals(action)) {
			dismessProcessDialog();
			int resultCode = data.getIntExtra(
					SMSBroadcastManager.EXTRA_RESULT_CODE,
					LocalUtils.CODE_FAILED_UNKNOW);
			if (resultCode == LocalUtils.CODE_SUCCESS) {
				Toast.makeText(getActivity(),
						R.string.toast_order_report_generate_success,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mStatisticActionInterface = (StatisticActionInterface) activity;
		mRefreshListener = (RefreshListener) activity;
	}

	@Override
	public void onDetach() {
		mRefreshListener = null;
		mStatisticActionInterface = null;

		super.onDetach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog
				.setMessage(getString(R.string.dialog_edit_order_progress_message));
		mProgressDialog.setCancelable(false);
		
		return inflater.inflate(R.layout.statistic_product_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		UserController controller = new UserController(getActivity());
		View generateBtn = view.findViewById(R.id.statistic_report_btn);
		if (controller.userIsSupplier()) {
			generateBtn.setOnClickListener(this);
		} else {
			generateBtn.setVisibility(View.GONE);
		}
		
		mAdapter = new StatisticProductAdapter(getActivity());
		ListView listView = (ListView) view.findViewById(R.id.listview);
		listView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.statistic_report_btn:
			if (mAdapter.getCount() == 0) {
				Toast.makeText(getActivity(), R.string.toast_statistic_is_empty, Toast.LENGTH_SHORT).show();
			} else {
				mStatisticActionInterface.onStartGenerateReport();
			}
			break;
		default:
			break;
		}
	}
	
	public void showProcessDialog() {
		mProgressDialog.show();
	}

	public void dismessProcessDialog() {
		mProgressDialog.dismiss();
	}
}
