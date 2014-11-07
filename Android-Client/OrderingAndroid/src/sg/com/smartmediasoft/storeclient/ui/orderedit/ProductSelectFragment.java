package sg.com.smartmediasoft.storeclient.ui.orderedit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import sg.com.smartmediasoft.storeclient.ui.ProductSelectFragmentBase;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProductSelectFragment extends ProductSelectFragmentBase implements
		OnClickListener {
	public static interface EditOrderStep2Callback {
		public void onStep2Finished(ArrayList<LocalProduct> datas);

		public ArrayList<LocalProduct> step2GetInitialProductList();

		public void onStep2BackToPreviousStep(ArrayList<LocalProduct> datas);
	}

	private Button mBottomLeftBtn;
	private Button mBottomRightBtn;

	private EditOrderStep2Callback mCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setHasOptionsMenu(true);

		mCallback = (EditOrderStep2Callback) activity;
	}

	@Override
	public void onDetach() {
		mCallback = null;

		super.onDetach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.step_two_product_select_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mBottomLeftBtn = (Button) view
				.findViewById(R.id.select_product_fragment_bottom_left_btn);
		mBottomRightBtn = (Button) view
				.findViewById(R.id.select_product_fragment_bottom_right_btn);
		mBottomLeftBtn.setOnClickListener(this);
		mBottomRightBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_product_fragment_bottom_left_btn: {
			closeSearchBoxIfNeeded();
			Map<String, LocalProduct> selectMap = getProductSelectMap();
			mCallback.onStep2BackToPreviousStep(new ArrayList<LocalProduct>(
					selectMap.values()));
			break;
		}
		case R.id.select_product_fragment_bottom_right_btn: {
			closeSearchBoxIfNeeded();
			Map<String, LocalProduct> selectMap = getProductSelectMap();
			if (selectMap.isEmpty()) {
				Toast.makeText(getActivity(),
						R.string.toast_order_product_list_is_empty,
						Toast.LENGTH_SHORT).show();
			} else {
				mCallback.onStep2Finished(new ArrayList<LocalProduct>(selectMap
						.values()));
			}
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void OnProductSelected(LocalProduct product) {
		showAmountDialog(product);
	}

	@Override
	public List<LocalProduct> getInitialSelectItems() {
		return mCallback.step2GetInitialProductList();
	}

	@Override
	public void getListereningActions(Set<String> actions) {

	}

	@Override
	public void onActionReveived(Context context, String action, Intent data) {

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

}
