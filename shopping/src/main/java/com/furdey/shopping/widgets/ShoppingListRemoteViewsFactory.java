package com.furdey.shopping.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.furdey.shopping.R;
import com.furdey.shopping.content.model.Purchase.PurchaseState;
import com.furdey.shopping.contentproviders.PurchasesContentProvider;
import com.furdey.shopping.contentproviders.PurchasesContentProvider.Columns;

// http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.0.1_r1/com/example/android/weatherlistwidget/WeatherWidgetService.java
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
			mCursor = mContext.getContentResolver().query(PurchasesContentProvider.PURCHASES_URI,
					projection, null, null, null);

		} finally {
			Binder.restoreCallingIdentity(token);
		}
	}
}
