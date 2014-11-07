package sg.com.smartmediasoft.storeclient.ui.main;

import sg.com.smartmediasoft.storeclient.R;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainOrderListItemLayout extends LinearLayout {
	private TextView mCustomerNameView;
	private TextView mOrderHumanId;
	private TextView mStateView;
	private TextView mDateView;
	private View mHeaderLayout;
	private TextView mHeaderTextView;
	private View mBottomLine;
	private View mReadLayout;

	public MainOrderListItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mCustomerNameView = (TextView) findViewById(R.id.main_list_item_customer_name);
		mOrderHumanId = (TextView) findViewById(R.id.main_list_item_human_id);
		mStateView = (TextView) findViewById(R.id.main_list_item_state);
		mDateView = (TextView) findViewById(R.id.main_list_item_date);
		mHeaderLayout = findViewById(R.id.pinned_header_layout);
		mHeaderTextView = (TextView) findViewById(R.id.pinned_header_text);
		mBottomLine = findViewById(R.id.pinned_list_item_footer_line);
		mReadLayout = findViewById(R.id.main_list_item_read_background);
	}

	public void bindContent(String customerName, CharSequence humanId,
			String state, String date, boolean read, boolean highLight,
			String sectionHeader, boolean lastInSection) {
		if (TextUtils.isEmpty(customerName)) {
			mCustomerNameView.setVisibility(GONE);
		} else {
			mCustomerNameView.setVisibility(VISIBLE);
			mCustomerNameView.setText(customerName);
		}
		
		if (read) {
			mReadLayout.setBackgroundColor(Color.TRANSPARENT);
		} else {
			if (highLight) {
				mReadLayout.setBackgroundColor(Color.rgb(255, 204, 204));
			} else {
				mReadLayout.setBackgroundColor(Color.rgb(255, 255, 204));
			}
		}

		mOrderHumanId.setText(humanId);
		mStateView.setText(getResources().getString(
				R.string.text_order_state_prefix, state));
		mDateView.setText(date);

		if (TextUtils.isEmpty(sectionHeader)) {
			mHeaderLayout.setVisibility(View.GONE);
		} else {
			mHeaderLayout.setVisibility(View.VISIBLE);
			mHeaderTextView.setText(sectionHeader);
		}

		if (!lastInSection) {
			mBottomLine.setVisibility(VISIBLE);
		} else {
			mBottomLine.setVisibility(GONE);
		}
	}
}
