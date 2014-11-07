package sg.com.smartmediasoft.storeclient.ui.orderdetail;

import java.util.ArrayList;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.database.LocalOperate;
import sg.com.smartmediasoft.storeclient.database.LocalOperateItem;
import sg.com.smartmediasoft.storeclient.database.LocalOrderItem;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

public class OrderDetailLoader extends AsyncTaskLoader<Void> {
	private ArrayList<LocalOrderItem> mOrderItems;
	private ArrayList<LocalOperate> mOperates;
	private String mOrderId;

	public OrderDetailLoader(Context context, String orderId) {
		super(context);

		mOrderId = orderId;
	}

	public ArrayList<LocalOrderItem> getOrderItems() {
		return mOrderItems;
	}

	public ArrayList<LocalOperate> getOperates() {
		return mOperates;
	}

	@Override
	public Void loadInBackground() {
		mOperates = new ArrayList<LocalOperate>();
		mOrderItems = new ArrayList<LocalOrderItem>();

		Cursor cursor = null;
		try {
			cursor = getContext().getContentResolver().query(
					LocalOperate.CONTENT_URI, LocalOperate.CONTENT_PROJECTION,
					LocalOperate.ORDER_SELECTION, new String[] { mOrderId },
					LocalOperate.DATE + " DESC");
			if (cursor == null || !cursor.moveToFirst()) {
				return null;
			}

			for (; !cursor.isAfterLast(); cursor.moveToNext()) {
				LocalOperate operate = new LocalOperate(cursor);
				operate.mItems = LocalOperateItem.getOrderOperateItems(
						getContext(), mOrderId, operate.mUUID);
				mOperates.add(operate);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		try {
			cursor = getContext().getContentResolver().query(
					LocalOrderItem.CONTENT_URI,
					LocalOrderItem.CONTENT_PROJECTION,
					LocalOrderItem.ORDER_SELECTION,
					new String[] { mOrderId },
					LocalUtils.isDeveceInChinese() ? LocalOrderItem.NAME_CN
							: LocalOrderItem.NAME_EN + " DESC");
			if (cursor == null || !cursor.moveToFirst()) {
				return null;
			}

			for (; !cursor.isAfterLast(); cursor.moveToNext()) {
				LocalOrderItem orderItem = new LocalOrderItem(cursor);
				mOrderItems.add(orderItem);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return null;
	}
}
