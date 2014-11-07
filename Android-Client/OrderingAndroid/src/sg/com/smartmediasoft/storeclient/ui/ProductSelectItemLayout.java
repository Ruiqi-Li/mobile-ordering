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

public class ProductSelectItemLayout extends LinearLayout {

	private TextView mNameView;
	private TextView mDescriptionView;
	private ImageView mImageView;
	private View mHeaderLayout;
	private TextView mHeaderTextView;
	private View mFooterLineView;

	public ProductSelectItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mNameView = (TextView) findViewById(R.id.order_product_name);
		mDescriptionView = (TextView) findViewById(R.id.order_product_description);
		mImageView = (ImageView) findViewById(R.id.order_product_image);
		mHeaderLayout = findViewById(R.id.pinned_header_layout);
		mHeaderTextView = (TextView) findViewById(R.id.pinned_header_text);
		mFooterLineView = findViewById(R.id.pinned_list_item_footer_line);
	}

	public void bindContent(CharSequence name, String imageUrl,
			String amountText, String sectionHeader, boolean lastInSection) {
		mNameView.setText(name);

		if (TextUtils.isEmpty(sectionHeader)) {
			mHeaderLayout.setVisibility(View.GONE);
		} else {
			mHeaderLayout.setVisibility(View.VISIBLE);
			mHeaderTextView.setText(sectionHeader);
		}

		if (!TextUtils.isEmpty(amountText)) {
			mDescriptionView.setVisibility(VISIBLE);
			mDescriptionView.setText(amountText);
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
