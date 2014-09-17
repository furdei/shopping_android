package com.furdey.shopping.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InternetConnectionBroadcastReceiver extends BroadcastReceiver {

	private Activity activity;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (activity != null)
            activity.invalidateOptionsMenu();
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

}
