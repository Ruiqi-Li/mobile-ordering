package sg.com.smartmediasoft.storeclient.ui.statistic;

import java.util.ArrayList;
import java.util.List;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.database.LocalCustomer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CustomerSelectAdapter2 extends BaseAdapter {
	private Context mContext;
	private List<LocalCustomer> mCustomers = new ArrayList<LocalCustomer>();
	private boolean mDeviceInChinese;

	OnClickListener mCheckListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag(R.id.customer_checkbox);
			processCheckChanged(false, position);
		}
	};

	OnClickListener mGroupCheckListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag(R.id.customer_checkbox);
			processCheckChanged(true, position);
		}
	};

	private void processCheckChanged(boolean groupSelect, int position) {
		LocalCustomer select = mCustomers.get(position);
		select.mChecked = !select.mChecked;

		int size = mCustomers.size();
		if (groupSelect) {
			for (int index = position + 1; index < size; index++) {
				LocalCustomer item = mCustomers.get(index);
				if ("_GROUP_".equals(item.mGroupId)) {
					break;
				}

				item.mChecked = select.mChecked;
			}
		} else {
			int groupIndex = -1;
			for (int index = position - 1; index >= 0; index--) {
				if ("_GROUP_".equals(mCustomers.get(index).mGroupId)) {
					groupIndex = index;
					break;
				}
			}
			
			if (groupIndex != -1) {
				boolean allChecked = true;
				for (int index = groupIndex + 1; index < size; index++) {
					if ("_GROUP_".equals(mCustomers.get(index).mGroupId)) {
						break;
					}
					
					LocalCustomer item = mCustomers.get(index);
					allChecked &= item.mChecked;
				}
				
				mCustomers.get(groupIndex).mChecked = allChecked;
			}
		}

		notifyDataSetChanged();
	}

	public CustomerSelectAdapter2(Context context, List<LocalCustomer> customers) {
		mContext = context;
		mDeviceInChinese = LocalUtils.isDeveceInChinese();

		for (LocalCustomer customer : customers) {
			int groupIndex = findGroupIndex(customer);
			if (groupIndex == -1) {
				LocalCustomer group = new LocalCustomer();
				group.mGroupId = "_GROUP_";
				group.mUuid = customer.mGroupId;
				group.mEnglishName = customer.mEnglishGroupName;
				group.mChineseName = customer.mChineseGroupName;
				mCustomers.add(group);
				mCustomers.add(customer);
			} else {
				mCustomers.add(groupIndex + 1, customer);
			}
		}
	}

	@Override
	public int getCount() {
		return mCustomers.size();
	}

	@Override
	public LocalCustomer getItem(int position) {
		return mCustomers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		LocalCustomer customer = getItem(position);
		if ("_GROUP_".equals(customer.mGroupId)) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int viewType = getItemViewType(position);

		if (convertView == null) {
			switch (viewType) {
			case 0:
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.customer_select_item_header, null);
				break;
			default:
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.customer_select_item, null);
				break;
			}
		}

		LocalCustomer customer = getItem(position);

		TextView textView = (TextView) convertView
				.findViewById(R.id.customer_text);
		textView.setText(mDeviceInChinese ? customer.mChineseName
				: customer.mEnglishName);

		CheckBox checkBox = (CheckBox) convertView
				.findViewById(R.id.customer_checkbox);
		checkBox.setTag(R.id.customer_checkbox, position);
		checkBox.setChecked(customer.mChecked);
		if (viewType == 0) {
			checkBox.setOnClickListener(mGroupCheckListener);
		} else {
			checkBox.setOnClickListener(mCheckListener);
		}

		return convertView;
	}

	public List<String> getCustomerSelections() {
		List<String> result = new ArrayList<String>();
		for (LocalCustomer customer : mCustomers) {
			if (!"_GROUP_".equals(customer.mGroupId) && customer.mChecked) {
				result.add(customer.mUuid);
			}
		}
		
		return result;
	}

	private int findGroupIndex(LocalCustomer customer) {
		int size = mCustomers.size();
		for (int i = 0; i < size; i++) {
			if (customer.mGroupId.equals(mCustomers.get(i).mUuid)) {
				return i;
			}
		}

		return -1;
	}
}
