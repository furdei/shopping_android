package com.furdey.shopping.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;

import com.furdey.shopping.content.model.Purchase;
import com.furdey.shopping.content.model.Purchase.PurchaseState;
import com.furdey.shopping.content.model.Unit;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

public class PurchasesDao extends BaseDao<Purchase, Integer> {

	public static final String GOODS_NAME_FIELD_NAME = "goods_name";
	public static final String UNITS_NAME_FIELD_NAME = "units_name";
	public static final String GOODSCATEGORY_ICON_FIELD_NAME = "category_icon";
	public static final String IS_HEADER_FIELD_NAME = "is_header";

	private static final String TAG = PurchasesDao.class.getSimpleName();

	public PurchasesDao(ConnectionSource connectionSource, Class<Purchase> dataClass)
			throws SQLException {
		super(connectionSource, dataClass);
	}

	/**
	 * Constructs model from the current cursor position
	 */
	// public static Purchase fromCursor(Cursor cursor) {
	// Purchase model = new Purchase();
	// BaseDao.fromCursor(cursor, model);
	//
	// int field = cursor.getColumnIndexOrThrow(Purchase.COUNT_FIELD_NAME);
	// model.setCount(new BigDecimal(cursor.getString(field)));
	//
	// field = cursor.getColumnIndexOrThrow(Purchase.DESCR_FIELD_NAME);
	// model.setDescr(cursor.getString(field));
	//
	// field = cursor.getColumnIndexOrThrow(Purchase.GOODS_FIELD_NAME);
	// Goods good = new Goods();
	// good.setId(cursor.getInt(field));
	// model.setGoods(good);
	//
	// field = cursor.getColumnIndexOrThrow(Purchase.UNITS_FIELD_NAME);
	// Unit unit = new Unit();
	// unit.setId(cursor.getInt(field));
	// model.setUnits(unit);
	//
	// field = cursor.getColumnIndexOrThrow(Purchase.STATE_FIELD_NAME);
	// model.setState(PurchaseState.valueOf(cursor.getString(field)));
	//
	// field = cursor.getColumnIndexOrThrow(Purchase.STRDATE_FIELD_NAME);
	// model.setStrdate(getDateFromCursor(cursor, field));
	//
	// field = cursor.getColumnIndexOrThrow(Purchase.FINDATE_FIELD_NAME);
	// model.setFindate(getDateFromCursor(cursor, field));
	//
	// return model;
	// }

	// @SuppressLint("SimpleDateFormat")
	// public Cursor fetchAll(PurchasesSortOrder sortOrder) throws SQLException {
	// SimpleDateFormat sdf = new SimpleDateFormat(BaseDao.DATE_FORMAT);
	// SimpleDateFormat sdtf = new SimpleDateFormat(BaseDao.DATETIME_FORMAT);
	// String curDate;
	// try {
	// curDate = sdtf.format(sdf.parse(sdf.format(new Date())));
	// } catch (ParseException e) {
	// throw new RuntimeException("Unknown error while preparing date", e);
	// }
	//
	// String sortOrderQuery;
	//
	// switch (sortOrder) {
	// case ORDER_BY_ADD_TIME:
	// sortOrderQuery =
	// Purchase.TABLE_NAME.concat(".").concat(Purchase.ID_FIELD_NAME);
	// break;
	// case ORDER_BY_NAME:
	// sortOrderQuery =
	// Goods.TABLE_NAME.concat(".").concat(Goods.NAME_FIELD_NAME);
	// break;
	// default:
	// sortOrderQuery =
	// GoodsCategory.TABLE_NAME.concat(".").concat(GoodsCategory.NAME_FIELD_NAME)
	// .concat(", ").concat(Goods.TABLE_NAME).concat(".").concat(Goods.NAME_FIELD_NAME);
	// }
	//
	// // This temporary table will contain headers for the purchases
	// String rawQuery = "SELECT ".concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.ID_FIELD_NAME).concat(", ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.CHANGED_FIELD_NAME).concat(", ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.DELETED_FIELD_NAME).concat(", ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.STANDARD_FIELD_NAME).concat(", ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.COUNT_FIELD_NAME).concat(", ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.DESCR_FIELD_NAME).concat(", ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.GOODS_FIELD_NAME).concat(", ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.UNITS_FIELD_NAME).concat(", ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.STATE_FIELD_NAME).concat(", ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.STRDATE_FIELD_NAME).concat(", ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.FINDATE_FIELD_NAME).concat(", ").concat(Goods.TABLE_NAME).concat(".")
	// .concat(Goods.NAME_FIELD_NAME).concat(" AS ").concat(GOODS_NAME_FIELD_NAME).concat(", ")
	// .concat(Unit.TABLE_NAME).concat(".").concat(Unit.NAME_FIELD_NAME).concat(" AS ")
	// .concat(UNITS_NAME_FIELD_NAME).concat(", ").concat(GoodsCategory.TABLE_NAME).concat(".")
	// .concat(GoodsCategory.ICON_FIELD_NAME).concat(" AS ").concat(GOODSCATEGORY_ICON_FIELD_NAME)
	// .concat(" FROM ").concat(Purchase.TABLE_NAME).concat(" JOIN ").concat(Goods.TABLE_NAME)
	// .concat(" ON ").concat(Purchase.TABLE_NAME).concat(".").concat(Purchase.GOODS_FIELD_NAME)
	// .concat(" = ").concat(Goods.TABLE_NAME).concat(".").concat(Goods.ID_FIELD_NAME)
	// .concat(" AND '").concat(curDate).concat("' BETWEEN ").concat(Purchase.TABLE_NAME)
	// .concat(".").concat(Purchase.STRDATE_FIELD_NAME).concat(" AND ")
	// .concat(Purchase.TABLE_NAME).concat(".").concat(Purchase.FINDATE_FIELD_NAME)
	// .concat(" AND ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.DELETED_FIELD_NAME).concat(" IS NULL JOIN ").concat(Unit.TABLE_NAME)
	// .concat(" ON ").concat(Purchase.TABLE_NAME).concat(".").concat(Purchase.UNITS_FIELD_NAME)
	// .concat(" = ").concat(Unit.TABLE_NAME).concat(".").concat(Goods.ID_FIELD_NAME)
	// .concat(" JOIN ").concat(GoodsCategory.TABLE_NAME).concat(" ON ").concat(Goods.TABLE_NAME)
	// .concat(".").concat(Goods.CATEGORY_FIELD_NAME).concat(" = ")
	// .concat(GoodsCategory.TABLE_NAME).concat(".").concat(GoodsCategory.ID_FIELD_NAME)
	// .concat(" ORDER BY ").concat(Purchase.TABLE_NAME).concat(".")
	// .concat(Purchase.STATE_FIELD_NAME).concat(" DESC, ").concat(sortOrderQuery);
	//
	// final GenericRawResults<String[]> grr = this.queryRaw(rawQuery);
	// final AndroidDatabaseResults res = (AndroidDatabaseResults)
	// grr.closeableIterator()
	// .getRawResults();
	// final Cursor curs = res.getRawCursor();
	// curs.registerDataSetObserver(new DataSetObserver() {
	// private boolean isClosed = false;
	//
	// @Override
	// public void onInvalidated() {
	// Log.d("GoodsDao", "DataSetObserver.onInvalidated");
	// if (!isClosed) {
	// isClosed = true;
	// try {
	// grr.close();
	// res.close();
	// } catch (SQLException e) {
	// Log.e(TAG, "Error while closing DB results", e);
	// }
	// }
	// }
	// });
	// return curs;
	// }

