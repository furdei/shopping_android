package com.furdey.shopping.utils;

import android.app.Application;

public class ShoppingApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// RoboGuice.setBaseApplicationInjector(
		// this,
		// RoboGuice.DEFAULT_STAGE,
		// Modules.override(RoboGuice.newDefaultRoboModule(this)).with(new
		// RoboModule())
		// );

		// Menu bindings:
		// MenuConfiguration.configureOptionsMenu();

		//Settings.getInstance().setUnknownError(R.string.errorUnknown);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
