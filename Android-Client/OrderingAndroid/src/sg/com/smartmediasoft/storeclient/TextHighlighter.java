/*
 * Copyright (C) 2011 The Android Open Source Project
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

package sg.com.smartmediasoft.storeclient;

import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

/**
 * Highlights the text in a text field.
 */
public class TextHighlighter {
    private int mTextStyle;

    private CharacterStyle mTextStyleSpan;
    private ForegroundColorSpan mTextColorSpan;

    public TextHighlighter(int hightListColor, int textStyle) {
        mTextStyle = textStyle;
        mTextStyleSpan = getStyleSpan();
        mTextColorSpan = new ForegroundColorSpan(hightListColor);
    }

    /**
     * Sets the text on the given text view, highlighting the word that matches the given prefix.
     *
     * @param view the view on which to set the text
     * @param text the string to use as the text
     * @param prefix the prefix to look for
     */
    public void setPrefixText(TextView view, String text, String prefix) {
        view.setText(applyPrefixHighlight(text, prefix));
    }

    private CharacterStyle getStyleSpan() {
        return new StyleSpan(mTextStyle);
    }

    /**
     * Applies highlight span to the text.
     * @param text Text sequence to be highlighted.
     * @param start Start position of the highlight sequence.
     * @param end End position of the highlight sequence.
     */
    public void applyMaskingHighlight(SpannableString text, int start, int end) {
        /** Sets text color of the masked locations to be highlighted. */
        text.setSpan(getStyleSpan(), start, end, 0);
    }

    /**
     * Returns a CharSequence which highlights the given prefix if found in the given text.
     *
     * @param text the text to which to apply the highlight
     * @param prefix the prefix to look for
     */
    public CharSequence applyPrefixHighlight(CharSequence text, String prefix) {
        if (prefix == null) {
            return text;
        }

        // Skip non-word characters at the beginning of prefix.
        int prefixStart = 0;
        while (prefixStart < prefix.length() &&
                !Character.isLetterOrDigit(prefix.charAt(prefixStart))) {
            prefixStart++;
        }
        final String trimmedPrefix = prefix.substring(prefixStart);

		int index = text.toString().toLowerCase()
				.indexOf(trimmedPrefix.toLowerCase());
		if (index != -1) {
            final SpannableString result = new SpannableString(text);
            result.setSpan(mTextStyleSpan, index, index + trimmedPrefix.length(), 0 /* flags */);
            result.setSpan(mTextColorSpan, index, index + trimmedPrefix.length(), 0 /* flags */);
            return result;
        } else {
            return text;
        }
    }
}
