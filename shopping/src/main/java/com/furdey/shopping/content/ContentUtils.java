package com.furdey.shopping.content;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.furdey.shopping.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ContentUtils {

	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATE_INFINITY = "2999-12-31";
	public static final String MIDNIGHT = "%s 00:00:00.000";

	public static final int NONSTANDARD = 0;
	public static final int STANDARD = 1;

	private static final String ERROR_PARSING_DATE = "Error parsing date %s";

	private static final SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_FORMAT, Locale.US);
	private static final SimpleDateFormat sdtf = new SimpleDateFormat(DateUtils.DATETIME_FORMAT,
			Locale.US);

	public static final SimpleDateFormat getDateFormat() {
		return sdf;
	}

	public static final SimpleDateFormat getDateTimeFormat() {
		return sdtf;
	}

	public static final String getCurrentDateMidnight() {
		try {
			return getDateTimeFormat().format(getDateFormat().parse(getDateFormat().format(new Date())));
		} catch (ParseException e) {
			throw new RuntimeException(String.format(ERROR_PARSING_DATE,
					getDateFormat().format(new Date())));
		}
	}

	public static final String getCurrentDate() {
		return getDateFormat().format(new Date());
	}

	public static final String getCurrentDateAndTime() {
		return getDateTimeFormat().format(new Date());
	}

	public static Date getDateTimeFromCursor(Cursor cursor, int field) {
		try {
			String date = cursor.getString(field);
			return (date == null) ? null : getDateTimeFormat().parse(date);
		} catch (ParseException e) {
			throw new IllegalStateException("Date ".concat(cursor.getString(field))
					.concat(" doesn't meet the format ").concat(DATETIME_FORMAT));
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromCursor(Cursor cursor, int field) {
		try {
			String date = cursor.getString(field);
			return (date == null) ? null : getDateFormat().parse(date);
		} catch (ParseException e) {
			throw new IllegalStateException("Date ".concat(cursor.getString(field))
					.concat(" doesn't meet the format ").concat(DATE_FORMAT));
		}
	}

}
