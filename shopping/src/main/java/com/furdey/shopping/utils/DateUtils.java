package com.furdey.shopping.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtils {

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_INFINITY = "2999-12-31";

    public static Date getDateWoTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		try {
			return sdf.parse(sdf.format(date));
		} catch (ParseException e) {
			throw new RuntimeException("Error while parsing date", e);
		}
	}

}
