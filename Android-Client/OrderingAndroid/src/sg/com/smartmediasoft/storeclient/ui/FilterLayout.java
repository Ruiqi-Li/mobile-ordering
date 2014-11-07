package sg.com.smartmediasoft.storeclient.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.database.LocalOrder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

public class FilterLayout extends LinearLayout implements OnClickListener,
		OnItemSelectedListener {
	private static final int ONGOING_TYPE_ALL_POSITION = 0;
	private static final int ONGOING_TYPE_PENNDING_POSITION = 1;
	private static final int ONGOING_TYPE_RECEIVED_POSITION = 2;
	private static final int ONGOING_TYPE_DELIVERED_POSITION = 3;

	private static final int HISTORY_TYPE_ALL_POSITION = 0;
	private static final int HISTORY_TYPE_DELIVERED_POSITION = 1;
	private static final int HISTORY_TYPE_CANCELED_POSITION = 2;

	private static final int SORT_BY_CREATE_TIME = 0;
	private static final int SORT_BY_TARGET_TIME = 1;

	private ImageView mFilterIndicator;
	private View mMainFilterPannel;

	private Spinner mTypeSpinner;
	private TextView mDateSpinner;
	private Spinner mSortSpinner;

	private Date mStartDate;
	private Date mEndDate;

	private OnFilterChangeListener mListener;

	private Dialog mDatePickerDialog;
	private CalendarPickerView mCalendarView;
	private boolean mHistory;

	private SearchView mSearchView;

	public static interface OnFilterChangeListener {
		public void onFilterSelectionChange();

		public void onFilterSearchTextChanged(String text);
	}

	public FilterLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public String getQueryText() {
		if (mSearchView.isIconified()) {
			return "";
		} else {
			return mSearchView.getQuery().toString();
		}
	}

	public void closeSearchViewIfNeeded() {
		if (!mSearchView.isIconified()) {
			mSearchView.setQuery(null, false);
			mSearchView.clearFocus();
			mSearchView.setIconified(true);
		}
	}

	public void resetFilterTodefault(boolean history) {
		mHistory = history;

		if (history) {
			ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<String>(
					getContext(), R.layout.widget_spinner_text, getResources()
							.getStringArray(R.array.spinner_type_history_array));
			typeSpinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mTypeSpinner.setAdapter(typeSpinnerAdapter);
			mTypeSpinner.setSelection(HISTORY_TYPE_ALL_POSITION);
		} else {
			ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<String>(
					getContext(), R.layout.widget_spinner_text, getResources()
							.getStringArray(R.array.spinner_type_ongoing_array));
			typeSpinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mTypeSpinner.setAdapter(typeSpinnerAdapter);
			mTypeSpinner.setSelection(ONGOING_TYPE_ALL_POSITION);
		}

		mSortSpinner.setSelection(SORT_BY_CREATE_TIME);
		mDateSpinner.setText(R.string.text_main_date_filter_all);
		mStartDate = null;
		mEndDate = null;
	}

	public void setOnFilterChangeListener(OnFilterChangeListener listener) {
		mListener = listener;
	}

	public String getShowDateType() {
		switch (mSortSpinner.getSelectedItemPosition()) {
		case SORT_BY_CREATE_TIME:
			return LocalOrder.CREATE_DATE;
		default:
			return LocalOrder.TARGET_DATE;
		}
	}

	public void configSelectionAndSort(CursorLoader laoder, String customerId) {
		StringBuilder selection = new StringBuilder();
		List<String> selectionArgs = new ArrayList<String>();
		String sortBy = LocalOrder.CREATE_DATE + " DESC";

		if (!TextUtils.isEmpty(customerId)) {
			selection = selection.append(LocalOrder.CUSTOMER_ID).append("=?");
			selectionArgs.add(customerId);
		}

		int typeSelection = mTypeSpinner.getSelectedItemPosition();
		if (mHistory) {
			switch (typeSelection) {
			case HISTORY_TYPE_ALL_POSITION: {
				appendSelectionAndIfNeeded(selection);
				selection.append(LocalOrder.STATE).append(" IN (?,?)");
				selectionArgs.add(LocalUtils.ORDER_STATE_RECEIVED);
				selectionArgs.add(LocalUtils.ORDER_STATE_CANCELED);
				break;
			}
			case HISTORY_TYPE_DELIVERED_POSITION: {
				appendSelectionAndIfNeeded(selection);
				selection.append(LocalOrder.STATE).append("=?");
				selectionArgs.add(LocalUtils.ORDER_STATE_RECEIVED);
				break;
			}
			case HISTORY_TYPE_CANCELED_POSITION: {
				appendSelectionAndIfNeeded(selection);
				selection.append(LocalOrder.STATE).append("=?");
				selectionArgs.add(LocalUtils.ORDER_STATE_CANCELED);
				break;
			}
			default:
				break;
			}
		} else {
			switch (typeSelection) {
			case ONGOING_TYPE_ALL_POSITION: {
				appendSelectionAndIfNeeded(selection);
				selection.append(LocalOrder.STATE).append(" IN (?,?,?)");
				selectionArgs.add(LocalUtils.ORDER_STATE_PENDING);
				selectionArgs.add(LocalUtils.ORDER_STATE_CONFIRMED);
				selectionArgs.add(LocalUtils.ORDER_STATE_DELIVERED);
				break;
			}
			case ONGOING_TYPE_PENNDING_POSITION: {
				appendSelectionAndIfNeeded(selection);
				selection.append(LocalOrder.STATE).append("=?");
				selectionArgs.add(LocalUtils.ORDER_STATE_PENDING);
				break;
			}
			case ONGOING_TYPE_RECEIVED_POSITION: {
				appendSelectionAndIfNeeded(selection);
				selection.append(LocalOrder.STATE).append("=?");
				selectionArgs.add(LocalUtils.ORDER_STATE_CONFIRMED);
				break;
			}
			case ONGOING_TYPE_DELIVERED_POSITION: {
				appendSelectionAndIfNeeded(selection);
				selection.append(LocalOrder.STATE).append("=?");
				selectionArgs.add(LocalUtils.ORDER_STATE_DELIVERED);
				break;
			}
			default:
				break;
			}
		}

		if (!mSearchView.isIconified() && mSearchView.getQuery().length() > 0) {
			appendSelectionAndIfNeeded(selection);

			selection.append(LocalOrder.HUMAN_ID).append(" LIKE ?");
			selectionArgs.add("%" + mSearchView.getQuery().toString() + "%");
		}

		int sortSelection = mSortSpinner.getSelectedItemPosition();
		switch (sortSelection) {
		case SORT_BY_CREATE_TIME: {
			sortBy = LocalOrder.CREATE_DATE + " DESC";
			break;
		}
		case SORT_BY_TARGET_TIME: {
			sortBy = LocalOrder.TARGET_DATE + " ASC";
			break;
		}
		default:
			break;
		}

		if (mStartDate != null && mEndDate != null) {
			String dateColumn = sortBy.startsWith(LocalOrder.CREATE_DATE) ? LocalOrder.CREATE_DATE
					: LocalOrder.TARGET_DATE;
			appendSelectionAndIfNeeded(selection);

			selection.append(dateColumn).append(">=? AND ").append(dateColumn)
					.append("<=?");
			selectionArgs.add(String.valueOf(mStartDate.getTime()));
			selectionArgs.add(String.valueOf(mEndDate.getTime() + 24 * 60 * 60
					* 1000 - 1));
		}

		laoder.setSelection(selection.toString());
		laoder.setSelectionArgs(selectionArgs.toArray(new String[selectionArgs
				.size()]));
		laoder.setSortOrder(sortBy);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mFilterIndicator = (ImageView) findViewById(R.id.main_list_filters_indicator);

		mMainFilterPannel = findViewById(R.id.main_list_main_filter_pannel);
		mDateSpinner = (TextView) findViewById(R.id.main_list_date_spinner);

		findViewById(R.id.main_list_filters_trigger).setOnClickListener(this);
		mDateSpinner.setOnClickListener(this);

		mTypeSpinner = (Spinner) findViewById(R.id.main_list_type_spinner);
		mTypeSpinner.setOnItemSelectedListener(this);

		mSortSpinner = (Spinner) findViewById(R.id.main_list_sort_spinner);
		ArrayAdapter<String> sortSpinnerAdapter = new ArrayAdapter<String>(
				getContext(), R.layout.widget_spinner_text, getResources()
						.getStringArray(R.array.spinner_sort_array));
		sortSpinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSortSpinner.setAdapter(sortSpinnerAdapter);
		mSortSpinner.setOnItemSelectedListener(this);

		mDateSpinner.setText(R.string.text_main_date_filter_all);
		mStartDate = null;
		mEndDate = null;

		mSearchView = (SearchView) findViewById(R.id.main_list_filters_searchview);
		mSearchView.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_SIGNED);
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (mListener != null) {
					mListener.onFilterSearchTextChanged(newText);
				}
				return true;
			}
		});

		View rootView = LayoutInflater.from(getContext()).inflate(
				R.layout.widget_calendar, null);
		mCalendarView = (CalendarPickerView) rootView
				.findViewById(R.id.dialog_calendar_view);
		mDatePickerDialog = new AlertDialog.Builder(getContext())
				.setTitle(R.string.dialog_date_title)
				.setView(rootView)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								List<Date> selectedDates = mCalendarView
										.getSelectedDates();
								if (selectedDates != null
										&& !selectedDates.isEmpty()) {
									mStartDate = selectedDates.get(0);
									mEndDate = selectedDates.get(selectedDates
											.size() - 1);

									DateFormat formater = LocalUtils
											.getDateFormaterShort();
									if (mStartDate.getTime() == mEndDate
											.getTime()) {
										mDateSpinner.setText(formater
												.format(mStartDate));
									} else {
										mDateSpinner.setText(formater
												.format(mStartDate)
												+ "~"
												+ formater.format(mEndDate));
									}
								} else {
									mDateSpinner
											.setText(R.string.text_main_date_filter_all);
									mStartDate = null;
									mEndDate = null;
								}

								if (mListener != null) {
									mListener.onFilterSelectionChange();
								}
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								mDateSpinner
										.setText(R.string.text_main_date_filter_all);
								mStartDate = null;
								mEndDate = null;

								if (mListener != null) {
									mListener.onFilterSelectionChange();
								}
							}
						}).create();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_list_filters_trigger:
			if (mMainFilterPannel.getVisibility() == VISIBLE) {
				mFilterIndicator.setImageResource(R.drawable.ic_action_expand);
				mMainFilterPannel.setVisibility(GONE);
			} else {
				mFilterIndicator
						.setImageResource(R.drawable.ic_action_collapse);
				mMainFilterPannel.setVisibility(VISIBLE);
			}
			break;
		case R.id.main_list_date_spinner:
			showDatePickerDialog();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getId() == R.id.main_list_sort_spinner) {
			mDateSpinner.setText(R.string.text_main_date_filter_all);
			mStartDate = null;
			mEndDate = null;
		}
		if (mListener != null) {
			mListener.onFilterSelectionChange();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private void appendSelectionAndIfNeeded(StringBuilder builder) {
		if (builder.length() > 0) {
			builder.append(" AND ");
		}
	}

	private void showDatePickerDialog() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);
		Date miniDate = calendar.getTime();

		calendar.add(Calendar.YEAR, 2);
		Date maxDate = calendar.getTime();

		mCalendarView.init(miniDate, maxDate).inMode(SelectionMode.RANGE)
				.withSelectedDate(new Date(System.currentTimeMillis()));
		mDatePickerDialog.show();
	}

}
