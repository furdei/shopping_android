package com.furdey.engine.android.controllers;

import android.database.Cursor;

/**
 * Use this interface to define a listener for the
 * cursor loading is completed event
 *
 * @author Stepan Furdey
 */
public interface OnCursorLoadListener {
	/**
	 * This method is called on the UI thread after
	 * cursor loading is completed
	 */
	public void onLoadComplete(Cursor cursor);
}
