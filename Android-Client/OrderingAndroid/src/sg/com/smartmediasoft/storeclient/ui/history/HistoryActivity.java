package sg.com.smartmediasoft.storeclient.ui.history;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.database.LocalOrder;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.ui.NameSectionIndexer;
import sg.com.smartmediasoft.storeclient.ui.NotifyAwareActivity;
import sg.com.smartmediasoft.storeclient.ui.PinnedHeaderListView;
import sg.com.smartmediasoft.storeclient.ui.main.MainOrderListAdapter;
import sg.com.smartmediasoft.storeclient.ui.main.MainOrderListAdapter.SearchBoxCallback;
import sg.com.smartmediasoft.storeclient.ui.main.MainOrderListLoader;
import sg.com.smartmediasoft.storeclient.ui.orderdetail.OrderDetailActivity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends NotifyAwareActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, SearchBoxCallback {
	public static final String EXTRA_CUSTOMER_ID = "customer_id";
	public static final String EXTRA_CUSTOMER_NAME = "customer_name";

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private MainOrderListAdapter mAdapter;

	private View mFooterProgressView;
	private TextView mFooterTextView;
	private SearchView mMenuSearchView;

	private String mCustomerId;
	private long mStartId = -1;
	private long mEndId = -1;

	@Override
	public void getListereningActions(Set<String> actions) {
		actions.add(SMSBroadcastManager.ACTION_REFRESH_ORDER_HISTORY_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_MORE_ORDER_HISTORY_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_SEARCH_ORDER_HISTORY_FINISHED);
	}

	@Override
	public void onActionReveived(Context context, String action, Intent data) {
		if (SMSBroadcastManager.ACTION_REFRESH_ORDER_HISTORY_FINISHED
				.equals(action)
				|| SMSBroadcastManager.ACTION_MORE_ORDER_HISTORY_FINISHED
						.equals(action)) {
			mSwipeRefreshLayout.setRefreshing(false);
			updateFooterState(false);

			int resultCode = data.getIntExtra(
					SMSBroadcastManager.EXTRA_RESULT_CODE,
					LocalUtils.CODE_FAILED_UNKNOW);
			if (resultCode == LocalUtils.CODE_SUCCESS) {
                mStartId = data.getLongExtra(SMSBroadcastManager.DATA_INT_1,
						mStartId);
				mEndId = data.getLongExtra(SMSBroadcastManager.DATA_INT_2,
						mEndId);

				boolean hasData = data.getBooleanExtra(
						SMSBroadcastManager.DATA_HAS_DATA, false);
				if (hasData) {
					restartLoader();
				} else {
					if (SMSBroadcastManager.ACTION_MORE_ORDER_HISTORY_FINISHED
							.equals(action)) {
						Toast.makeText(this,
								R.string.toast_no_more_hsitory_order,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		} else if (SMSBroadcastManager.ACTION_SEARCH_ORDER_HISTORY_FINISHED.equals(action)) {
			mSwipeRefreshLayout.setRefreshing(false);
			updateFooterState(false);
			
			int resultCode = data.getIntExtra(
					SMSBroadcastManager.EXTRA_RESULT_CODE,
					LocalUtils.CODE_FAILED_UNKNOW);
			boolean hasData = data.getBooleanExtra(
					SMSBroadcastManager.DATA_HAS_DATA, false);
			if (resultCode == LocalUtils.CODE_SUCCESS && hasData) {
				restartLoader();
			} else {
				Toast.makeText(this, R.string.toast_not_found,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		String customerName = getIntent().getStringExtra(EXTRA_CUSTOMER_NAME);
		if (customerName != null) {
			setTitle(customerName + " "
					+ getString(R.string.title_history_order));
		}

		mCustomerId = getIntent().getStringExtra(EXTRA_CUSTOMER_ID);

		setContentView(R.layout.history_activity);

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_swiperefresh);
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.white,
				android.R.color.holo_orange_light, android.R.color.white,
				android.R.color.holo_orange_light);
		mSwipeRefreshLayout.setEnabled(false);
		PinnedHeaderListView listView = (PinnedHeaderListView) findViewById(R.id.widget_pinnedheader);
		int headerHeight = getResources().getDimensionPixelSize(
				R.dimen.order_list_item_header_height);
		listView.setHeaderHeight(headerHeight);

		View footerView = LayoutInflater.from(this).inflate(
				R.layout.widget_list_loadmore_layout, null);
		mFooterProgressView = footerView.findViewById(R.id.loadmore_progress);
		mFooterTextView = (TextView) footerView
				.findViewById(R.id.loadmore_text);

		listView.addFooterView(footerView);
		mAdapter = new MainOrderListAdapter(this, null, this, true);
		mAdapter.setShowDateType(LocalOrder.FINISH_DATE);
		mAdapter.showCustomerName(false);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);

		updateFooterState(false);

		refreshHistoryOrder();
		restartLoader();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_menu, menu);

		mMenuSearchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		mMenuSearchView.setSubmitButtonEnabled(true);
		mMenuSearchView.setQueryHint(getString(R.string.hint_online_search));
		mMenuSearchView.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_SIGNED);
		mMenuSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				startOnlineSearch(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				restartLoader();
				return true;
			}
		});
		mMenuSearchView.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				restartLoader();
				return false;
			}
		});
		
		return true;
	}

	@Override
	protected void onDestroy() {
		getContentResolver().delete(
				LocalOrder.CONTENT_URI,
				LocalOrder.STATE + " IN (?,?)",
				new String[] { LocalUtils.ORDER_STATE_CANCELED,
						LocalUtils.ORDER_STATE_RECEIVED });

		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		StringBuilder selection = new StringBuilder(LocalOrder.STATE + " IN (?,?)");
		List<String> selectionArgs = new ArrayList<String>();
		selectionArgs.add(LocalUtils.ORDER_STATE_CANCELED);
		selectionArgs.add(LocalUtils.ORDER_STATE_RECEIVED);

        selection.append(" AND ").append(LocalOrder.CUSTOMER_ID).append("=?");
        selectionArgs.add(mCustomerId);

        if (mMenuSearchView != null && !mMenuSearchView.isIconified()) {
			selection.append(" AND ").append(LocalOrder.HUMAN_ID)
					.append(" LIKE ?");
			selectionArgs.add("%" + mMenuSearchView.getQuery() + "%");
		}

		MainOrderListLoader loader = new MainOrderListLoader(this,
				LocalOrder.CONTENT_URI, LocalOrder.CONTENT_PROJECTION,
				selection.toString(),
				selectionArgs.toArray(new String[selectionArgs.size()]),
				LocalOrder.FINISH_DATE + " DESC");
		loader.setShowDateType(LocalOrder.FINISH_DATE);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.changeCursor(data);

		if (loader instanceof MainOrderListLoader) {
			MainOrderListLoader productLoader = (MainOrderListLoader) loader;
			if (productLoader.getSectionHeaders() != null) {
				mAdapter.setIndexer(new NameSectionIndexer(productLoader
						.getSectionHeaders(), productLoader.getSectionCounts()));
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mAdapter.getCount() == position) {
			moreHsitoryOrder();
		} else {
			Cursor cursor = (Cursor) mAdapter.getItem(position);
			Intent intent = new Intent(this, OrderDetailActivity.class);
			intent.putExtra(OrderDetailActivity.EXTRA_LOCAL_ORDER_ID,
					cursor.getString(LocalOrder.CONTENT_PROJECTION_UUID_INDEX));
			startActivity(intent);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
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

	private void startOnlineSearch(String queryId) {
		boolean start = BackendService.searchHistoryOrder(this, mCustomerId,
				queryId);
		if (start) {
			mSwipeRefreshLayout.setRefreshing(true);
			updateFooterState(true);
		}
	}

	private void updateFooterState(boolean loading) {
		if (loading) {
			mFooterProgressView.setVisibility(View.VISIBLE);
			mFooterTextView.setText(R.string.text_loading);
		} else {
			mFooterProgressView.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.text_more_history_order);
		}
	}

	private void refreshHistoryOrder() {
		boolean start = BackendService.refreshHistoryOrder(this, mCustomerId,
				mStartId);
		if (start) {
			mSwipeRefreshLayout.setRefreshing(true);
			updateFooterState(true);
		}
	}

	private void moreHsitoryOrder() {
		if (mFooterProgressView.getVisibility() == View.GONE) {
			boolean start = BackendService.moreHistoryOrder(this, mCustomerId,
					mEndId);
			if (start) {
				mSwipeRefreshLayout.setRefreshing(true);
				updateFooterState(true);
			}
		}
	}

	private void restartLoader() {
		LoaderManager manager = getLoaderManager();
		if (manager.getLoader(LocalUtils.MAIN_ORDER_LOADER) == null) {
			getLoaderManager().initLoader(LocalUtils.MAIN_ORDER_LOADER, null,
					this);
		} else {
			getLoaderManager().restartLoader(LocalUtils.MAIN_ORDER_LOADER,
					null, this);
		}
	}

	@Override
	public String getCurrentQueryText() {
		if (mMenuSearchView == null || mMenuSearchView.isIconified()) {
			return "";
		} else {
			return mMenuSearchView.getQuery().toString();
		}
	}
}
