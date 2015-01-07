package com.furdey.shopping.content;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.content.model.Unit;
import com.furdey.shopping.contentproviders.GoodsContentProvider;
import com.furdey.shopping.contentproviders.GoodsContentProvider.Columns;

public class GoodsUtils {

	private static final int UNDEFINED = -1;

	/**
	 * Constructs model from the current cursor position
	 */
	public static Goods fromCursor(Cursor cursor) {
		Goods model = new Goods();

		int field = cursor.getColumnIndex(GoodsContentProvider.Columns._id.toString());
		if (field != UNDEFINED)
			model.setId(cursor.getLong(field));

		field = cursor.getColumnIndex(GoodsContentProvider.Columns.NAME.toString());
		if (field != UNDEFINED)
			model.setName(cursor.getString(field));

		field = cursor.getColumnIndex(GoodsContentProvider.Columns.DEFAULTUNITS_ID.toString());
		if (field != UNDEFINED) {
			Unit unit = new Unit();
			unit.setId(cursor.getLong(field));
			model.setDefaultUnits(unit);
		}

		field = cursor.getColumnIndex(GoodsContentProvider.Columns.CATEGORY_ID.toString());
		if (field != UNDEFINED) {
			GoodsCategory category = new GoodsCategory();
			category.setId(cursor.getLong(field));

			field = cursor.getColumnIndex(GoodsContentProvider.Columns.CATEGORY_NAME.toString());
			if (field != UNDEFINED) {
				category.setName(cursor.getString(field));
			}

			field = cursor.getColumnIndex(GoodsContentProvider.Columns.ICON.toString());
			if (field != UNDEFINED) {
				category.setIcon(cursor.getString(field));
			}

			model.setCategory(category);
		}

		return model;
	}

	private static final String[] goodsListProjection = new String[] {
			GoodsContentProvider.Columns._id.toString(), GoodsContentProvider.Columns.NAME.toString(),
			GoodsContentProvider.Columns.DEFAULTUNITS_ID.toString(),
			GoodsContentProvider.Columns.CATEGORY_ID.toString(),
			GoodsContentProvider.Columns.CATEGORY_NAME.toString(),
			GoodsContentProvider.Columns.ICON.toString(),
			GoodsContentProvider.Columns.PURCHASE_ID.toString(),
			GoodsContentProvider.Columns.PURCHASE_DELAYED.toString() };

	public static CursorLoader getGoodsLoader(Context context, String[] projection, String selection,
			String[] selectionArgs, String orderBy) {
		return new CursorLoader(context, GoodsContentProvider.GOODS_URI, projection, selection,
				selectionArgs, orderBy);
	}

	@SuppressLint("DefaultLocale")
	public static CursorLoader getGoodsLoader(Context context, String filter) {
		String selection = null;
		String[] selectionArgs = null;

		if (filter != null && filter.trim().length() > 0) {
			selection = Columns.NAME_LOWER.getDbName() + " LIKE ?";
			selectionArgs = new String[] { "%" + filter.toLowerCase() + "%" };
		}

		return getGoodsLoader(context, goodsListProjection, selection, selectionArgs,
				GoodsContentProvider.Columns.NAME.toString() + " COLLATE NOCASE");
	}

	@SuppressLint("DefaultLocale")
	public static CursorLoader getExactGoodsLoader(Context context, String goodsName) {
		String selection = Columns.NAME_LOWER.getDbName() + " LIKE ?";
		String[] selectionArgs = new String[] { goodsName != null ? goodsName.toLowerCase() : "" };
		return getGoodsLoader(context, goodsListProjection, selection, selectionArgs, null);
	}

	public static ContentValues getContentValues(Goods goods, boolean includeId) {
		ContentValues contentValues = new ContentValues();

		if (goods.getId() != null && includeId)
			contentValues.put(GoodsContentProvider.Columns._id.toString(), goods.getId());

		if (goods.getDefaultUnits() != null && goods.getDefaultUnits().getId() != null)
			contentValues.put(GoodsContentProvider.Columns.DEFAULTUNITS_ID.toString(), goods
					.getDefaultUnits().getId());

		if (goods.getName() != null) {
            contentValues.put(GoodsContentProvider.Columns.NAME.toString(), goods.getName());
            contentValues.put(Columns.NAME_LOWER.toString(), goods.getName().toLowerCase());
        }

		if (goods.getCategory() != null && goods.getCategory().getId() != null)
			contentValues.put(GoodsContentProvider.Columns.CATEGORY_ID.toString(), goods.getCategory()
					.getId());

		return contentValues;
	}

