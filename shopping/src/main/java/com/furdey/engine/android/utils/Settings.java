package com.furdey.engine.android.utils;

public class Settings {
	private static volatile Settings instance;
	
	private Integer unknownError;
	
	private Settings() {
	}
	
	public static Settings getInstance() {
		if (instance == null)
			synchronized (Settings.class) {
				if (instance == null) {
					instance = new Settings();
				}
			}
		
		return instance;
	}

	public Integer getUnknownError() {
		if (unknownError == null)
			throw new IllegalStateException("You must call setUnknownError before calling getUnknownError");
		
		return unknownError;
	}

	public void setUnknownError(Integer unknownError) {
		this.unknownError = unknownError;
	}
}
