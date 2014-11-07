package sg.com.smartmediasoft.storeclient.ui.main;

import java.util.ArrayList;
import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.UserController;
import sg.com.smartmediasoft.storeclient.database.LocalCustomer;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SupplierDrawerAdapter extends BaseAdapter {
	public static final int USER_ITEM_POSITION 				= 0;
	public static final int SUPPLIER_STORAGE_POSITION 		= 1;
	public static final int USER_CUSTOMER_DIVILIER_POSITION = 2;

	private static final int CUSTOMER_ITEM_COUNT = 3;

	private static final int VIEW_TYPE_COUNT = 4;

	private static final int VIEW_TYPE_USER_LAYOUT 			= 0;
	private static final int VIEW_TYPE_MAIN_MENU 			= 1;
	private static final int VIEW_TYPE_CUSTOMER_DIVILIER 	= 2;
	private static final int VIEW_TYPE_CUSTOMER_ITEM 		= 3;

	private Context mContext;
	private ArrayList<LocalCustomer> mCustomers;
	private String mUserTitle;
	private String mUserSumery;
	private boolean mChineseDevice;

	private int mTotalUnReveivedCount;
	private int mTotalUnShipedCount;

	public SupplierDrawerAdapter(Context context) {
		mContext = context;
		mCustomers = new ArrayList<LocalCustomer>();
		UserController controller = new UserController(context);
		mUserTitle = LocalUtils.isDeveceInChinese() ? controller
				.getChineseCompanyName() : controller.getEnglishCompanyName();
		mUserSumery = context.getString(R.string.text_login_user,
				controller.getDisplayName());
		mChineseDevice = LocalUtils.isDeveceInChinese();
	}

	public ArrayList<LocalCustomer> getCustomerList() {
		return mCustomers;
	}

	public void changeCursor(Cursor cursor, int unReceivedCount,
			int unShipedCount) {
		mTotalUnReveivedCount = unReceivedCount;
		mTotalUnShipedCount = unShipedCount;

		try {
			mCustomers.clear();
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					for (; !cursor.isAfterLast(); cursor.moveToNext()) {
						mCustomers.add(new LocalCustomer(cursor));
					}
				}
			}

			notifyDataSetChanged();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	@Override
	public int getCount() {
		return mCustomers.size() + CUSTOMER_ITEM_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		switch (position) {
		case USER_ITEM_POSITION:
			return VIEW_TYPE_USER_LAYOUT;
		case SUPPLIER_STORAGE_POSITION:
			return VIEW_TYPE_MAIN_MENU;
		case USER_CUSTOMER_DIVILIER_POSITION:
			return VIEW_TYPE_CUSTOMER_DIVILIER;
		default:
			return VIEW_TYPE_CUSTOMER_ITEM;
		}
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	@Override
	public LocalCustomer getItem(int position) {
		if (position < CUSTOMER_ITEM_COUNT) {
			return null;
		} else {
			return mCustomers.get(position - CUSTOMER_ITEM_COUNT);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = newView(position, convertView, parent);
		}

		bindView(position, convertView, parent);

		return convertView;
	}

	private View newView(int position, View convertView, ViewGroup parent) {
		switch (getItemViewType(position)) {
		case VIEW_TYPE_USER_LAYOUT:
			return LayoutInflater.from(mContext).inflate(
					R.layout.drawer_list_item_supplier_loginuser, null);
		case VIEW_TYPE_MAIN_MENU:
			return LayoutInflater.from(mContext).inflate(
					R.layout.drawer_menu_list_item, null);
		case VIEW_TYPE_CUSTOMER_DIVILIER:
			return LayoutInflater.from(mContext).inflate(
					R.layout.drawer_list_item_supplier_cusomter_diviler, null);
		default:
			return LayoutInflater.from(mContext).inflate(
					R.layout.drawer_list_item_customer_layout, null);
		}
	}

	private void bindView(int position, View convertView, ViewGroup parent) {
		switch (getItemViewType(position)) {
		case VIEW_TYPE_USER_LAYOUT: {
			TextView titleText = (TextView) convertView
					.findViewById(R.id.drawer_list_item_supplier_title);
			TextView summeryText = (TextView) convertView
					.findViewById(R.id.drawer_list_item_supplier_summery);
			TextView unReveivedCount = (TextView) convertView
					.findViewById(R.id.drawer_list_item_supplier_unreceived_count);
			TextView unShipedCount = (TextView) convertView
					.findViewById(R.id.drawer_list_item_supplier_unshiped_count);
			titleText.setText(mUserTitle);
			summeryText.setText(mUserSumery);
			unReveivedCount.setText(mContext.getString(
					R.string.text_wait_for_receive, mTotalUnReveivedCount));
			unShipedCount.setText(mContext.getString(
					R.string.text_wait_for_ship, mTotalUnShipedCount));
			break;
		}
		case VIEW_TYPE_MAIN_MENU: {
			TextView menuText = (TextView) convertView
					.findViewById(R.id.drawer_menu_item_name);
			ImageView menuIcon = (ImageView) convertView
					.findViewById(R.id.drawer_menu_item_icon);
			menuText.setText(R.string.text_store_manage);
			menuIcon.setImageResource(R.drawable.ic_action_storage_dark);
			break;
		}
		case VIEW_TYPE_CUSTOMER_ITEM: {
			LocalCustomer customer = getItem(position);
			TextView nameTextView = (TextView) convertView
					.findViewById(R.id.main_drawer_list_item_name);
			TextView unConfirmTextView = (TextView) convertView
					.findViewById(R.id.main_drawer_list_item_unconfirm);
			TextView unDliverTextView = (TextView) convertView
					.findViewById(R.id.main_drawer_list_item_undliver);

			nameTextView.setText(mChineseDevice ? customer.mChineseName
					: customer.mEnglishName);

			if (customer.mUnconfirmCount > 0) {
				unConfirmTextView.setText(String
						.valueOf(customer.mUnconfirmCount));
			} else {
				unConfirmTextView.setText("");
			}

			if (customer.mUnShipCount > 0) {
				unDliverTextView.setText(String.valueOf(customer.mUnShipCount));
			} else {
				unDliverTextView.setText("");
			}
		}
		}
	}

}