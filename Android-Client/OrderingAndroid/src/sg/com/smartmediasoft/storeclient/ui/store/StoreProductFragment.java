package sg.com.smartmediasoft.storeclient.ui.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.StoreApplication;
import sg.com.smartmediasoft.storeclient.TextHighlighter;
import sg.com.smartmediasoft.storeclient.UserController;
import sg.com.smartmediasoft.storeclient.database.LocalCustomer;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.ui.IndexerListAdapter;
import sg.com.smartmediasoft.storeclient.ui.IndexerListAdapter.Placement;
import sg.com.smartmediasoft.storeclient.ui.ProductSelectFragmentBase;
import sg.com.smartmediasoft.storeclient.ui.ProductSelectLoader;
import sg.com.smartmediasoft.storeclient.ui.store.StoreListItemLayout.StoreActionCallbeck;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StoreProductFragment extends ProductSelectFragmentBase implements
		StoreActionCallbeck {
	public static final String EXTRA_IS_SUPPLIER_STORE = "supplier_store";
	public static final String EXTRA_CUSTOMER_ID = "customer_id";

	private CheckBox mCheckBox;
	private boolean mSupplierStore;
	private boolean mUserIsSupplier;
	private boolean mUserCanChangeStorage;
	private String mCustomerId;
	
	protected TextHighlighter mTextHighlighter;

	@Override
	public void getListereningActions(Set<String> actions) {
		actions.add(SMSBroadcastManager.ACTION_SYNC_CUSTOMER_STORE_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_SYNC_SUPPLIER_STORE_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_UPDATE_STORE_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_SYNC_PRODUCT_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_USER_CONFIG_CHANGED);
	}

	@Override
	public void onActionReveived(Context context, String action, Intent data) {
		if (SMSBroadcastManager.ACTION_SYNC_PRODUCT_FINISHED.equals(action)) {
			int resultCode = data.getIntExtra(
					SMSBroadcastManager.EXTRA_RESULT_CODE,
					LocalUtils.CODE_FAILED_UNKNOW);
			if (resultCode == LocalUtils.CODE_SUCCESS) {
				restartLoader();
			}
		} else if (SMSBroadcastManager.ACTION_UPDATE_STORE_FINISHED
				.equals(action)) {
			dismessProcessDialog();

			int resultCode = data.getIntExtra(
					SMSBroadcastManager.EXTRA_RESULT_CODE,
					LocalUtils.CODE_FAILED_UNKNOW);
			if (resultCode == LocalUtils.CODE_SUCCESS) {
				resetSelectedData();
				restartLoader();

				Toast.makeText(context, R.string.toast_common_change_success,
						Toast.LENGTH_SHORT).show();
			} else if (resultCode == LocalUtils.CODE_FAILED_DATA_ALREADY_CHANGE) {
				Bundle bundle = data.getExtras();
				String productId = bundle
						.getString(SMSBroadcastManager.DATA_EXRTA_ID);
				double intentChangeValue = bundle
						.getDouble(SMSBroadcastManager.DATA_EXTRA_VALUE);

				Map<String, LocalProduct> selectedMap = getProductSelectMap();
				LocalProduct product = selectedMap.get(productId);
				double oldValue = product == null ? 0 : product.mAmount;

				resetSelectedData();
				double newAmount = 0;
				if (selectedMap.containsKey(productId)) {
					product = selectedMap.get(productId);
					newAmount = product.mAmount;
				}

				String displayAmount = LocalUtils.getDesplayUnitText(oldValue,
						product.mUnitName, product.mDecimalUnit == 1)
						+ " ---> "
						+ LocalUtils.getDesplayUnitText(newAmount,
								product.mUnitName, product.mDecimalUnit == 1);

				showReconfirmRerviewDialog(
						product.mImageUrl,
						LocalUtils.isDeveceInChinese() ? product.mChineseName
								: product.mEnglishName,
						displayAmount,
						getString(R.string.dialog_change_data_positive_confirm,
								LocalUtils.getDesplayUnitText(
										intentChangeValue, product.mUnitName,
										product.mDecimalUnit == 1)), productId,
						newAmount, intentChangeValue);
			} else {
				Toast.makeText(context, R.string.toast_common_change_failed,
						Toast.LENGTH_SHORT).show();
			}
		} else if (SMSBroadcastManager.ACTION_SYNC_CUSTOMER_STORE_FINISHED
				.equals(action)
				|| SMSBroadcastManager.ACTION_SYNC_SUPPLIER_STORE_FINISHED
						.equals(action)) {
			resetSelectedData();
			restartLoader();
		} else if (SMSBroadcastManager.ACTION_USER_CONFIG_CHANGED
				.equals(action)) {
			UserController userController = new UserController(getActivity());
			mUserCanChangeStorage = userController.getUserCanChangeStorage();
			notifyProductChange();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		Bundle args = getArguments();
		if (args == null) {
			getActivity().finish();
			return null;
		}

		mTextHighlighter = new TextHighlighter(getResources().getColor(
				R.color.smartmediasoft_color), Typeface.BOLD);
		mSupplierStore = args.getBoolean(EXTRA_IS_SUPPLIER_STORE);
		mCustomerId = args.getString(EXTRA_CUSTOMER_ID);

		UserController userController = new UserController(getActivity());
		mUserIsSupplier = userController.userIsSupplier();
		mUserCanChangeStorage = userController.getUserCanChangeStorage();

		if (mSupplierStore) {
			BackendService.syncSupplierStore(getActivity(),
					userController.getSupplierStoreCheckTime(),
					userController.getSupplierStoreChanhgeId());
		} else {
			LocalCustomer custoemr = LocalCustomer.getCustomerById(
					getActivity(), mCustomerId);
			BackendService.syncCustomerStore(getActivity(), custoemr.mUuid,
					custoemr.mStoreLastCheckTime, custoemr.mStoreChangeId);
		}

		return inflater.inflate(R.layout.store_product_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mCheckBox = (CheckBox) view.findViewById(R.id.store_list_checkbox);
		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				restartLoader();
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		if (mSupplierStore || !mUserIsSupplier) {
			inflater.inflate(R.menu.fragment_store, menu);
			if (mSupplierStore) {
				menu.findItem(R.id.menu_show_storage_only).setTitle(
						R.string.menu_show_store_limited_only);
			} else {
				menu.findItem(R.id.menu_show_storage_only).setTitle(
						R.string.menu_show_store_storage_only);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_show_all:
			item.setChecked(!item.isChecked());
			resetSelectMode(false);
			return true;
		case R.id.menu_show_storage_only:
			item.setChecked(!item.isChecked());
			resetSelectMode(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View newProductItemView(IndexerListAdapter adapter, Context context,
			Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(
				R.layout.store_list_item_layout, null);
	}

	@Override
	public void bindProductItemView(IndexerListAdapter adapter, View view,
			Context context, Cursor cursor) {
		StoreListItemLayout itenLayout = (StoreListItemLayout) view;

		String uuid = cursor
				.getString(LocalProduct.CONTENT_PROJECTION_UUID_INDEX);
		String name = mIdDeviceInChinese ? cursor
				.getString(LocalProduct.CONTENT_PROJECTION_CHINESE_NAME_INDEX)
				: cursor.getString(LocalProduct.CONTENT_PROJECTION_ENGLISH_NAME_INDEX);
		String imageUrl = cursor
				.getString(LocalProduct.CONTENT_PROJECTION_IMAGE_URL_INDEX);
		String unitName = cursor
				.getString(LocalProduct.CONTENT_PROJECTION_UNIT_NAME_INDEX);
		int descimalUnit = cursor
				.getInt(LocalProduct.CONTENT_PROJECTION_DECIMAL_UNIT_INDEX);
		double amount = getProductSelectAmount(uuid);

		Placement placement = adapter.getItemPlacementInSection(cursor
				.getPosition());

		itenLayout.bindContent(mUserCanChangeStorage && !(!mSupplierStore && mUserIsSupplier), this,
				uuid,
				mTextHighlighter.applyPrefixHighlight(name, getQueryText()),
				unitName, descimalUnit, amount, imageUrl,
				placement.sectionHeader, placement.lastInSection);
	}

	@Override
	public void onConfigLoader(ProductSelectLoader laoder) {
		if (mUserIsSupplier && !mSupplierStore) {
			String selection = laoder.getSelection();
			List<String> selectionArgs = new ArrayList<String>(
					Arrays.asList(laoder.getSelectionArgs()));

			Map<String, LocalProduct> selectedMap = getProductSelectMap();
			StringBuilder additionalSelection = new StringBuilder();
			if (!TextUtils.isEmpty(selection)) {
				additionalSelection.append(" AND ");
			}

			additionalSelection.append(LocalProduct.UUID).append(" IN (");
			if (selectedMap != null && !selectedMap.isEmpty()) {
				for (String key : selectedMap.keySet()) {
					additionalSelection.append("?,");
					selectionArgs.add(key);
				}

				additionalSelection
						.deleteCharAt(additionalSelection.length() - 1);
				additionalSelection.append(")");
			} else {
				additionalSelection.append("?)");
				selectionArgs.add("ruiqi");
			}

			laoder.setSelection(selection + additionalSelection.toString());
			laoder.setSelectionArgs(selectionArgs
					.toArray(new String[selectionArgs.size()]));
		}
	}

	@Override
	public List<LocalProduct> getInitialSelectItems() {
		if (mSupplierStore) {
			UserController userController = new UserController(getActivity());
			return LocalUtils.getProductListByStorageInfo(getActivity(),
					userController.getSupplierStorageMap());
		} else {
			LocalCustomer custoemr = LocalCustomer.getCustomerById(
					getActivity(), mCustomerId);
			return LocalUtils.getProductListByStorageInfo(getActivity(),
					custoemr.mStoreStorageMap);
		}
	}

	@Override
	public void OnProductSelected(LocalProduct product) {

	}

	@Override
	public void onStoreActionAdd(String productUUID, String productName,
			String productUnitName, int productDecimalUnit, double productAmount) {
		showStoreChangeDialog(true, productUUID, productName, productUnitName,
				productDecimalUnit, productAmount);
	}

	@Override
	public void onStoreActionMinus(String productUUID, String productName,
			String productUnitName, int productDecimalUnit, double productAmount) {
		showStoreChangeDialog(false, productUUID, productName, productUnitName,
				productDecimalUnit, productAmount);
	}

	private void showReconfirmRerviewDialog(String imageUrl,
			String displayName, String displayAmount, String positiveBtnText,
			final String productId, final double changeFrom,
			final double changeTo) {
		View rootView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_data_change_preview_layout, null);
		ImageView imageView = (ImageView) rootView
				.findViewById(R.id.dialog_data_image);
		TextView nameView = (TextView) rootView
				.findViewById(R.id.dialog_data_name);
		TextView amountView = (TextView) rootView
				.findViewById(R.id.dialog_data_summery);

		StoreApplication.loadImage(imageUrl, imageView);
		nameView.setText(displayName);
		amountView.setText(displayAmount);

		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.dialog_server_data_already_change)
				.setView(rootView)
				.setPositiveButton(positiveBtnText,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								UserController userController = new UserController(
										getActivity());
								long storeChangeId = -1;
								if (mSupplierStore) {
									storeChangeId = userController
											.getSupplierStoreChanhgeId();
								} else {
									LocalCustomer customer = LocalCustomer
											.getCustomerById(getActivity(),
													userController
															.getCustomerId());
									storeChangeId = customer.mStoreChangeId;
								}

								boolean start = BackendService.updateStore(
										getActivity(), storeChangeId,
										productId, changeFrom, changeTo, false);
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

	private void showStoreChangeDialog(final boolean add,
			final String productUUID, final String productName,
			final String productUnitName, final int productDecimalUnit,
			final double productAmount) {
		View rootView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_store_operate_layout, null);
		final EditText addEditText = (EditText) rootView
				.findViewById(R.id.dialog_store_edit);

		TextView preTextView = (TextView) rootView
				.findViewById(R.id.dialog_store_pretext);
		TextView addUnitText = (TextView) rootView
				.findViewById(R.id.dialog_store_edit_unitname);
		addUnitText.setText(productUnitName);
		configAmountEditText(addEditText, productDecimalUnit == 1);

		if (add) {
			preTextView.setTextColor(Color.rgb(0, 204, 0));
			preTextView.setText(R.string.text_add);
		} else {
			preTextView.setTextColor(Color.rgb(204, 0, 0));
			preTextView.setText(R.string.text_minus);
		}

		String displayAmount = "";
		if (hasSelectProduct(productUUID)) {
			if (productDecimalUnit == 1) {
				displayAmount = String.valueOf(productAmount);
			} else {
				displayAmount = String.valueOf((int) productAmount);
			}

			displayAmount = displayAmount + " " + productUnitName;
		} else {
			displayAmount = getString(R.string.text_product_none);
		}

		if (mSupplierStore) {
			displayAmount = getString(
					R.string.text_prefix_supplier_storage_amount, displayAmount);
		} else {
			displayAmount = getString(
					R.string.text_prefix_customer_storage_amount, displayAmount);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setTitle(productName + " " + displayAmount)
				.setView(rootView)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String amountString = addEditText.getText()
										.toString();
								double amount = Math.abs(TextUtils
										.isEmpty(amountString) ? 0 : Double
										.valueOf(amountString));
								if (amount == 0) {
									return;
								}

								UserController userController = new UserController(
										getActivity());
								long storeChangeId = -1;
								if (userController.userIsSupplier()) {
									storeChangeId = userController
											.getSupplierStoreChanhgeId();
								} else {
									LocalCustomer customer = LocalCustomer
											.getCustomerById(getActivity(),
													userController
															.getCustomerId());
									storeChangeId = customer.mStoreChangeId;
								}

								boolean start = BackendService.updateStore(
										getActivity(), storeChangeId,
										productUUID, productAmount,
										add ? amount : -amount, false);
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
						});

		if (mSupplierStore && hasSelectProduct(productUUID)) {
			builder.setNeutralButton(R.string.text_store_reset,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String amountString = addEditText.getText()
									.toString();
							double amount = TextUtils.isEmpty(amountString) ? 0
									: Double.valueOf(amountString);
							UserController userController = new UserController(
									getActivity());
							long storeChangeId = userController
									.getSupplierStoreChanhgeId();

							boolean start = BackendService
									.updateStore(getActivity(), storeChangeId,
											productUUID, productAmount,
											add ? amount : -amount, true);
							if (start) {
								showProcessDialog();
							}
						}

					});
		}

		Dialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();
	}

}
