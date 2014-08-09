package com.furdey.shopping.dao;

import java.sql.SQLException;

import com.furdey.shopping.content.model.CategoriesStatistics;
import com.furdey.shopping.content.model.GoodsCategory;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

public class CategoriesStatisticsDao extends
		BaseDao<CategoriesStatistics, Integer> {

	public CategoriesStatisticsDao(ConnectionSource connectionSource,
			Class<CategoriesStatistics> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	@SuppressWarnings("unchecked")
	public CategoriesStatistics getStatistics(GoodsCategory prev,
			GoodsCategory next) throws SQLException {
		QueryBuilder<CategoriesStatistics, Integer> query = queryBuilder();
		Where<CategoriesStatistics, Integer> w = query.where();
		w.and(
				(prev == null) ? w
						.isNull(CategoriesStatistics.PREV_CATEGORY_FIELD_NAME) : w.eq(
						CategoriesStatistics.PREV_CATEGORY_FIELD_NAME, prev), w.eq(
						CategoriesStatistics.NEXT_CATEGORY_FIELD_NAME, next), w
						.isNull(CategoriesStatistics.DELETED_FIELD_NAME));

		PreparedQuery<CategoriesStatistics> statement = query.prepare();
		CategoriesStatistics stat = queryForFirst(statement);

		if (stat == null) {
			stat = new CategoriesStatistics();
			stat.setPrevCategory(prev);
			stat.setNextCategory(next);
			stat.setBuyCount(0);
		}

		return stat;
	}
}
