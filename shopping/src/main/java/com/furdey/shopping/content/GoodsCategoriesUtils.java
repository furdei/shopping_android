package com.furdey.shopping.content;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

import com.furdey.shopping.content.bridge.CachingFactory;
import com.furdey.shopping.content.bridge.ContentUri;
import com.furdey.shopping.content.bridge.ContentUriFactory;
import com.furdey.shopping.content.bridge.ContentValuesFactory;
import com.furdey.shopping.content.bridge.Factory;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.contentproviders.GoodsCategoriesContentProvider;
import com.furdey.shopping.contentproviders.GoodsCategoriesContentProvider.Columns;

public class GoodsCategoriesUtils {

	private static final int UNDEFINED = -1;

	private static Factory<ContentValues> contentValuesFactory = new ContentValuesFactory();
    private static Factory<Uri> uriFactory = new CachingFactory<Uri>() {
        @Override
        protected Uri createInstance() {
            return GoodsCategoriesContentProvider.GOODS_CATEGORIES_URI;
        }
    };
    private static Factory<ContentUri> contentUriFactory = new ContentUriFactory();

	public static void setContentValuesFactory(Factory<ContentValues> contentValuesFactory) {
		GoodsCategoriesUtils.contentValuesFactory = contentValuesFactory;
	}

    public static void setUriFactory(Factory<Uri> uriFactory) {
        GoodsCategoriesUtils.uriFactory = uriFactory;
    }

    public static void setContentUriFactory(Factory<ContentUri> contentUriFactory) {
        GoodsCategoriesUtils.contentUriFactory = contentUriFactory;
    }

    /**
	 * Constructs model from the current cursor position
	 */
	public static GoodsCategory fromCursor(Cursor cursor) {
		GoodsCategory model = new GoodsCategory();

		int field = cursor.getColumnIndex(GoodsCategoriesContentProvider.Columns._id.toString());
		if (field != UNDEFINED)
			model.setId(cursor.getLong(field));

		field = cursor.getColumnIndex(GoodsCategoriesContentProvider.Columns.NAME.toString());
		if (field != UNDEFINED)
			model.setName(cursor.getString(field));

		field = cursor.getColumnIndex(GoodsCategoriesContentProvider.Columns.DESCR.toString());
		if (field != UNDEFINED)
			model.setDescr(cursor.getString(field));

		field = cursor.getColumnIndex(GoodsCategoriesContentProvider.Columns.ICON.toString());
		if (field != UNDEFINED)
			model.setIcon(cursor.getString(field));

		return model;
	}

	private static final String[] goodsCategoriesListProjection = new String[] {
			GoodsCategoriesContentProvider.Columns._id.toString(),
			GoodsCategoriesContentProvider.Columns.NAME.toString(),
			GoodsCategoriesContentProvider.Columns.DESCR.toString(),
			GoodsCategoriesContentProvider.Columns.ICON.toString() };

	public static CursorLoader getGoodsCategoriesLoader(Context context, String[] projection,
			String selection, String[] selectionArgs, String orderBy) {
		return new CursorLoader(context, uriFactory.getInstance(),
				projection, selection, selectionArgs, orderBy);
	}

	@SuppressLint("DefaultLocale")
	public static CursorLoader getGoodsCategoriesLoader(Context context, String filter) {
		String selection = null;
		String[] selectionArgs = null;

		if (filter != null && filter.trim().length() > 0) {
			selection = "LOWER(" + Columns.NAME_LOWER.toString() + ") LIKE ?";
			selectionArgs = new String[] { "%" + filter.toLowerCase() + "%" };
		}

		return getGoodsCategoriesLoader(context, goodsCategoriesListProjection, selection,
				selectionArgs, null);
	}

	public static Cursor getGoodsCategories(Context context, String[] projection, String selection,
			String[] selectionArgs, String orderBy) {
		return context.getContentResolver().query(uriFactory.getInstance(),
                projection, selection, selectionArgs, orderBy);
	}

	public static Cursor getGoodsCategoriesByName(Context context, String name) {
		return getGoodsCategories(context, goodsCategoriesListProjection,
                Columns.NAME_LOWER.toString() + " = ?", new String[]{name.toLowerCase()},
                null);
	}

    public static GoodsCategory getGoodsCategoryById(Context context, long id) {
        Cursor cursor = getGoodsCategories(context, goodsCategoriesListProjection,
                Columns._id.toString() + " = ?", new String[] { Long.toString(id) },
                null);
        GoodsCategory goodsCategory = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                goodsCategory = fromCursor(cursor);
            }

            cursor.close();
        }

        return goodsCategory;
    }

    public static ContentValues getContentValues(GoodsCategory goodsCategory, boolean includeId) {
		ContentValues contentValues = contentValuesFactory.getInstance();

		if (goodsCategory.getId() != null && includeId)
			contentValues.put(GoodsCategoriesContentProvider.Columns._id.toString(),
					goodsCategory.getId());

		if (goodsCategory.getDescr() != null)
			contentValues.put(GoodsCategoriesContentProvider.Columns.DESCR.toString(),
					goodsCategory.getDescr());

		if (goodsCategory.getName() != null) {
            contentValues.put(GoodsCategoriesContentProvider.Columns.NAME.toString(),
                    goodsCategory.getName());
            contentValues.put(GoodsCategoriesContentProvider.Columns.NAME_LOWER.toString(),
                    goodsCategory.getName().toLowerCase());
        }

		if (goodsCategory.getIcon() != null)
			contentValues.put(GoodsCategoriesContentProvider.Columns.ICON.toString(), goodsCategory
					.getIcon().toString());

		return contentValues;
	}

	public static Uri saveGoodsCategory(Context context, GoodsCategory goodsCategory) {
		if (goodsCategory.getId() == null) {
			return context.getContentResolver().insert(
                    uriFactory.getInstance(),
					getContentValues(goodsCategory, false));
		} else {
            context.getContentResolver().update(
                    contentUriFactory.getInstance().withAppendedId(uriFactory.getInstance(),
                            goodsCategory.getId()), getContentValues(goodsCategory, false), null, null);
			return uriFactory.getInstance();
		}
	}

	public static int deleteGoodsCategory(Context context, long goodsCategoryId) {
		return context.getContentResolver().delete(
                contentUriFactory.getInstance().withAppendedId(uriFactory.getInstance(),
                        goodsCategoryId), null, null);
	}

}
