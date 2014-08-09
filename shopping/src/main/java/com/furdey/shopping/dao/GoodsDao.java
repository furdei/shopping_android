package com.furdey.shopping.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.util.Log;

import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.content.model.Unit;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

public class GoodsDao extends BaseDao<Goods, Long> {

	public static final String CATEGORY_OF_GOODS_FIELD_NAME = "category_name";
	public static final String CATEGORY_ICON_FIELD_NAME = "category_icon";
	public static final String PURCHASE_ID_FIELD_NAME = "purchase_id";
	public static final String PURCHASE_DELAYED_FIELD_NAME = "purchase_delayed";

	private static final String TAG = GoodsDao.class.getSimpleName();

	public GoodsDao(ConnectionSource connectionSource, Class<Goods> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	/**
	 * Constructs model from the current cursor position
	 */
	public static Goods fromCursor(Cursor cursor) {
		Goods model = new Goods();
		BaseDao.fromCursor(cursor, model);

		int field = cursor.getColumnIndexOrThrow(Goods.NAME_FIELD_NAME);
		model.setName(cursor.getString(field));

		field = cursor.getColumnIndexOrThrow(Goods.DEFAULTUNITS_FIELD_NAME);
		Unit unit = new Unit();
		unit.setId(cursor.getLong(field));
		model.setDefaultUnits(unit);

		field = cursor.getColumnIndexOrThrow(Goods.CATEGORY_FIELD_NAME);
		GoodsCategory goodsCategory = new GoodsCategory();
		goodsCategory.setId(cursor.getLong(field));
		model.setCategory(goodsCategory);

		return model;
	}

	@SuppressLint("DefaultLocale")
	public Cursor filterByName(String name) throws SQLException {
		String upperNameSubquery = "";

		if (name == null)
			name = "%";
		else {
			String upperName = name;
			name = "%".concat(name.toLowerCase()).concat("%");
			upperNameSubquery = " OR ".concat(Goods.TABLE_NAME).concat(".").concat(Goods.NAME_FIELD_NAME)
					.concat(" LIKE '%").concat(upperName).concat("%' ");
		}

		System.out.println("name.toLowerCase(): " + name.toLowerCase());

		SimpleDateFormat sdf = new SimpleDateFormat(BaseDao.DATE_FORMAT);
		SimpleDateFormat sdtf = new SimpleDateFormat(BaseDao.DATETIME_FORMAT);
		String curDate;
		try {
			curDate = sdtf.format(sdf.parse(sdf.format(new Date())));
		} catch (ParseException e) {
			throw new RuntimeException("Unknown error while preparing date", e);
		}

		String rawQuery = "";
		// String rawQuery =
		// "SELECT ".concat(Goods.TABLE_NAME).concat(".").concat(Goods.ID_FIELD_NAME)
		// .concat(", ").concat(Goods.TABLE_NAME).concat(".").concat(Goods.CHANGED_FIELD_NAME)
		// .concat(", ").concat(Goods.TABLE_NAME).concat(".").concat(Goods.DELETED_FIELD_NAME)
		// .concat(", ").concat(Goods.TABLE_NAME).concat(".").concat(Goods.STANDARD_FIELD_NAME)
		// .concat(", LOWER(").concat(Goods.TABLE_NAME).concat(".").concat(Goods.NAME_FIELD_NAME)
		// .concat(") AS name, ").concat(Goods.TABLE_NAME).concat(".")
		// .concat(Goods.DEFAULTUNITS_FIELD_NAME).concat(", ").concat(Goods.TABLE_NAME).concat(".")
		// .concat(Goods.CATEGORY_FIELD_NAME).concat(", ").concat(GoodsCategory.TABLE_NAME).concat(".")
		// .concat(GoodsCategory.NAME_FIELD_NAME).concat(" AS ").concat(CATEGORY_OF_GOODS_FIELD_NAME)
		// .concat(", ").concat(GoodsCategory.TABLE_NAME).concat(".")
		// .concat(GoodsCategory.ICON_FIELD_NAME).concat(" AS ").concat(CATEGORY_ICON_FIELD_NAME)
		// .concat(", ").concat(Purchase.TABLE_NAME).concat(".").concat(Purchase.ID_FIELD_NAME)
		// .concat(" AS ").concat(PURCHASE_ID_FIELD_NAME).concat(", ").concat("CASE WHEN ")
		// .concat(Purchase.TABLE_NAME).concat(".").concat(Purchase.STRDATE_FIELD_NAME).concat(" > '")
		// .concat(curDate).concat("' THEN 1 ELSE 0 END AS ").concat(PURCHASE_DELAYED_FIELD_NAME)
		// .concat(" FROM ").concat(Goods.TABLE_NAME).concat(" JOIN ").concat(GoodsCategory.TABLE_NAME)
		// .concat(" ON ").concat(Goods.TABLE_NAME).concat(".").concat(Goods.CATEGORY_FIELD_NAME)
		// .concat(" = ").concat(GoodsCategory.TABLE_NAME).concat(".")
		// .concat(GoodsCategory.ID_FIELD_NAME).concat(" AND (LOWER(").concat(Goods.TABLE_NAME)
		// .concat(".").concat(Goods.NAME_FIELD_NAME).concat(") LIKE '").concat(name).concat("'")
		// .concat(upperNameSubquery).concat(") AND ").concat(Goods.TABLE_NAME).concat(".")
		// .concat(Goods.DELETED_FIELD_NAME).concat(" IS NULL LEFT OUTER JOIN ")
		// .concat(Purchase.TABLE_NAME).concat(" ON ").concat(Goods.TABLE_NAME).concat(".")
		// .concat(Goods.ID_FIELD_NAME).concat(" = ").concat(Purchase.TABLE_NAME).concat(".")
		// .concat(Purchase.GOODS_FIELD_NAME).concat(" AND ").concat(Purchase.TABLE_NAME).concat(".")
		// .concat(Purchase.STATE_FIELD_NAME).concat(" = '").concat(PurchaseState.ENTERED.toString())
		// .concat("'").concat(" AND ").concat(Purchase.TABLE_NAME).concat(".")
		// .concat(Purchase.DELETED_FIELD_NAME).concat(" IS NULL ORDER BY ").concat(Goods.TABLE_NAME)
		// .concat(".").concat(Goods.NAME_FIELD_NAME);

		final GenericRawResults<String[]> grr = this.queryRaw(rawQuery);
		final AndroidDatabaseResults res = (AndroidDatabaseResults) grr.closeableIterator()
				.getRawResults();
		final Cursor curs = res.getRawCursor();
		curs.registerDataSetObserver(new DataSetObserver() {
			private boolean isClosed = false;

			@Override
			public void onInvalidated() {
				Log.d("GoodsDao", "DataSetObserver.onInvalidated");
				if (!isClosed) {
					isClosed = true;
					try {
						grr.close();
						res.close();
					} catch (SQLException e) {
						Log.e(TAG, "Error while closing DB results", e);
					}
				}
			}
		});
		return curs;
	}

	/**
	 * Checks if Good with the same name already exists in the DB
	 */
	@SuppressWarnings("unchecked")
	public boolean isNameAlreadyExists(String name, Long id) throws SQLException {
		QueryBuilder<Goods, Long> query = queryBuilder();
		query.setCountOf(true);
		Where<Goods, Long> w = query.where();
		w.and(w.eq(Goods.NAME_FIELD_NAME, name), w.isNull(Goods.DELETED_FIELD_NAME));

		if (id != null)
			w.and().ne(Goods.ID_FIELD_NAME, id);

		PreparedQuery<Goods> statement = query.prepare();
		return countOf(statement) > 0;
	}

	@SuppressWarnings("unchecked")
	public List<Goods> getGoodsByCategory(GoodsCategory category) throws SQLException {
		QueryBuilder<Goods, Long> query = queryBuilder();
		Where<Goods, Long> w = query.where();
		w.and(w.eq(Goods.CATEGORY_FIELD_NAME, category), w.isNull(Goods.DELETED_FIELD_NAME));

		PreparedQuery<Goods> statement = query.prepare();
		return query(statement);
	}
}
