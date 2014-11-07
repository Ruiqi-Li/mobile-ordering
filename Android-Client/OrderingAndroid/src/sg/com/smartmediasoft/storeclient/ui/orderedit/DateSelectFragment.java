package sg.com.smartmediasoft.storeclient.ui.orderedit;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

public class DateSelectFragment extends Fragment implements
		OnDateSelectedListener, OnClickListener {

	public static interface EditOrderStep1Callback {
		public void onStep1Finished(long targetDate);
	}

	private DateFormat mDateFormater = LocalUtils.getDateFormater();
	private TextView mTitleTimeView;
	private CalendarPickerView mCalendarView;

	private EditOrderStep1Callback mCallback;
	private EditOrderActivityInterface mActivityCallback;

	public void initCalendar(Date targetDate, int orderRange) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date minial = calendar.getTime();
		
		if (targetDate == null) {
			targetDate = minial;
		}
		
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, orderRange);
		Date maxinal = calendar.getTime();
		
		if (targetDate.after(maxinal)) {
			maxinal = targetDate;
		}
		
		calendar = Calendar.getInstance();
		calendar.setTime(maxinal);
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		maxinal = calendar.getTime();
		
		if (targetDate.before(minial)) {
			minial = targetDate;
		}
		
		mCalendarView.init(minial, maxinal)
				.inMode(SelectionMode.SINGLE).withSelectedDate(targetDate);

		resetTitleText(targetDate);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mCallback = (EditOrderStep1Callback) activity;
		mActivityCallback = (EditOrderActivityInterface) activity;
	}

	@Override
	public void onDetach() {
		mCallback = null;
		mActivityCallback = null;

		super.onDetach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.step_one_time_select_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mTitleTimeView = (TextView) view
				.findViewById(R.id.editorder_step_one_target_time_text);

		mCalendarView = (CalendarPickerView) view
				.findViewById(R.id.editorder_step_one_calendar_view);
		mCalendarView.setOnDateSelectedListener(this);

		view.findViewById(R.id.editorder_step_one_next_button)
				.setOnClickListener(this);

		initCalendar(mActivityCallback.getSelectedOrderDate(), 1);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDateSelected(Date date) {
		resetTitleText(date);
	}

	@Override
	public void onDateUnselected(Date date) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editorder_step_one_next_button:
			mCallback
					.onStep1Finished(mCalendarView.getSelectedDate().getTime());
			break;
		default:
			break;
		}
	}

	private void resetTitleText(Date date) {
		mTitleTimeView.setText(getString(R.string.edit_title_time,
				mDateFormater.format(date)));
	}

}
