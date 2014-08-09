package com.furdey.engine.android.controllers;

/**
 * Use this interface to define a listener for the
 * model loading is completed event
 *
 * @author Stepan Furdey
 */
public interface OnModelLoadListener<Model> {
	/**
	 * This method is called on the UI thread after
	 * model loading is completed
	 */
	public void onLoadComplete(Model model);
}
