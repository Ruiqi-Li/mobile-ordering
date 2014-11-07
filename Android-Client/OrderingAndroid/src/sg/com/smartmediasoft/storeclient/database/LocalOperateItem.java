package sg.com.smartmediasoft.storeclient.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.conn.SingleClientConnManager;

import sg.com.smartmediasoft.storeclient.ui.main.CustomerDrawerAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class LocalOperateItem extends ObjectBase implements
		LocalOperateItemColumns, Serializable, Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "sms_operate_item";
	public static final String ITEM_PATH = "operate_item/#";
	public static final String MIME_TYPE = "vnd.android.cursor.dir/operate_item";
	public static final String MIME_ITEM_TYPE = "vnd.android.cursor.item/operate_item";

	public static final String PATH = "operate_item";
	public static Uri CONTENT_URI = Uri.parse(ObjectBase.CONTENT_URI + "/"
			+ PATH);
	
	public static final String SINGLE_OPERATE_ITEMS_SELECTION = ORDER_ID + "=? AND " + OPERATE_UUID + "=?";

	public static final String[] CONTENT_PROJECTION = new String[] {
		LocalOperateItemColumns._ID,
		LocalOperateItemColumns.ORDER_ID,
		LocalOperateItemColumns.OPERATE_UUID,
		LocalOperateItemColumns.OPERATE_UNIT_NAME,
		LocalOperateItemColumns.OPERATE_NAME_CN,
		LocalOperateItemColumns.OPERATE_NAME_EN,
		LocalOperateItemColumns.OPERATE_IMAGE_URL,
		LocalOperateItemColumns.OPERATE_VALUE_BEFORE,
		LocalOperateItemColumns.OPERATE_VALUE_AFTER
	};

	public static final String OPERATE_SELECTION = OPERATE_UUID + "=?";

	public static final int CONTENT_PROJECTION_ID_INDEX 			= 0;
	public static final int CONTENT_PROJECTION_ORDER_ID_INDEX 		= 1;
	public static final int CONTENT_PROJECTION_OPERATE_UUID_INDEX 	= 2;
	public static final int CONTENT_PROJECTION_UNIT_NAME_INDEX 		= 3;
	public static final int CONTENT_PROJECTION_NAME_CN_INDEX 		= 4;
	public static final int CONTENT_PROJECTION_NAME_EN_INDEX 		= 5;
	public static final int CONTENT_PROJECTION_IMAGE_URL_INDEX 		= 6;
	public static final int CONTENT_PROJECTION_VALUE_BEFORE_INDEX 	= 7;
	public static final int CONTENT_PROJECTION_VALUE_AFTER_INDEX 	= 8;

	public long mOperateId;
	public String mOrderId;
	public String mUnitName;
	public String mNameCN;
	public String mNameEN;
	public String mImageUrl;
	public double mValueBefore;
	public double mValueAfter;

	public static ArrayList<LocalOperateItem> getOrderOperateItems(Context context,
			String orderId, long operateId) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(
					LocalOperateItem.CONTENT_URI,
					LocalOperateItem.CONTENT_PROJECTION,
					SINGLE_OPERATE_ITEMS_SELECTION,
					new String[] { orderId, String.valueOf(operateId) },
					OPERATE_VALUE_BEFORE);
			if (cursor != null && cursor.moveToFirst()) {
				ArrayList<LocalOperateItem> results = new ArrayList<LocalOperateItem>();
				for (; !cursor.isAfterLast(); cursor.moveToNext()) {
					results.add(new LocalOperateItem(cursor));
				}
				
				return results;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		
		return null;
	}

	public LocalOperateItem() {

	}

	public LocalOperateItem(Cursor cursor) {
		restore(cursor);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mOrderId);
		out.writeLong(mOperateId);
		out.writeString(mUnitName);
		out.writeString(mNameCN);
		out.writeString(mNameEN);
		out.writeString(mImageUrl);
		out.writeDouble(mValueBefore);
		out.writeDouble(mValueAfter);
	}

	@Override
	public void toContentValues(ContentValues values) {
		values.put(ORDER_ID, mOrderId);
		values.put(OPERATE_UUID, mOperateId);
		values.put(OPERATE_UNIT_NAME, mUnitName);
		values.put(OPERATE_NAME_CN, mNameCN);
		values.put(OPERATE_NAME_EN, mNameEN);
		values.put(OPERATE_IMAGE_URL, mImageUrl);
		values.put(OPERATE_VALUE_BEFORE, mValueBefore);
		values.put(OPERATE_VALUE_AFTER, mValueAfter);
	}

	@Override
	public void restore(Cursor cursor) {
		mOrderId = cursor.getString(CONTENT_PROJECTION_ORDER_ID_INDEX);
		mOperateId = cursor.getLong(CONTENT_PROJECTION_OPERATE_UUID_INDEX);
		mUnitName = cursor.getString(CONTENT_PROJECTION_UNIT_NAME_INDEX);
		mNameCN = cursor.getString(CONTENT_PROJECTION_NAME_CN_INDEX);
		mNameEN = cursor.getString(CONTENT_PROJECTION_NAME_EN_INDEX);
		mImageUrl = cursor.getString(CONTENT_PROJECTION_IMAGE_URL_INDEX);
		mValueBefore = cursor.getDouble(CONTENT_PROJECTION_VALUE_BEFORE_INDEX);
		mValueAfter = cursor.getDouble(CONTENT_PROJECTION_VALUE_AFTER_INDEX);
	}

	public static final Parcelable.Creator<LocalOperateItem> CREATOR = new Parcelable.Creator<LocalOperateItem>() {
		public LocalOperateItem createFromParcel(Parcel in) {
			return new LocalOperateItem(in);
		}

		public LocalOperateItem[] newArray(int size) {
			return new LocalOperateItem[size];
		}
	};

	private LocalOperateItem(Parcel in) {
		mOrderId = in.readString();
		mOperateId = in.readLong();
		mUnitName = in.readString();
		mNameCN = in.readString();
		mNameEN = in.readString();
		mImageUrl = in.readString();
		mValueBefore = in.readDouble();
		mValueAfter = in.readDouble();
	}

}
