package sg.com.smartmediasoft.storeclient.database;

import java.io.Serializable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class LocalCustomer extends ObjectBase implements LocalCustomerColumns,
		Serializable, Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "sms_customer";
	public static final String ITEM_PATH = "customer/#";
	public static final String MIME_TYPE = "vnd.android.cursor.dir/customer";
	public static final String MIME_ITEM_TYPE = "vnd.android.cursor.item/customer";

	public static final String PATH = "customer";
	public static Uri CONTENT_URI = Uri.parse(ObjectBase.CONTENT_URI + "/"
			+ PATH);

	public static final String[] CONTENT_PROJECTION = new String[] {
		LocalCustomerColumns._ID,
		LocalCustomerColumns.UUID,
		LocalCustomerColumns.GROUP_ID,
		LocalCustomerColumns.CHINESE_GROUP_NAME,
		LocalCustomerColumns.ENGLISH_GROUP_NAME,
		LocalCustomerColumns.CHINESE_NAME,
		LocalCustomerColumns.ENGLISH_NAME,
		LocalCustomerColumns.CHANGE_ID,
		LocalCustomerColumns.STORE_CHANGE_ID,
		LocalCustomerColumns.STORE_STORAGE_MAP,
		LocalCustomerColumns.STORE_LAST_CHECK_TIME
	};

	public static final String ITEM_SELECTION = UUID + "=?";

	public static final int CONTENT_PROJECTION_ID_INDEX 					= 0;
	public static final int CONTENT_PROJECTION_UUID_INDEX 					= 1;
	public static final int CONTENT_PROJECTION_GROUP_ID_INDEX 				= 2;
	public static final int CONTENT_PROJECTION_CHINESE_GROUP_NAME_INDEX 	= 3;
	public static final int CONTENT_PROJECTION_ENGLISH_GROUP_NAME_INDEX 	= 4;
	public static final int CONTENT_PROJECTION_CHINESE_NAME_INDEX 			= 5;
	public static final int CONTENT_PROJECTION_ENGLISH_NAME_INDEX 			= 6;
	public static final int CONTENT_PROJECTION_CHANGE_ID_INDEX 				= 7;
	public static final int CONTENT_PROJECTION_STORE_CHANGE_ID_INDEX 		= 8;
	public static final int CONTENT_PROJECTION_STORE_STORAGE_MAP_INDEX 		= 9;
	public static final int CONTENT_PROJECTION_STORE_LAST_CHECK_TIME_INDEX 	= 10;
	
	public static final int CONTENT_PROJECTION_UNREVEIVE_INDEX 				= 11;
	public static final int CONTENT_PROJECTION_UNSHIP_INDEX					= 12;

	public static final int COMBINE_COLUMN_COUNT 							= 13;

	public static LocalCustomer getCustomerById(Context context,
			String customerId) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(
					LocalCustomer.CONTENT_URI,
					LocalCustomer.CONTENT_PROJECTION,
					LocalCustomer.ITEM_SELECTION, new String[] { customerId },
					null);
			if (cursor != null && cursor.moveToFirst()) {
				return new LocalCustomer(cursor);
			}

			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	public String mUuid;
	public String mGroupId;
	public String mEnglishGroupName;
	public String mChineseGroupName;
	public String mChineseName;
	public String mEnglishName;
	public long mChangeId;
	public long mStoreChangeId;
	public String mStoreStorageMap;
	public long mStoreLastCheckTime;

	public int mUnconfirmCount;
	public int mUnShipCount;
	
	public boolean mChecked = false;

	public LocalCustomer() {

	}

	public LocalCustomer(Cursor cursor) {
		restore(cursor);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mUuid);
		out.writeString(mGroupId);
		out.writeString(mChineseGroupName);
		out.writeString(mEnglishGroupName);
		out.writeString(mChineseName);
		out.writeString(mEnglishName);
		out.writeLong(mChangeId);
		out.writeLong(mStoreChangeId);
		out.writeString(mStoreStorageMap);
		out.writeLong(mStoreLastCheckTime);
	}

	@Override
	public void toContentValues(ContentValues values) {
		values.put(UUID, mUuid);
		values.put(GROUP_ID, mGroupId);
		values.put(CHINESE_GROUP_NAME, mChineseGroupName);
		values.put(ENGLISH_GROUP_NAME, mEnglishGroupName);
		values.put(CHINESE_NAME, mChineseName);
		values.put(ENGLISH_NAME, mEnglishName);
		values.put(CHANGE_ID, mChangeId);
		values.put(STORE_CHANGE_ID, mStoreChangeId);
		values.put(STORE_STORAGE_MAP, mStoreStorageMap);
		values.put(STORE_LAST_CHECK_TIME, mStoreLastCheckTime);
	}

	@Override
	public void restore(Cursor cursor) {
		mUuid = cursor.getString(CONTENT_PROJECTION_UUID_INDEX);
		mGroupId = cursor.getString(CONTENT_PROJECTION_GROUP_ID_INDEX);
		mChineseGroupName = cursor.getString(CONTENT_PROJECTION_CHINESE_GROUP_NAME_INDEX);
		mEnglishGroupName = cursor.getString(CONTENT_PROJECTION_ENGLISH_GROUP_NAME_INDEX);
		mChineseName = cursor.getString(CONTENT_PROJECTION_CHINESE_NAME_INDEX);
		mEnglishName = cursor.getString(CONTENT_PROJECTION_ENGLISH_NAME_INDEX);
		mChangeId = cursor.getLong(CONTENT_PROJECTION_CHANGE_ID_INDEX);
		mStoreChangeId = cursor
				.getLong(CONTENT_PROJECTION_STORE_CHANGE_ID_INDEX);
		mStoreStorageMap = cursor
				.getString(CONTENT_PROJECTION_STORE_STORAGE_MAP_INDEX);
		mStoreLastCheckTime = cursor.getLong(CONTENT_PROJECTION_STORE_LAST_CHECK_TIME_INDEX);

		if (cursor.getColumnCount() == COMBINE_COLUMN_COUNT) {
			mUnconfirmCount = cursor.getInt(CONTENT_PROJECTION_UNREVEIVE_INDEX);
			mUnShipCount = cursor.getInt(CONTENT_PROJECTION_UNSHIP_INDEX);
		}
	}

	public static final Parcelable.Creator<LocalCustomer> CREATOR = new Parcelable.Creator<LocalCustomer>() {
		public LocalCustomer createFromParcel(Parcel in) {
			return new LocalCustomer(in);
		}

		public LocalCustomer[] newArray(int size) {
			return new LocalCustomer[size];
		}
	};

	private LocalCustomer(Parcel in) {
		mUuid = in.readString();
		mGroupId = in.readString();
		mChineseGroupName = in.readString();
		mEnglishGroupName = in.readString();
		mChineseName = in.readString();
		mEnglishName = in.readString();
		mChangeId = in.readLong();
		mStoreChangeId = in.readLong();
		mStoreStorageMap = in.readString();
		mStoreLastCheckTime = in.readLong();
	}
}
