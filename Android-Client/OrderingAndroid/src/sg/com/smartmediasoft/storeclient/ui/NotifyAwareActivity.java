package sg.com.smartmediasoft.storeclient.ui;

import java.util.HashSet;
import java.util.Set;

import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

abstract public class NotifyAwareActivity extends Activity {

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mListereningActionSet = new HashSet<String>();
		getListereningActions(mListereningActionSet);

		SMSBroadcastManager.registReceiver(this, mReceiver,
				mListereningActionSet);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mCreated = true;
	}

	@Override
	protected void onDestroy() {
		SMSBroadcastManager.unregistReveiver(this, mReceiver);

		super.onDestroy();
	}

}
