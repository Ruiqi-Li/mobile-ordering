package sg.com.smartmediasoft.storeclient.ui;

import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import sg.com.smartmediasoft.storeclient.ui.ProductSelectAdapter.ProductSelectAdapterCalback;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SectionIndexer;

public class ProductSelectPagerAdapter extends PagerAdapter {
	private static final int PAGE_COUNT = 2;
	private static final int VEGETABLE_PAGE_INDEX = 0;
	private static final int FRUIT_PAGE_INDEX = 1;

	private Context mContext;

	private ProductSelectAdapter mVegetableAdapter;
	private ProductSelectAdapter mFruitAdapter;

	private ProductItemClickCalback mProductItemClickCalback;

	public static interface ProductItemClickCalback {
		public void onSelectProduct(LocalProduct product);
	}

	public ProductSelectPagerAdapter(Context context,
			ProductItemClickCalback productItemClickCalback,
			ProductSelectAdapterCalback productSelectCallback) {
		mContext = context;
		mProductItemClickCalback = productItemClickCalback;

		mVegetableAdapter = new ProductSelectAdapter(mContext, null,
				productSelectCallback);
		mFruitAdapter = new ProductSelectAdapter(mContext, null,
				productSelectCallback);
	}

	public void notifyProductChange() {
		mVegetableAdapter.notifyDataSetChanged();
		mFruitAdapter.notifyDataSetChanged();
	}

	public void changeVegetableCursor(Cursor cursor, SectionIndexer indexer) {
		mVegetableAdapter.changeCursor(cursor);
		mVegetableAdapter.setIndexer(indexer);
	}

	public void changeFruitCursor(Cursor cursor, SectionIndexer indexer) {
		mFruitAdapter.changeCursor(cursor);
		mFruitAdapter.setIndexer(indexer);
	}

	/**
	 * @return the number of pages to display
	 */
	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

	/**
	 * @return true if the value returned from
	 *         {@link #instantiateItem(ViewGroup, int)} is the same object as
	 *         the {@link View} added to the {@link ViewPager}.
	 */
	@Override
	public boolean isViewFromObject(View view, Object o) {
		return o == view;
	}

	// BEGIN_INCLUDE (pageradapter_getpagetitle)
	/**
	 * Return the title of the item at {@code position}. This is important as
	 * what this method returns is what is displayed in the
	 * {@link SlidingTabLayout}.
	 * <p>
	 * Here we construct one using the position value, but for real application
	 * the title should refer to the item's contents.
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case VEGETABLE_PAGE_INDEX:
			return mContext.getString(R.string.text_vegetable);
		case FRUIT_PAGE_INDEX:
			return mContext.getString(R.string.text_fruit);
		default:
			return null;
		}
	}

	// END_INCLUDE (pageradapter_getpagetitle)

	/**
	 * Instantiate the {@link View} which should be displayed at
	 * {@code position}. Here we inflate a layout from the apps resources and
	 * then change the text view to signify the position.
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		int headerHeight = mContext.getResources().getDimensionPixelSize(
				R.dimen.order_list_item_header_height);
		
		switch (position) {
		case VEGETABLE_PAGE_INDEX: {
			PinnedHeaderListView vegetableListView = (PinnedHeaderListView) LayoutInflater.from(
					mContext).inflate(R.layout.widget_pinned_header_list, null);
			vegetableListView.setHeaderHeight(headerHeight);
			container.addView(vegetableListView);

			vegetableListView.setAdapter(mVegetableAdapter);
			vegetableListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mProductItemClickCalback.onSelectProduct(new LocalProduct(
							(Cursor) mVegetableAdapter.getItem(position)));
				}
			});

			return vegetableListView;
		}
		case FRUIT_PAGE_INDEX: {
			PinnedHeaderListView fruitListView = (PinnedHeaderListView) LayoutInflater.from(mContext)
					.inflate(R.layout.widget_pinned_header_list, null);
			fruitListView.setHeaderHeight(headerHeight);
			container.addView(fruitListView);

			fruitListView.setAdapter(mFruitAdapter);
			fruitListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mProductItemClickCalback.onSelectProduct(new LocalProduct(
							(Cursor) mFruitAdapter.getItem(position)));
				}
			});

			return fruitListView;
		}
		default:
			return null;
		}
	}

	/**
	 * Destroy the item from the {@link ViewPager}. In our case this is simply
	 * removing the {@link View}.
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}
