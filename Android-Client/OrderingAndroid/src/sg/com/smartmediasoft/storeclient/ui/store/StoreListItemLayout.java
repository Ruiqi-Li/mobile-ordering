package sg.com.smartmediasoft.storeclient.ui.store;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.StoreApplication;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StoreListItemLayout extends LinearLayout implements
		OnClickListener {

	private TextView mNameView;
	private TextView mDescriptionView;
	private ImageView mImageView;
	private View mHeaderLayout;
	private TextView mHeaderTextView;
	private View mFooterLineView;
	private View mAddButton;
	private View mMinusButton;

	private StoreActionCallbeck mActionCallbeck;
	private String mProductUUID;
	private String mProductName;
	private String mProductUnitName;
	private int mProductDescimalUnit;
	private double mProductAmount;

	public static interface StoreActionCallbeck {
		public void onStoreActionAdd(String productUUID, String productName,
				String productUnitName, int productDecimalUnit,
				double productAmount);

		public void onStoreActionMinus(String productUUID, String productName,
				String productUnitName, int productDecimalUnit,
				double productAmount);
	}

	public StoreListItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mNameView = (TextView) findViewById(R.id.store_product_name);
		mDescriptionView = (TextView) findViewById(R.id.store_product_description);
		mImageView = (ImageView) findViewById(R.id.store_product_image);
		mHeaderLayout = findViewById(R.id.pinned_header_layout);
		mHeaderTextView = (TextView) findViewById(R.id.pinned_header_text);
		mFooterLineView = findViewById(R.id.pinned_list_item_footer_line);

		mAddButton = findViewById(R.id.store_add);
		mAddButton.setOnClickListener(this);
		mMinusButton = findViewById(R.id.store_minus);
		mMinusButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.store_add:
			mActionCallbeck.onStoreActionAdd(mProductUUID, mProductName,
					mProductUnitName, mProductDescimalUnit, mProductAmount);
			break;
		case R.id.store_minus:
			mActionCallbeck.onStoreActionMinus(mProductUUID, mProductName,
					mProductUnitName, mProductDescimalUnit, mProductAmount);
			break;
		default:
			break;
		}
	}

	public void bindContent(boolean showEditBtn, StoreActionCallbeck callback,
			String uuid, CharSequence name, String unitName, int descimalUnit,
			double amount, String imageUrl, String sectionHeader,
			boolean lastInSection) {
		if (showEditBtn) {
			mAddButton.setVisibility(View.VISIBLE);
			mMinusButton.setVisibility(View.VISIBLE);
			mActionCallbeck = callback;
			mProductUUID = uuid;
			mProductName = name.toString();
			mProductUnitName = unitName;
			mProductDescimalUnit = descimalUnit;
			mProductAmount = amount;
		} else {
			mAddButton.setVisibility(View.GONE);
			mMinusButton.setVisibility(View.GONE);
		}

		mNameView.setText(name);
		if (TextUtils.isEmpty(sectionHeader)) {
			mHeaderLayout.setVisibility(View.GONE);
		} else {
			mHeaderLayout.setVisibility(View.VISIBLE);
			mHeaderTextView.setText(sectionHeader);
		}

		if (amount == 0) {
			String amountText = LocalUtils.getDesplayUnitText(amount, unitName,
					descimalUnit == 1);
			mDescriptionView.setText(amountText);

			mDescriptionView.setVisibility(VISIBLE);
			mDescriptionView.setTextColor(Color.rgb(204, 0, 0));
		} else if (amount > 0) {
			String amountText = LocalUtils.getDesplayUnitText(amount, unitName,
					descimalUnit == 1);
			mDescriptionView.setText(amountText);

			mDescriptionView.setVisibility(VISIBLE);
			mDescriptionView.setTextColor(getResources().getColor(
					R.color.smartmediasoft_color));
		} else {
			mDescriptionView.setVisibility(GONE);
		}

		if (lastInSection) {
			mFooterLineView.setVisibility(GONE);
		} else {
			mFooterLineView.setVisibility(VISIBLE);
		}

		StoreApplication.loadImage(imageUrl, mImageView);
	}

}