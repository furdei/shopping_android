package com.furdey.shopping.content;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.furdey.shopping.content.model.Unit;
import com.furdey.shopping.content.model.Unit.UnitType;
import com.furdey.shopping.contentproviders.UnitsContentProvider;

public class UnitsUtils {

	private static final int UNDEFINED = -1;

	/**
	 * Constructs model from the current cursor position
	 */
	public static Unit fromCursor(Cursor cursor) {
		Unit model = new Unit();

		int field = cursor.getColumnIndex(UnitsContentProvider.Columns._id.toString());
		if (field != UNDEFINED)
			model.setId(cursor.getLong(field));

		field = cursor.getColumnIndex(UnitsContentProvider.Columns.NAME.toString());
		if (field != UNDEFINED)
			model.setName(cursor.getString(field));

		field = cursor.getColumnIndex(UnitsContentProvider.Columns.DESCR.toString());
		if (field != UNDEFINED)
			model.setDescr(cursor.getString(field));

		field = cursor.getColumnIndex(UnitsContentProvider.Columns.DECIMALS.toString());
		if (field != UNDEFINED)
			model.setDecimals(cursor.getInt(field));

		field = cursor.getColumnIndex(UnitsContentProvider.Columns.UNITTYPE.toString());
		if (field != UNDEFINED)
			model.setUnitType(UnitType.valueOf(cursor.getString(field)));

		field = cursor.getColumnIndex(UnitsContentProvider.Columns.ISDEFAULT.toString());
		if (field != UNDEFINED)
			model.setIsDefault(cursor.getInt(field) != 0);

		return model;
	}

	private static final String[] unitsListProjection = new String[] {
			UnitsContentProvider.Columns._id.toString(), UnitsContentProvider.Columns.NAME.toString(),
			UnitsContentProvider.Columns.DESCR.toString(),
			UnitsContentProvider.Columns.DECIMALS.toString(),
			UnitsContentProvider.Columns.UNITTYPE.toString(),
			UnitsContentProvider.Columns.ISDEFAULT.toString() };

	public static CursorLoader getUnitsLoader(Context context, String[] projection, String selection,
			String[] selectionArgs, String orderBy) {
		return new CursorLoader(context, UnitsContentProvider.UNITS_URI, projection, selection,
				selectionArgs, orderBy);
	}

	public static CursorLoader getUnitsLoader(Context context) {
		return getUnitsLoader(context, unitsListProjection, null, null, null);
	}

	public static Cursor getUnits(Context context, String[] projection, String selection,
			String[] selectionArgs, String orderBy) {
		return context.getContentResolver().query(UnitsContentProvider.UNITS_URI, projection,
				selection, selectionArgs, orderBy);
	}

	public static Cursor getUnitsByNameOrDescr(Context context, String name, String descr) {
		return getUnits(context, unitsListProjection, UnitsContentProvider.Columns.NAME.toString()
				+ " = ? OR " + UnitsContentProvider.Columns.DESCR.toString() + " = ?", new String[] { name,
				descr }, null);
	}

	public static ContentValues getContentValues(Unit unit, boolean includeId) {
		ContentValues contentValues = new ContentValues();

		if (unit.getId() != null && includeId)
			contentValues.put(UnitsContentProvider.Columns._id.toString(), unit.getId());

		if (unit.getDecimals() != null)
			contentValues.put(UnitsContentProvider.Columns.DECIMALS.toString(), unit.getDecimals());

		if (unit.getDescr() != null)
			contentValues.put(UnitsContentProvider.Columns.DESCR.toString(), unit.getDescr());

		if (unit.getIsDefault() != null)
			contentValues.put(UnitsContentProvider.Columns.ISDEFAULT.toString(), unit.getIsDefault());

		if (unit.getName() != null)
			contentValues.put(UnitsContentProvider.Columns.NAME.toString(), unit.getName());

		if (unit.getUnitType() != null)
			contentValues.put(UnitsContentProvider.Columns.UNITTYPE.toString(), unit.getUnitType()
					.toString());

		return contentValues;
	}

	public static Uri saveUnit(Context context, Unit unit) {
		if (unit.getId() == null) {
			return context.getContentResolver().insert(UnitsContentProvider.UNITS_URI,
					getContentValues(unit, false));
		} else {
			context.getContentResolver().update(
					ContentUris.withAppendedId(UnitsContentProvider.UNITS_URI, unit.getId()),
					getContentValues(unit, false), null, null);
			return UnitsContentProvider.UNITS_URI;
		}
	}

	public static int deleteUnit(Context context, long unitId) {
		return context.getContentResolver().delete(
				ContentUris.withAppendedId(UnitsContentProvider.UNITS_URI, unitId), null, null);
	}
}
