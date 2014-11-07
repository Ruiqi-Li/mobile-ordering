package sg.com.smartmediasoft.storeclient.ui.main;

import java.text.DateFormat;
import java.util.Date;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.TextHighlighter;
import sg.com.smartmediasoft.storeclient.database.LocalOrder;
import sg.com.smartmediasoft.storeclient.ui.IndexerListAdapter;
import sg.com.smartmediasoft.storeclient.ui.PinnedHeaderListView;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainOrderListAdapter extends IndexerListAdapter implements
		PinnedHeaderListView.PinnedHeaderAdapter {
	private LayoutInflater mInflater;
	private DateFormat mDateFormat = LocalUtils.getDateTimeFormater();
	private String mShowDateType;
	private boolean mShowCustomerName;
	private boolean mDeviceInChinese;
	private TextHighlighter mTextHighlighter;
	private SearchBoxCallback mSearchBoxCallback;
	private boolean mhistoryOrderList;
	
	public static interface SearchBoxCallback {
		public String getCurrentQueryText();
	}

	public MainOrderListAdapter(Context context, Cursor c,
			SearchBoxCallback callback, boolean historyOrder) {
		super(context, c, false);

		setSectionHeaderDisplayEnabled(true);
		mSearchBoxCallback = callback;
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDeviceInChinese = LocalUtils.isDeveceInChinese();
		mTextHighlighter = new TextHighlighter(context.getResources().getColor(
				android.R.color.holo_orange_light), Typeface.BOLD);
		mhistoryOrderList = historyOrder;
	}
	
	public void setShowDateType(String dateType) {
		mShowDateType = dateType;
	}

	public void showCustomerName(boolean show) {
		mShowCustomerName = show;
	}

	@Override
	protected View createPinnedSectionHeaderView(Context context,
			ViewGroup parent) {
		return mInflater.inflate(R.layout.pinned_list_header, null);
	}

	@Override
	protected void setPinnedSectionTitle(View pinnedHeaderView, String title) {
		TextView textView = (TextView) pinnedHeaderView
				.findViewById(R.id.pinned_header_text);
		textView.setText(title);
	}

	@Override
	protected void setPinnedHeaderContactsCount(View header) {

	}

	@Override
	public int getScrollPositionForHeader(int viewIndex) {
		return 0;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.main_list_item, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		MainOrderListItemLayout itemLayout = (MainOrderListItemLayout) view;

		String humanId = cursor
				.getString(LocalOrder.CONTENT_PROJECTION_HUMAN_ID_INDEX);
		String state = cursor
				.getString(LocalOrder.CONTENT_PROJECTION_STATE_INDEX);
		boolean read = cursor.getInt(LocalOrder.CONTENT_PROJECTION_READ_INDEX) == 1;
		boolean heghtLight = cursor.getInt(LocalOrder.CONTENT_PROJECTION_HIGH_LIGHT_INDEX) == 1;

		Placement placement = getItemPlacementInSection(cursor.getPosition());

		String dateText = null;
		if (LocalOrder.TARGET_DATE.equals(mShowDateType)) {
			long date = cursor
					.getLong(LocalOrder.CONTENT_PROJECTION_TARGET_DATE_INDEX);
			dateText = mContext
					.getString(R.string.detail_text_target_time_prefix)
					+ mDateFormat.format(new Date(date));
		} else if (LocalOrder.CREATE_DATE.equals(mShowDateType)) {
			long date = cursor
					.getLong(LocalOrder.CONTENT_PROJECTION_CREATE_DATE_INDEX);
			dateText = mContext
					.getString(R.string.detail_text_create_time_prefix)
					+ mDateFormat.format(new Date(date));
		} else {
			long date = cursor
					.getLong(LocalOrder.CONTENT_PROJECTION_FINISH_DATE_INDEX);
			dateText = mContext
					.getString(R.string.detail_text_finish_time_prefix)
					+ mDateFormat.format(new Date(date));
		}

		CharSequence displayHumanId = null;
		if (mSearchBoxCallback != null
				&& mSearchBoxCallback.getCurrentQueryText().length() > 0) {
			displayHumanId = mTextHighlighter
					.applyPrefixHighlight(
							mContext.getString(R.string.text_prefix_order_human_id)
									+ humanId, mSearchBoxCallback
									.getCurrentQueryText());
		} else {
			displayHumanId = mContext
					.getString(R.string.text_prefix_order_human_id) + humanId;
		}

		itemLayout.bindContent(mShowCustomerName ? getDisplayName(cursor)
				: null, displayHumanId, LocalUtils.stateCodeToString(state,
				mContext), dateText, mhistoryOrderList || read,
				!mhistoryOrderList && heghtLight, placement.sectionHeader,
				placement.lastInSection);
	}

	private String getDisplayName(Cursor cursor) {
		return mDeviceInChinese ? cursor
				.getString(LocalOrder.CONTENT_PROJECTION_COMBINE_CUSTOMER_CN_NAME)
				: cursor.getString(LocalOrder.CONTENT_PROJECTION_COMBINE_CUSTOMER_EN_NAME);
	}

}
