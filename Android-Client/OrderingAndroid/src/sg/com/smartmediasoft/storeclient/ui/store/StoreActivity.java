package sg.com.smartmediasoft.storeclient.ui.store;

import sg.com.smartmediasoft.storeclient.R;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

public class StoreActivity extends Activity {
	public static final String EXTRA_SUPPLIER_STORAGE = "supplier_storage";
	public static final String EXTRA_CUSTOMER_ID = "customer_id";
	public static final String EXTRA_CUSTOMER_NAME = "customer_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.store_activity);

		String customerName = getIntent().getStringExtra(EXTRA_CUSTOMER_NAME);
		setTitle(getString(R.string.title_store_activity, customerName));

		if (savedInstanceState == null) {
			Fragment fragment = new StoreProductFragment();
			Bundle args = new Bundle();
			args.putBoolean(StoreProductFragment.EXTRA_IS_SUPPLIER_STORE,
					getIntent().getBooleanExtra(EXTRA_SUPPLIER_STORAGE, false));
			args.putString(StoreProductFragment.EXTRA_CUSTOMER_ID, getIntent()
					.getStringExtra(EXTRA_CUSTOMER_ID));
			fragment.setArguments(args);
			getFragmentManager().beginTransaction()
					.replace(R.id.activity_root, fragment).commit();
		}
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

}
