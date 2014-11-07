package sg.com.smartmediasoft.storeclient.ui.orderdetail;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.database.LocalOperate;
import sg.com.smartmediasoft.storeclient.database.LocalOrder;
import sg.com.smartmediasoft.storeclient.database.LocalOrderItem;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import sg.com.smartmediasoft.storeclient.ui.SlidingTabLayout;
import sg.com.smartmediasoft.storeclient.ui.orderedit.ProductPreviewAdapter;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class OrderDetailPagerAdapter extends PagerAdapter implements
		OnItemClickListener {
	private static final int PAGE_COUNT = 2;
	private static final int PRODUCT_PAGE_INDEX = 0;
	private static final int OPERATE_PAGE_INDEX = 1;

	private Context mContext;
	private List<LocalOrderItem> mOrderItems;
	private List<LocalOperate> mOperateList;
	private LocalOrder mOrder;

	private ListView mProductListView;
	private ListView mOperateListView;

	public OrderDetailPagerAdapter(Context context, LocalOrder order,
			List<LocalOrderItem> orderItems, List<LocalOperate> operateList) {
		mContext = context;
		mOrder = order;
		mOrderItems = orderItems;
		mOperateList = operateList;
	}

	/**
	 * @return the number of pages to display
	 */
	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

	/**
	 * @return true if the value returned from
	 *         {@link #instantiateItem(ViewGroup, int)} is the same object as
	 *         the {@link View} added to the {@link ViewPager}.
	 */
	@Override
	public boolean isViewFromObject(View view, Object o) {
		return o == view;
	}

	// BEGIN_INCLUDE (pageradapter_getpagetitle)
	/**
	 * Return the title of the item at {@code position}. This is important as
	 * what this method returns is what is displayed in the
	 * {@link SlidingTabLayout}.
	 * <p>
	 * Here we construct one using the position value, but for real application
	 * the title should refer to the item's contents.
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case PRODUCT_PAGE_INDEX:
			return mContext.getString(R.string.detail_pager_order_detail_title);
		case OPERATE_PAGE_INDEX:
			return mContext.getString(R.string.detail_pager_operate_title);
		default:
			return null;
		}
	}

	// END_INCLUDE (pageradapter_getpagetitle)

	/**
	 * Instantiate the {@link View} which should be displayed at
	 * {@code position}. Here we inflate a layout from the apps resources and
	 * then change the text view to signify the position.
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (position == PRODUCT_PAGE_INDEX) {
			mProductListView = (ListView) LayoutInflater.from(mContext)
					.inflate(R.layout.widget_list, null);
			container.addView(mProductListView);
			mProductListView.addHeaderView(createListHeder(LayoutInflater
					.from(mContext)));
			mProductListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			mProductListView.setAdapter(new OrderItemAdapter(mContext,
					mOrderItems));
			mProductListView.setOnItemClickListener(this);

			return mProductListView;
		} else {
			mOperateListView = (ListView) LayoutInflater.from(mContext)
					.inflate(R.layout.widget_list, null);
			container.addView(mOperateListView);
			mOperateListView.setAdapter(new OrderDetailOperateAdapter(mContext,
					mOperateList));

			return mOperateListView;
		}
	}

	/**
	 * Destroy the item from the {@link ViewPager}. In our case this is simply
	 * removing the {@link View}.
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mProductListView.setItemChecked(position,
				!mProductListView.isItemChecked(position));
	}

	private View createListHeder(LayoutInflater inflater) {
		View headerView = inflater.inflate(R.layout.detail_list_header, null);
		TextView targetTimeTextView = (TextView) headerView
				.findViewById(R.id.detail_list_header_target_time);
		TextView createTimeTextView = (TextView) headerView
				.findViewById(R.id.detail_list_header_create_time);
		TextView confirmTimeTextView = (TextView) headerView
				.findViewById(R.id.detail_list_header_confirm_time);
		TextView deliverTimeTextView = (TextView) headerView
				.findViewById(R.id.detail_list_header_deliver_time);
		TextView finishTimeTextView = (TextView) headerView
				.findViewById(R.id.detail_list_header_finish_time);
		TextView finishTimePfefix = (TextView) headerView
				.findViewById(R.id.detail_list_header_finish_prefix);
		TextView humanIdTextView = (TextView) headerView
				.findViewById(R.id.detail_list_header_order_human_id);
		TextView stateTextView = (TextView) headerView
				.findViewById(R.id.detail_list_header_order_state);

		humanIdTextView.setText("  " + mOrder.mHumanId);
		stateTextView.setText("  " + LocalUtils.stateCodeToString(mOrder.mState,
				mContext));

		DateFormat dateFormator = LocalUtils.getDateFormater();
		DateFormat dateTimeFormator = LocalUtils.getDateTimeFormater();

		targetTimeTextView.setText("  " + dateFormator.format(new Date(
				mOrder.mTargetDate)));
		createTimeTextView.setText("  " + dateTimeFormator.format(new Date(
				mOrder.mCreateDate)));

		String confirmText = mOrder.mConfirmDate <= 0 ? mContext
				.getString(R.string.detail_text_confirm_none)
				: dateTimeFormator.format(new Date(mOrder.mConfirmDate));
		String deliverText = mOrder.mDeliverDate <= 0 ? mContext
				.getString(R.string.detail_text_deliver_none)
				: dateTimeFormator.format(new Date(mOrder.mDeliverDate));
		String finishTextText = mOrder.mFinishDate <= 0 ? mContext
				.getString(R.string.detail_text_deliver_none)
				: dateTimeFormator.format(new Date(mOrder.mFinishDate));

		if (LocalUtils.ORDER_STATE_CANCELED.equals(mOrder.mState)) {
			finishTimePfefix.setText(R.string.detail_text_cancel_time_prefix);
		} else {
			finishTimePfefix.setText(R.string.detail_text_receive_time_prefix);
		}
		finishTimeTextView.setText("  " + finishTextText);
		confirmTimeTextView.setText("  " + confirmText);
		deliverTimeTextView.setText("  " + deliverText);

		return headerView;
	}

}
