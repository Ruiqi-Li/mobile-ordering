package sg.com.smartmediasoft.storeclient.ui.tamplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.AppPreference;
import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.UserController;
import sg.com.smartmediasoft.storeclient.database.LocalOrderTemplate;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.ui.ProductSelectFragmentBase;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OrderTemplateProductFragment extends ProductSelectFragmentBase
		implements OnClickListener {

	private PreviewDialogListAdapter mAdapter;
	private EditText mNameView;

	private ArrayList<LocalProduct> mProducts;
	private AppPreference mPreference;
	private LocalOrderTemplate mOrderTelplate;
	private UserController mUserController;
	private View mBottomBtn;

	@Override
	public void getListereningActions(Set<String> actions) {
		actions.add(SMSBroadcastManager.ACTION_UPDATE_ORDER_TEMPLATE_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_USER_CONFIG_CHANGED);
	}

	@Override
	public void onActionReveived(Context context, String action, Intent data) {
		if (SMSBroadcastManager.ACTION_UPDATE_ORDER_TEMPLATE_FINISHED
						.equals(action)) {
			dismessProcessDialog();

			int resultCode = data.getIntExtra(
					SMSBroadcastManager.EXTRA_RESULT_CODE,
					LocalUtils.CODE_FAILED_UNKNOW);
			if (resultCode == LocalUtils.CODE_SUCCESS) {
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();

				Toast.makeText(context, R.string.toast_common_change_success,
						Toast.LENGTH_SHORT).show();
			} else {
				if (SMSBroadcastManager.ACTION_UPDATE_ORDER_TEMPLATE_FINISHED
						.equals(action)
						&& resultCode == LocalUtils.CODE_ERROR_DATA_FOUND) {
					new AlertDialog.Builder(OrderTemplateProductFragment.this.getActivity())
							.setTitle(R.string.dialog_template_deleted_title)
							.setMessage(
									R.string.dialog_template_deleted_message)
							.setPositiveButton(android.R.string.ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {

										}
									})
							.setOnDismissListener(new OnDismissListener() {

								@Override
								public void onDismiss(DialogInterface dialog) {
									getActivity().finish();
								}
							}).create().show();
					return;
				}

				Toast.makeText(context, R.string.toast_common_change_failed,
						Toast.LENGTH_SHORT).show();
			}
		} else if (SMSBroadcastManager.ACTION_USER_CONFIG_CHANGED
				.equals(action)) {
			if (mOrderTelplate == null
					&& !mUserController.getUserCanEditOrderTemplate()) {
				getActivity().finish();
				return;
			}

			getActivity().invalidateOptionsMenu();
			updateView();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		mUserController = new UserController(getActivity());

		Bundle args = getArguments();
		if (args != null) {
			mOrderTelplate = args
					.getParcelable(OrderTamplateActivity.EXTRA_ORDER_TEMPLATE);
		}

		if (mOrderTelplate != null) {
			mProducts = LocalUtils.extractProductFromTemplate(getActivity(),
					mOrderTelplate);
		}

		return inflater.inflate(R.layout.template_product_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mPreference = new AppPreference(getActivity());

		mBottomBtn = view.findViewById(R.id.template_preview_btn);
		mBottomBtn.setOnClickListener(this);
		mNameView = (EditText) view.findViewById(R.id.tamplate_name);
		if (mOrderTelplate == null) {
			mNameView.setHint(getString(R.string.template_name_hint,
					mPreference.getTemplateCount()));
		} else {
			mNameView.setText(mOrderTelplate.mName);
			mNameView.setSelection(mOrderTelplate.mName.length());
		}
		
		updateView();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		if (mOrderTelplate != null && mUserController.getUserCanEditOrderTemplate()) {
			inflater.inflate(R.menu.activity_template, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_delete:
			if (mOrderTelplate != null) {
				showCancelConfirmDialog();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public List<LocalProduct> getInitialSelectItems() {
		return mProducts;
	}

	@Override
	public void OnProductSelected(LocalProduct product) {
		showAmountDialog(product);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.template_preview_btn:
			if (getProductSelectMap().isEmpty()) {
				Toast.makeText(getActivity(),
						R.string.toast_order_product_list_is_empty,
						Toast.LENGTH_SHORT).show();
			} else {
				showPreviewDialog();
			}
			break;
		default:
			break;
		}
	}

	private void updateView() {
		if (mUserController.getUserCanEditOrderTemplate()) {
			mBottomBtn.setVisibility(View.VISIBLE);
			mNameView.setEnabled(true);
		} else {
			mBottomBtn.setVisibility(View.GONE);
			mNameView.setEnabled(false);
		}
	}

	private void showPreviewDialog() {
		mAdapter = new PreviewDialogListAdapter(getActivity(),
				new ArrayList<LocalProduct>(getProductSelectMap().values()));

		String title = mNameView.getText().toString();
		if (TextUtils.isEmpty(title)) {
			if (mOrderTelplate == null) {
				title = mNameView.getHint().toString();
			} else {
				Toast.makeText(getActivity(),
						R.string.toast_tempalte_name_is_none,
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				getActivity());
		builderSingle.setIcon(R.drawable.ic_launcher);
		builderSingle.setTitle(title);
		builderSingle.setAdapter(mAdapter, null);
		builderSingle.setNegativeButton(R.string.dialog_continue,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builderSingle.setPositiveButton(R.string.dialog_finished,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPreference.increaceTemplateCount();
						startNetworkChangeTemplate();
						dialog.dismiss();
					}
				});
		builderSingle.show();
	}

	private void startNetworkChangeTemplate() {
		String title = mNameView.getText().toString();
		if (TextUtils.isEmpty(title)) {
			title = mNameView.getHint().toString();
		}

		List<LocalProduct> datas = mAdapter.getDatas();
		ArrayList<String> productIdList = new ArrayList<String>();
		double[] amounts = new double[datas.size()];
		for (int i = 0; i < datas.size(); i++) {
			LocalProduct product = datas.get(i);
			productIdList.add(product.mUUID);
			amounts[i] = product.mAmount;
		}

		boolean start = BackendService.updateOrderTemplate(getActivity(),
				productIdList, amounts, title);
		if (start) {
			showProcessDialog();
		}
	}

	private void showAmountDialog(final LocalProduct selectProduct) {
		View rootView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_choose_amount_layout, null);
		final EditText editText = (EditText) rootView
				.findViewById(R.id.order_dialog_amount_edittext);
		final TextView unitText = (TextView) rootView
				.findViewById(R.id.order_dialog_amount_unitname);
		configAmountEditText(editText, selectProduct.mDecimalUnit == 1);

		unitText.setText(selectProduct.mUnitName);
		if (selectProduct.mDecimalUnit == 1) {
			editText.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			editText.setHint(R.string.edit_dialog_edittext_decimal_hint);

			if (selectProduct.mAmount > 0) {
				String amountText = String.valueOf(selectProduct.mAmount);
				editText.setText(amountText);
				editText.setSelection(amountText.length());
			}
		} else {
			editText.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_SIGNED);
			editText.setHint(R.string.edit_dialog_edittext_single_hint);

			if (selectProduct.mAmount > 0) {
				String amountText = String.valueOf((int) selectProduct.mAmount);
				editText.setText(amountText);
				editText.setSelection(amountText.length());
			}
		}

		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.edit_dialog_title_amount_select)
				.setView(rootView)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String amountString = editText.getText()
										.toString();
								double amount = TextUtils.isEmpty(amountString) ? 0
										: Double.valueOf(amountString);
								Map<String, LocalProduct> selectMap = getProductSelectMap();
								if (amount > 0) {
									selectProduct.mAmount = amount;
									selectMap.put(selectProduct.mUUID,
											selectProduct);
								} else {
									selectMap.remove(selectProduct.mUUID);
								}

								notifyProductChange();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						}).create();

		dialog.getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();
	}
	
	private void showCancelConfirmDialog() {
		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.dialog_delete_tempalte_title)
				.setMessage(R.string.dialog_delete_tempalte_message)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								boolean start = BackendService.deleteOrderTemplate(
										getActivity(), mOrderTelplate.mName);
								if (start) {
									showProcessDialog();
								}
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						}).create();

		dialog.show();
	}

}
