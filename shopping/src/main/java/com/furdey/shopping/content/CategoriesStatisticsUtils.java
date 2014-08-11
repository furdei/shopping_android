package com.furdey.shopping.content;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.furdey.shopping.content.model.CategoriesStatistics;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.contentproviders.CategoriesStatisticsContentProvider;
import com.furdey.shopping.contentproviders.CategoriesStatisticsContentProvider.Columns;

public class CategoriesStatisticsUtils {

	private static final int UNDEFINED = -1;

	public static CategoriesStatistics fromCursor(Cursor cursor) {
		CategoriesStatistics model = new CategoriesStatistics();

		int field = cursor.getColumnIndex(Columns._id.toString());
		if (field != UNDEFINED)
			model.setId(cursor.getLong(field));

		field = cursor.getColumnIndex(Columns.PREV_CATEGORY_ID.toString());
		if (field != UNDEFINED) {
			GoodsCategory category = new GoodsCategory();
			category.setId(cursor.getLong(field));
			model.setPrevCategory(category);
		}

		field = cursor.getColumnIndex(Columns.NEXT_CATEGORY_ID.toString());
		if (field != UNDEFINED) {
			GoodsCategory category = new GoodsCategory();
			category.setId(cursor.getLong(field));
			model.setNextCategory(category);
		}

		field = cursor.getColumnIndex(Columns.BUY_COUNT.toString());
		if (field != UNDEFINED) {
			model.setBuyCount(cursor.getInt(field));
		}

		return model;
	}

	public static ContentValues getContentValues(CategoriesStatistics categoriesStatistics,
			boolean includeId) {
		ContentValues contentValues = new ContentValues();

		if (categoriesStatistics.getId() != null && includeId) {
			contentValues.put(Columns._id.toString(), categoriesStatistics.getId());
		}

		if (categoriesStatistics.getPrevCategory() != null
				&& categoriesStatistics.getPrevCategory().getId() != null) {
			contentValues.put(Columns.PREV_CATEGORY_ID.toString(), categoriesStatistics.getPrevCategory()
					.getId());
		}

		if (categoriesStatistics.getNextCategory() != null
				&& categoriesStatistics.getNextCategory().getId() != null) {
			contentValues.put(Columns.NEXT_CATEGORY_ID.toString(), categoriesStatistics.getNextCategory()
					.getId());
		}

		if (categoriesStatistics.getBuyCount() != null) {
			contentValues.put(Columns.BUY_COUNT.toString(), categoriesStatistics.getBuyCount());
		}

		return contentValues;
	}

	public static Cursor getCategoriesStatistics(Context context, Long prevCategoryId,
			Long nextCategoryId) {
		String[] projection = new String[] { Columns._id.toString(), Columns.BUY_COUNT.toString() };
		String prevComparison = (prevCategoryId != null) ? "=?" : " IS NULL";
		String nextComparison = (nextCategoryId != null) ? "=?" : " IS NULL";
		String selection = Columns.PREV_CATEGORY_ID.getDbName() + prevComparison + " AND "
				+ Columns.NEXT_CATEGORY_ID.getDbName() + nextComparison;
		String[] selectionArgs;

		if (prevCategoryId != null && nextCategoryId != null) {
			selectionArgs = new String[] { prevCategoryId.toString(), nextCategoryId.toString() };
		} else if (prevCategoryId != null) {
			selectionArgs = new String[] { prevCategoryId.toString() };
		} else {
			selectionArgs = new String[] { nextCategoryId.toString() };
		}

		return context.getContentResolver().query(
				CategoriesStatisticsContentProvider.CATEGORIES_STATISTICS_URI, projection, selection,
				selectionArgs, null);
	}

	public static void updateCategoriesStatistics(Context context, Long prevGoodsId,
			Long nextGoodsId, int count) {
		Cursor categoriesStatisticsCursor = getCategoriesStatistics(context, prevGoodsId, nextGoodsId);
		CategoriesStatistics categoriesStatistics;

		if (categoriesStatisticsCursor == null || !categoriesStatisticsCursor.moveToFirst())
			categoriesStatistics = null;
		else
			categoriesStatistics = fromCursor(categoriesStatisticsCursor);

		if (categoriesStatistics == null) {
			// insert a new row
			categoriesStatistics = new CategoriesStatistics();
			GoodsCategory prevCategory = new GoodsCategory();
			prevCategory.setId(prevGoodsId);
			categoriesStatistics.setPrevCategory(prevCategory);
			GoodsCategory nextCategory = new GoodsCategory();
			nextCategory.setId(nextGoodsId);
			categoriesStatistics.setNextCategory(nextCategory);
			categoriesStatistics.setBuyCount(1);

			context.getContentResolver().insert(
					CategoriesStatisticsContentProvider.CATEGORIES_STATISTICS_URI,
					getContentValues(categoriesStatistics, false));
		} else {
			// update an existing one
			categoriesStatistics.setBuyCount(categoriesStatistics.getBuyCount() + count);

            if (categoriesStatistics.getBuyCount() < 0)
                categoriesStatistics.setBuyCount(0);

			context.getContentResolver().update(
					ContentUris.withAppendedId(CategoriesStatisticsContentProvider.CATEGORIES_STATISTICS_URI,
							categoriesStatistics.getId()), getContentValues(categoriesStatistics, false), null,
					null);
		}
	}

}
