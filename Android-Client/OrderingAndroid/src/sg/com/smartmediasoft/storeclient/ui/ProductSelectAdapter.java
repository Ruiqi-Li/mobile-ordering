package sg.com.smartmediasoft.storeclient.ui;

import sg.com.smartmediasoft.storeclient.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProductSelectAdapter extends IndexerListAdapter implements
		PinnedHeaderListView.PinnedHeaderAdapter {
	private LayoutInflater mInflater;
	private ProductSelectAdapterCalback mSelectDataCalback;

	public static interface ProductSelectAdapterCalback {
		public View newProductItemView(IndexerListAdapter adapter,
				Context context, Cursor cursor, ViewGroup parent);

		public void bindProductItemView(IndexerListAdapter adapter, View view,
				Context context, Cursor cursor);
	}

	public ProductSelectAdapter(Context context, Cursor c,
			ProductSelectAdapterCalback callback) {
		super(context, c, false);

		setSectionHeaderDisplayEnabled(true);
		mSelectDataCalback = callback;
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	protected View createPinnedSectionHeaderView(Context context,
			ViewGroup parent) {
		return mInflater.inflate(R.layout.pinned_list_header, null);
	}

	@Override
	protected void setPinnedSectionTitle(View pinnedHeaderView, String title) {
		TextView textView = (TextView) pinnedHeaderView
				.findViewById(R.id.pinned_header_text);
		textView.setText(title);
	}

	@Override
	protected void setPinnedHeaderContactsCount(View header) {

	}

	@Override
	public int getScrollPositionForHeader(int viewIndex) {
		return 0;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mSelectDataCalback.newProductItemView(this, context, cursor,
				parent);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		mSelectDataCalback.bindProductItemView(this, view, context, cursor);
	}

}
