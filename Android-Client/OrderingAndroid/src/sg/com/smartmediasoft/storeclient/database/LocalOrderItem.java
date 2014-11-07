package sg.com.smartmediasoft.storeclient.database;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class LocalOrderItem extends ObjectBase implements LocalOrderItemColumns,
		Serializable, Parcelable {
	/**
* 
*/
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "sms_order_item";
	public static final String ITEM_PATH = "order_item/#";
	public static final String MIME_TYPE = "vnd.android.cursor.dir/order_item";
	public static final String MIME_ITEM_TYPE = "vnd.android.cursor.item/order_item";

	public static final String PATH = "order_item";
	public static Uri CONTENT_URI = Uri.parse(ObjectBase.CONTENT_URI + "/"
			+ PATH);

	public static final String[] CONTENT_PROJECTION = new String[] {
			LocalOrderItemColumns._ID,
			LocalOrderItemColumns.ORDER_ID,
			LocalOrderItemColumns.PRODUCT_ID,
			LocalOrderItemColumns.AMOUNT,
			LocalOrderItemColumns.UNIT_NAME,
			LocalOrderItemColumns.DESCIMAL_UNIT,
			LocalOrderItemColumns.NAME_CN,
			LocalOrderItemColumns.NAME_EN,
			LocalOrderItemColumns.IMAGE_URL };

	public static final String ORDER_SELECTION = ORDER_ID + "=?";

	public static final int CONTENT_PROJECTION_ID_INDEX 		= 0;
	public static final int CONTENT_PROJECTION_ORDER_ID_INDEX 	= 1;
	public static final int CONTENT_PROJECTION_PRODUCT_ID_INDEX = 2;
	public static final int CONTENT_PROJECTION_AMOUNT_INDEX 	= 3;
	public static final int CONTENT_PROJECTION_UNIT_NAME_INDEX 	= 4;
	public static final int CONTENT_PROJECTION_DESCIMAL_UNIT_INDEX = 5;
	public static final int CONTENT_PROJECTION_NAME_CN_INDEX 	= 6;
	public static final int CONTENT_PROJECTION_NAME_EN_INDEX 	= 7;
	public static final int CONTENT_PROJECTION_IMAGE_URL_INDEX 	= 8;

	public String mOrderId;
	public String mProductId;
	public double mAmount;
	public String mUnitName;
	public int mDescimalUnit;
	public String mNameCN;
	public String mNameEN;
	public String mImageUrl;

	public LocalOrderItem() {

	}

	public LocalOrderItem(Cursor cursor) {
		restore(cursor);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mOrderId);
		out.writeString(mProductId);
		out.writeDouble(mAmount);
		out.writeString(mUnitName);
		out.writeInt(mDescimalUnit);
		out.writeString(mNameCN);
		out.writeString(mNameEN);
		out.writeString(mImageUrl);
	}

	@Override
	public void toContentValues(ContentValues values) {
		values.put(ORDER_ID, mOrderId);
		values.put(PRODUCT_ID, mProductId);
		values.put(AMOUNT, mAmount);
		values.put(UNIT_NAME, mUnitName);
		values.put(DESCIMAL_UNIT, mDescimalUnit);
		values.put(NAME_CN, mNameCN);
		values.put(NAME_EN, mNameEN);
		values.put(IMAGE_URL, mImageUrl);
	}

	@Override
	public void restore(Cursor cursor) {
		mOrderId = cursor.getString(CONTENT_PROJECTION_ORDER_ID_INDEX);
		mProductId = cursor.getString(CONTENT_PROJECTION_PRODUCT_ID_INDEX);
		mAmount = cursor
				.getDouble(CONTENT_PROJECTION_AMOUNT_INDEX);
		mUnitName = cursor
				.getString(CONTENT_PROJECTION_UNIT_NAME_INDEX);
		mDescimalUnit = cursor.getInt(CONTENT_PROJECTION_DESCIMAL_UNIT_INDEX);
		mNameCN = cursor.getString(CONTENT_PROJECTION_NAME_CN_INDEX);
		mNameEN = cursor.getString(CONTENT_PROJECTION_NAME_EN_INDEX);
		mImageUrl = cursor.getString(CONTENT_PROJECTION_IMAGE_URL_INDEX);
	}

	public static final Parcelable.Creator<LocalOrderItem> CREATOR = new Parcelable.Creator<LocalOrderItem>() {
		public LocalOrderItem createFromParcel(Parcel in) {
			return new LocalOrderItem(in);
		}

		public LocalOrderItem[] newArray(int size) {
			return new LocalOrderItem[size];
		}
	};

	private LocalOrderItem(Parcel in) {
		mOrderId = in.readString();
		mProductId = in.readString();
		mAmount = in.readDouble();
		mUnitName = in.readString();
		mDescimalUnit = in.readInt();
		mNameCN = in.readString();
		mNameEN = in.readString();
		mImageUrl = in.readString();
	}
}