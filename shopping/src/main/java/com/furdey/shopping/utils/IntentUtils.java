package com.furdey.shopping.utils;

import android.content.Context;
import android.content.Intent;

import com.furdey.shopping.activities.PurchasesActivity;

/**
 * Created by Masya on 07.01.2015.
 */
public class IntentUtils {

    public static Intent purchaseActivityIntent(Context context) {
        return new Intent(context, PurchasesActivity.class);
    }

}
