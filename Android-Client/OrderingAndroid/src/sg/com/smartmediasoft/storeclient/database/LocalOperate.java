package sg.com.smartmediasoft.storeclient.database;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class LocalOperate extends ObjectBase implements LocalOperateColumns,
		Serializable, Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "sms_operate";
	public static final String ITEM_PATH = "operate/#";
	public static final String MIME_TYPE = "vnd.android.cursor.dir/operate";
	public static final String MIME_ITEM_TYPE = "vnd.android.cursor.item/operate";

	public static final String PATH = "operate";
	public static Uri CONTENT_URI = Uri.parse(ObjectBase.CONTENT_URI + "/"
			+ PATH);

	public static final String[] CONTENT_PROJECTION = new String[] {
		LocalOperateColumns._ID,
		LocalOperateColumns.UUID,
		LocalOperateColumns.ORDER_ID,
		LocalOperateColumns.OPERATOR_NAME,
		LocalOperateColumns.OPERATE_ACTION,
		LocalOperateColumns.DATE,
		LocalOperateColumns.DESCRIPTION,
		LocalOperateColumns.TARGET_TIME_FROM,
		LocalOperateColumns.TARGET_TIME_TO
	};

	public static final String ORDER_SELECTION = ORDER_ID + "=?";

	public static final int CONTENT_PROJECTION_ID_INDEX 			= 0;
	public static final int CONTENT_PROJECTION_UUID_INDEX 			= 1;
	public static final int CONTENT_PROJECTION_ORDER_UUID_INDEX 	= 2;
	public static final int CONTENT_PROJECTION_OPERATOR_NAME_INDEX 	= 3;
	public static final int CONTENT_PROJECTION_OPERATE_ACTION_INDEX = 4;
	public static final int CONTENT_PROJECTION_DATE_INDEX 			= 5;
	public static final int CONTENT_PROJECTION_DESCRIPTION_INDEX 	= 6;
	public static final int CONTENT_PROJECTION_TIME_FROM_INDEX 		= 7;
	public static final int CONTENT_PROJECTION_TIME_TO_INDEX 		= 8;

	public long mUUID;
	public String mOrderUUID;
	public String mOperatorName;
	public String mOperateAction;
	public String mDescription;
	public long mDate;
	public long mTargetTimeFrom;
	public long mTargetTimeTo;

	public ArrayList<LocalOperateItem> mItems = new ArrayList<LocalOperateItem>();

	public LocalOperate() {

	}

	public LocalOperate(Cursor cursor) {
		restore(cursor);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(mUUID);
		out.writeString(mOrderUUID);
		out.writeString(mOperatorName);
		out.writeString(mOperateAction);
		out.writeString(mDescription);
		out.writeLong(mDate);
		out.writeLong(mTargetTimeFrom);
		out.writeLong(mTargetTimeTo);
		out.writeTypedList(mItems);
	}

	@Override
	public void toContentValues(ContentValues values) {
		values.put(UUID, mUUID);
		values.put(ORDER_ID, mOrderUUID);
		values.put(OPERATOR_NAME, mOperatorName);
		values.put(OPERATE_ACTION, mOperateAction);
		values.put(DESCRIPTION, mDescription);
		values.put(DATE, mDate);
		values.put(TARGET_TIME_FROM, mTargetTimeFrom);
		values.put(TARGET_TIME_TO, mTargetTimeTo);
	}

	@Override
	public void restore(Cursor cursor) {
		mUUID = cursor.getLong(CONTENT_PROJECTION_UUID_INDEX);
		mOrderUUID = cursor.getString(CONTENT_PROJECTION_ORDER_UUID_INDEX);
		mOperatorName = cursor.getString(CONTENT_PROJECTION_OPERATOR_NAME_INDEX);
		mOperateAction = cursor.getString(CONTENT_PROJECTION_OPERATE_ACTION_INDEX);
		mDescription = cursor.getString(CONTENT_PROJECTION_DESCRIPTION_INDEX);
		mDate = cursor.getLong(CONTENT_PROJECTION_DATE_INDEX);
		mTargetTimeFrom = cursor.getLong(CONTENT_PROJECTION_TIME_FROM_INDEX);
		mTargetTimeTo = cursor.getLong(CONTENT_PROJECTION_TIME_TO_INDEX);
	}

	public static final Parcelable.Creator<LocalOperate> CREATOR = new Parcelable.Creator<LocalOperate>() {
		public LocalOperate createFromParcel(Parcel in) {
			return new LocalOperate(in);
		}

		public LocalOperate[] newArray(int size) {
			return new LocalOperate[size];
		}
	};

	private LocalOperate(Parcel in) {
		mUUID = in.readLong();
		mOrderUUID = in.readString();
		mOperatorName = in.readString();
		mOperateAction = in.readString();
		mDescription = in.readString();
		mDate = in.readLong();
		mTargetTimeFrom = in.readLong();
		mTargetTimeTo = in.readLong();
		in.readTypedList(mItems, LocalOperateItem.CREATOR);
	}
}
