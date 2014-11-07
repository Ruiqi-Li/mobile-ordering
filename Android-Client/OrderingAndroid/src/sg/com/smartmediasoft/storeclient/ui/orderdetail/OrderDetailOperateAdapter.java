package sg.com.smartmediasoft.storeclient.ui.orderdetail;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.database.LocalOperate;
import sg.com.smartmediasoft.storeclient.database.LocalOperateItem;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class OrderDetailOperateAdapter extends BaseAdapter {

	private Context mContext;
	private List<LocalOperate> mOperates;
	private StringBuilder mNameBuilder;
	private StringBuilder mDescriptionBuilder;

	private DateFormat mDateTimeFormat;
	private DateFormat mDateFormat = LocalUtils.getDateFormater();

	private String mSupplierPrefix;

	public OrderDetailOperateAdapter(Context context,
			List<LocalOperate> operates) {
		mContext = context;
		mOperates = operates;
		mDateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.SHORT);
		mNameBuilder = new StringBuilder();
		mDescriptionBuilder = new StringBuilder();
		mSupplierPrefix = context
				.getString(R.string.detail_operate_list_item_supplier_prefix);
	}

	@Override
	public int getCount() {
		return mOperates.size();
	}

	@Override
	public LocalOperate getItem(int position) {
		return mOperates.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.detail_operate_list_item, null);
		}

		LocalOperate operate = getItem(position);
		String leftTitle = mDateTimeFormat.format(new Date(operate.mDate));
		String rightTitle = LocalUtils.operateActionToText(mContext,
				operate.mOperateAction);

		mNameBuilder.setLength(0);
		mDescriptionBuilder.setLength(0);

		mNameBuilder.append(mSupplierPrefix).append("\n");
		mDescriptionBuilder.append(operate.mOperatorName).append("\n");

		if (operate.mTargetTimeFrom > 0 || operate.mTargetTimeTo > 0) {
			mNameBuilder.append(mContext
					.getString(R.string.detail_list_item_tart_time));

			if (LocalUtils.OPERATE_ACTION_CUSTOMER_AMEND
					.equals(operate.mOperateAction)
					|| LocalUtils.OPERATE_ACTION_SUPPLIER_AMEND
							.equals(operate.mOperateAction)
					|| LocalUtils.OPERATE_ACTION_SYSTEM_AMEND
							.equals(operate.mOperateAction)) {
				mDescriptionBuilder
						.append(mDateFormat.format(new Date(
								(long) operate.mTargetTimeFrom)))
						.append("\n")
						.append(" ---> ")
						.append(mDateFormat.format(new Date(
								(long) operate.mTargetTimeTo)));
				mNameBuilder.append("\n");
			} else {
				mDescriptionBuilder.append(mDateFormat.format(new Date(
						(long) operate.mTargetTimeTo)));
			}

			mNameBuilder.append("\n");
			mDescriptionBuilder.append("\n");
		}

		int operateItemSize = operate.mItems == null ? 0 : operate.mItems
				.size();
		for (int i = 0; i < operateItemSize; i++) {
			LocalOperateItem item = operate.mItems.get(i);

			mNameBuilder.append(LocalUtils.isDeveceInChinese() ? item.mNameCN
					: item.mNameEN);

			if (LocalUtils.OPERATE_ACTION_CUSTOMER_AMEND
					.equals(operate.mOperateAction)
					|| LocalUtils.OPERATE_ACTION_SUPPLIER_AMEND
							.equals(operate.mOperateAction)
					|| LocalUtils.OPERATE_ACTION_SYSTEM_AMEND
							.equals(operate.mOperateAction)) {
				mDescriptionBuilder.append(item.mValueBefore)
						.append(item.mUnitName).append(" ---> ")
						.append(item.mValueAfter).append(item.mUnitName);
			} else {
				mDescriptionBuilder.append(item.mValueAfter).append(
						item.mUnitName);
			}

			if (i < operateItemSize - 1) {
				mNameBuilder.append("\n");
				mDescriptionBuilder.append("\n");
			}
		}

		OrderDetailOperateListItem itemView = (OrderDetailOperateListItem) convertView;
		itemView.bindContent(
				leftTitle,
				rightTitle,
				mNameBuilder.toString(),
				mDescriptionBuilder.toString(),
				TextUtils.isEmpty(operate.mDescription) ? null : mContext
						.getString(R.string.text_operate_description_prefix,
								operate.mDescription));
		return itemView;
	}
}
