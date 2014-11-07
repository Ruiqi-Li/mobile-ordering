package sg.com.smartmediasoft.storeclient.ui;

import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.StoreApplication;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleProductListItem extends LinearLayout {

	private TextView mNameView;
	private ImageView mImageView;
	private TextView mRightView;
	private View mHeaderLayout;
	private TextView mHeaderTextView;
	private View mFooterLineView;

	public SimpleProductListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mNameView = (TextView) findViewById(R.id.order_product_name);
		mImageView = (ImageView) findViewById(R.id.order_product_image);
		mRightView = (TextView) findViewById(R.id.order_product_kilogram);
		mHeaderLayout = findViewById(R.id.pinned_header_layout);
		mHeaderTextView = (TextView) findViewById(R.id.pinned_header_text);
		mFooterLineView = findViewById(R.id.pinned_list_item_footer_line);
	}
	
	public void setRightTextColor(int color) {
		mRightView.setTextColor(color);
	}

	public void bindContent(CharSequence name, String imageUrl,
			String amountText, String sectionHeader, boolean lastInSection) {
		mNameView.setText(name);
		mRightView.setText(amountText);

		if (TextUtils.isEmpty(sectionHeader)) {
			mHeaderLayout.setVisibility(View.GONE);
		} else {
			mHeaderLayout.setVisibility(View.VISIBLE);
			mHeaderTextView.setText(sectionHeader);
		}

		if (lastInSection) {
			mFooterLineView.setVisibility(GONE);
		} else {
			mFooterLineView.setVisibility(VISIBLE);
		}

		StoreApplication.loadImage(imageUrl, mImageView);
	}
}
