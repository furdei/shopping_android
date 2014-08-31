package com.furdey.shopping.contentproviders;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.furdey.shopping.content.ContentUtils;
import com.furdey.shopping.content.model.Purchase;

public class PurchasesContentProvider extends
		BaseContentProvider<com.furdey.shopping.contentproviders.PurchasesContentProvider.Columns> {

	private static final String AUTHORITY = PurchasesContentProvider.class.getCanonicalName();
	public static final String PURCHASES_PATH = "purchases";

	public static final Uri PURCHASES_URI = Uri.parse("content://" + AUTHORITY + "/" + PURCHASES_PATH);

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
        System.out.println("PurchasesContentProvider.query");
        // Set the table
		String tables = PURCHASES_PATH.concat(" JOIN ").concat(GoodsContentProvider.GOODS_PATH).concat(" ON ")
                .concat(Columns.GOOD_ID.getDbName()).concat(" = ").concat(Columns.GOODS__ID.getDbName())
                .concat(" AND '").concat(ContentUtils.getCurrentDateMidnight()).concat("' BETWEEN ")
                .concat(Columns.STRDATE.getDbName()).concat(" AND ").concat(Columns.FINDATE.getDbName())
                .concat(" JOIN ").concat(UnitsContentProvider.UNITS_PATH).concat(" ON ")
                .concat(Columns.UNITS_ID.getDbName()).concat(" = ").concat(Columns.UNIT__ID.getDbName())
                .concat(" JOIN ").concat(GoodsCategoriesContentProvider.GOODS_CATEGORIES_PATH).concat(" ON ")
                .concat(Columns.GOODS_CATEGORY.getDbName()).concat(" = ")
                .concat(Columns.GOODSCATEGORY_ID.getDbName());

        Cursor cursor = queryAll(sURIMatcher, uri, tables, PURCHASES_PATH, projection,
                selection, selectionArgs, sortOrder);
        //dump(cursor);

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
        values.put(Columns.STATE.name(), Purchase.PurchaseState.ENTERED.toString());
        return insert(PURCHASES_PATH, uri, values);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        return delete(sURIMatcher, uri, PURCHASES_PATH, selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return update(sURIMatcher, uri, PURCHASES_PATH, values, selection, selectionArgs);
	}

	@Override
	protected Columns[] getColumns() {
		return Columns.values();
	}

}
