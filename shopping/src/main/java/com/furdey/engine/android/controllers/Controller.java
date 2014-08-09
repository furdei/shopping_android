package com.furdey.engine.android.controllers;

import com.furdey.engine.android.activities.DataLinkActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class Controller<H extends OrmLiteSqliteOpenHelper> {
	
	private DataLinkActivity<H> activity;

	public Controller(DataLinkActivity<H> activity) {
		this.setActivity(activity);
	}

	public DataLinkActivity<H> getActivity() {
		return activity;
	}

	private void setActivity(DataLinkActivity<H> activity) {
		this.activity = activity;
	}
	
}
