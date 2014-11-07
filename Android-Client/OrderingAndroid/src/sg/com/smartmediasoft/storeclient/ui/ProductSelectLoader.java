package sg.com.smartmediasoft.storeclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.database.LocalProduct;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

public class ProductSelectLoader extends CursorLoader {

	private List<String> mSectionHeaders;
	private List<Integer> mSectionCounts;
	private Map<String, Integer> mIdToPositionMap;

	public ProductSelectLoader(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArg, String order) {
		super(context, uri, projection, selection, selectionArg, order);
	}

	@Override
	public Cursor loadInBackground() {
		Cursor cursor = super.loadInBackground();
		
		if (cursor.moveToFirst()) {
			boolean chineseDevice = LocalUtils.isDeveceInChinese();
			mSectionHeaders = new ArrayList<String>();
			mSectionCounts = new ArrayList<Integer>();
			mIdToPositionMap = new HashMap<String, Integer>();

			String indexName = "A";
			int count = 0;
			for (; !cursor.isAfterLast(); cursor.moveToNext()) { 
				mIdToPositionMap.put(cursor
						.getString(LocalProduct.CONTENT_PROJECTION_UUID_INDEX),
						cursor.getPosition());
				String name =  chineseDevice ? cursor
						.getString(
								LocalProduct.CONTENT_PROJECTION_CHINESE_SORT_NAME_INDEX)
						.toUpperCase()
						: cursor.getString(
								LocalProduct.CONTENT_PROJECTION_ENGLISH_NAME_INDEX)
								.toUpperCase();
				if (name.startsWith(indexName)) {
					count++;
				} else {
					if (count != 0) {
						mSectionHeaders.add(indexName);
						mSectionCounts.add(count);
					}

					indexName = name.substring(0, 1);
					count = 1;
				}
			}

			if (count > 0
					&& (mSectionHeaders.isEmpty() || !indexName
							.equals(mSectionHeaders.get(mSectionHeaders.size() - 1)))) {
				mSectionHeaders.add(indexName);
				mSectionCounts.add(count);
			}
		} else {
			mSectionHeaders = null;
			mSectionCounts = null;
		}

		return cursor;
	}

	public Map<String, Integer> getIdtoPositionMap() {
		return mIdToPositionMap;
	}

	public List<String> getSectionHeaders() {
		return mSectionHeaders;
	}

	public List<Integer> getSectionCounts() {
		return mSectionCounts;
	}

}
