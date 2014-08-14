package com.furdey.shopping.widgets;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;

import com.furdey.shopping.content.PurchasesUtils;
import com.furdey.shopping.content.model.Purchase;

public class ShoppingListWidgetActionsService extends IntentService {

    public static Intent getOnPurchaseClickedIntent(Context context, long purchaseId) {
        Intent intent = getOnPurchaseClickedIntent(context);
        intent.setData(ContentUris.withAppendedId(Uri.parse(
                ACTIONS_URI.toString().concat("/").concat(PURCHASE_CLICKED_PATH)), purchaseId));
        return intent;
    }

    public static Intent getOnPurchaseClickedIntent(Context context) {
        return new Intent(context, ShoppingListWidgetActionsService.class);
    }

    private static final String ERROR_UNKNOWN_URI = "Unknown URI: %s";

    private static final String AUTHORITY = ShoppingListWidgetActionsService.class.getCanonicalName();
    private static final String PURCHASE_CLICKED_PATH = "purchaseClicked";

    public static final Uri ACTIONS_URI = Uri.parse("content://" + AUTHORITY);

    private static final int PURCHASE_CLICKED = 1;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, PURCHASE_CLICKED_PATH + "/#", PURCHASE_CLICKED);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ShoppingListWidgetActionsService() {
        super(ShoppingListWidgetActionsService.class.getCanonicalName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int actionType = sURIMatcher.match(intent.getData());
        switch (actionType) {
            case PURCHASE_CLICKED:
                onPurchaseClicked(ContentUris.parseId(intent.getData()));
                break;
            default:
                throw new IllegalArgumentException(String.format(ERROR_UNKNOWN_URI, intent.getData()));
        }
    }

    private void onPurchaseClicked(long purchaseId) {
        Purchase purchase = PurchasesUtils.getPurchaseById(getApplicationContext(), purchaseId);
        PurchasesUtils.savePurchase(getApplicationContext(),
                PurchasesUtils.revertState(purchase));
        ShoppingListWidgetProvider.updateWidgets(getApplicationContext());
    }
}
