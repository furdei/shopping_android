package com.furdey.shopping.dao;

import java.sql.SQLException;
import java.util.List;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.util.Log;

import com.furdey.shopping.content.model.Unit;
import com.furdey.shopping.content.model.Unit.UnitType;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

public class UnitsDao extends BaseDao<Unit, Integer> {
	
	private static final String TAG = UnitsDao.class.getSimpleName();

	public UnitsDao(ConnectionSource connectionSource, Class<Unit> dataClass) throws SQLException{
		super(connectionSource, dataClass);
	}
	
	/**
	 * Constructs model from the current cursor position
	 */
	public static Unit fromCursor(Cursor cursor) {
		Unit model = new Unit();
		BaseDao.fromCursor(cursor, model);

		int field = cursor.getColumnIndexOrThrow(Unit.NAME_FIELD_NAME);
		model.setName(cursor.getString(field));
		
		field = cursor.getColumnIndexOrThrow(Unit.DESCR_FIELD_NAME);
		model.setDescr(cursor.getString(field));
		
		field = cursor.getColumnIndexOrThrow(Unit.DECIMALS_FIELD_NAME);
		model.setDecimals(cursor.getInt(field));
		
		field = cursor.getColumnIndexOrThrow(Unit.UNITTYPE_FIELD_NAME);
		model.setUnitType(UnitType.valueOf(cursor.getString(field)));
		
		field = cursor.getColumnIndexOrThrow(Unit.ISDEFAULT_FIELD_NAME);
		model.setIsDefault(cursor.getInt(field) != 0);
		
		return model;
	}

	public Cursor fetchAll() throws SQLException {
		QueryBuilder<Unit, Integer> query = queryBuilder();
		query.where()
			.isNull(Unit.DELETED_FIELD_NAME);
		query.orderBy(Unit.ISDEFAULT_FIELD_NAME, false);
		PreparedQuery<Unit> statement = query.prepare();
		final AndroidDatabaseResults res = (AndroidDatabaseResults) this.iterator(statement).getRawResults();
		final Cursor curs = res.getRawCursor(); 
		curs.registerDataSetObserver(new DataSetObserver() {
			private boolean isClosed = false;
			
			@Override
			public void onInvalidated() {
				Log.d("GoodsDao", "DataSetObserver.onInvalidated");
				if (!isClosed) {
					isClosed = true;
					try {
						res.close();
					} catch (Exception e) {
						Log.e(TAG, "Error while closing DB results", e);
					}
				}
			}
		});
		return curs;

	}
	
	/**
	 * Checks if Unit with the same name already exists in the DB
	 */
	@SuppressWarnings("unchecked")
	public boolean isNameAlreadyExists(String name, Integer id) throws SQLException {
		QueryBuilder<Unit, Integer> query = queryBuilder();
		query.setCountOf(true);
		Where<Unit, Integer> w = query.where();
		w.and(
			w.eq(Unit.NAME_FIELD_NAME, name),
			w.isNull(Unit.DELETED_FIELD_NAME)
		);
		
		if (id != null)
			w.and().ne(Unit.ID_FIELD_NAME, id);

		PreparedQuery<Unit> statement = query.prepare();
		return countOf(statement) > 0;
	}
	
	
	/**
	 * Checks if Unit with the same description already exists in the DB
	 */
	@SuppressWarnings("unchecked")
	public boolean isDescrAlreadyExists(String descr, Integer id) throws SQLException {
		QueryBuilder<Unit, Integer> query = queryBuilder();
		query.setCountOf(true);
		Where<Unit, Integer> w = query.where();
		w.and(
			w.eq(Unit.DESCR_FIELD_NAME, descr),
			w.isNull(Unit.DELETED_FIELD_NAME)
		);
		
		if (id != null)
			w.and().ne(Unit.ID_FIELD_NAME, id);

		PreparedQuery<Unit> statement = query.prepare();
		return countOf(statement) > 0;
	}
	
	@SuppressWarnings("unchecked")
	public Unit getDefaultUnit(Unit.UnitType unitType) throws SQLException {
		QueryBuilder<Unit, Integer> query = queryBuilder();
		Where<Unit, Integer> w = query.where();
		w.and(
			w.eq(Unit.UNITTYPE_FIELD_NAME, unitType),
			w.eq(Unit.ISDEFAULT_FIELD_NAME, true),
			w.isNull(Unit.DELETED_FIELD_NAME)
		);
		
		PreparedQuery<Unit> statement = query.prepare();
		List<Unit> res = query(statement);

		if (res == null || res.size() == 0)
			return null;
		
		return res.get(0);
	}
}
