package sg.com.smartmediasoft.storeclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.com.smartmediasoft.storeclient.InputFilterMinMax;
import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.TextHighlighter;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import sg.com.smartmediasoft.storeclient.ui.IndexerListAdapter.Placement;
import sg.com.smartmediasoft.storeclient.ui.ProductSelectAdapter.ProductSelectAdapterCalback;
import sg.com.smartmediasoft.storeclient.ui.ProductSelectPagerAdapter.ProductItemClickCalback;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SectionIndexer;

abstract public class ProductSelectFragmentBase extends NotifyAwareFragment
		implements LoaderCallbacks<Cursor>, ProductItemClickCalback,
		ProductSelectAdapterCalback {

	private SearchView mMenuSearchView;
	private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;

	private ProductSelectPagerAdapter mPagerAdapter;
	private Map<String, LocalProduct> mSelectMap = new HashMap<String, LocalProduct>();
	private boolean mOnlyShowSelectedProduct;

	private ProgressDialog mProgressDialog;

	protected boolean mIdDeviceInChinese;
	protected TextHighlighter mTextHighlighter;

	public void notifyProductChange() {
		mPagerAdapter.notifyProductChange();
	}

	abstract public List<LocalProduct> getInitialSelectItems();

	abstract public void OnProductSelected(LocalProduct product);
	
	public void resetSelectMode(boolean onlyShowSelectedProduct) {
		if (mOnlyShowSelectedProduct != onlyShowSelectedProduct) {
			mOnlyShowSelectedProduct = onlyShowSelectedProduct;
			restartLoader();
		}
	}

	public void showProcessDialog() {
		mProgressDialog.show();
	}

	public void dismessProcessDialog() {
		mProgressDialog.dismiss();
	}

	public Map<String, LocalProduct> getProductSelectMap() {
		return mSelectMap;
	}
	
	public boolean hasSelectProduct(String id) {
		return mSelectMap != null && mSelectMap.containsKey(id);
	}
	
	public void closeSearchBoxIfNeeded() {
		if (!mMenuSearchView.isIconified()) {
			mMenuSearchView.setQuery(null, false);
			mMenuSearchView.clearFocus();
			mMenuSearchView.setIconified(true);
		}
	}
	
	public void configAmountEditText(EditText editText, boolean descimalUnit) {
		if (descimalUnit) {
			editText.setFilters(new InputFilter[] { new InputFilterMinMax(
					0.1, 10000) });
			editText.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		} else {
			editText.setFilters(new InputFilter[] { new InputFilterMinMax(1,
					10000) });
			editText.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_SIGNED);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		List<LocalProduct> initialProducts = getInitialSelectItems();

		mTextHighlighter = new TextHighlighter(getResources().getColor(
				R.color.smartmediasoft_color), Typeface.BOLD);
		mIdDeviceInChinese = LocalUtils.isDeveceInChinese();

		if (initialProducts != null) {
			for (LocalProduct product : initialProducts) {
				mSelectMap.put(product.mUUID, product);
			}
		}

		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog
				.setMessage(getString(R.string.dialog_edit_order_progress_message));
		mProgressDialog.setCancelable(false);

		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		mPagerAdapter = new ProductSelectPagerAdapter(getActivity(), this, this);
		mViewPager.setAdapter(mPagerAdapter);
		mSlidingTabLayout = (SlidingTabLayout) view
				.findViewById(R.id.sliding_tabs);
		mSlidingTabLayout.setViewPager(mViewPager);

		initLoader();
	}

	@Override
	public void onDestroyView() {
		destoryLoader();

		super.onDestroyView();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_menu, menu);

		mMenuSearchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		mMenuSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (!ProductSelectFragmentBase.this.isResumed()) {
					return true;
				}

				restartLoader();
				return true;
			}
		});
		mMenuSearchView.setOnSearchClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!ProductSelectFragmentBase.this.isResumed()) {
					return;
				}

				restartLoader();
			}
		});
		mMenuSearchView.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				if (!ProductSelectFragmentBase.this.isResumed()) {
					return false;
				}

				restartLoader();
				return false;
			}
		});
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<String>();

        selection.append(LocalProduct.DELETED).append("=?");
        selectionArgs.add("0");

		if (mMenuSearchView != null && !mMenuSearchView.isIconified()
				&& !TextUtils.isEmpty(mMenuSearchView.getQuery())) {
		    appendANDIfNeeded(selection);
		    
			selection.append(
					LocalUtils.isDeveceInChinese() ? LocalProduct.CHINESE_NAME
							: LocalProduct.ENGLISH_NAME).append(" LIKE ?");
			selectionArgs.add("%" + mMenuSearchView.getQuery() + "%");
		}
		
		if (mOnlyShowSelectedProduct) {
			appendANDIfNeeded(selection);
			
			selection.append(LocalProduct.UUID).append(" IN (");
			if (!mSelectMap.isEmpty()) {
				for (String uuid : mSelectMap.keySet()) {
					selection.append("?,");
					selectionArgs.add(uuid);
				}
				selection.deleteCharAt(selection.length() - 1);
			}
			selection.append(")");
		}

		switch (id) {
		case LocalUtils.PRODUCT_LIST_VEGETABLE_LOADER: {
			appendANDIfNeeded(selection);

			selection.append(LocalProduct.TYPE).append("=?");
			selectionArgs.add(LocalUtils.PRODUCT_TYPE_VEGETABLE);
			break;
		}
		case LocalUtils.PRODUCT_LIST_FRUIT_LOADER:
			appendANDIfNeeded(selection);

			selection.append(LocalProduct.TYPE).append("=?");
			selectionArgs.add(LocalUtils.PRODUCT_TYPE_FRUIT);
			break;
		default:
		}

		ProductSelectLoader loader = new ProductSelectLoader(getActivity(),
				LocalProduct.CONTENT_URI, LocalProduct.CONTENT_PROJECTION,
				selection.toString(),
				selectionArgs.toArray(new String[selectionArgs.size()]),
				LocalUtils.isDeveceInChinese() ? LocalProduct.CHINESE_SORT_NAME
						: LocalProduct.ENGLISH_SORT_NAME);
		onConfigLoader(loader);

		return loader;
	}

	private void appendANDIfNeeded(StringBuilder builder) {
		if (builder.length() > 0) {
			builder.append(" AND ");
		}
	}

	@Override
	public View newProductItemView(IndexerListAdapter adapter, Context context,
			Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.product_list_item,
				null);
	}

	@Override
	public void bindProductItemView(IndexerListAdapter adapter, View view,
			Context context, Cursor cursor) {
		ProductSelectItemLayout itenLayout = (ProductSelectItemLayout) view;

		String uuid = cursor
				.getString(LocalProduct.CONTENT_PROJECTION_UUID_INDEX);
		String name = mIdDeviceInChinese ? cursor
				.getString(LocalProduct.CONTENT_PROJECTION_CHINESE_NAME_INDEX)
				: cursor.getString(LocalProduct.CONTENT_PROJECTION_ENGLISH_NAME_INDEX);
		String imageUrl = cursor
				.getString(LocalProduct.CONTENT_PROJECTION_IMAGE_URL_INDEX);
		double amount = getProductSelectAmount(uuid);
		String amountText = null;
		if (amount > 0) {
			amountText = LocalUtils
					.getDesplayUnitText(
							amount,
							cursor.getString(LocalProduct.CONTENT_PROJECTION_UNIT_NAME_INDEX),
							cursor.getInt(LocalProduct.CONTENT_PROJECTION_DECIMAL_UNIT_INDEX) == 1);
		}

		Placement placement = adapter.getItemPlacementInSection(cursor
				.getPosition());

		itenLayout.bindContent(
				mTextHighlighter.applyPrefixHighlight(name, getQueryText()),
				imageUrl, amountText, placement.sectionHeader,
				placement.lastInSection);
	}

	public void onConfigLoader(ProductSelectLoader laoder) {

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case LocalUtils.PRODUCT_LIST_VEGETABLE_LOADER: {
			SectionIndexer indexer = null;
			ProductSelectLoader productLoader = (ProductSelectLoader) loader;
			if (productLoader.getSectionHeaders() != null) {
				indexer = new NameSectionIndexer(
						productLoader.getSectionHeaders(),
						productLoader.getSectionCounts());
			}

			mPagerAdapter.changeVegetableCursor(data, indexer);
			break;
		}
		case LocalUtils.PRODUCT_LIST_FRUIT_LOADER: {
			SectionIndexer indexer = null;
			ProductSelectLoader productLoader = (ProductSelectLoader) loader;
			if (productLoader.getSectionHeaders() != null) {
				indexer = new NameSectionIndexer(
						productLoader.getSectionHeaders(),
						productLoader.getSectionCounts());
			}

			mPagerAdapter.changeFruitCursor(data, indexer);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
		case LocalUtils.PRODUCT_LIST_VEGETABLE_LOADER: {
			mPagerAdapter.changeVegetableCursor(null, null);
			break;
		}
		case LocalUtils.PRODUCT_LIST_FRUIT_LOADER: {
			mPagerAdapter.changeFruitCursor(null, null);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onSelectProduct(LocalProduct product) {
		if (mSelectMap.containsKey(product.mUUID)) {
			product = mSelectMap.get(product.mUUID);
		}

		OnProductSelected(product);
	}

	public void resetSelectedData() {
		mSelectMap.clear();
		List<LocalProduct> initialProducts = getInitialSelectItems();
		if (initialProducts != null) {
			for (LocalProduct product : initialProducts) {
				Log.d("RUIQI", "PUT: " + product.mUUID);
				mSelectMap.put(product.mUUID, product);
			}
		}

		mPagerAdapter.notifyProductChange();
	}

	protected double getProductSelectAmount(String productId) {
		Log.d("RUIQI", "SIZE: " +  mSelectMap.size());
		if (mSelectMap.containsKey(productId)) {
			return mSelectMap.get(productId).mAmount;
		} else {
			return -1;
		}
	}

	protected String getQueryText() {
		if (mMenuSearchView == null || mMenuSearchView.isIconified()
				|| TextUtils.isEmpty(mMenuSearchView.getQuery())) {
			return null;
		} else {
			return mMenuSearchView.getQuery().toString();
		}
	}

	private void initLoader() {
		if (getActivity() == null) {
			return;
		}
		
		LoaderManager manager = getLoaderManager();
		manager.initLoader(LocalUtils.PRODUCT_LIST_VEGETABLE_LOADER, null, this);
		manager.initLoader(LocalUtils.PRODUCT_LIST_FRUIT_LOADER, null, this);
	}

	public void restartLoader() {
		if (getActivity() == null) {
			return;
		}
		
		LoaderManager manager = getLoaderManager();
		if (manager.getLoader(LocalUtils.PRODUCT_LIST_VEGETABLE_LOADER) == null) {
			manager.initLoader(LocalUtils.PRODUCT_LIST_VEGETABLE_LOADER, null,
					this);
		} else {
			manager.restartLoader(LocalUtils.PRODUCT_LIST_VEGETABLE_LOADER,
					null, this);
		}

		if (manager.getLoader(LocalUtils.PRODUCT_LIST_FRUIT_LOADER) == null) {
			manager.initLoader(LocalUtils.PRODUCT_LIST_FRUIT_LOADER, null, this);
		} else {
			manager.restartLoader(LocalUtils.PRODUCT_LIST_FRUIT_LOADER, null,
					this);
		}
	}

	private void destoryLoader() {
		LoaderManager manager = getLoaderManager();
		if (manager.getLoader(LocalUtils.PRODUCT_LIST_VEGETABLE_LOADER) != null) {
			manager.destroyLoader(LocalUtils.PRODUCT_LIST_VEGETABLE_LOADER);
		}

		if (manager.getLoader(LocalUtils.PRODUCT_LIST_FRUIT_LOADER) == null) {
			manager.destroyLoader(LocalUtils.PRODUCT_LIST_FRUIT_LOADER);
		}
	}
}
