package com.furdey.shopping.contentproviders;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.furdey.shopping.content.ContentUtils;
import com.furdey.shopping.content.model.Purchase;

public class PurchasesContentProvider extends
		BaseContentProvider<com.furdey.shopping.contentproviders.PurchasesContentProvider.Columns> {

	private static final String ERROR_UNKNOWN_URI = "Unknown URI: %s";
	private static final String ERROR_SELECTION_SELECTION_ARGS_ARE_NOT_SUPPORTED = "Neither selection nor selectionArgs are supported, should be null";
	private static final String ERROR_FAILED_TO_ADD_A_ROW = "Failed to add a row to %s (id = %d)";

	private static final String AUTHORITY = PurchasesContentProvider.class.getCanonicalName();
	public static final String PURCHASES_PATH = "purchases";

	public static final Uri PURCHASES_URI = Uri.parse("content://" + AUTHORITY + "/" + PURCHASES_PATH);

	private static final int ALL_RECORDS = 10;
	private static final int PARTICULAR_RECORD = 20;

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, PURCHASES_PATH, ALL_RECORDS);
		sURIMatcher.addURI(AUTHORITY, PURCHASES_PATH + "/#", PARTICULAR_RECORD);
	}

	public static enum Columns implements BaseContentProvider.Columns {
		_id("purchases._id"), CHANGED("purchases.changed"), DELETED("purchases.deleted"), STANDARD(
				"purchases.standard"), SYNCHRONIZEDTM("purchases.synchronizedtm"),

		COUNT("purchases.count"), DESCR("purchases.descr"), GOOD_ID("purchases.good_id"), UNITS_ID(
				"purchases.units_id"), STATE("purchases.state"), STRDATE("purchases.strdate"), FINDATE(
				"purchases.findate"), SORTORDER("purchases.sortorder"),

		GOODS__ID("goods._id"), GOODS_CHANGED("goods.changed"), GOODS_DELETED("goods.deleted"), GOODS_STANDARD(
				"goods.standard"), GOODS_SYNCHRONIZEDTM("goods.synchronizedtm"),

		GOODS_NAME("goods.name"), GOODS_DEFAULTUNITS("goods.defaultUnits_id"), GOODS_CATEGORY(
				"goods.category_id"),

		UNIT__ID("units._id"), UNIT_CHANGED("units.changed"), UNIT_DELETED("units.deleted"), UNIT_STANDARD(
				"units.standard"), UNIT_SYNCHRONIZEDTM("units.synchronizedtm"),

		UNIT_NAME("units.name"), UNIT_DESCR("units.descr"), UNIT_DECIMALS("units.decimals"), UNIT_UNITTYPE(
				"units.unitType"), UNIT_ISDEFAULT("units.isDefault"),

		GOODSCATEGORY_ID("goods_categories._id"), GOODSCATEGORY_CHANGED("goods_categories.changed"), GOODSCATEGORY_DELETED(
				"goods_categories.deleted"), GOODSCATEGORY_STANDARD("goods_categories.standard"), GOODSCATEGORY_SYNCHRONIZEDTM(
				"goods_categories.synchronizedtm"),

		GOODSCATEGORY_NAME("goods_categories.name"), GOODSCATEGORY_DESCR("goods_categories.descr"), GOODSCATEGORY_ICON(
				"goods_categories.icon");

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
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(PURCHASES_PATH.concat(" JOIN ").concat(GoodsContentProvider.GOODS_PATH).concat(" ON ")
                .concat(Columns.GOOD_ID.getDbName()).concat(" = ").concat(Columns.GOODS__ID.getDbName())
                .concat(" AND '").concat(ContentUtils.getCurrentDateMidnight()).concat("' BETWEEN ")
                .concat(Columns.STRDATE.getDbName()).concat(" AND ").concat(Columns.FINDATE.getDbName())
                .concat(" AND ").concat(Columns.DELETED.getDbName()).concat(" IS NULL JOIN ")
                .concat(UnitsContentProvider.UNITS_PATH).concat(" ON ")
                .concat(Columns.UNITS_ID.getDbName()).concat(" = ").concat(Columns.UNIT__ID.getDbName())
                .concat(" JOIN ").concat(GoodsCategoriesContentProvider.GOODS_CATEGORIES_PATH).concat(" ON ")
                .concat(Columns.GOODS_CATEGORY.getDbName()).concat(" = ")
                .concat(Columns.GOODSCATEGORY_ID.getDbName()));

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case ALL_RECORDS:
			break;
		case PARTICULAR_RECORD:
			// adding the ID to the original query
			queryBuilder.appendWhere(Columns._id.getDbName() + "=" + getId(uri));
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
		values.put(Columns.STRDATE.name(), ContentUtils.getCurrentDate());
		values.put(Columns.FINDATE.name(), ContentUtils.DATE_INFINITY);
		values.put(Columns.STANDARD.name(), ContentUtils.NONSTANDARD);
		values.put(Columns.CHANGED.name(), ContentUtils.getCurrentDateAndTime());
        values.put(Columns.STATE.name(), Purchase.PurchaseState.ENTERED.toString());

		SQLiteDatabase db = getDbHelper().getDb();
		long id = db.insert(PURCHASES_PATH, null, values);

		if (id < 0)
			throw new IllegalStateException(String.format(ERROR_FAILED_TO_ADD_A_ROW, PURCHASES_PATH, id));

		Uri inserted = ContentUris.withAppendedId(PURCHASES_URI, id);
		getContext().getContentResolver().notifyChange(inserted, null);
		return inserted;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = getDbHelper().getDb();
		int rowsAffected = db.delete(PURCHASES_PATH, selection, selectionArgs);
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
		int rowsAffected = db.update(PURCHASES_PATH, values, Columns._id.getDbName() + "=?",
				new String[] { getId(uri) });
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAffected;
	}

	@Override
	protected Columns[] getColumns() {
		return Columns.values();
	}

}
