package com.furdey.engine.android.validators;

public interface Validator<T> {

	/**
	 * Override this to perform validation. It must add the error to the errorList 
	 * if error was found, but it should not do any tasks connected with UI. Remember
	 * that this method is called from a non-UI thread.
	 */
	public void validate(ValidationErrorList errorList);
	
	/**
	 * Returns the provider you can use to obtain data for validation
	 */
	public ValidationDataProvider<T> getDataProvider();
	
	/**
	 * Assigns the provider you can use to obtain data for validation
	 */
	public void setDataProvider(ValidationDataProvider<T> dataProvider);

}