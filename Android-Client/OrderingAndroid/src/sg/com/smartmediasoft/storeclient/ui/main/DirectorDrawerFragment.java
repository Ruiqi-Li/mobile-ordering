package sg.com.smartmediasoft.storeclient.ui.main;

import java.util.ArrayList;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.database.LocalCustomer;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.ui.NotifyAwareFragment;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DirectorDrawerFragment extends NotifyAwareFragment implements
		OnClickListener, LoaderCallbacks<Cursor>, OnItemClickListener, GetCustomerInterface {
	public static String EXTRA_CUSTOMER_UUID = "customer_uuid";
	public static String EXTRA_CUSTOMER_NAME = "customer_name";

	private DrawerActionCallback mCallback;
	private DirectorDrawerAdapter mAdapter;
	
    @Override
    public ArrayList<LocalCustomer> getCustomerList() {
		return mAdapter.getCustomerList();
    }

	@Override
	public void getListereningActions(Set<String> actions) {
		actions.add(SMSBroadcastManager.ACTION_SYNC_CUSTOMER_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_SYNC_ORDER_FINISHED);
	}

	@Override
	public void onActionReveived(Context context, String action, Intent data) {
		if (SMSBroadcastManager.ACTION_SYNC_CUSTOMER_FINISHED.equals(action)
				|| SMSBroadcastManager.ACTION_SYNC_ORDER_FINISHED
						.equals(action)) {
			if (getActivity() == null) {
				return;
			}
			
			LoaderManager manager = getLoaderManager();
			if (manager.getLoader(LocalUtils.DRAWER_LIST_LOADER) == null) {
				manager.initLoader(LocalUtils.DRAWER_LIST_LOADER, null,
						DirectorDrawerFragment.this);
			} else {
				manager.restartLoader(LocalUtils.DRAWER_LIST_LOADER, null,
						DirectorDrawerFragment.this);
			}

			if (SMSBroadcastManager.ACTION_SYNC_CUSTOMER_FINISHED
					.equals(action)) {
				if (mAdapter != null) {
					mAdapter.refreshUserDisplay();
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mCallback = (DrawerActionCallback) activity;
	}

	@Override
	public void onDetach() {
		mCallback = null;

		super.onDetach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		BackendService.syncCustomer(getActivity(), false);

		getLoaderManager()
				.initLoader(LocalUtils.DRAWER_LIST_LOADER, null, this);

		return inflater.inflate(R.layout.main_drawer_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		view.findViewById(R.id.main_drawer_bottom_btn).setOnClickListener(this);

		ListView listView = (ListView) view
				.findViewById(R.id.main_drawer_listview);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(this);

		mAdapter = new DirectorDrawerAdapter(getActivity());
		listView.setAdapter(mAdapter);
	}

	@Override
	public void onDestroyView() {
		LoaderManager manager = getLoaderManager();
		if (manager.getLoader(LocalUtils.DRAWER_LIST_LOADER) != null) {
			manager.destroyLoader(LocalUtils.DRAWER_LIST_LOADER);
		}

		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_drawer_bottom_btn:
			mCallback.onDrawerActionStart(DrawerActionCallback.ACTION_LOGOUT,
					null);
			break;
		default:
			break;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new DrawerOrderLoader(getActivity(), LocalCustomer.CONTENT_URI,
				LocalCustomer.CONTENT_PROJECTION, null, null,
				LocalCustomer.CHINESE_NAME);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case DirectorDrawerAdapter.USER_ITEM_POSITION:
			mCallback.onDrawerActionStart(
					DrawerActionCallback.ACTION_SELECT_LOGIN_USER, null);
			break;
		case DirectorDrawerAdapter.USER_CUSTOMER_DIVILIER_POSITION:
			break;
		default:
			LocalCustomer cuatomer = mAdapter.getItem(position);
			Bundle data = new Bundle();
			data.putString(EXTRA_CUSTOMER_UUID, cuatomer.mUuid);
			data.putString(EXTRA_CUSTOMER_NAME,
					LocalUtils.isDeveceInChinese() ? cuatomer.mChineseName
							: cuatomer.mEnglishName);
			mCallback.onDrawerActionStart(
					DrawerActionCallback.ACTION_SELECT_CUSTOMER, data);
			break;
		}
	}

}
