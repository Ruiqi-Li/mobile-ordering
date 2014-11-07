package sg.com.smartmediasoft.storeclient.ui.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.UserController;
import sg.com.smartmediasoft.storeclient.database.LocalOrderTemplate;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.ui.NotifyAwareFragment;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
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

public class CustomerDrawerFragment extends NotifyAwareFragment implements
        OnClickListener, LoaderCallbacks<Cursor>, OnItemClickListener {
    public static final int TEMPLATE_ACTIVITY_RESULT_CODE = 10;

    public static String EXTRA_CUSTOMER_UUID = "customer_uuid";
    public static String EXTRA_CUSTOMER_NAME = "customer_name";
    public static String EXTRA_ORDER_TEMPLATE = "order_template";

    private DrawerActionCallback mCallback;
    private ListView mListView;
    private CustomerDrawerAdapter mAdapter;

    public List<LocalOrderTemplate> getCustomerTemplates() {
        return new ArrayList<LocalOrderTemplate>(mAdapter.getTemplateList());
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(LocalUtils.DRAWER_LIST_LOADER, null,
                this);
    }

    @Override
    public void getListereningActions(Set<String> actions) {
        actions.add(SMSBroadcastManager.ACTION_SYNC_ORDER_TEMPLATE_FINISHED);
        actions.add(SMSBroadcastManager.ACTION_SYNC_CUSTOMER_FINISHED);
        actions.add(SMSBroadcastManager.ACTION_USER_CONFIG_CHANGED);
        actions.add(SMSBroadcastManager.ACTION_CHANGE_ORDER_STATE_FINISHED);
    }

    @Override
    public void onActionReveived(Context context, String action, Intent data) {
        if (SMSBroadcastManager.ACTION_SYNC_ORDER_TEMPLATE_FINISHED
                .equals(action)
                || SMSBroadcastManager.ACTION_SYNC_CUSTOMER_FINISHED
                        .equals(action)
                || SMSBroadcastManager.ACTION_CHANGE_ORDER_STATE_FINISHED
                        .equals(action)) {
            LoaderManager manager = getLoaderManager();
            if (manager.getLoader(LocalUtils.DRAWER_LIST_LOADER) != null) {
                manager.restartLoader(LocalUtils.DRAWER_LIST_LOADER, null, this);
            }

            if (mAdapter != null) {
                mAdapter.refreshUserDisplay();
                mAdapter.notifyDataSetChanged();
            }
        } else if (SMSBroadcastManager.ACTION_USER_CONFIG_CHANGED
                .equals(action)) {
            if (mAdapter != null) {
                UserController controller = new UserController(getActivity());
                mAdapter.resetCanEditTemplate(controller
                        .getUserCanEditOrderTemplate());
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
        getLoaderManager()
                .initLoader(LocalUtils.DRAWER_LIST_LOADER, null, this);

        return inflater.inflate(R.layout.main_drawer_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.main_drawer_bottom_btn).setOnClickListener(this);

        UserController controller = new UserController(getActivity());

        mListView = (ListView) view.findViewById(R.id.main_drawer_listview);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(this);
        mAdapter = new CustomerDrawerAdapter(getActivity(),
                controller.getUserCanEditOrderTemplate());
        mListView.setAdapter(mAdapter);

        BackendService.syncCustomer(getActivity(), false);
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
        return new CursorLoader(getActivity(), LocalOrderTemplate.CONTENT_URI,
                LocalOrderTemplate.CONTENT_PROJECTION, null, null,
                LocalOrderTemplate._ID);
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
        case CustomerDrawerAdapter.USER_ITEM_POSITION: {
            mCallback.onDrawerActionStart(
                    DrawerActionCallback.ACTION_SELECT_LOGIN_USER, null);
            break;
        }
        case CustomerDrawerAdapter.HISTORY_ITEM_POSITION: {
            mListView.setItemChecked(position, true);
            mCallback.onDrawerActionStart(
                    DrawerActionCallback.ACTION_SELECT_ORDER_HISTORY, null);
            break;
        }
        case CustomerDrawerAdapter.STORE_ITEM_POSITION: {
            mListView.setItemChecked(position, true);
            mCallback.onDrawerActionStart(
                    DrawerActionCallback.ACTION_SELECT_PRODUCT_MANAGE, null);
            break;
        }
        default: {
            if (mAdapter.getCanEditTemplate()) {
                if (position == CustomerDrawerAdapter.WITH_TEMPLATE_TEMPLATE_POSITION) {
                    mListView.setItemChecked(position, false);
                    mCallback.onDrawerActionStart(
                            DrawerActionCallback.ACTION_SELECT_ORDER_TEMPLATE,
                            null);
                    return;
                } else if (position == CustomerDrawerAdapter.WITH_TEMPLATE_TEMPLATE_DIVILER) {
                    return;
                }
            } else {
                if (position == CustomerDrawerAdapter.WITHOUT_TEMPLATE_TEMPLATE_DIVILER) {
                    return;
                }
            }

            Bundle data = new Bundle();
            data.putParcelable(EXTRA_ORDER_TEMPLATE, mAdapter.getItem(position));

            mCallback.onDrawerActionStart(
                    DrawerActionCallback.ACTION_SELECT_ORDER_TEMPLATE, data);
        }
        }
    }

}
