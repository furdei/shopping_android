package com.furdey.shopping.contentproviders;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.furdey.shopping.content.ContentUtils;
import com.furdey.shopping.content.model.Purchase.PurchaseState;
import com.furdey.shopping.contentproviders.GoodsContentProvider.Columns;

public class GoodsContentProvider extends BaseContentProvider<Columns> {

	private static final String AUTHORITY = GoodsContentProvider.class.getCanonicalName();
	protected static final String GOODS_PATH = "goods";

	public static final Uri GOODS_URI = Uri.parse("content://" + AUTHORITY + "/" + GOODS_PATH);

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, GOODS_PATH, ALL_RECORDS);
		sURIMatcher.addURI(AUTHORITY, GOODS_PATH + "/#", PARTICULAR_RECORD);
	}

	public static enum Columns implements BaseContentProvider.Columns {
		_id("goods._id"), CHANGED("goods.changed"), DELETED("goods.deleted"), STANDARD("goods.standard"), SYNCHRONIZEDTM(
				"goods.synchronizedtm"),

		NAME("goods.name"), DEFAULTUNITS_ID("goods.defaultUnits_id"), CATEGORY_ID("goods.category_id"),

		CATEGORY_NAME("goods_categories.name"), ICON("goods_categories.icon"),

		PURCHASE_ID("purchases._id"), PURCHASE_DELAYED(
				"CASE WHEN purchases.strdate > datetime() THEN 1 ELSE 0 END");

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
		// Set the table
		String tables = GOODS_PATH.concat(" JOIN ")
				.concat(GoodsCategoriesContentProvider.GOODS_CATEGORIES_PATH).concat(" ON ")
				.concat(Columns.CATEGORY_ID.getDbName()).concat(" = ")
				.concat(GoodsCategoriesContentProvider.Columns._id.getDbName()).concat(" LEFT OUTER JOIN ")
				.concat(PurchasesContentProvider.PURCHASES_PATH).concat(" ON ")
				.concat(Columns._id.getDbName()).concat(" = ")
				.concat(PurchasesContentProvider.Columns.GOOD_ID.getDbName()).concat(" AND ")
				.concat(PurchasesContentProvider.Columns.STATE.getDbName()).concat(" = '")
				.concat(PurchaseState.ENTERED.toString()).concat("' AND ")
				.concat(PurchasesContentProvider.Columns.DELETED.getDbName()).concat(" IS NULL")
                .concat(" AND '").concat(ContentUtils.getCurrentDateMidnight()).concat("' BETWEEN ")
                .concat(PurchasesContentProvider.Columns.STRDATE.getDbName()).concat(" AND ")
                .concat(PurchasesContentProvider.Columns.FINDATE.getDbName());

        Cursor cursor = queryAll(sURIMatcher, uri, tables, GOODS_PATH, projection,
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
        return insert(GOODS_PATH, uri, values);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        return delete(sURIMatcher, uri, GOODS_PATH, selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return update(sURIMatcher, uri, GOODS_PATH, values, selection, selectionArgs);
	}

	@Override
	protected Columns[] getColumns() {
		return Columns.values();
	}

}
