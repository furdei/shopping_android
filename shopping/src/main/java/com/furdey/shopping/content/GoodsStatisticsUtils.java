package com.furdey.shopping.content;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsStatistics;
import com.furdey.shopping.contentproviders.GoodsStatisticsContentProvider;
import com.furdey.shopping.contentproviders.GoodsStatisticsContentProvider.Columns;

public class GoodsStatisticsUtils {

	private static final int UNDEFINED = -1;

	public static GoodsStatistics fromCursor(Cursor cursor) {
		GoodsStatistics model = new GoodsStatistics();

		int field = cursor.getColumnIndex(Columns._id.toString());
		if (field != UNDEFINED)
			model.setId(cursor.getLong(field));

		field = cursor.getColumnIndex(Columns.PREV_GOOD_ID.toString());
		if (field != UNDEFINED) {
			Goods goods = new Goods();
			goods.setId(cursor.getLong(field));
			model.setPrevGood(goods);
		}

		field = cursor.getColumnIndex(Columns.NEXT_GOOD_ID.toString());
		if (field != UNDEFINED) {
			Goods goods = new Goods();
			goods.setId(cursor.getLong(field));
			model.setNextGood(goods);
		}

		field = cursor.getColumnIndex(Columns.BUY_COUNT.toString());
		if (field != UNDEFINED) {
			model.setBuyCount(cursor.getInt(field));
		}

		return model;
	}

	public static ContentValues getContentValues(GoodsStatistics goodsStatistics, boolean includeId) {
		ContentValues contentValues = new ContentValues();

		if (goodsStatistics.getId() != null && includeId) {
			contentValues.put(Columns._id.toString(), goodsStatistics.getId());
		}

		if (goodsStatistics.getPrevGood() != null && goodsStatistics.getPrevGood().getId() != null) {
			contentValues.put(Columns.PREV_GOOD_ID.toString(), goodsStatistics.getPrevGood().getId());
		}

		if (goodsStatistics.getNextGood() != null && goodsStatistics.getNextGood().getId() != null) {
			contentValues.put(Columns.NEXT_GOOD_ID.toString(), goodsStatistics.getNextGood().getId());
		}

		if (goodsStatistics.getBuyCount() != null) {
			contentValues.put(Columns.BUY_COUNT.toString(), goodsStatistics.getBuyCount());
		}

		return contentValues;
	}

	public static Cursor getGoodsStatistics(Context context, Long prevGoodsId, Long nextGoodsId) {
		String[] projection = new String[] { GoodsStatisticsContentProvider.Columns._id.toString(),
				GoodsStatisticsContentProvider.Columns.BUY_COUNT.toString() };
		String prevComparison = (prevGoodsId != null) ? "=?" : " IS NULL";
		String nextComparison = (nextGoodsId != null) ? "=?" : " IS NULL";
		String selection = GoodsStatisticsContentProvider.Columns.PREV_GOOD_ID.getDbName()
				+ prevComparison + " AND "
				+ GoodsStatisticsContentProvider.Columns.NEXT_GOOD_ID.getDbName() + nextComparison;
		String[] selectionArgs;

		if (prevGoodsId != null && nextGoodsId != null) {
			selectionArgs = new String[] { prevGoodsId.toString(), nextGoodsId.toString() };
		} else if (prevGoodsId != null) {
			selectionArgs = new String[] { prevGoodsId.toString() };
		} else {
			selectionArgs = new String[] { nextGoodsId.toString() };
		}

		return context.getContentResolver().query(GoodsStatisticsContentProvider.GOODS_STATISTICS_URI,
				projection, selection, selectionArgs, null);
	}

	public static void updateGoodsStatistics(Context context, Long prevGoodsId, Long nextGoodsId,
			int count) {
		Cursor goodsStatisticsCursor = getGoodsStatistics(context, prevGoodsId, nextGoodsId);
		GoodsStatistics goodsStatistics;

		if (goodsStatisticsCursor == null || !goodsStatisticsCursor.moveToFirst())
			goodsStatistics = null;
		else
			goodsStatistics = fromCursor(goodsStatisticsCursor);

		if (goodsStatistics == null) {
			// insert a new row
			goodsStatistics = new GoodsStatistics();
			Goods prevGoods = new Goods();
			prevGoods.setId(prevGoodsId);
			goodsStatistics.setPrevGood(prevGoods);
			Goods nextGoods = new Goods();
			nextGoods.setId(nextGoodsId);
			goodsStatistics.setNextGood(nextGoods);
			goodsStatistics.setBuyCount(1);

			context.getContentResolver().insert(GoodsStatisticsContentProvider.GOODS_STATISTICS_URI,
					getContentValues(goodsStatistics, false));
		} else {
			// update an existing one
			goodsStatistics.setBuyCount(goodsStatistics.getBuyCount() + count);

            if (goodsStatistics.getBuyCount() < 0)
                goodsStatistics.setBuyCount(0);

            context.getContentResolver().update(
					ContentUris.withAppendedId(GoodsStatisticsContentProvider.GOODS_STATISTICS_URI,
							goodsStatistics.getId()), getContentValues(goodsStatistics, false), null, null);
		}
	}

}
