package com.furdey.shopping.dao;

import java.sql.SQLException;

import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsStatistics;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

public class GoodsStatisticsDao extends BaseDao<GoodsStatistics, Integer> {

	public GoodsStatisticsDao(ConnectionSource connectionSource,
			Class<GoodsStatistics> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	@SuppressWarnings("unchecked")
	public GoodsStatistics getStatistics(Goods prev, Goods next)
			throws SQLException {
		QueryBuilder<GoodsStatistics, Integer> query = queryBuilder();
		Where<GoodsStatistics, Integer> w = query.where();
		w.and(
				(prev == null) ? w.isNull(GoodsStatistics.PREV_GOOD_FIELD_NAME) : w.eq(
						GoodsStatistics.PREV_GOOD_FIELD_NAME, prev), w.eq(
						GoodsStatistics.NEXT_GOOD_FIELD_NAME, next), w
						.isNull(GoodsStatistics.DELETED_FIELD_NAME));

		PreparedQuery<GoodsStatistics> statement = query.prepare();
		GoodsStatistics stat = queryForFirst(statement);

		if (stat == null) {
			stat = new GoodsStatistics();
			stat.setPrevGood(prev);
			stat.setNextGood(next);
			stat.setBuyCount(0);
		}

		return stat;
	}
}
