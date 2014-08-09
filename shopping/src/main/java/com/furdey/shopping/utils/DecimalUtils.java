package com.furdey.shopping.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import android.annotation.SuppressLint;

public class DecimalUtils {

	@SuppressLint("NewApi")
	public static String makeFormatString(BigDecimal number, int numbers) {
		if (number == null)
			number = BigDecimal.ZERO;

		number = number.setScale(numbers, BigDecimal.ROUND_DOWN);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(numbers);
		df.setMinimumFractionDigits(numbers);
		DecimalFormatSymbols format;

		if (android.os.Build.VERSION.SDK_INT < 9)
			format = new DecimalFormatSymbols();
		else
			format = DecimalFormatSymbols.getInstance();

		format.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(format);
		df.setGroupingUsed(false);
		return df.format(number);
	}

}
