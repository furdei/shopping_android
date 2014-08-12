package com.furdey.shopping.contentproviders;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class UnitsContentProvider extends
		BaseContentProvider<com.furdey.shopping.contentproviders.UnitsContentProvider.Columns> {

	private static final String AUTHORITY = UnitsContentProvider.class.getCanonicalName();
	protected static final String UNITS_PATH = "units";

	public static final Uri UNITS_URI = Uri.parse("content://" + AUTHORITY + "/" + UNITS_PATH);

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, UNITS_PATH, ALL_RECORDS);
		sURIMatcher.addURI(AUTHORITY, UNITS_PATH + "/#", PARTICULAR_RECORD);
	}

	public static enum Columns implements BaseContentProvider.Columns {
		_id("units._id"), CHANGED("units.changed"), DELETED("units.deleted"), STANDARD("units.standard"), SYNCHRONIZEDTM(
				"units.synchronizedtm"),

		NAME("units.name"), DESCR("units.descr"), DECIMALS("units.decimals"), UNITTYPE("units.unitType"), ISDEFAULT(
				"units.isDefault");

		private final String dbName;

		private Columns(final String s) {
			dbName = s;
		}

		public String getDbName() {
			return dbName;
		}
	};

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		if (sortOrder == null) {
			sortOrder = Columns.ISDEFAULT.getDbName() + " DESC";
		}

        return queryAll(sURIMatcher, uri, UNITS_PATH, projection,
                selection, selectionArgs, sortOrder);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
        return insert(UNITS_PATH, uri, values);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        return delete(sURIMatcher, uri, UNITS_PATH, selection, selectionArgs);

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return update(sURIMatcher, uri, UNITS_PATH, values, selection, selectionArgs);
	}

	@Override
	protected Columns[] getColumns() {
		return Columns.values();
	}
}
