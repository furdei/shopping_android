package com.furdey.shopping.content;

import android.annotation.SuppressLint;
import android.database.Cursor;

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

	private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
	private static final SimpleDateFormat sdtf = new SimpleDateFormat(DATETIME_FORMAT,
			Locale.US);

	public static final SimpleDateFormat getDateFormat() {
		return sdf;
	}

	public static final SimpleDateFormat getDateTimeFormat() {
		return sdtf;
	}

	public static final String getDateMidnight(Date date) {
		try {
			return getDateTimeFormat().format(getDateFormat().parse(getDateFormat().format(date)));
		} catch (ParseException e) {
			throw new RuntimeException(String.format(ERROR_PARSING_DATE,
					getDateFormat().format(new Date())));
		}
	}

    public static final String getCurrentDateMidnight() {
        return getDateMidnight(new Date());
    }

    public static final String getCurrentDate() {
		return getDateFormat().format(new Date());
	}

	public static final String getCurrentDateAndTime() {
		return getDateTimeFormat().format(new Date());
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

    public static Date getDateWoTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            return sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            throw new RuntimeException("Error while parsing date", e);
        }
    }
}
