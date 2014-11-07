package sg.com.smartmediasoft.storeclient.ui.main;

import sg.com.smartmediasoft.storeclient.database.LocalCustomer;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

public class DrawerOrderLoader extends CursorLoader {

	private int mUnReveive;
	private int mUnShip;

	public DrawerOrderLoader(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		super(context, uri, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public Cursor loadInBackground() {
		Cursor cursor = super.loadInBackground();

		mUnReveive = 0;
		mUnShip = 0;

		if (cursor != null && cursor.moveToFirst()) {
			for (; !cursor.isAfterLast(); cursor.moveToNext()) {
				mUnReveive += cursor
						.getInt(LocalCustomer.CONTENT_PROJECTION_UNREVEIVE_INDEX);
				mUnShip += cursor
						.getInt(LocalCustomer.CONTENT_PROJECTION_UNSHIP_INDEX);
			}
		}

		return cursor;
	}

	public int getUnReveiveCount() {
		return mUnReveive;
	}

	public int getUnShipCount() {
		return mUnShip;
	}

}
