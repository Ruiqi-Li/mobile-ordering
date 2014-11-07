package sg.com.smartmediasoft.storeclient.ui.orderedit;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class EditOrderPagerAdapter extends FragmentPagerAdapter {
	private static final int NEW_ORDER_PAGE_COUNT = 3;

	public static final int EDITORDER_STEP1_PAGE = 0;
	public static final int EDITORDER_STEP2_PAGE = 1;
	public static final int EDITORDER_STEP3_PAGE = 2;

	public EditOrderPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case EDITORDER_STEP1_PAGE:
			return new DateSelectFragment();
		case EDITORDER_STEP2_PAGE:
			return new ProductSelectFragment();
		case EDITORDER_STEP3_PAGE:
			return new OrderPreviewFragment();
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		return NEW_ORDER_PAGE_COUNT;
	}

}
