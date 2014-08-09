package com.furdey.shopping.dao;

import java.sql.SQLException;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.util.Log;

import com.furdey.shopping.content.model.GoodsCategory;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

public class GoodsCategoriesDao extends BaseDao<GoodsCategory, Integer> {

	private static final String TAG = GoodsCategoriesDao.class.getSimpleName();

	public GoodsCategoriesDao(ConnectionSource connectionSource, Class<GoodsCategory> dataClass)
			throws SQLException {
		super(connectionSource, dataClass);
	}

	/**
	 * Constructs model from the current cursor position
	 */
	public static GoodsCategory fromCursor(Cursor cursor) {
		GoodsCategory model = new GoodsCategory();
		BaseDao.fromCursor(cursor, model);

		int field = cursor.getColumnIndexOrThrow(GoodsCategory.NAME_FIELD_NAME);
		model.setName(cursor.getString(field));

		field = cursor.getColumnIndexOrThrow(GoodsCategory.DESCR_FIELD_NAME);
		model.setDescr(cursor.getString(field));

		field = cursor.getColumnIndexOrThrow(GoodsCategory.ICON_FIELD_NAME);
		model.setIcon(cursor.getString(field));

		return model;
	}

	@SuppressLint("DefaultLocale")
	public Cursor filterByName(String name) throws SQLException {
		if (name == null)
			name = "%";
		else
			name = "%".concat(name.toLowerCase()).concat("%");

		String rawQuery = "SELECT ".concat(GoodsCategory.ID_FIELD_NAME).concat(", ")
				.concat(GoodsCategory.CHANGED_FIELD_NAME).concat(", ")
				.concat(GoodsCategory.DELETED_FIELD_NAME).concat(", ")
				.concat(GoodsCategory.STANDARD_FIELD_NAME).concat(", ")
				.concat(GoodsCategory.NAME_FIELD_NAME).concat(", ").concat(GoodsCategory.DESCR_FIELD_NAME)
				.concat(", ").concat(GoodsCategory.ICON_FIELD_NAME).concat(" FROM ")
				.concat(GoodsCategory.TABLE_NAME).concat(" WHERE LOWER(")
				.concat(GoodsCategory.NAME_FIELD_NAME).concat(") LIKE '").concat(name).concat("' AND ")
				.concat(GoodsCategory.DELETED_FIELD_NAME).concat(" IS NULL ORDER BY ")
				.concat(GoodsCategory.NAME_FIELD_NAME);

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
	 * Checks if GoodsCategory with the same name already exists in the DB
	 */
	@SuppressWarnings("unchecked")
	public boolean isNameAlreadyExists(String name, Integer id) throws SQLException {
		QueryBuilder<GoodsCategory, Integer> query = queryBuilder();
		query.setCountOf(true);
		Where<GoodsCategory, Integer> w = query.where();
		w.and(w.eq(GoodsCategory.NAME_FIELD_NAME, name), w.isNull(GoodsCategory.DELETED_FIELD_NAME));

		if (id != null)
			w.and().ne(GoodsCategory.ID_FIELD_NAME, id);

		PreparedQuery<GoodsCategory> statement = query.prepare();
		return countOf(statement) > 0;
	}

	/**
	 * Checks if GoodsCategory with the same description already exists in the DB
	 */
	@SuppressWarnings("unchecked")
	public boolean isDescrAlreadyExists(String descr, Integer id) throws SQLException {
		QueryBuilder<GoodsCategory, Integer> query = queryBuilder();
		query.setCountOf(true);
		Where<GoodsCategory, Integer> w = query.where();
		w.and(w.eq(GoodsCategory.DESCR_FIELD_NAME, descr), w.isNull(GoodsCategory.DELETED_FIELD_NAME));

		if (id != null)
			w.and().ne(GoodsCategory.ID_FIELD_NAME, id);

		PreparedQuery<GoodsCategory> statement = query.prepare();
		return countOf(statement) > 0;
	}

}
