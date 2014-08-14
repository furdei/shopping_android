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
import com.furdey.shopping.activities.PurchasesActivity;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ShoppingListWidgetProvider extends AppWidgetProvider {

	public static final String WIDGET_IDS_KEY = ShoppingListWidgetProvider.class.getCanonicalName()
			.concat(".widgetIDs");

    private static final int REQUEST_PURCHASES_LIST   = 0;
    private static final int REQUEST_ADD_NEW_PURCHASE = 1;
    private static final int REQUEST_PURCHASE_CLICKED = 2;

    public static void updateWidgets(Context context) {
		AppWidgetManager man = AppWidgetManager.getInstance(context);
		int[] ids = man.getAppWidgetIds(new ComponentName(context, ShoppingListWidgetProvider.class));
		Intent updateIntent = new Intent();
		//updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.setAction("com.furdey.shopping.action.APPWIDGET_UPDATE");
		updateIntent.putExtra(WIDGET_IDS_KEY, ids);
		context.sendBroadcast(updateIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.hasExtra(WIDGET_IDS_KEY)) {
			int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
			AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(ids,
					R.id.shoppingListGrid);
		} else
			super.onReceive(context, intent);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// update each of the app widgets with the remote adapter
		for (int i = 0; i < appWidgetIds.length; ++i) {
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

			rv.setEmptyView(R.id.shoppingListGrid, R.id.shoppingListGridEmpty);

			// Start the main activity when user clicks an icon
			Intent showPurchasesListIntent = new Intent(context, PurchasesActivity.class);
            showPurchasesListIntent.putExtra(PurchasesActivity.MODE_PARAMETER,
                    PurchasesActivity.Mode.PURCHASES_LIST.toString());
			PendingIntent showPurchasesListPendingIntent = PendingIntent.getActivity(context,
                    REQUEST_PURCHASES_LIST, showPurchasesListIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.shoppingListWidgetLogo, showPurchasesListPendingIntent);

			// Start 'Add a purchase' activity when user clicks an 'add' icon
			Intent newRecordIntent = new Intent(context, PurchasesActivity.class);
            newRecordIntent.putExtra(PurchasesActivity.MODE_PARAMETER,
                    PurchasesActivity.Mode.ADD_NEW_PURCHASE.toString());
			PendingIntent newRecordPendingIntent = PendingIntent.getActivity(context,
                    REQUEST_ADD_NEW_PURCHASE, newRecordIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.shoppingListWidgetNewRecord, newRecordPendingIntent);

            // A template for purchases list items clicks
            PendingIntent purchaseClickedPendingIntent = PendingIntent.getService(context,
                    REQUEST_PURCHASE_CLICKED,
                    ShoppingListWidgetActionsService.getOnPurchaseClickedIntent(context),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.shoppingListGrid, purchaseClickedPendingIntent);

			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
