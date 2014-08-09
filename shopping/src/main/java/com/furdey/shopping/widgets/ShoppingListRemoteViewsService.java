package com.furdey.shopping.widgets;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViewsService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ShoppingListRemoteViewsService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		System.out.println("ShoppingListRemoteViewsService.onGetViewFactory()");
		return new ShoppingListRemoteViewsFactory(this.getApplicationContext(), intent);
	}

}
