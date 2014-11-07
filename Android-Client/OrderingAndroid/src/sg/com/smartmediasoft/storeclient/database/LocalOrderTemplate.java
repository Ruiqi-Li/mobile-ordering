package sg.com.smartmediasoft.storeclient.database;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class LocalOrderTemplate extends ObjectBase implements
	LocalOrderTemplateColumns, Serializable, Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "order_template";
	public static final String ITEM_PATH = "order_template/#";
	public static final String MIME_TYPE = "vnd.android.cursor.dir/order_template";
	public static final String MIME_ITEM_TYPE = "vnd.android.cursor.item/order_template";

	public static final String PATH = "order_template";
	public static Uri CONTENT_URI = Uri.parse(ObjectBase.CONTENT_URI + "/"
			+ PATH);
	
	public static final String ITEM_SELECTION = NAME + "=?";

	public static final String[] CONTENT_PROJECTION = new String[] {
		LocalOrderTemplateColumns._ID,
		LocalOrderTemplateColumns.NAME,
		LocalOrderTemplateColumns.SUMMERY_ENGLISH,
		LocalOrderTemplateColumns.SUMMERY_CHINESE,
		LocalOrderTemplateColumns.PRODUCTS_MAP
	};

	public static final int CONTENT_PROJECTION_ID_INDEX 				= 0;
	public static final int CONTENT_PROJECTION_NAME_INDEX 				= 1;
	public static final int CONTENT_PROJECTION_SUMMERY_ENGLISH_INDEX 	= 2;
	public static final int CONTENT_PROJECTION_SUMMERY_CHINESE_INDEX 	= 3;
	public static final int CONTENT_PROJECTION_PRODUCTS_MAP_INDEX 		= 4;

	public long mId;
	public String mName;
	public String mSummeryEnglish;
	public String mSummeryChinese;
	public String mProductsMap;

	public LocalOrderTemplate() {

	}

	public LocalOrderTemplate(Cursor cursor) {
		restore(cursor);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(mId);
		out.writeString(mName);
		out.writeString(mSummeryEnglish);
		out.writeString(mSummeryChinese);
		out.writeString(mProductsMap);
	}

	@Override
	public void toContentValues(ContentValues values) {
		values.put(NAME, mName);
		values.put(SUMMERY_ENGLISH, mSummeryEnglish);
		values.put(SUMMERY_CHINESE, mSummeryChinese);
		values.put(PRODUCTS_MAP, mProductsMap);
	}

	@Override
	public void restore(Cursor cursor) {
		mId = cursor.getLong(CONTENT_PROJECTION_ID_INDEX);
		mName = cursor.getString(CONTENT_PROJECTION_NAME_INDEX);
		mSummeryEnglish = cursor.getString(CONTENT_PROJECTION_SUMMERY_ENGLISH_INDEX);
		mSummeryChinese = cursor.getString(CONTENT_PROJECTION_SUMMERY_CHINESE_INDEX);
		mProductsMap = cursor.getString(CONTENT_PROJECTION_PRODUCTS_MAP_INDEX);
	}

	public static final Parcelable.Creator<LocalOrderTemplate> CREATOR = new Parcelable.Creator<LocalOrderTemplate>() {
		public LocalOrderTemplate createFromParcel(Parcel in) {
			return new LocalOrderTemplate(in);
		}

		public LocalOrderTemplate[] newArray(int size) {
			return new LocalOrderTemplate[size];
		}
	};

	private LocalOrderTemplate(Parcel in) {
		mId = in.readLong();
		mName = in.readString();
		mSummeryEnglish = in.readString();
		mSummeryChinese = in.readString();
		mProductsMap = in.readString();
	}

}
