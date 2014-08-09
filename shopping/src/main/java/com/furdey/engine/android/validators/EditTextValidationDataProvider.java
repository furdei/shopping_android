package com.furdey.engine.android.validators;

import android.app.Activity;
import android.widget.EditText;

public class EditTextValidationDataProvider implements
		ValidationDataProvider<String> {
	
	private EditText control;
	private String valueFromTextWatcher;
	private String controlText;
	private volatile boolean dataReady;
	
	public EditTextValidationDataProvider(EditText control) {
		this.control = control;
		
		if (control == null)
			throw new IllegalStateException("Control can not be null");
	}

	@Override
	public String getData() {
		if (valueFromTextWatcher != null)
			return valueFromTextWatcher;
		
		controlText = null;
		dataReady = false;
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				controlText = control.getText().toString();
				
				synchronized (this) {
					dataReady = true;
					this.notifyAll();
				}
			}
		};
		
		try {
			synchronized(runnable) {
				((Activity)control.getContext()).runOnUiThread(runnable);
				
				while (!dataReady)
					runnable.wait(); // unlocks runnable while waiting
			}
		} catch (InterruptedException e) {
			e.printStackTrace();			
			return null;
		}

		return controlText;
	}

	@Override
	public void setData(String data) {
		valueFromTextWatcher = data;
	}

}
