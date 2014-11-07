package sg.com.smartmediasoft.storeclient.ui.main;

import java.util.List;

import sg.com.smartmediasoft.storeclient.R;
import sg.com.smartmediasoft.storeclient.database.LocalOrderTemplate;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewOrderSelectAdapter extends BaseAdapter {
	private Context mContext;
	private List<LocalOrderTemplate> mTamplates;

	public NewOrderSelectAdapter(Context context,
			List<LocalOrderTemplate> tamplates) {
		mContext = context;
		mTamplates = tamplates;
	}

	@Override
	public int getCount() {
		return mTamplates == null ? 0 : mTamplates.size();
	}

	@Override
	public LocalOrderTemplate getItem(int position) {
		return mTamplates.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					android.R.layout.simple_list_item_1, null);
		}

		TextView textView = (TextView) convertView
				.findViewById(android.R.id.text1);
		if (position > 0) {
			textView.setText(mContext.getString(R.string.text_template_prefix)
					+ getItem(position).mName);
		} else {
			textView.setText(getItem(position).mName);	
		}

		return convertView;
	}
}