	public static Cursor getGoods(Context context, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return context.getContentResolver().query(GoodsContentProvider.GOODS_URI, projection,
				selection, selectionArgs, sortOrder);
	}

	public static Goods getGoodsById(Context context, String[] projection, long id) {
		Cursor cursor = context.getContentResolver().query(
				ContentUris.withAppendedId(GoodsContentProvider.GOODS_URI, id), projection, null, null,
				null);

		if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }

            return null;
        }

		Goods goods = fromCursor(cursor);
		cursor.close();
		return goods;
	}

	public static Goods getGoodsById(Context context, long id) {
		return getGoodsById(context, goodsListProjection, id);
	}

	public static Cursor getGoodsByName(Context context, String[] projection, String name,
			String sortOrder) {
		return getGoods(context, projection, GoodsContentProvider.Columns.NAME.getDbName() + "=?",
				new String[] { name }, sortOrder);
	}

	public static Uri saveGoods(Context context, Goods goods) {
		// create a new category if it does not exist or update an existing one
		GoodsCategory category = goods.getCategory();

        if (category != null) {
            if (category.getId() == null) {
                Uri categoryUri = GoodsCategoriesUtils.saveGoodsCategory(context, category);
                long categoryId = ContentUris.parseId(categoryUri);
                category.setId(categoryId);
            } else {
                GoodsCategory oldCategory = GoodsCategoriesUtils.getGoodsCategoryById(context, category.getId());

                if (oldCategory.getName().compareTo(category.getName()) != 0) {
                    GoodsCategoriesUtils.saveGoodsCategory(context, category);
                }
            }
        }

		// save a goods
		if (goods.getId() == null) {
			return context.getContentResolver().insert(GoodsContentProvider.GOODS_URI,
					getContentValues(goods, false));
		} else {
            context.getContentResolver().update(
                    ContentUris.withAppendedId(GoodsContentProvider.GOODS_URI, goods.getId()),
                    getContentValues(goods, false), null, null);
			return GoodsContentProvider.GOODS_URI;
		}
	}

	public static int deleteGoods(Context context, long goodsId) {
		return context.getContentResolver().delete(
				ContentUris.withAppendedId(GoodsContentProvider.GOODS_URI, goodsId), null, null);
	}

//    public static int isNameLowerExist(Context context) {
//        if (SearchUtils.isSearchFieldExist(context, Columns.NAME_LOWER.toString(),
//                new SearchUtils.ObjectsProvider() {
//            @Override
//            public Cursor getObjects(Context context, String[] projection, String selection) {
//                return getGoods(context, projection, selection, null, null);
//            }
//        })) {
//            return -1;
//        } else {
//            String[] projection = new String[] { Columns._id.toString(), Columns.NAME.toString() };
//            Cursor cursor = getGoods(context, projection, null, null, null);
//            int recordsCount = cursor.getCount();
//            cursor.close();
//            return recordsCount;
//        }
//    }
//
//    public static void addNameLowerIfNotExists(Context context) {
//        System.out.println("GoodsUtils.addNameLowerIfNotExists");
//        SearchUtils.addSearchFieldIfNotExists(context, Columns.NAME_LOWER.toString(), projection,
//                new SearchUtils.ObjectsProvider() {
//                    @Override
//                    public Cursor getObjects(Context context, String[] projection,
//                                             String selection) {
//                        System.out.println("GoodsUtils.getObjects");
//                        return getGoods(context, projection, selection, null, null);
//                    }
//
//                    @Override
//                    public void saveObject(Context context, Cursor c) {
//                        System.out.println("GoodsUtils.saveObject");
//                        Goods g = fromCursor(c);
//                        saveGoods(context, g);
//                    }
//
//                    @Override
//                    public void addColumn(Context context, String columnName) {
//                        System.out.println("GoodsUtils.addColumn");
//                        GoodsContentProvider.addColumn(context, columnName, "VARCHAR");
//                    }
//                });
//        System.out.println("GoodsUtils.addNameLowerIfNotExists finished");
//    }
}
