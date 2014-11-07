package sg.com.smartmediasoft.storeclient.ui;

import java.util.Arrays;
import java.util.List;

import android.text.TextUtils;
import android.widget.SectionIndexer;

public class NameSectionIndexer implements SectionIndexer {

	private String[] mSections;
	private int[] mPositions;
	private int mCount;
	private static final String BLANK_HEADER_STRING = " ";

	/**
	 * Constructor.
	 * 
	 * @param sections
	 *            a non-null array
	 * @param counts
	 *            a non-null array of the same size as <code>sections</code>
	 */
	public NameSectionIndexer(List<String> sections, List<Integer> counts) {
		if (sections == null || counts == null) {
			throw new NullPointerException();
		}

		if (sections.size() != counts.size()) {
			throw new IllegalArgumentException(
					"The sections and counts arrays must have the same length");
		}

		this.mSections = sections.toArray(new String[sections.size()]);
		mPositions = new int[counts.size()];
		int position = 0;
		for (int i = 0; i < counts.size(); i++) {
			if (TextUtils.isEmpty(mSections[i])) {
				mSections[i] = BLANK_HEADER_STRING;
			} else if (!mSections[i].equals(BLANK_HEADER_STRING)) {
				mSections[i] = mSections[i].trim();
			}

			mPositions[i] = position;
			position += counts.get(i);
		}
		mCount = position;
	}

	@Override
	public Object[] getSections() {
		return mSections;
	}

	@Override
	public int getPositionForSection(int section) {
		if (section < 0 || section >= mSections.length) {
			return -1;
		}

		return mPositions[section];
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position < 0 || position >= mCount) {
			return -1;
		}

		int index = Arrays.binarySearch(mPositions, position);

		/*
		 * Consider this example: section positions are 0, 3, 5; the supplied
		 * position is 4. The section corresponding to position 4 starts at
		 * position 3, so the expected return value is 1. Binary search will not
		 * find 4 in the array and thus will return -insertPosition-1, i.e. -3.
		 * To get from that number to the expected value of 1 we need to negate
		 * and subtract 2.
		 */
		return index >= 0 ? index : -index - 2;
	}

	public void setProfileHeader(String header) {
		if (mSections != null) {
			// Don't do anything if the header is already set properly.
			if (mSections.length > 0 && header.equals(mSections[0])) {
				return;
			}

			// Since the section indexer isn't aware of the profile at the top,
			// we need to add a
			// special section at the top for it and shift everything else down.
			String[] tempSections = new String[mSections.length + 1];
			int[] tempPositions = new int[mPositions.length + 1];
			tempSections[0] = header;
			tempPositions[0] = 0;
			for (int i = 1; i <= mPositions.length; i++) {
				tempSections[i] = mSections[i - 1];
				tempPositions[i] = mPositions[i - 1] + 1;
			}
			mSections = tempSections;
			mPositions = tempPositions;
			mCount++;
		}
	}
}