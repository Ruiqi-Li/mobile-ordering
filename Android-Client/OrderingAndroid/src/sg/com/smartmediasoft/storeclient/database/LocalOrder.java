package sg.com.smartmediasoft.storeclient.database;

import java.io.Serializable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class LocalOrder extends ObjectBase implements LocalOrderColumns,
		Serializable, Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "sms_order";
	public static final String ITEM_PATH = "order/#";
	public static final String MIME_TYPE = "vnd.android.cursor.dir/order";
	public static final String MIME_ITEM_TYPE = "vnd.android.cursor.item/order";

	public static final String PATH = "order";
	public static final String FULL_PATH = "full_order";
	public static Uri CONTENT_URI = Uri.parse(ObjectBase.CONTENT_URI + "/"
			+ PATH);
	public static Uri FULL_CONTENT_URI = Uri.parse(ObjectBase.CONTENT_URI + "/"
			+ FULL_PATH);

	public static final String[] CONTENT_PROJECTION = new String[] {
		LocalOrderColumns._ID,
		LocalOrderColumns.UUID,
		LocalOrderColumns.CUSTOMER_ID,
		LocalOrderColumns.STATE,
		LocalOrderColumns.TARGET_DATE,
		LocalOrderColumns.CREATE_DATE,
		LocalOrderColumns.CONFIRM_DATE,
		LocalOrderColumns.DELIVER_DATE,
		LocalOrderColumns.FINISH_DATE,
		LocalOrderColumns.CHANGE_ID,
		LocalOrderColumns.HUMAN_ID,
		LocalOrderColumns.READ,
		LocalOrderColumns.HIGH_LIGHT
	};

	public static final String ITEM_SELECTION = TABLE_NAME + "." + UUID + "=?";
	public static final String HUMAN_ID_SELECTION = TABLE_NAME + "." + HUMAN_ID + "=?";

	public static final int CONTENT_PROJECTION_ID_INDEX 					= 0;
	public static final int CONTENT_PROJECTION_UUID_INDEX 					= 1;
	public static final int CONTENT_PROJECTION_CUSTOMER_ID_INDEX 			= 2;
	public static final int CONTENT_PROJECTION_STATE_INDEX 					= 3;
	public static final int CONTENT_PROJECTION_TARGET_DATE_INDEX 			= 4;
	public static final int CONTENT_PROJECTION_CREATE_DATE_INDEX 			= 5;
	public static final int CONTENT_PROJECTION_CONFIRM_DATE_INDEX 			= 6;
	public static final int CONTENT_PROJECTION_DELIVER_DATE_INDEX 			= 7;
	public static final int CONTENT_PROJECTION_FINISH_DATE_INDEX 			= 8;
	public static final int CONTENT_PROJECTION_CHANGE_ID_INDEX 				= 9;
	public static final int CONTENT_PROJECTION_HUMAN_ID_INDEX 				= 10;
	public static final int CONTENT_PROJECTION_READ_INDEX	 				= 11;
	public static final int CONTENT_PROJECTION_HIGH_LIGHT_INDEX	 			= 12;
	
	public static final int CONTENT_PROJECTION_COMBINE_CUSTOMER_CN_NAME	 	= 13;
	public static final int CONTENT_PROJECTION_COMBINE_CUSTOMER_EN_NAME	 	= 14;
	
	public String mUUID;
	public String mCustomerId;
	public String mState;
	public long mTargetDate;
	public long mCreateDate;
	public long mConfirmDate;
	public long mDeliverDate;
	public long mFinishDate;
	public long mChangeId;
	public String mHumanId;
	public int mRead;
	public int mHighLight;
	
	public String mCombineCustomerCn;
	public String mCombineCustomerEn;

	public static LocalOrder getOrderById(Context context, String orderId) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LocalOrder.CONTENT_URI,
					LocalOrder.CONTENT_PROJECTION, LocalOrder.ITEM_SELECTION,
					new String[] { orderId }, null);
			if (cursor != null && cursor.moveToFirst()) {
				return new LocalOrder(cursor);
			}
			
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	public static LocalOrder getOrderByCustomerAndChangeId(Context context,
			String customerId, long changeId) {
		Cursor cursor = null;
		try {
			cursor = context
					.getContentResolver()
					.query(LocalOrder.CONTENT_URI,
							LocalOrder.CONTENT_PROJECTION,
							LocalOrder.CUSTOMER_ID + "=? AND "
									+ LocalOrder.CHANGE_ID + "=?",
							new String[] { customerId, String.valueOf(changeId) },
							null);
			if (cursor != null && cursor.moveToFirst()) {
				return new LocalOrder(cursor);
			}
			
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	public static LocalOrder getOrderByChangeId(Context context, long changeId) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LocalOrder.CONTENT_URI,
					LocalOrder.CONTENT_PROJECTION, LocalOrder.CHANGE_ID + "=?",
					new String[] { String.valueOf(changeId) }, null);
			if (cursor != null && cursor.moveToFirst()) {
				return new LocalOrder(cursor);
			}

			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	public LocalOrder() {

	}

	public LocalOrder(Cursor cursor) {
		restore(cursor);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void toContentValues(ContentValues values) {
		values.put(UUID, mUUID);
		values.put(CUSTOMER_ID, mCustomerId);
		values.put(STATE, mState);
		values.put(TARGET_DATE, mTargetDate);
		values.put(CREATE_DATE, mCreateDate);
		values.put(CONFIRM_DATE, mConfirmDate);
		values.put(DELIVER_DATE, mDeliverDate);
		values.put(FINISH_DATE, mFinishDate);
		values.put(CHANGE_ID, mChangeId);
		values.put(HUMAN_ID, mHumanId);
		values.put(READ, mRead);
		values.put(HIGH_LIGHT, mHighLight);
	}

	@Override
	public void restore(Cursor cursor) {
		mUUID = cursor.getString(CONTENT_PROJECTION_UUID_INDEX);
		mCustomerId = cursor.getString(CONTENT_PROJECTION_CUSTOMER_ID_INDEX);
		mState = cursor.getString(CONTENT_PROJECTION_STATE_INDEX);
		mTargetDate = cursor.getLong(CONTENT_PROJECTION_TARGET_DATE_INDEX);
		mCreateDate = cursor.getLong(CONTENT_PROJECTION_CREATE_DATE_INDEX);
		mConfirmDate = cursor.getLong(CONTENT_PROJECTION_CONFIRM_DATE_INDEX);
		mDeliverDate = cursor.getLong(CONTENT_PROJECTION_DELIVER_DATE_INDEX);
		mFinishDate = cursor.getLong(CONTENT_PROJECTION_FINISH_DATE_INDEX);
		mChangeId = cursor.getLong(CONTENT_PROJECTION_CHANGE_ID_INDEX);
		mHumanId = cursor.getString(CONTENT_PROJECTION_HUMAN_ID_INDEX);
		mRead = cursor.getInt(CONTENT_PROJECTION_READ_INDEX);
		mHighLight = cursor.getInt(CONTENT_PROJECTION_HIGH_LIGHT_INDEX);
		mCombineCustomerCn = cursor.getString(CONTENT_PROJECTION_COMBINE_CUSTOMER_CN_NAME);
		mCombineCustomerEn = cursor.getString(CONTENT_PROJECTION_COMBINE_CUSTOMER_EN_NAME);
	}

	public static final Parcelable.Creator<LocalOrder> CREATOR = new Parcelable.Creator<LocalOrder>() {
		public LocalOrder createFromParcel(Parcel in) {
			return new LocalOrder(in);
		}

		public LocalOrder[] newArray(int size) {
			return new LocalOrder[size];
		}
	};
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mUUID);
		out.writeString(mCustomerId);
		out.writeString(mState);
		out.writeLong(mTargetDate);
		out.writeLong(mCreateDate);
		out.writeLong(mConfirmDate);
		out.writeLong(mDeliverDate);
		out.writeLong(mFinishDate);
		out.writeLong(mChangeId);
		out.writeString(mHumanId);
		out.writeInt(mRead);
		out.writeInt(mHighLight);
	}

	private LocalOrder(Parcel in) {
		mUUID = in.readString();
		mCustomerId = in.readString();
		mState = in.readString();
		mTargetDate = in.readLong();
		mCreateDate = in.readLong();
		mConfirmDate = in.readLong();
		mDeliverDate = in.readLong();
		mFinishDate = in.readLong();
		mChangeId = in.readLong();
		mHumanId = in.readString();
		mRead = in.readInt();
		mHighLight = in.readInt();
	}
}