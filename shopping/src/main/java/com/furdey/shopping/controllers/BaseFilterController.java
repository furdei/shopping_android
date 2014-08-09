package com.furdey.shopping.controllers;

import com.furdey.engine.android.activities.DataLinkActivity;
import com.furdey.engine.android.controllers.BaseController;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class BaseFilterController<Model, H extends OrmLiteSqliteOpenHelper> extends BaseController<Model, H> {
	
	public static final String FILTER_PARAM_NAME = "com.furdey.shopping.controllers.BaseFilterController.filter";	
	public static final int STATE_LOOKUP = 5;

	private String filter;
	
	public BaseFilterController(DataLinkActivity<H> activity) {
		super(activity);
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

}
