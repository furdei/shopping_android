package com.furdey.shopping.contentproviders;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class CategoriesStatisticsContentProvider extends
		BaseContentProvider<CategoriesStatisticsContentProvider.Columns> {

	private static final String AUTHORITY = CategoriesStatisticsContentProvider.class
			.getCanonicalName();
	protected static final String CATEGORIES_STATISTICS_PATH = "categories_statistics";

	public static final Uri CATEGORIES_STATISTICS_URI = Uri.parse("content://" + AUTHORITY + "/"
			+ CATEGORIES_STATISTICS_PATH);

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, CATEGORIES_STATISTICS_PATH, ALL_RECORDS);
		sURIMatcher.addURI(AUTHORITY, CATEGORIES_STATISTICS_PATH + "/#", PARTICULAR_RECORD);
	}

	public static enum Columns implements BaseContentProvider.Columns {
		_id("categories_statistics._id"), CHANGED("categories_statistics.changed"), DELETED(
				"categories_statistics.deleted"), STANDARD("categories_statistics.standard"), SYNCHRONIZEDTM(
				"categories_statistics.synchronizedtm"),

		PREV_CATEGORY_ID("categories_statistics.prev_category_id"), NEXT_CATEGORY_ID(
				"categories_statistics.next_category_id"), BUY_COUNT("categories_statistics.buy_count");

		private final String dbName;

		private Columns(final String s) {
			dbName = s;
		}

		public String getDbName() {
			return dbName;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
        return queryAll(sURIMatcher, uri, CATEGORIES_STATISTICS_PATH, projection,
                selection, selectionArgs, sortOrder);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
        return insert(CATEGORIES_STATISTICS_PATH, uri, values);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        return delete(sURIMatcher, uri, CATEGORIES_STATISTICS_PATH, selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return update(sURIMatcher, uri, CATEGORIES_STATISTICS_PATH, values, selection, selectionArgs);
	};

	@Override
	protected Columns[] getColumns() {
		return Columns.values();
	}

}
