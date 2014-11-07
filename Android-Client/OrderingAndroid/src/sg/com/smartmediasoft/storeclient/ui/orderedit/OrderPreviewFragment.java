package sg.com.smartmediasoft.storeclient.ui.orderedit;

import java.text.DateFormat;
import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.database.LocalOrderItem;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class OrderPreviewFragment extends Fragment implements OnClickListener {

	public static interface EditOrderStep3Callback {
		public void onStep3SubmitOrder(String description);

		public void onStep3BackToPreviousStep();
	}

	private DateFormat mDateFormater = LocalUtils.getDateFormater();
	private EditOrderStep3Callback mCallback;
	private EditOrderActivityInterface mActivityCallback;

	private EditText mDescriptionView;
	private ListView mListView;
	private ProductPreviewAdapter mAdapter;

	public void changeDate(List<LocalProduct> datas) {
		mAdapter.changeData(datas);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mCallback = (EditOrderStep3Callback) activity;
		mActivityCallback = (EditOrderActivityInterface) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		mActivityCallback = null;
		mCallback = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.step_three_order_preview_fragment,
				null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		view.findViewById(R.id.select_product_fragment_bottom_left_btn)
				.setOnClickListener(this);
		view.findViewById(R.id.select_product_fragment_bottom_right_btn)
				.setOnClickListener(this);

		TextView textView = (TextView) view
				.findViewById(R.id.editorder_step_three_target_time);
		textView.setText(getString(R.string.edit_title_time,
				mDateFormater.format(mActivityCallback.getSelectedOrderDate())));

		mDescriptionView = (EditText) view
				.findViewById(R.id.editorder_step_three_order_description);

		mListView = (ListView) view
				.findViewById(R.id.editorder_step_three_list_view);
		mAdapter = new ProductPreviewAdapter(getActivity(),
				mActivityCallback.getSelectedProductList());
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_product_fragment_bottom_right_btn:
			mCallback.onStep3SubmitOrder(mDescriptionView.getText().toString());
			break;
		case R.id.select_product_fragment_bottom_left_btn:
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mDescriptionView.getWindowToken(), 0);
			mCallback.onStep3BackToPreviousStep();
			break;
		default:
			break;
		}
	}

}
