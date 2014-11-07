package sg.com.smartmediasoft.storeclient.ui.main;

import java.util.ArrayList;
import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.UserController;
import sg.com.smartmediasoft.storeclient.database.LocalOrderTemplate;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomerDrawerAdapter extends BaseAdapter {
	public static final int USER_ITEM_POSITION 			= 0;
	public static final int HISTORY_ITEM_POSITION 		= 1;
	public static final int STORE_ITEM_POSITION 		= 2;
	
	
	public static final int WITH_TEMPLATE_TEMPLATE_POSITION = 3;
	public static final int WITH_TEMPLATE_TEMPLATE_DIVILER 	= 4;
	
	public static final int WITHOUT_TEMPLATE_TEMPLATE_DIVILER = 3;

	private static final int WITH_TEMPLATE_CUSTOMER_ITEM_COUNT = 5;
	
	private static final int WITHOUT_TEMPLATE_CUSTOMER_ITEM_COUNT = 4;

	private static final int VIEW_TYPE_PLAIN_TEXT 		= 0;
	private static final int VIEW_TYPE_TEXT_WITH_IMAGE 	= 1;
	private static final int VIEW_TYPE_TEMPLATE_ITEM 	= 2;
	private static final int VIEW_TYPE_DIVILER 			= 3;
	
	private static final int VIEW_TYPE_COUNT 			= 4;

	private Context mContext;
	private List<LocalOrderTemplate> mTemplates;
	private String mUserTitle;
	private String mUserSumery;
	private boolean mCanEditTemplate;

	public CustomerDrawerAdapter(Context context, boolean canEditTemplate) {
		mContext = context;
		mTemplates = new ArrayList<LocalOrderTemplate>();
		mCanEditTemplate = canEditTemplate;

		refreshUserDisplay();
	}
	
	public void resetCanEditTemplate(boolean can) {
		mCanEditTemplate = can;
		notifyDataSetChanged();
	}
	
	public boolean getCanEditTemplate() {
		return mCanEditTemplate;
	}

	public void refreshUserDisplay() {
		UserController controller = new UserController(mContext);
		mUserTitle = LocalUtils.isDeveceInChinese() ? controller
				.getChineseCompanyName() : controller.getEnglishCompanyName();
		mUserSumery = mContext.getString(R.string.text_login_user,
				controller.getDisplayName());
	}

	public List<LocalOrderTemplate> getTemplateList() {
		return mTemplates;
	}

	public void changeCursor(Cursor cursor) {
		try {
			mTemplates.clear();
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					for (; !cursor.isAfterLast(); cursor.moveToNext()) {
						mTemplates.add(new LocalOrderTemplate(cursor));
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
		return mTemplates.size()
				+ (mCanEditTemplate ? WITH_TEMPLATE_CUSTOMER_ITEM_COUNT
						: WITHOUT_TEMPLATE_CUSTOMER_ITEM_COUNT);
	}

	@Override
	public int getItemViewType(int position) {
		if (mCanEditTemplate) {
			switch (position) {
			case USER_ITEM_POSITION:
				return VIEW_TYPE_PLAIN_TEXT;
			case HISTORY_ITEM_POSITION:
			case STORE_ITEM_POSITION:
			case WITH_TEMPLATE_TEMPLATE_POSITION:
				return VIEW_TYPE_TEXT_WITH_IMAGE;
			case WITH_TEMPLATE_TEMPLATE_DIVILER:
				return VIEW_TYPE_DIVILER;
			default:
				return VIEW_TYPE_TEMPLATE_ITEM;
			}	
		} else {
			switch (position) {
			case USER_ITEM_POSITION:
				return VIEW_TYPE_PLAIN_TEXT;
			case HISTORY_ITEM_POSITION:
			case STORE_ITEM_POSITION:
				return VIEW_TYPE_TEXT_WITH_IMAGE;
			case WITHOUT_TEMPLATE_TEMPLATE_DIVILER:
				return VIEW_TYPE_DIVILER;
			default:
				return VIEW_TYPE_TEMPLATE_ITEM;
			}
		}
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}

	@Override
	public LocalOrderTemplate getItem(int position) {
		if (position < (mCanEditTemplate ? WITH_TEMPLATE_CUSTOMER_ITEM_COUNT
				: WITHOUT_TEMPLATE_CUSTOMER_ITEM_COUNT)) {
			return null;
		} else {
			return mTemplates.get(position
					- (mCanEditTemplate ? WITH_TEMPLATE_CUSTOMER_ITEM_COUNT
							: WITHOUT_TEMPLATE_CUSTOMER_ITEM_COUNT));
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
		case VIEW_TYPE_PLAIN_TEXT:
			return LayoutInflater.from(mContext).inflate(
					R.layout.drawer_list_item_customer_loginuser, null);
		case VIEW_TYPE_TEXT_WITH_IMAGE:
			return LayoutInflater.from(mContext).inflate(
					R.layout.drawer_list_item_image_text, null);
		case VIEW_TYPE_DIVILER:
			return LayoutInflater.from(mContext).inflate(
					R.layout.drawer_list_item_divilier, null);
		default:
			return LayoutInflater.from(mContext).inflate(
					R.layout.drawer_list_item_main_summery_text, null);
		}
	}

	private void bindView(int position, View convertView, ViewGroup parent) {
		switch (getItemViewType(position)) {
		case VIEW_TYPE_PLAIN_TEXT: {
			TextView titleText = (TextView) convertView
					.findViewById(R.id.drawer_list_item_user_title);
			TextView summeryText = (TextView) convertView
					.findViewById(R.id.drawer_list_item_user_summery);
			titleText.setText(mUserTitle);
			summeryText.setText(mUserSumery);
			break;
		}
		case VIEW_TYPE_TEXT_WITH_IMAGE: {
			TextView titleView = (TextView) convertView
					.findViewById(R.id.drawer_list_item_text);
			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.drawer_list_item_image);

			if (position == 1) {
				titleView.setText(R.string.text_history_order);
				imageView.setImageResource(R.drawable.ic_action_time_dark);
			} else if (position == 2) {
				titleView.setText(R.string.text_store_manage);
				imageView.setImageResource(R.drawable.ic_action_storage_dark);
			} else if (position == 3) {
				titleView.setText(R.string.text_add_template);
				imageView.setImageResource(R.drawable.ic_action_new_dark);
			}
			break;
		}
		case VIEW_TYPE_DIVILER: {
			TextView titleView = (TextView) convertView
					.findViewById(R.id.drawer_list_item_diviler_title);
			titleView.setText(R.string.text_title_template);
			break;
		}
		default: {
			LocalOrderTemplate template = getItem(position);
			if (template == null) {
				return;
			}

			TextView templateTitle = (TextView) convertView
					.findViewById(R.id.main_drawer_list_template_item_title);
			TextView summeryTitle = (TextView) convertView
					.findViewById(R.id.main_drawer_list_template_item_summery);
			templateTitle.setText(template.mName);
			summeryTitle
					.setText(LocalUtils.isDeveceInChinese() ? template.mSummeryChinese
							: template.mSummeryEnglish);
		}
		}
	}

}
