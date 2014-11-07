package sg.com.smartmediasoft.storeclient.ui.orderedit;

import sg.com.smartmediasoft.storeclient.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DialogSendOrder extends DialogFragment {

	private SendOrderDialogCallback mCallback;

	public static interface SendOrderDialogCallback {
		public void onSendOrder();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mCallback = (SendOrderDialogCallback) activity;
	}

	@Override
	public void onDetach() {
		mCallback = null;

		super.onDetach();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.send_dialog_title)
				.setMessage(R.string.send_dialog_message)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (mCallback != null) {
									mCallback.onSendOrder();
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
