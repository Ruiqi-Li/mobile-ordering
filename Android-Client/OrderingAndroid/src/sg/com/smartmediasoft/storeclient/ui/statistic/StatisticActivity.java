package sg.com.smartmediasoft.storeclient.ui.statistic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.database.LocalCustomer;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.ui.RefreshListener;
import sg.com.smartmediasoft.storeclient.ui.statistic.StatisticProductFragment.StatisticActionInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

public class StatisticActivity extends Activity implements OnClickListener,
		OnDateSelectedListener, OnItemSelectedListener, RefreshListener,
		StatisticActionInterface {
	public static final String EXTRA_CUSTOMER_LIST = "customer_list";

	private CalendarPickerView mCalendarView;
	private Spinner mCheckModeSpinner;
	private TextView mCustomerTextSpinner;
	private Button mSelectConfimBtn;
	private SlidingPaneLayout mSlidingPaneLayout;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	private Dialog mCustomerChoiceDialog;
	private CustomerSelectAdapter2 mCustomerAdapter;

	private ArrayList<String> mSelectCustomerList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.statistic_activity);

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {

			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.white,
				android.R.color.holo_orange_light, android.R.color.white,
				android.R.color.holo_orange_light);
		mSwipeRefreshLayout.setEnabled(false);

		mSlidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.statistic_activity_root);
		mSlidingPaneLayout.setSliderFadeColor(Color.TRANSPARENT);
		mSlidingPaneLayout.setPanelSlideListener(new PanelSlideListener() {

			@Override
			public void onPanelSlide(View arg0, float arg1) {
			}

			@Override
			public void onPanelOpened(View arg0) {
				invalidateOptionsMenu();
			}

			@Override
			public void onPanelClosed(View arg0) {
				invalidateOptionsMenu();
			}
		});
		mSlidingPaneLayout
				.setShadowResourceLeft(R.drawable.drawer_shadow_right);
		mSlidingPaneLayout.setParallaxDistance(100);
		mSlidingPaneLayout.openPane();

		mCalendarView = (CalendarPickerView) findViewById(R.id.overview_calendar);
		mCheckModeSpinner = (Spinner) findViewById(R.id.statistic_mode_spinner);
		mSelectConfimBtn = (Button) findViewById(R.id.overview_select_confirm_btn);
		mCalendarView.setOnDateSelectedListener(this);
		mCheckModeSpinner.setOnItemSelectedListener(this);
		mSelectConfimBtn.setOnClickListener(this);

		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				R.layout.widget_spinner_text, getResources().getStringArray(
						R.array.spinner_calendar_mode));
		modeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCheckModeSpinner.setAdapter(modeAdapter);

		mCustomerTextSpinner = (TextView) findViewById(R.id.statistic_customer_spinner);
		mCustomerTextSpinner.setOnClickListener(this);
		resetCustomerSpinnerText();

		resetCalendar(0);

		List<LocalCustomer> customers = getIntent()
				.getParcelableArrayListExtra(EXTRA_CUSTOMER_LIST);
		List<String> sectionHeaders = new ArrayList<String>();
		List<Integer> sectionCounts = new ArrayList<Integer>();
		setupCustomerListSections(customers, sectionHeaders, sectionCounts);
		mCustomerAdapter = new CustomerSelectAdapter2(this, customers);

		mCustomerChoiceDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_customer_choice_title)
				.setAdapter(mCustomerAdapter, null)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						mSelectCustomerList.clear();
						mSelectCustomerList.addAll(mCustomerAdapter.getCustomerSelections());
						resetCustomerSpinnerText();
					}
				})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create();

		if (savedInstanceState == null) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.statistic_slide_pannel,
							new StatisticProductFragment()).commit();
		}
	}

	@Override
	public void onRefreshStateChange(boolean refreshing) {
		mSwipeRefreshLayout.setRefreshing(refreshing);
	}

	@Override
	public void onBackPressed() {
		if (!mSlidingPaneLayout.isOpen()) {
			mSlidingPaneLayout.openPane();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mSlidingPaneLayout.isOpen()) {
			menu.removeItem(R.id.menu_search);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	private void resetCustomerSpinnerText() {
		if (mSelectCustomerList.isEmpty()) {
			mCustomerTextSpinner.setText(R.string.text_select_customer_hit);
		} else {
			mCustomerTextSpinner.setText(getString(
					R.string.text_select_customer_prefix,
					mSelectCustomerList.size()));
		}
	}

	private void resetCalendar(int spinnerPosition) {
		Date currentSelect = mCalendarView.getSelectedDate();
		if (currentSelect == null) {
			currentSelect = new Date(System.currentTimeMillis());
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -10);
		Date miniDate = calendar.getTime();

		calendar.add(Calendar.MONTH, 12);
		Date maxDate = calendar.getTime();

		SelectionMode mode = null;
		switch (spinnerPosition) {
		case 0:
			mode = SelectionMode.SINGLE;
			break;
		case 1:
			mode = SelectionMode.RANGE;
			break;
		default:
			return;
		}

		mCalendarView.init(miniDate, maxDate).inMode(mode)
				.withSelectedDate(currentSelect);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.overview_select_confirm_btn:
			if (mSelectCustomerList.isEmpty()) {
				Toast.makeText(this, R.string.toast_select_customer_is_none,
						Toast.LENGTH_SHORT).show();
				return;
			}

			boolean start = BackendService.getOrderStatistic(this,
					getSelectDateArray(), mSelectCustomerList);
			if (start) {
				StatisticProductFragment fragment = (StatisticProductFragment) getFragmentManager()
						.findFragmentById(R.id.statistic_slide_pannel);
				fragment.clearSelectProduct();

				mSwipeRefreshLayout.setRefreshing(true);
				mSlidingPaneLayout.closePane();
			}
			break;
		case R.id.statistic_customer_spinner:
			mCustomerChoiceDialog.show();
			break;
		default:
			break;
		}
	}

	@Override
	public void onStartGenerateReport() {
		boolean start = BackendService.generateReport(this,
				getSelectDateArray(), mSelectCustomerList);
		if (start) {
			StatisticProductFragment fragment = (StatisticProductFragment) getFragmentManager()
					.findFragmentById(R.id.statistic_slide_pannel);
			fragment.showProcessDialog();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onDateSelected(Date date) {

	}

	@Override
	public void onDateUnselected(Date date) {

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		resetCalendar(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private long[] getSelectDateArray() {
		List<Date> dates = mCalendarView.getSelectedDates();

		long[] datasArray = null;
		if (mCalendarView.getSelectionMode() == SelectionMode.RANGE) {
			datasArray = new long[2];
			datasArray[0] = dates.get(0).getTime();
			datasArray[1] = dates.get(Math.max(0, dates.size() - 1)).getTime();
		} else {
			datasArray = new long[1];
			datasArray[0] = dates.get(0).getTime();
		}

		return datasArray;
	}

	private void setupCustomerListSections(List<LocalCustomer> customers,
			List<String> sectionHeaders, List<Integer> sectionCounts) {
		boolean deviceInChinese = LocalUtils.isDeveceInChinese();
		String currentCheckGroupName = null;
		int count = 0;
		for (LocalCustomer customer : customers) {
			String name = deviceInChinese ? customer.mChineseGroupName
					: customer.mEnglishGroupName;
			if (currentCheckGroupName == null) {
				currentCheckGroupName = name;
			}

			if (name.equals(currentCheckGroupName)) {
				count++;
			} else {
				if (count != 0) {
					sectionHeaders.add(currentCheckGroupName);
					sectionCounts.add(count);
				}

				currentCheckGroupName = name;
				count = 1;
			}
		}

		if (count > 0
				&& (sectionHeaders.isEmpty() || !currentCheckGroupName
						.equals(sectionHeaders.get(sectionHeaders.size() - 1)))) {
			sectionHeaders.add(currentCheckGroupName);
			sectionCounts.add(count);
		}
	}

}
