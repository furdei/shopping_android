package com.furdey.shopping.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.furdey.shopping.content.model.BaseModel;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

public class BaseDao<Type extends BaseModel, Key> extends BaseDaoImpl<Type, Key> {

	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATE_INFINITY = "2999-12-31";

	private static SimpleDateFormat formatterTime;
	private static SimpleDateFormat formatterDate;

	protected BaseDao(ConnectionSource connectionSource, Class<Type> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	/**
	 * Constructs model from the current cursor position
	 */
	public static BaseModel fromCursor(Cursor cursor, BaseModel model) {
		if (model == null)
			model = new BaseModel();

		int field = cursor.getColumnIndexOrThrow(BaseModel.ID_FIELD_NAME);
		model.setId(cursor.getLong(field));

		field = cursor.getColumnIndexOrThrow(BaseModel.CHANGED_FIELD_NAME);
		model.setChanged(getDateTimeFromCursor(cursor, field));

		field = cursor.getColumnIndexOrThrow(BaseModel.DELETED_FIELD_NAME);
		model.setDeleted(getDateTimeFromCursor(cursor, field));

		field = cursor.getColumnIndexOrThrow(BaseModel.STANDARD_FIELD_NAME);
		model.setStandard(cursor.getInt(field) != 0);

		return model;
	}

	/**
	 * It doesn't delete a row from a table. Instead of this it updates a
	 * <code>deleted</code> field with the current date and time. After this an
	 * object won't be selected to the cursor with "non-deleted" objects, but it
	 * can be accessed by it's id.
	 */
	public void softDelete(Key key) throws SQLException {
		Type object = queryForId(key);

		if (object == null)
			throw new SQLException("Object with specified id is not found");

		object.setDeleted(new Date());
		update(object);
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDateTimeFromCursor(Cursor cursor, int field) {
		if (formatterTime == null)
			synchronized (BaseModel.class) {
				if (formatterTime == null)
					formatterTime = new SimpleDateFormat(DATETIME_FORMAT);
			}

		try {
			String date = cursor.getString(field);
			return (date == null) ? null : formatterTime.parse(date);
		} catch (ParseException e) {
			throw new IllegalStateException("Date ".concat(cursor.getString(field))
					.concat(" doesn't meet the format ").concat(DATETIME_FORMAT));
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromCursor(Cursor cursor, int field) {
		if (formatterDate == null)
			synchronized (BaseModel.class) {
				if (formatterDate == null)
					formatterDate = new SimpleDateFormat(DATE_FORMAT);
			}

		try {
			String date = cursor.getString(field);
			return (date == null) ? null : formatterDate.parse(date);
		} catch (ParseException e) {
			throw new IllegalStateException("Date ".concat(cursor.getString(field))
					.concat(" doesn't meet the format ").concat(DATE_FORMAT));
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getInfinity() {
		if (formatterDate == null)
			synchronized (BaseModel.class) {
				if (formatterDate == null)
					formatterDate = new SimpleDateFormat(DATE_FORMAT);
			}

		try {
			return formatterDate.parse(DATE_INFINITY);
		} catch (ParseException e) {
			throw new IllegalStateException("Date ".concat(DATE_INFINITY)
					.concat(" doesn't meet the format ").concat(DATE_FORMAT));
		}
	}
}
