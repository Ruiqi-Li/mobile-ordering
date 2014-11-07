package sg.com.smartmediasoft.storeclient.ui.tamplate;

import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.database.LocalOrderTemplate;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

public class OrderTamplateActivity extends Activity {
	public static final String EXTRA_ORDER_TEMPLATE = "order_template";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.tamplate_activity);

		if (savedInstanceState == null) {
			LocalOrderTemplate template = getIntent().getParcelableExtra(
					EXTRA_ORDER_TEMPLATE);
			if (template == null) {
				setTitle(R.string.title_order_template_create);
			} else {
				setTitle(R.string.title_order_template_edit);
			}

			Fragment fragment = new OrderTemplateProductFragment();
			Bundle args = new Bundle();
			args.putParcelable(EXTRA_ORDER_TEMPLATE, template);
			fragment.setArguments(args);

			getFragmentManager().beginTransaction()
					.replace(R.id.tamplate_fragment_root, fragment).commit();
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
