package sg.com.smartmediasoft.storeclient.ui.statistic;

import java.util.Collections;
import java.util.Comparator;
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

public class StatisticProductAdapter extends BaseAdapter {
	private Context mContext;
	private List<LocalOrderItem> mOrderItems;
	boolean mDeviceInChinese;

	public StatisticProductAdapter(Context context) {
		mContext = context;
		mDeviceInChinese = LocalUtils.isDeveceInChinese();
	}
	
	public void changeDatas(List<LocalOrderItem> datas) {
		if (datas != null) {
			Collections.sort(datas, new Comparator<LocalOrderItem>() {

				@Override
				public int compare(LocalOrderItem lhs, LocalOrderItem rhs) {
					if (lhs.mAmount > rhs.mAmount) {
						return -1;
					} else if (lhs.mAmount < rhs.mAmount) {
						return 1;
					} else {
						return 0;
					}
				}
				
			});
		}
		mOrderItems = datas;
		notifyDataSetChanged();
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
	public View getView(int position, View convertorView, ViewGroup parent) {
		if (convertorView == null) {
			convertorView = LayoutInflater.from(mContext).inflate(
					R.layout.product_list_item_sample, null);
		}
		
		LocalOrderItem item = getItem(position);

		ImageView productImage = (ImageView) convertorView
				.findViewById(R.id.order_product_image);
		TextView productName = (TextView) convertorView
				.findViewById(R.id.order_product_name);
		TextView productDescription = (TextView) convertorView
				.findViewById(R.id.order_product_description);

		StoreApplication.loadImage(item.mImageUrl, productImage);
		productName.setText(mDeviceInChinese ? item.mNameCN : item.mNameEN);
		productDescription.setText(LocalUtils.getDesplayUnitText(item.mAmount,
				item.mUnitName, item.mDescimalUnit == 1));

		return convertorView;
	}

}
