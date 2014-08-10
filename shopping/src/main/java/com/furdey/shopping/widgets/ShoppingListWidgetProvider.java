package com.furdey.shopping.widgets;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.furdey.shopping.R;
import com.furdey.shopping.activities.GoodsActivity;
import com.furdey.shopping.activities.PurchasesActivity;

import java.util.Arrays;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ShoppingListWidgetProvider extends AppWidgetProvider {

	public static final String WIDGET_IDS_KEY = ShoppingListWidgetProvider.class.getCanonicalName()
			.concat(".widgetIDs");

	public static void updateWidgets(Context context) {
		System.out.println("ShoppingListWidgetProvider.updateWidgets()");
		AppWidgetManager man = AppWidgetManager.getInstance(context);
		int[] ids = man.getAppWidgetIds(new ComponentName(context, ShoppingListWidgetProvider.class));
		System.out.println("ShoppingListWidgetProvider.updateWidgets() ids: " + Arrays.toString(ids));
		Intent updateIntent = new Intent();
		updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updateIntent.putExtra(WIDGET_IDS_KEY, ids);
		context.sendBroadcast(updateIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("ShoppingListWidgetProvider.onReceive()");
		if (intent.hasExtra(WIDGET_IDS_KEY)) {
			int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
			System.out.println("ShoppingListWidgetProvider.onReceive() ids=" + Arrays.toString(ids));
			AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(ids,
					R.id.shoppingListGrid);
		} else
			super.onReceive(context, intent);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		System.out.println("ShoppingListWidgetProvider.onUpdate()");
		// update each of the app widgets with the remote adapter
		for (int i = 0; i < appWidgetIds.length; ++i) {
			System.out.println("ShoppingListWidgetProvider.onUpdate() i=" + i + " id=" + appWidgetIds[i]);

			// Sets up the intent that points to the StackViewService that will
			// provide the views for this collection.
			Intent intent = new Intent(context, ShoppingListRemoteViewsService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			// When intents are compared, the extras are ignored, so we need to embed
			// the extras
			// into the data so that the extras will not be ignored.
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.shopping_list_widget);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				rv.setRemoteAdapter(R.id.shoppingListGrid, intent);
			else
				rv.setRemoteAdapter(appWidgetIds[i], R.id.shoppingListGrid, intent);

			// rv.setRemoteAdapter(R.id.shoppingListGrid, intent);
			rv.setEmptyView(R.id.shoppingListGrid, R.id.shoppingListGridEmpty);

			// По клику на лого открывать главное окно приложения
			Intent showPurchasesListIntent = new Intent(context, PurchasesActivity.class);
			PendingIntent showPurchasesListPendingIntent = PendingIntent.getActivity(context, 0,
					showPurchasesListIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.shoppingListWidgetLogo, showPurchasesListPendingIntent);

			// По клику на плюс открывать окно добавления покупки
			Intent newRecordIntent = new Intent(context, GoodsActivity.class);
			//newRecordIntent.putExtra(BaseController.STATE_PARAM_NAME, BaseFilterController.STATE_LOOKUP);
			PendingIntent newRecordPendingIntent = PendingIntent.getActivity(context, 0, newRecordIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.shoppingListWidgetNewRecord, newRecordPendingIntent);

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
