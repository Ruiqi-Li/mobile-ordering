package sg.com.smartmediasoft.storeclient.ui;

import java.util.HashSet;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

abstract public class NotifyAwareFragment extends Fragment {

	private Set<String> mListereningActionSet;
	private boolean mCreated = false;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!mCreated) {
				return;
			}

			String action = intent.getAction();
			if (mListereningActionSet.contains(action)) {
				onActionReveived(context, action, intent);
			}
		}
	};

	abstract public void getListereningActions(Set<String> actions);

	abstract public void onActionReveived(Context context, String action,
			Intent data);

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mListereningActionSet = new HashSet<String>();
		getListereningActions(mListereningActionSet);

		SMSBroadcastManager.registReceiver(getActivity(), mReceiver,
				mListereningActionSet);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mCreated = true;
	}

	@Override
	public void onDetach() {
		SMSBroadcastManager.unregistReveiver(getActivity(), mReceiver);

		super.onDetach();
	}

}
