package sg.com.smartmediasoft.storeclient.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public abstract class ObjectBase {

	public static final String AUTHORITY = "sg.com.smartmediasoft.storeclient.database.StoreProvider";
	public static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public static final String PARAMETER_LIMIT = "limit";

	public static final String WHERE_ID = "_id = ?";

	// Write the Content into a ContentValues container
	public abstract void toContentValues(ContentValues values);

	// Read the Content from a ContentCursor
	public abstract void restore(Cursor cursor);
}
