package sg.com.smartmediasoft.storeclient;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {

	private double min, max;

	public InputFilterMinMax(double min, double max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		try {
			if (source.equals('.')) {
				return null;
			}
			
			String currentText = dest.toString();
			int index = currentText.indexOf('.');
			if (index > 0) {
				if (currentText.substring(index).length() > 1) {
					return "";
				}
			}

			double input = Double.parseDouble(dest.toString()
					+ source.toString());
			if (input == 0) {
				return null;
			}
			
			if (isInRange(min, max, input))
				return null;
		} catch (NumberFormatException nfe) {
		}
		return "";
	}

	private boolean isInRange(double a, double b, double c) {
		return b > a ? c >= a && c <= b : c >= b && c <= a;
	}
}
