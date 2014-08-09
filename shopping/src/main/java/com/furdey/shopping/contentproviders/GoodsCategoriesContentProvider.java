package com.furdey.shopping.contentproviders;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.furdey.shopping.content.ContentUtils;
import com.furdey.shopping.contentproviders.GoodsCategoriesContentProvider.Columns;

public class GoodsCategoriesContentProvider extends BaseContentProvider<Columns> {

	private static final String ERROR_UNKNOWN_URI = "Unknown URI: %s";
	private static final String ERROR_SELECTION_SELECTION_ARGS_ARE_NOT_SUPPORTED = "Neither selection nor selectionArgs are supported, should be null";
	private static final String ERROR_FAILED_TO_ADD_A_ROW = "Failed to add a row to %s (id = %d)";

	private static final String AUTHORITY = GoodsCategoriesContentProvider.class.getCanonicalName();
	static final String GOODS_CATEGORIES_PATH = "goods_categories";

	public static final Uri GOODS_CATEGORIES_URI = Uri.parse("content://" + AUTHORITY + "/"
			+ GOODS_CATEGORIES_PATH);

	private static final int ALL_RECORDS = 10;
	private static final int PARTICULAR_RECORD = 20;

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, GOODS_CATEGORIES_PATH, ALL_RECORDS);
		sURIMatcher.addURI(AUTHORITY, GOODS_CATEGORIES_PATH + "/#", PARTICULAR_RECORD);
	}

	public static enum Columns implements BaseContentProvider.Columns {
		_id("goods_categories._id"), CHANGED("goods_categories.changed"), DELETED(
				"goods_categories.deleted"), STANDARD("goods_categories.standard"), SYNCHRONIZEDTM(
				"goods_categories.synchronizedtm"),

		NAME("goods_categories.name"), DESCR("goods_categories.descr"), ICON("goods_categories.icon");

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
		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(GOODS_CATEGORIES_PATH);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case ALL_RECORDS:
			queryBuilder.appendWhere(Columns.DELETED.getDbName() + " IS NULL");
			break;
		case PARTICULAR_RECORD:
			// adding the ID to the original query
			queryBuilder.appendWhere(Columns._id + "=" + getId(uri));
			break;
		default:
			throw new IllegalArgumentException(String.format(ERROR_UNKNOWN_URI, uri));
		}

		queryBuilder.setProjectionMap(getColumnsMap());

		Cursor cursor = queryBuilder.query(getDbHelper().getDb(), projection, selection, selectionArgs,
				null, null, sortOrder);
		// make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		values.put(Columns.CHANGED.name(), ContentUtils.getCurrentDateAndTime());
		values.put(Columns.STANDARD.name(), ContentUtils.NONSTANDARD);

		SQLiteDatabase db = getDbHelper().getDb();
		long id = db.insert(GOODS_CATEGORIES_PATH, null, values);

		if (id < 0)
			throw new IllegalStateException(String.format(ERROR_FAILED_TO_ADD_A_ROW,
					GOODS_CATEGORIES_PATH, id));

		Uri inserted = ContentUris.withAppendedId(GOODS_CATEGORIES_URI, id);
		getContext().getContentResolver().notifyChange(inserted, null);
		return inserted;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		if (uriType != PARTICULAR_RECORD) {
			throw new IllegalArgumentException(String.format(ERROR_UNKNOWN_URI, uri));
		}

		if (selection != null || selectionArgs != null) {
			throw new IllegalArgumentException(ERROR_SELECTION_SELECTION_ARGS_ARE_NOT_SUPPORTED);
		}

		ContentValues values = new ContentValues();
		values.put(Columns.DELETED.name(), 1);
		values.put(Columns.CHANGED.name(), ContentUtils.getCurrentDateAndTime());

		SQLiteDatabase db = getDbHelper().getDb();
		int rowsAffected = db.update(GOODS_CATEGORIES_PATH, values, Columns._id.getDbName() + "=?",
				new String[] { getId(uri) });

		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAffected;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		if (uriType != PARTICULAR_RECORD) {
			throw new IllegalArgumentException(String.format(ERROR_UNKNOWN_URI, uri));
		}

		if (selection != null || selectionArgs != null) {
			throw new IllegalArgumentException(ERROR_SELECTION_SELECTION_ARGS_ARE_NOT_SUPPORTED);
		}

		values.put(Columns.CHANGED.name(), ContentUtils.getCurrentDateAndTime());

		SQLiteDatabase db = getDbHelper().getDb();
		int rowsAffected = db.update(GOODS_CATEGORIES_PATH, values, Columns._id.getDbName() + "=?",
				new String[] { getId(uri) });
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAffected;
	}

	@Override
	protected Columns[] getColumns() {
		return Columns.values();
	}

}
