package com.furdey.shopping.contentproviders;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class GoodsStatisticsContentProvider extends
		BaseContentProvider<GoodsStatisticsContentProvider.Columns> {

	private static final String AUTHORITY = GoodsStatisticsContentProvider.class.getCanonicalName();
	protected static final String GOODS_STATISTICS_PATH = "goods_statistics";

	public static final Uri GOODS_STATISTICS_URI = Uri.parse("content://" + AUTHORITY + "/"
			+ GOODS_STATISTICS_PATH);

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, GOODS_STATISTICS_PATH, ALL_RECORDS);
		sURIMatcher.addURI(AUTHORITY, GOODS_STATISTICS_PATH + "/#", PARTICULAR_RECORD);
	}

	public static enum Columns implements BaseContentProvider.Columns {
		_id("goods_statistics._id"), CHANGED("goods_statistics.changed"), DELETED(
				"goods_statistics.deleted"), STANDARD("goods_statistics.standard"), SYNCHRONIZEDTM(
				"goods_statistics.synchronizedtm"),

		PREV_GOOD_ID("goods_statistics.prev_good_id"), NEXT_GOOD_ID("goods_statistics.next_good_id"), BUY_COUNT(
				"goods_statistics.buy_count");

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
        return queryAll(sURIMatcher, uri, GOODS_STATISTICS_PATH, projection,
                selection, selectionArgs, sortOrder);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
        return insert(GOODS_STATISTICS_PATH, uri, values);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        return delete(sURIMatcher, uri, GOODS_STATISTICS_PATH, selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return update(sURIMatcher, uri, GOODS_STATISTICS_PATH, values, selection, selectionArgs);
	};

	@Override
	protected Columns[] getColumns() {
		return Columns.values();
	}

}
