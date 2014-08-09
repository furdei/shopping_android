package com.furdey.engine.android.menus;

import android.os.Bundle;

import com.furdey.engine.android.activities.DataLinkActivity;

public class ActivityHolder {
	private Class<? extends DataLinkActivity<?>> clazz;
	private Bundle parameters;
	
	public Class<? extends DataLinkActivity<?>> getClazz() {
		return clazz;
	}
	public void setClazz(Class<? extends DataLinkActivity<?>> clazz) {
		this.clazz = clazz;
	}
	public Bundle getParameters() {
		return parameters;
	}
	public void setParameters(Bundle parameters) {
		this.parameters = parameters;
	}
	
	public ActivityHolder() {
	}
}
