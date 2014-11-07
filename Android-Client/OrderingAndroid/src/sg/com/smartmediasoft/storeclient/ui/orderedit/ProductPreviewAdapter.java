package sg.com.smartmediasoft.storeclient.ui.orderedit;

import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.StoreApplication;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductPreviewAdapter extends BaseAdapter {
	private Context mContext;
	private List<LocalProduct> mProducts;

	public ProductPreviewAdapter(Context context, List<LocalProduct> products) {
		mProducts = products;
		mContext = context;
	}

	public void changeData(List<LocalProduct> datas) {
		mProducts = datas;
		notifyDataSetChanged();
	}

	public List<LocalProduct> getProducts() {
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
					R.layout.detail_list_item_layout, null);
		}

		TextView nameView = (TextView) convertView
				.findViewById(R.id.order_product_name);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.order_product_image);
		TextView amountVew = (TextView) convertView
				.findViewById(R.id.order_product_kilogram);

		LocalProduct product = getItem(position);
		nameView.setText(LocalUtils.isDeveceInChinese() ? product.mChineseName
				: product.mEnglishName);

		if (product.mDecimalUnit == 1) {
			String amountText = product.mAmount > 0 ? product.mAmount + " "
					+ product.mUnitName : null;
			amountVew.setText(amountText);
		} else {
			int intAmount = (int) product.mAmount;
			String amountText = intAmount > 0 ? intAmount + " "
					+ product.mUnitName : null;
			amountVew.setText(amountText);
		}

		StoreApplication.loadImage(product.mImageUrl, imageView);

		return convertView;
	}

}
