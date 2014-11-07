package sg.com.smartmediasoft.storeclient.ui.main;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

public class MainOrderListLoader extends CursorLoader {

	private List<String> mSectionHeaders;
	private List<Integer> mSectionCounts;
	private String mShowDateType;

	public MainOrderListLoader(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArg, String order) {
		super(context, uri, projection, selection, selectionArg, order);

		mSectionHeaders = new ArrayList<String>();
		mSectionCounts = new ArrayList<Integer>();
	}
	
	public void setShowDateType(String dateType) {
		mShowDateType = dateType;
	}

	@Override
	public Cursor loadInBackground() {
		Cursor cursor = super.loadInBackground();
		mSectionHeaders.clear();
		mSectionCounts.clear();

		if (cursor.moveToFirst()) {
			Calendar calendar = Calendar.getInstance();
			DateFormat dateFormat = LocalUtils.getDateFormater();
			
			int timeIndex = cursor.getColumnIndex(mShowDateType);

			calendar.setTimeInMillis(cursor.getLong(timeIndex));
			String header = dateFormat.format(calendar.getTime());
			int count = 0;

			for (; !cursor.isAfterLast(); cursor.moveToNext()) {
				calendar.setTimeInMillis(cursor.getLong(timeIndex));
				String newHeader = dateFormat.format(calendar.getTime());

				if (header.equals(newHeader)) {
					count++;
				} else {
					mSectionHeaders.add(header);
					mSectionCounts.add(count);

					header = newHeader;
					count = 1;
				}
			}

			if (mSectionHeaders.isEmpty()
					|| header != mSectionHeaders
							.get(mSectionHeaders.size() - 1)) {
				mSectionHeaders.add(header);
				mSectionCounts.add(count);
			}
		}

		return cursor;
	}

	public List<String> getSectionHeaders() {
		return mSectionHeaders;
	}

	public List<Integer> getSectionCounts() {
		return mSectionCounts;
	}

}
