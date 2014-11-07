package sg.com.smartmediasoft.storeclient.ui.main;

import sg.com.smartmediasoft.storeclient.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DialogFragmentlogoutConfirm extends DialogFragment {
	
	public static interface onComfirmedListener {
		void onLogoutConfirmed();
	}
	
	private onComfirmedListener mConfirmListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mConfirmListener = (onComfirmedListener) activity;
	}

	@Override
	public void onDetach() {
		mConfirmListener = null;
		
		super.onDetach();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.main_drawer_logout_dialog_title)
				.setMessage(R.string.main_drawer_logout_dialog_message)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								mConfirmListener.onLogoutConfirmed();
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
