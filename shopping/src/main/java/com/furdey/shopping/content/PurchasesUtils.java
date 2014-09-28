package com.furdey.shopping.content;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

import com.furdey.shopping.R;
import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.content.model.Purchase;
import com.furdey.shopping.content.model.Purchase.PurchaseState;
import com.furdey.shopping.content.model.Unit;
import com.furdey.shopping.contentproviders.PurchasesContentProvider;
import com.furdey.shopping.contentproviders.PurchasesContentProvider.Columns;
import com.furdey.shopping.utils.PreferencesManager;
import com.furdey.shopping.utils.PreferencesManager.PurchasesSortOrder;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PurchasesUtils {

	private static final int UNDEFINED = -1;

	/**
	 * Constructs model from the current cursor position
	 */
	public static Purchase fromCursor(Cursor cursor) {
		Purchase model = new Purchase();

		int field = cursor.getColumnIndex(Columns._id.toString());
		if (field != UNDEFINED)
			model.setId(cursor.getLong(field));

		field = cursor.getColumnIndex(Columns.COUNT.toString());
		if (field != UNDEFINED)
			model.setCount(new BigDecimal(cursor.getString(field)));

		field = cursor.getColumnIndex(Columns.DESCR.toString());
		if (field != UNDEFINED)
			model.setDescr(cursor.getString(field));

		Goods good = new Goods();

		field = cursor.getColumnIndex(Columns.GOOD_ID.toString());
		if (field != UNDEFINED)
			good.setId(cursor.getLong(field));
		field = cursor.getColumnIndex(Columns.GOODS_NAME.toString());
		if (field != UNDEFINED)
			good.setName(cursor.getString(field));

		GoodsCategory category = new GoodsCategory();

		field = cursor.getColumnIndex(Columns.GOODSCATEGORY_ID.toString());
		if (field != UNDEFINED)
			category.setId(cursor.getLong(field));
		field = cursor.getColumnIndex(Columns.GOODSCATEGORY_NAME.toString());
		if (field != UNDEFINED)
			category.setName(cursor.getString(field));

		good.setCategory(category);
		model.setGoods(good);

		Unit unit = new Unit();

		field = cursor.getColumnIndex(Columns.UNITS_ID.toString());
		if (field != UNDEFINED)
			unit.setId(cursor.getLong(field));

		field = cursor.getColumnIndex(Columns.UNIT_NAME.toString());
		if (field != UNDEFINED)
			unit.setName(cursor.getString(field));

		model.setUnits(unit);

		field = cursor.getColumnIndex(Columns.STATE.toString());
		if (field != UNDEFINED)
			model.setState(PurchaseState.valueOf(cursor.getString(field)));

		field = cursor.getColumnIndex(Columns.STRDATE.toString());
		if (field != UNDEFINED)
			model.setStrdate(ContentUtils.getDateFromCursor(cursor, field));

		field = cursor.getColumnIndex(Columns.FINDATE.toString());
		if (field != UNDEFINED)
			model.setFindate(ContentUtils.getDateFromCursor(cursor, field));

		return model;
	}

	private static final String[] purchasesListProjection = new String[] {
			PurchasesContentProvider.Columns._id.toString(),
			PurchasesContentProvider.Columns.STATE.toString(),
			PurchasesContentProvider.Columns.GOODS_NAME.toString(),
			PurchasesContentProvider.Columns.GOOD_ID.toString(),
			PurchasesContentProvider.Columns.DESCR.toString(),
			PurchasesContentProvider.Columns.COUNT.toString(),
			PurchasesContentProvider.Columns.UNITS_ID.toString(),
			PurchasesContentProvider.Columns.UNIT_NAME.toString(),
			PurchasesContentProvider.Columns.GOODSCATEGORY_ICON.toString(),
			PurchasesContentProvider.Columns.GOODSCATEGORY_NAME.toString(),
			PurchasesContentProvider.Columns.GOODSCATEGORY_ID.toString() };

	public static CursorLoader getPurchasesLoader(Context context, String sortOrder) {
		return new CursorLoader(context, PurchasesContentProvider.PURCHASES_URI,
				purchasesListProjection, null, null, sortOrder);
	}

	public static Cursor getPurchases(Context context, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return context.getContentResolver().query(PurchasesContentProvider.PURCHASES_URI, projection,
				selection, selectionArgs, sortOrder);
	}

	public static Cursor getActivePurchasesByGoods(Context context, long goodsId) {
		String selection = PurchasesContentProvider.Columns.GOOD_ID.getDbName().concat(" = ? AND ")
				.concat(PurchasesContentProvider.Columns.STATE.getDbName()).concat(" = ? AND ")
				.concat(PurchasesContentProvider.Columns.DELETED.getDbName()).concat(" IS NULL");
		String[] selectionArgs = new String[] { Long.toString(goodsId),
				PurchaseState.ENTERED.toString() };
        return getPurchases(context, purchasesListProjection, selection, selectionArgs, null);
	}

    public static Purchase getPurchaseById(Context context, long purchaseId) {
        String selection = Columns._id.getDbName() + "=?";
        String[] selectionArgs = new String[] { Long.toString(purchaseId) };

        Cursor cursor = getPurchases(context, purchasesListProjection, selection, selectionArgs, null);

        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }

            return null;
        }

        Purchase purchase = fromCursor(cursor);
        cursor.close();

        return purchase;
    }

    public static ContentValues getContentValues(Purchase purchase, boolean includeId) {
		ContentValues contentValues = new ContentValues();

		if (purchase.getId() != null && includeId)
			contentValues.put(PurchasesContentProvider.Columns._id.toString(), purchase.getId());

		if (purchase.getCount() != null)
			contentValues.put(PurchasesContentProvider.Columns.COUNT.toString(), purchase.getCount()
					.toPlainString());

		if (purchase.getDescr() != null)
			contentValues.put(PurchasesContentProvider.Columns.DESCR.toString(), purchase.getDescr());

		if (purchase.getGoods() != null && purchase.getGoods().getId() != null)
			contentValues.put(PurchasesContentProvider.Columns.GOOD_ID.toString(), purchase.getGoods()
					.getId());

		if (purchase.getUnits() != null && purchase.getUnits().getId() != null)
			contentValues.put(PurchasesContentProvider.Columns.UNITS_ID.toString(), purchase.getUnits()
					.getId());

		if (purchase.getState() != null)
			contentValues.put(PurchasesContentProvider.Columns.STATE.toString(), purchase.getState()
					.toString());

		if (purchase.getStrdate() != null)
			contentValues.put(PurchasesContentProvider.Columns.STRDATE.toString(), ContentUtils
					.getDateMidnight(purchase.getStrdate()));

		if (purchase.getFindate() != null)
			contentValues.put(PurchasesContentProvider.Columns.FINDATE.toString(), ContentUtils
					.getDateMidnight(purchase.getFindate()));

		contentValues.put(PurchasesContentProvider.Columns.SORTORDER.toString(),
				purchase.getSortorder());

		return contentValues;
	}

	public static Uri savePurchase(Context context, Purchase purchase) {
		// create a new goods if it does not exist or update an existing one
		Goods goods = purchase.getGoods();
        goods.setDefaultUnits(purchase.getUnits());

		if (goods.getId() == null) {
            Uri goodsUri = GoodsUtils.saveGoods(context, goods);
			long goodsId = ContentUris.parseId(goodsUri);
			goods.setId(goodsId);
		} else {
            Goods oldGoods = GoodsUtils.getGoodsById(context, goods.getId());

            if (oldGoods.getCategory().getId().compareTo(goods.getCategory().getId()) != 0 ||
                    oldGoods.getDefaultUnits().getId().compareTo(goods.getDefaultUnits().getId()) != 0) {
                GoodsUtils.saveGoods(context, goods);
            }
        }

        // here we have to update purchases statistics
		updatePurchaseStatistics(context, purchase);

		// save a purchase
		if (purchase.getId() == null) {
			return context.getContentResolver().insert(PurchasesContentProvider.PURCHASES_URI,
					getContentValues(purchase, false));
		} else {
			context.getContentResolver().update(
					ContentUris.withAppendedId(PurchasesContentProvider.PURCHASES_URI, purchase.getId()),
					getContentValues(purchase, false), null, null);
			return PurchasesContentProvider.PURCHASES_URI;
		}
	}

	public static int deletePurchase(Context context, long purchaseId) {
		return context.getContentResolver().delete(
				ContentUris.withAppendedId(PurchasesContentProvider.PURCHASES_URI, purchaseId), null, null);
	}

	public static Purchase putPurchaseOff(Purchase purchase) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, 1);
		purchase.setStrdate(c.getTime());
		return purchase;
	}

    public static Purchase revertState(Purchase purchase) throws ParseException {
        purchase.setState(purchase.getState() == PurchaseState.ENTERED ? PurchaseState.ACCEPTED
                : PurchaseState.ENTERED);

        purchase.setFindate(purchase.getState() == PurchaseState.ACCEPTED ?
                ContentUtils.getDateWoTime(new Date()) :
                ContentUtils.getDateFormat().parse(ContentUtils.DATE_INFINITY));

        return purchase;
    }

	public static String getPurchasesSortOrder(Context context) {
		PurchasesSortOrder purchasesSortOrder = PreferencesManager.getPurchasesSortOrder(context);
		String sortOrder = PurchasesContentProvider.Columns.STATE.getDbName().concat(" DESC, ")
				.concat(purchasesSortOrder.getSortOrder());
		return sortOrder;
	}

    public static List<Purchase> getPurchasesList(Context context) {
        String[] projection = new String[] { Columns.STATE.toString(), Columns.GOODS_NAME.toString(),
                Columns.COUNT.toString(), Columns.UNIT_NAME.toString() };
        String selection = Columns.STATE.getDbName() + "=?";
        String[] selectionArgs = new String[] { PurchaseState.ENTERED.toString() };
        String sortOrder = getPurchasesSortOrder(context);
        Cursor cursor = getPurchases(context, projection, selection, selectionArgs, sortOrder);

        if (cursor == null || cursor.getCount() < 1) {
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }

        List<Purchase> list = new ArrayList<Purchase>(cursor.getCount());

        while (cursor.moveToNext()) {
            Purchase purchase = fromCursor(cursor);
            list.add(purchase);
        }

        cursor.close();
        return list;
    }

	public static String getPurchasesListString(Context context) {
        List<Purchase> list = getPurchasesList(context);
		String listStr = "";
		String purchasesLiSendListItemsDelimeter = context
				.getString(R.string.purchasesLiSendListItemsDelimiter);
		String purchasesLiSendListCounterDelimeter = context
				.getString(R.string.purchasesLiSendListCounterDelimiter);
		String purchasesLiSendListDescrFormat = context
				.getString(R.string.purchasesLiSendListDescrFormat);

        if (list != null) {
            for (Purchase purchase : list) {
                //if (purchase.getState() == PurchaseState.ENTERED) {
                    if (listStr.length() > 0) {
                        listStr = listStr.concat(purchasesLiSendListItemsDelimeter);
                    }

                    listStr = listStr.concat(purchase.getGoods().getName());

                    if (purchase.getCount().floatValue() >= Purchase.MINIMAL_VIEWABLE_COUNT) {
                        listStr = listStr.concat(purchasesLiSendListCounterDelimeter).concat(purchase.getCount().toString())
                                .concat(purchasesLiSendListCounterDelimeter).concat(purchase.getUnits().getName());
                    }

                    String descr = purchase.getDescr();

                    if (descr != null)
                        if (descr.length() > 0) {
                            listStr = listStr.concat(String.format(purchasesLiSendListDescrFormat, descr));
                        }
                //}
            }
        }

		return listStr;
	}

    public static String getPurchasesListNotificationString(Context context, int maxLength) {
        List<Purchase> list = getPurchasesList(context);

        if (list == null || list.size() < 1) {
            return null;
        }

        String listStrTrailerTemplate = context.getString(R.string.purchasesLiSendListTrailer);
        String listItemsDelimiter = context.getString(R.string.purchasesLiSendListItemsDelimiter);
        boolean done = false;
        Iterator<Purchase> purchaseIterator = list.iterator();
        String listStr = purchaseIterator.next().getGoods().getName();
        int unselected = list.size() - 1;
        String trailer = String.format(listStrTrailerTemplate, unselected);

        while (purchaseIterator.hasNext() && !done) {
            Purchase purchase = purchaseIterator.next();
            String newList = listStr + listItemsDelimiter + purchase.getGoods().getName();
            int newUnselected = unselected - 1;
            String newTrailer = String.format(listStrTrailerTemplate, newUnselected);

            if (newList.length() + newTrailer.length() >= maxLength && purchaseIterator.hasNext()) {
                done = true;
            } else {
                listStr = newList;
                unselected = newUnselected;
                trailer = newTrailer;
            }
        }

        if (unselected > 0) {
            listStr = listStr + trailer;
        }

        return listStr;
    }

    private static void updatePurchaseStatistics(Context context, Purchase newPurchase) {
		if (newPurchase.getId() == null) {
			// a new purchase, not accepted yet
			return;
		}

		Cursor oldPurchaseCursor = getPurchases(context,
				new String[] { PurchasesContentProvider.Columns.STATE.toString(),
						PurchasesContentProvider.Columns.CHANGED.toString(),
						PurchasesContentProvider.Columns.COUNT.toString() },
				PurchasesContentProvider.Columns._id.getDbName() + "=?", new String[] { newPurchase.getId()
						.toString() }, null);

		if (oldPurchaseCursor == null || !oldPurchaseCursor.moveToFirst()) {
			// there was no any purchases
            if (oldPurchaseCursor != null) {
                oldPurchaseCursor.close();
            }

			return;
		}

		Purchase oldPurchase = fromCursor(oldPurchaseCursor);
        oldPurchaseCursor.close();

		// we should update statistics in two cases:
		// 1) purchase was entered and now's going to become accepted
		// 2) purchase was accepted and now's going to become entered
		if (!newPurchase.getState().equals(oldPurchase.getState())) {
			Long nextGoodsId = newPurchase.getGoods().getId();
			int count;
			Date lastPurchaseDate;

			if (newPurchase.getState() == PurchaseState.ACCEPTED) {
				lastPurchaseDate = new Date();
				count = 1;
			} else {
				lastPurchaseDate = oldPurchase.getChanged();
				count = -1;
			}

			Purchase previousPurchase = getLastPurchase(context, lastPurchaseDate);
			Long prevGoodsId = (previousPurchase != null) ? previousPurchase.getGoods().getId() : null;

			GoodsStatisticsUtils.updateGoodsStatistics(context, prevGoodsId, nextGoodsId, count);

			Long nextGoodsCategoryId;

			if (newPurchase.getGoods().getCategory() == null
					|| newPurchase.getGoods().getCategory().getId() == null) {
				Goods newGoods = GoodsUtils.getGoodsById(context, newPurchase.getGoods().getId());
				nextGoodsCategoryId = newGoods.getCategory().getId();
			} else {
				nextGoodsCategoryId = newPurchase.getGoods().getCategory().getId();
			}

			Long prevGoodsCategoryId = previousPurchase != null && previousPurchase.getGoods() != null
					&& previousPurchase.getGoods().getCategory() != null ? previousPurchase.getGoods()
					.getCategory().getId() : null;

			CategoriesStatisticsUtils.updateCategoriesStatistics(context, prevGoodsCategoryId,
					nextGoodsCategoryId, count);
		}
	}

	/**
	 * returns the latest purchase before the date/time <code>before</code> but
	 * the same date as the <code>before</code>
	 * 
	 * @param context
	 * @param before
	 * @return
	 */
	private static Purchase getLastPurchase(Context context, Date before) {
		String selection = PurchasesContentProvider.Columns.CHANGED.getDbName() + " BETWEEN ? AND ?";
		SimpleDateFormat df = new SimpleDateFormat(ContentUtils.DATE_FORMAT);
		SimpleDateFormat dtf = new SimpleDateFormat(ContentUtils.DATETIME_FORMAT);
		String dateMidnight = String.format(ContentUtils.MIDNIGHT, df.format(before));
		String dateTime = dtf.format(before);
		String[] selectionArgs = new String[] { dateMidnight, dateTime };
		Cursor purchases = getPurchases(context, purchasesListProjection, selection, selectionArgs,
				PurchasesContentProvider.Columns.CHANGED.getDbName() + " DESC");

		if (purchases == null || !purchases.moveToFirst()) {
            if (purchases != null) {
                purchases.close();
            }

            return null;
        }

        Purchase purchase = fromCursor(purchases);
		purchases.close();
        return purchase;
	}

}
