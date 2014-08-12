package com.furdey.shopping.contentproviders;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.furdey.shopping.contentproviders.GoodsCategoriesContentProvider.Columns;

public class GoodsCategoriesContentProvider extends BaseContentProvider<Columns> {

	private static final String AUTHORITY = GoodsCategoriesContentProvider.class.getCanonicalName();
	static final String GOODS_CATEGORIES_PATH = "goods_categories";

	public static final Uri GOODS_CATEGORIES_URI = Uri.parse("content://" + AUTHORITY + "/"
			+ GOODS_CATEGORIES_PATH);

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
        return queryAll(sURIMatcher, uri, GOODS_CATEGORIES_PATH, projection,
                selection, selectionArgs, sortOrder);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
        return insert(GOODS_CATEGORIES_PATH, uri, values);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        return delete(sURIMatcher, uri, GOODS_CATEGORIES_PATH, selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return update(sURIMatcher, uri, GOODS_CATEGORIES_PATH, values, selection, selectionArgs);
	}

	@Override
	protected Columns[] getColumns() {
		return Columns.values();
	}

}
