package com.furdey.engine.android.controllers;

import android.content.Intent;

/**
 * Interface to call from a controller when model saving is completed. 
 *
 * @author Stepan Furdey
 */
public interface OnSaveCompleteListener {
	public void onSaveComplete(Intent result);
}
