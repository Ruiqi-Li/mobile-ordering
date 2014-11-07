package sg.com.smartmediasoft.storeclient.ui.tamplate;

import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.StoreApplication;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PreviewDialogListAdapter extends BaseAdapter {
	private Context mContext;
	private List<LocalProduct> mProducts;
	boolean mDeviceInChanese;

	public PreviewDialogListAdapter(Context context, List<LocalProduct> products) {
		mContext = context;
		mProducts = products;
		mDeviceInChanese = LocalUtils.isDeveceInChinese();
	}

	public List<LocalProduct> getDatas() {
		return mProducts;
	}

	@Override
	public int getCount() {
		return mProducts == null ? 0 : mProducts.size();
	}

	@Override
	public LocalProduct getItem(int position) {
		return mProducts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.simple_product_item_layout, null);
		}

		convertView.setBackgroundColor(Color.WHITE);

		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.order_product_image);
		TextView nameView = (TextView) convertView
				.findViewById(R.id.order_product_name);
		TextView kilogramView = (TextView) convertView
				.findViewById(R.id.order_product_kilogram);

		LocalProduct item = getItem(position);

		StoreApplication.loadImage(item.mImageUrl,
				imageView);
		nameView.setText(mDeviceInChanese ? item.mChineseName
				: item.mEnglishName);
		kilogramView.setText(LocalUtils.getDesplayUnitText(item));

		return convertView;
	}
}
