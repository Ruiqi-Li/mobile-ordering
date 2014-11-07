package sg.com.smartmediasoft.storeclient.ui.main;

import java.util.Set;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.UserController;
import sg.com.smartmediasoft.storeclient.database.LocalOrder;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.ui.FilterLayout;
import sg.com.smartmediasoft.storeclient.ui.FilterLayout.OnFilterChangeListener;
import sg.com.smartmediasoft.storeclient.ui.NameSectionIndexer;
import sg.com.smartmediasoft.storeclient.ui.NotifyAwareFragment;
import sg.com.smartmediasoft.storeclient.ui.RefreshListener;
import sg.com.smartmediasoft.storeclient.ui.main.MainOrderListAdapter.SearchBoxCallback;
import sg.com.smartmediasoft.storeclient.ui.orderdetail.OrderDetailActivity;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainOrderListFragment extends NotifyAwareFragment implements
		LoaderCallbacks<Cursor>, OnItemClickListener, OnFilterChangeListener,
		SearchBoxCallback {
	private FilterLayout mFilterLayout;
	private ListView mListView;
	private MainOrderListAdapter mAdapter;
	private RefreshListener mRefreshListener;

	private String mCurrentCustomerId;

	public void resetContent(String customerId) {
		UserController userController = new UserController(getActivity());
		if (userController.userIsCustomer()) {
			customerId = userController.getCustomerId();
		}

		mFilterLayout.resetFilterTodefault(false);
		mCurrentCustomerId = customerId;
		mAdapter.showCustomerName(userController.userIsCustomer() ? false
				: customerId == null);
		boolean start = BackendService.syncOrder(getActivity(), customerId,
				false);
		if (start) {
			mRefreshListener.onRefreshStateChange(true);
		}

		restartLoader(null);
	}

	@Override
	public void getListereningActions(Set<String> actions) {
		actions.add(SMSBroadcastManager.ACTION_SYNC_ORDER_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_MAKE_ORDER_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_EDIT_ORDER_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_CHANGE_ORDER_STATE_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_USER_CONFIG_CHANGED);
	}

	@Override
	public void onActionReveived(Context context, String action, Intent data) {
		if (SMSBroadcastManager.ACTION_SYNC_ORDER_FINISHED.equals(action)
				|| SMSBroadcastManager.ACTION_MAKE_ORDER_FINISHED
						.equals(action)
				|| SMSBroadcastManager.ACTION_EDIT_ORDER_FINISHED
						.equals(action)
				|| SMSBroadcastManager.ACTION_CHANGE_ORDER_STATE_FINISHED
						.equals(action)) {
			restartLoader(null);

			if (SMSBroadcastManager.ACTION_SYNC_ORDER_FINISHED.equals(action)) {
				mRefreshListener.onRefreshStateChange(false);
			}
		} else if (SMSBroadcastManager.ACTION_USER_CONFIG_CHANGED.equals(action)) {
			getActivity().invalidateOptionsMenu();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mRefreshListener = (RefreshListener) activity;
	}

	@Override
	public void onStop() {
		super.onStop();
		
		mFilterLayout.closeSearchViewIfNeeded();
	}

	@Override
	public void onDetach() {
		mRefreshListener = null;

		super.onDetach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		return inflater.inflate(R.layout.main_list_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mFilterLayout = (FilterLayout) view
				.findViewById(R.id.main_filter_layout);
		mFilterLayout.setOnFilterChangeListener(this);
		mListView = (ListView) view.findViewById(R.id.main_list_listview);
		mAdapter = new MainOrderListAdapter(getActivity(), null, this, false);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		resetContent(null);
	}

	@Override
	public void onDestroyView() {
		getLoaderManager().destroyLoader(LocalUtils.MAIN_ORDER_LOADER);

		super.onDestroyView();
	}
	
	@Override
	public String getCurrentQueryText() {
		return mFilterLayout.getQueryText();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		UserController userController = new UserController(getActivity());
		if (userController.userIsCustomer()) {
			if (userController.getUserCanMakeOrder()) {
				inflater.inflate(R.menu.activity_main_customer, menu);
			}
		} else {
			if (mCurrentCustomerId == null) {
				if (userController.userIsDirector()
						|| userController.getUserCanViewOrderStatistic()) {
					inflater.inflate(R.menu.activity_main_supplier_home, menu);
				}
			} else {
				inflater.inflate(R.menu.activity_main_supplier_single, menu);

				if (!userController.getUserCanMakeOrder()) {
					menu.removeItem(R.id.action_new_order);
				}
				
				if (!userController.getUserCanViewOrderHistory()) {
					menu.removeItem(R.id.action_history_order);
				}
				
				if (userController.userIsDirector()) {
					menu.removeItem(R.id.action_new_order);
				}
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		MainOrderListLoader loader = new MainOrderListLoader(getActivity(),
				LocalOrder.CONTENT_URI, LocalOrder.CONTENT_PROJECTION, null,
				null, null);

		mFilterLayout.configSelectionAndSort(loader, mCurrentCustomerId);
		mAdapter.setShowDateType(mFilterLayout.getShowDateType());
		loader.setShowDateType(mFilterLayout.getShowDateType());

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
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}

	@Override
	public void onFilterSelectionChange() {
		restartLoader(null);
	}
	
	@Override
	public void onFilterSearchTextChanged(String text) {
		restartLoader(null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor cursor = (Cursor) mAdapter.getItem(position);
		Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
		intent.putExtra(OrderDetailActivity.EXTRA_LOCAL_ORDER_ID,
				cursor.getString(LocalOrder.CONTENT_PROJECTION_UUID_INDEX));
		startActivity(intent);
	}

	private void restartLoader(Bundle args) {
		if (getActivity() == null) {
			return;
		}
		
		LoaderManager manager = getLoaderManager();
		if (manager.getLoader(LocalUtils.MAIN_ORDER_LOADER) == null) {
			getLoaderManager().initLoader(LocalUtils.MAIN_ORDER_LOADER, args,
					MainOrderListFragment.this);
		} else {
			getLoaderManager().restartLoader(LocalUtils.MAIN_ORDER_LOADER,
					args, MainOrderListFragment.this);
		}
	}
}
