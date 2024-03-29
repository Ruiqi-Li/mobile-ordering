/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sg.com.smartmediasoft.storeclient.ui;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 * A list adapter that supports section indexer and a pinned header.
 */
public abstract class IndexerListAdapter extends CursorAdapter implements SectionIndexer {

	protected Context mContext;
    private SectionIndexer mIndexer;
    private boolean mSectionHeaderDisplayEnabled;
    private View mHeader;

    /**
     * An item view is displayed differently depending on whether it is placed
     * at the beginning, middle or end of a section. It also needs to know the
     * section header when it is at the beginning of a section. This object
     * captures all this configuration.
     */
    public static final class Placement {
        private int position = ListView.INVALID_POSITION;
        public boolean firstInSection;
        public boolean lastInSection;
        public String sectionHeader;

        public void invalidate() {
            position = ListView.INVALID_POSITION;
        }
    }

    private Placement mPlacementCache = new Placement();

	public IndexerListAdapter(Context context, Cursor c, boolean autoQuery) {
		super(context, c, autoQuery);
		mContext = context;
	}

    /**
     * Creates a section header view that will be pinned at the top of the list
     * as the user scrolls.
     */
    protected abstract View createPinnedSectionHeaderView(Context context, ViewGroup parent);

    /**
     * Sets the title in the pinned header as the user scrolls.
     */
    protected abstract void setPinnedSectionTitle(View pinnedHeaderView, String title);

    /**
     * Sets the contacts count in the pinned header.
     */
    protected abstract void setPinnedHeaderContactsCount(View header);

    public boolean isSectionHeaderDisplayEnabled() {
        return mSectionHeaderDisplayEnabled;
    }

    public void setSectionHeaderDisplayEnabled(boolean flag) {
        this.mSectionHeaderDisplayEnabled = flag;
    }

    public SectionIndexer getIndexer() {
        return mIndexer;
    }

    public void setIndexer(SectionIndexer indexer) {
        mIndexer = indexer;
        mPlacementCache.invalidate();
    }

    public Object[] getSections() {
        if (mIndexer == null) {
            return new String[] { " " };
        } else {
            return mIndexer.getSections();
        }
    }

    /**
     * @return relative position of the section in the indexed partition
     */
    public int getPositionForSection(int sectionIndex) {
        if (mIndexer == null) {
            return -1;
        }

        return mIndexer.getPositionForSection(sectionIndex);
    }

    /**
     * @param position relative position in the indexed partition
     */
    public int getSectionForPosition(int position) {
        if (mIndexer == null) {
            return -1;
        }

        return mIndexer.getSectionForPosition(position);
    }

    public int getPinnedHeaderCount() {
        if (isSectionHeaderDisplayEnabled()) {
            return 1;
        } else {
            return 0;
        }
    }

    public View getPinnedHeaderView(int viewIndex, View convertView, ViewGroup parent) {
        if (isSectionHeaderDisplayEnabled() && viewIndex == getPinnedHeaderCount() - 1) {
            if (mHeader == null) {
                mHeader = createPinnedSectionHeaderView(mContext, parent);
                // mHeader.setLayoutDirection(parent.getLayoutDirection());
            }
            return mHeader;
        } else {
            return null;
        }
    }

    public void configurePinnedHeaders(PinnedHeaderListView listView) {

        if (!isSectionHeaderDisplayEnabled()) {
            return;
        }

        int index = getPinnedHeaderCount() - 1;
        if (mIndexer == null || getCount() == 0) {
            listView.setHeaderInvisible(index, false);
        } else {
            int listPosition = listView.getPositionAt(listView.getTotalTopPinnedHeaderHeight());
            int position = listPosition - listView.getHeaderViewsCount();

            int section = getSectionForPosition(position);

            if (section == -1) {
            	Log.d("INDEX", "setHeaderInvisible    index = " + index);
                listView.setHeaderInvisible(index, false);
            } else {
                setPinnedSectionTitle(mHeader, (String)mIndexer.getSections()[section]);
                // setPinnedHeaderContactsCount(mHeader, mIndexer.getSections()[section]);

                // Compute the item position where the next section begins
                int nextSectionPosition = getPositionForSection(section + 1);
                boolean isLastInSection = position == nextSectionPosition - 1;
                listView.setFadingHeader(index, listPosition, isLastInSection);
                // Log.d("INDEX", "setFadingHeader    index = " + index + " ; listPosition = " + listPosition + "; isLastInSection = " + isLastInSection);
            }
        }
    }

    /**
     * Computes the item's placement within its section and populates the {@code placement}
     * object accordingly.  Please note that the returned object is volatile and should be
     * copied if the result needs to be used later.
     */
    public Placement getItemPlacementInSection(int position) {
        if (mPlacementCache.position == position) {
            return mPlacementCache;
        }

        mPlacementCache.position = position;
        if (isSectionHeaderDisplayEnabled()) {
            int section = getSectionForPosition(position);
            if (section != -1 && getPositionForSection(section) == position) {
                mPlacementCache.firstInSection = true;
                mPlacementCache.sectionHeader = (String)getSections()[section];
            } else {
                mPlacementCache.firstInSection = false;
                mPlacementCache.sectionHeader = null;
            }

            mPlacementCache.lastInSection = (getPositionForSection(section + 1) - 1 == position);
        } else {
            mPlacementCache.firstInSection = false;
            mPlacementCache.lastInSection = false;
            mPlacementCache.sectionHeader = null;
        }
        return mPlacementCache;
    }
}
