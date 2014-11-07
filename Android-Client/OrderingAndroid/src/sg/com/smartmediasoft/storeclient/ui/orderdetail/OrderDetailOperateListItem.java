package sg.com.smartmediasoft.storeclient.ui.orderdetail;

import sg.com.smartmediasoft.storeclient.R;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderDetailOperateListItem extends LinearLayout {

	private TextView mNameView;
	private TextView mValueView;
	private TextView mDescriptionView;
	private TextView mHeaderLeftTextView;
	private TextView mHeaderRightTextView;

	public OrderDetailOperateListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		TextView rightTitle = (TextView) findViewById(R.id.pinned_header_right_text);
		rightTitle.setTextColor(getResources().getColor(
				R.color.smartmediasoft_color));
		mNameView = (TextView) findViewById(R.id.detail_list_item_product_name);
		mValueView = (TextView) findViewById(R.id.detail_list_item_product_value);
		mDescriptionView = (TextView) findViewById(R.id.detail_list_item_description);
		mHeaderLeftTextView = (TextView) findViewById(R.id.pinned_header_text);
		mHeaderRightTextView = (TextView) findViewById(R.id.pinned_header_right_text);
	}

	public void bindContent(String titleLeftText, String titleRightText,
			String name, String value, String description) {
		mNameView.setText(name);
		mValueView.setText(value);
		if (TextUtils.isEmpty(description)) {
			mDescriptionView.setVisibility(GONE);
		} else {
			mDescriptionView.setVisibility(VISIBLE);
			mDescriptionView.setText(description);
		}
		mHeaderLeftTextView.setText(titleLeftText);
		mHeaderRightTextView.setText(titleRightText);
	}
}
