package sg.com.smartmediasoft.storeclient.ui.login;

import java.util.Set;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.SMSBroadcastManager;
import sg.com.smartmediasoft.storeclient.UserController;
import sg.com.smartmediasoft.storeclient.service.BackendService;
import sg.com.smartmediasoft.storeclient.ui.NotifyAwareActivity;
import sg.com.smartmediasoft.storeclient.ui.main.MainActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends NotifyAwareActivity {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	// Values for email and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginStatusView;
	private Button mLoginBtn;

	private UserController mUserController;
	
	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			mPasswordView.setError(null);
		}
	};

	@Override
	public void getListereningActions(Set<String> actions) {
		actions.add(SMSBroadcastManager.ACTION_DEVICE_REGIST_FINISHED);
		actions.add(SMSBroadcastManager.ACTION_LOGIN_FINISHED);
	}

	@Override
	public void onActionReveived(Context context, String action, Intent data) {
		if (SMSBroadcastManager.ACTION_DEVICE_REGIST_FINISHED.equals(action)) {
			if (!TextUtils.isEmpty(mUserController.getDeviceToken())) {
				mLoginBtn.setEnabled(true);
			}
		} else if (SMSBroadcastManager.ACTION_LOGIN_FINISHED.equals(action)) {
			showProgress(false);

			int resultCode = data.getIntExtra(
					SMSBroadcastManager.EXTRA_RESULT_CODE,
					LocalUtils.CODE_FAILED_UNKNOW);
			if (resultCode == LocalUtils.CODE_SUCCESS) {
				LoginActivity.this.finish();
				LoginActivity.this.startActivity(new Intent(context,
						MainActivity.class));
			} else {
				mLoginBtn.setEnabled(true);
				mPasswordView
						.setError(getString(R.string.login_error_incorrect));
				mPasswordView.requestFocus();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_activity);

		mUserController = new UserController(this);
		if (mUserController.hasLoginUser()) {
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			finish();
			return;
		}

		TextView titleView = (TextView) findViewById(R.id.login_title);
		titleView.setText(R.string.login_customer_title);

		// Set up the login form.
		mUsername = getIntent().getStringExtra(EXTRA_EMAIL);
		mUsernameView = (EditText) findViewById(R.id.login_username);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.login_password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		mUsernameView.addTextChangedListener(mTextWatcher);
		mPasswordView.addTextChangedListener(mTextWatcher);

		mLoginStatusView = findViewById(R.id.sign_in_progressbar);

		mLoginBtn = (Button) findViewById(R.id.sign_in_button);
		mLoginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		if (TextUtils.isEmpty(mUserController.getDeviceToken())) {
			mLoginBtn.setEnabled(false);
		}

		boolean autoLogout = getIntent().getBooleanExtra(
				MainActivity.EXTRA_AUTO_LOGOUT, false);
		if (autoLogout) {
			String logOutMessage = getIntent().getStringExtra(
					MainActivity.EXTRA_AUTO_LOGOUT_MESSAGE);
			getIntent().putExtra(MainActivity.EXTRA_AUTO_LOGOUT, false);

			new AlertDialog.Builder(this)
					.setTitle(R.string.dialog_auto_logout_title)
					.setMessage(logOutMessage)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).create().show();
		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView
					.setError(getString(R.string.login_error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView
					.setError(getString(R.string.login_error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView
					.setError(getString(R.string.login_error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			boolean start = BackendService.login(this, mUsername, mPassword,
					mUserController.getDeviceToken());
			if (start) {
				// Show a progress spinner, and kick off a background task to
				// perform the user login attempt.
				mLoginBtn.setEnabled(false);
				showProgress(true);
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if (show) {
			mLoginBtn.setText("");
			mLoginBtn.setEnabled(false);
			mUsernameView.setEnabled(false);
			mPasswordView.setEnabled(false);
		} else {
			mLoginBtn.setText(R.string.login_text_login);
			mLoginBtn.setEnabled(true);
			mUsernameView.setEnabled(true);
			mPasswordView.setEnabled(true);
		}

		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

}
