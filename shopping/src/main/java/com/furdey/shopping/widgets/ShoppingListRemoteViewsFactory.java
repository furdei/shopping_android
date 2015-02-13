package com.furdey.shopping.widgets;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.furdey.shopping.R;
import com.furdey.shopping.ShoppingApplication;
import com.furdey.shopping.content.PurchasesUtils;
import com.furdey.shopping.content.model.Purchase.PurchaseState;
import com.furdey.shopping.contentproviders.PurchasesContentProvider.Columns;
import com.furdey.shopping.utils.PreferencesManager;

public class ShoppingListRemoteViewsFactory implements RemoteViewsFactory {

	private Context mContext;
	private Cursor mCursor;

	public ShoppingListRemoteViewsFactory(Context context, Intent intent) {
		mContext = context;
	}

	public void onCreate() {
		// Since we reload the cursor in onDataSetChanged() which gets called
		// immediately after
		// onCreate(), we do nothing here.
        // just track an event

        ((ShoppingApplication) mContext.getApplicationContext()).trackEvent(
                R.string.analyticsEventCategoryFeature,
                R.string.analyticsEventWidgetCreated,
                mContext.getString(PreferencesManager.isWidgetTracked(mContext) ?
                    R.string.analyticsEventRepeated : R.string.analyticsEventFirst));
        PreferencesManager.setWidgetTracked(mContext, true);
    }

	public void onDestroy() {
		if (mCursor != null) {
			mCursor.close();
		}
	}

	public int getCount() {
		return mCursor.getCount();
	}

	private static class ColumnsIndices {
		public int idIndex;
		public int stateIndex;
		public int nameIndex;
	};

	private static ColumnsIndices columnsIndices;

	private ColumnsIndices getColumnsIndices() {
		if (columnsIndices == null) {
			columnsIndices = new ColumnsIndices();
			columnsIndices.idIndex = mCursor.getColumnIndex(Columns._id.name());
			columnsIndices.stateIndex = mCursor.getColumnIndex(Columns.STATE.name());
			columnsIndices.nameIndex = mCursor.getColumnIndex(Columns.GOODS_NAME.name());
		}

		return columnsIndices;
	}

	public RemoteViews getViewAt(int position) {
		mCursor.moveToPosition(position);

		// Get the data for this position from the content provider
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.purchases_list_widget_li);
		rv.setTextViewText(R.id.purchasesListWidgetLiName,
				mCursor.getString(getColumnsIndices().nameIndex));

		PurchaseState state = PurchaseState.valueOf(mCursor.getString(getColumnsIndices().stateIndex));
		if (state == PurchaseState.ACCEPTED) {
			rv.setImageViewResource(R.id.purchasesListWidgetLiCheck, R.drawable.btn_check_on_holo);
			rv.setViewVisibility(R.id.purchasesListWidgetLiStrike, View.VISIBLE);
		} else {
			rv.setImageViewResource(R.id.purchasesListWidgetLiCheck, R.drawable.btn_check_off_holo);
			rv.setViewVisibility(R.id.purchasesListWidgetLiStrike, View.GONE);
		}

        // On click launch an intent
        Intent purchaseClickedIntent = ShoppingListWidgetActionsService.
                getOnPurchaseClickedIntent(mContext, mCursor.getLong(getColumnsIndices().idIndex));
//        PendingIntent newRecordPendingIntent = PendingIntent.getService(mContext,
//                REQUEST_PURCHASE_CLICKED, purchaseClickedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickFillInIntent(R.id.purchasesListWidgetLi, purchaseClickedIntent);
//        rv.setOnClickPendingIntent(R.id.purchasesListWidgetLi, newRecordPendingIntent);
//        rv.setOnClickPendingIntent(R.id.purchasesListWidgetLiCheck, newRecordPendingIntent);

		return rv;
	}

	public RemoteViews getLoadingView() {
		// We aren't going to return a default loading view in this sample
		return new RemoteViews(mContext.getPackageName(), R.layout.purchases_list_widget_li_loading);
	}

	public int getViewTypeCount() {
		return 1;
	}

	public long getItemId(int position) {
		mCursor.moveToPosition(position);
		return mCursor.getLong(getColumnsIndices().idIndex);
	}

	public boolean hasStableIds() {
		return true;
	}

	public void onDataSetChanged() {
		// Refresh the cursor
		if (mCursor != null) {
			mCursor.close();
		}

		String[] projection = new String[] { Columns._id.toString(), Columns.STATE.toString(),
				Columns.GOODS_NAME.toString() };

		final long token = Binder.clearCallingIdentity();
		try {
            mCursor = PurchasesUtils.getPurchases(mContext, projection, null, null,
                    PurchasesUtils.getPurchasesSortOrder(mContext));
		} finally {
			Binder.restoreCallingIdentity(token);
		}
	}
}
