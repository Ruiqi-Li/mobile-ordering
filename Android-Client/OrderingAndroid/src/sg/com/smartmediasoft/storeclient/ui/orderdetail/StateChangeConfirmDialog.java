package sg.com.smartmediasoft.storeclient.ui.orderdetail;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class StateChangeConfirmDialog extends DialogFragment {
	public static final String EXTRA_TARGET_STATE = "target_state";
	
	private StateActionConfirmCallback mCallback;

	public static interface StateActionConfirmCallback {
		public void onOrderStateChangeConfirmed(String currentState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mCallback = (StateActionConfirmCallback) activity;
	}

	@Override
	public void onDetach() {
		mCallback = null;

		super.onDetach();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String targetState = getArguments().getString(EXTRA_TARGET_STATE);

		String title = null;
		String message = null;
		if (LocalUtils.ORDER_STATE_CONFIRMED.equals(targetState)) {
			title = getString(R.string.order_state_pennding_action);
			message = getString(
					R.string.order_state_change_confirm_dialog_message,
					getString(R.string.order_state_pennding_action));
		} else if (LocalUtils.ORDER_STATE_DELIVERED.equals(targetState)) {
			title = getString(R.string.order_state_reveive_action);
			message = getString(
					R.string.order_state_change_confirm_dialog_message,
					getString(R.string.order_state_reveive_action));
		} else if (LocalUtils.ORDER_STATE_RECEIVED.equals(targetState)) {
			title = getString(R.string.order_state_shiped_action);
			message = getString(
					R.string.order_state_change_confirm_dialog_message,
					getString(R.string.order_state_shiped_action));
		} else if (LocalUtils.ORDER_STATE_CANCELED.equals(targetState)) {
			title = getString(R.string.order_state_cancel);
			message = getString(
					R.string.order_state_change_confirm_dialog_message,
					getString(R.string.order_state_cancel));
		}

		return new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (mCallback != null) {
									mCallback.onOrderStateChangeConfirmed(targetState);
								}
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						}).create();
	}

}