	/*
	 * public List<Purchase> getPurchasesForEdit() throws SQLException { return
	 * this.queryForAll(); }
	 */

	@SuppressWarnings("unchecked")
	public List<Purchase> getPurchasesByStateAndUnit(Purchase.PurchaseState state, Unit unit)
			throws SQLException {
		QueryBuilder<Purchase, Integer> query = queryBuilder();
		Where<Purchase, Integer> w = query.where();
		w.and(w.eq("state", state), w.eq("unit_id", unit), w.isNull(Purchase.DELETED_FIELD_NAME));

		PreparedQuery<Purchase> statement = query.prepare();
		return query(statement);
	}

	/**
	 * Returns the latest made purchase before specified. If the specified
	 * purchase is null than absolutely latest purchase is retrieved.
	 */
	@SuppressLint("SimpleDateFormat")
	public Purchase getLatestPurchase(Purchase purchase) throws SQLException {
		SimpleDateFormat sdf = new SimpleDateFormat(BaseDao.DATETIME_FORMAT);
		String rawQuery = "SELECT MAX(".concat(Purchase.CHANGED_FIELD_NAME).concat(") FROM ")
				.concat("purchases").concat(" WHERE ")
				// Even deleted can be selected
				// .concat(Purchase.DELETED_FIELD_NAME).concat(" IS NULL AND ")
				.concat("state").concat(" = '").concat(PurchaseState.ACCEPTED.toString()).concat("'");

		if (purchase != null) {
			rawQuery = rawQuery.concat(" AND ").concat(Purchase.CHANGED_FIELD_NAME).concat(" < '")
					.concat(sdf.format(purchase.getChanged())).concat("'");
		}

		GenericRawResults<String[]> grr = this.queryRaw(rawQuery);
		AndroidDatabaseResults res = (AndroidDatabaseResults) grr.closeableIterator().getRawResults();
		String maxBuyTime = res.getString(0);
		grr.close();
		res.close();

		if (maxBuyTime == null)
			return null;

		QueryBuilder<Purchase, Integer> query = queryBuilder();
		Where<Purchase, Integer> w = query.where();
		try {
			w.eq(Purchase.CHANGED_FIELD_NAME, sdf.parse(maxBuyTime));
		} catch (ParseException e) {
			throw new RuntimeException("Error while parsing date and time", e);
		}
		PreparedQuery<Purchase> statement = query.prepare();
		return queryForFirst(statement);
	}
}
