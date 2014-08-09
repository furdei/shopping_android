package com.furdey.engine.android.validators;

public interface ValidationResultListener {
	
	/**
	 * Called when validation finishes with or without any error
	 */
	public void onValidationResult(ValidationErrorList errorList);	
}
