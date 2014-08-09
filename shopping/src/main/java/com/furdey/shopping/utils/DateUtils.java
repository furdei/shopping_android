package com.furdey.shopping.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

import com.furdey.shopping.dao.BaseDao;

@SuppressLint("SimpleDateFormat")
public class DateUtils {

	public static Date getDateWoTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(BaseDao.DATE_FORMAT);
		try {
			return sdf.parse(sdf.format(date));
		} catch (ParseException e) {
			throw new RuntimeException("Error while parsing date", e);
		}
	}

}
