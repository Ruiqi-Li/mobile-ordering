package sg.com.smartmediasoft.storeclient.ui.orderdetail;

import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.StoreApplication;
import sg.com.smartmediasoft.storeclient.database.LocalOrderItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderItemAdapter extends BaseAdapter {
	private Context mContext;
	private List<LocalOrderItem> mOrderItems;

	public OrderItemAdapter(Context context,
			List<LocalOrderItem> orderItems) {
		mOrderItems = orderItems;
		mContext = context;
	}

	public void changeData(List<LocalOrderItem> datas) {
		mOrderItems = datas;
		notifyDataSetChanged();
	}

	public List<LocalOrderItem> getOrderItems() {
		return mOrderItems;
	}

	@Override
	public int getCount() {
		return mOrderItems == null ? 0 : mOrderItems.size();
	}

	@Override
	public LocalOrderItem getItem(int position) {
		return mOrderItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.detail_list_item_layout, null);
		}

		TextView nameView = (TextView) convertView
				.findViewById(R.id.order_product_name);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.order_product_image);
		TextView amountVew = (TextView) convertView
				.findViewById(R.id.order_product_kilogram);

		LocalOrderItem orderItem = getItem(position);
		nameView.setText(LocalUtils.isDeveceInChinese() ? orderItem.mNameCN
				: orderItem.mNameEN);

		if (orderItem.mDescimalUnit == 1) {
			String amountText = orderItem.mAmount > 0 ? orderItem.mAmount + " "
					+ orderItem.mUnitName : null;
			amountVew.setText(amountText);
		} else {
			int intAmount = (int) orderItem.mAmount;
			String amountText = intAmount > 0 ? intAmount + " "
					+ orderItem.mUnitName : null;
			amountVew.setText(amountText);
		}

		StoreApplication.loadImage(orderItem.mImageUrl, imageView);

		return convertView;
	}

}
