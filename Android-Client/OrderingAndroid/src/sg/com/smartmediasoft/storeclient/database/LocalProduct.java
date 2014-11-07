package sg.com.smartmediasoft.storeclient.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class LocalProduct extends ObjectBase implements LocalProductColumns,
		Serializable, Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "sms_product";
	public static final String ITEM_PATH = "product/#";
	public static final String MIME_TYPE = "vnd.android.cursor.dir/product";
	public static final String MIME_ITEM_TYPE = "vnd.android.cursor.item/product";

	public static final String PATH = "product";
	public static Uri CONTENT_URI = Uri.parse(ObjectBase.CONTENT_URI + "/"
			+ PATH);

	public static final String[] CONTENT_PROJECTION = new String[] {
		LocalProductColumns._ID,
		LocalProductColumns.UUID,
		LocalProductColumns.UNIT_NAME,
		LocalProductColumns.DECIMAL_UNIT,
		LocalProductColumns.CHINESE_NAME,
		LocalProductColumns.ENGLISH_NAME,
		LocalProductColumns.CHINESE_SORT_NAME,
		LocalProductColumns.ENGLISH_SORT_NAME,
		LocalProductColumns.IMAGE_URL,
		LocalProductColumns.TYPE,
		LocalProductColumns.CHANGE_ID,
		LocalProductColumns.DELETED
	};

	public static final String ITEM_SELECTION = UUID + "=?";

	public static final int CONTENT_PROJECTION_ID_INDEX 				= 0;
	public static final int CONTENT_PROJECTION_UUID_INDEX 				= 1;
	public static final int CONTENT_PROJECTION_UNIT_NAME_INDEX 			= 2;
	public static final int CONTENT_PROJECTION_DECIMAL_UNIT_INDEX 		= 3;
	public static final int CONTENT_PROJECTION_CHINESE_NAME_INDEX 		= 4;
	public static final int CONTENT_PROJECTION_ENGLISH_NAME_INDEX 		= 5;
	public static final int CONTENT_PROJECTION_CHINESE_SORT_NAME_INDEX 	= 6;
	public static final int CONTENT_PROJECTION_ENGLISH_SORT_NAME_INDEX 	= 7;
	public static final int CONTENT_PROJECTION_IMAGE_URL_INDEX 			= 8;
	public static final int CONTENT_PROJECTION_TYPE_INDEX 				= 9;
	public static final int CONTENT_PROJECTION_CHANGE_ID_INDEX 			= 10;
	public static final int CONTENT_PROJECTION_DELETED_INDEX            = 11;

	public String mUUID;
	public String mUnitName;
	public int mDecimalUnit;
	public String mChineseName;
	public String mEnglishName;
	public String mChineseSortName;
	public String mEnglishSortName;
	public String mImageUrl;
	public String mType;
	public long mChangeId;
	public int mDeleted;

    public double mAmount;

	public static List<LocalProduct> getProductsByIds(Context context,
			String productIds) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(
					LocalProduct.CONTENT_URI, LocalProduct.CONTENT_PROJECTION,
					LocalProduct.UUID + " IN (?)", new String[] { productIds },
					null);
			if (cursor != null && cursor.moveToFirst()) {
				List<LocalProduct> products = new ArrayList<LocalProduct>();
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					products.add(new LocalProduct(cursor));
				}

				return products;
			}

			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	public LocalProduct() {

	}

	public LocalProduct(Cursor cursor) {
		restore(cursor);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void toContentValues(ContentValues values) {
		values.put(UUID, mUUID);
		values.put(UNIT_NAME, mUnitName);
		values.put(DECIMAL_UNIT, mDecimalUnit);
		values.put(CHINESE_NAME, mChineseName);
		values.put(ENGLISH_NAME, mEnglishName);
		values.put(CHINESE_SORT_NAME, mChineseSortName);
		values.put(ENGLISH_SORT_NAME, mEnglishSortName);
		values.put(IMAGE_URL, mImageUrl);
		values.put(TYPE, mType);
		values.put(CHANGE_ID, mChangeId);
		values.put(DELETED, mDeleted);
	}

	@Override
	public void restore(Cursor cursor) {
		mUUID = cursor.getString(CONTENT_PROJECTION_UUID_INDEX);
		mUnitName = cursor.getString(CONTENT_PROJECTION_UNIT_NAME_INDEX);
		mDecimalUnit = cursor.getInt(CONTENT_PROJECTION_DECIMAL_UNIT_INDEX);
		mChineseName = cursor.getString(CONTENT_PROJECTION_CHINESE_NAME_INDEX);
		mEnglishName = cursor.getString(CONTENT_PROJECTION_ENGLISH_NAME_INDEX);
		mChineseSortName = cursor.getString(CONTENT_PROJECTION_CHINESE_SORT_NAME_INDEX);
		mEnglishSortName = cursor.getString(CONTENT_PROJECTION_ENGLISH_SORT_NAME_INDEX);
		mImageUrl = cursor.getString(CONTENT_PROJECTION_IMAGE_URL_INDEX);
		mType = cursor.getString(CONTENT_PROJECTION_TYPE_INDEX);
		mChangeId = cursor.getLong(CONTENT_PROJECTION_CHANGE_ID_INDEX);
		mDeleted = cursor.getInt(CONTENT_PROJECTION_DELETED_INDEX);
	}

	public static final Parcelable.Creator<LocalProduct> CREATOR = new Parcelable.Creator<LocalProduct>() {
		public LocalProduct createFromParcel(Parcel in) {
			return new LocalProduct(in);
		}

		public LocalProduct[] newArray(int size) {
			return new LocalProduct[size];
		}
	};
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mUUID);
		out.writeString(mUnitName);
		out.writeInt(mDecimalUnit);
		out.writeString(mChineseName);
		out.writeString(mEnglishName);
		out.writeString(mChineseSortName);
		out.writeString(mEnglishSortName);
		out.writeString(mImageUrl);
		out.writeDouble(mAmount);
		out.writeString(mType);
		out.writeLong(mChangeId);
		out.writeInt(mDeleted);
	}

	private LocalProduct(Parcel in) {
		mUUID = in.readString();
		mUnitName = in.readString();
		mDecimalUnit = in.readInt();
		mChineseName = in.readString();
		mEnglishName = in.readString();
		mChineseSortName = in.readString();
		mEnglishSortName = in.readString();
		mImageUrl = in.readString();
		mAmount = in.readDouble();
		mType = in.readString();
		mChangeId = in.readLong();
		mDeleted = in.readInt();
	}
}
