package com.furdey.engine.android.validators;

import android.widget.EditText;

import com.furdey.engine.android.activities.DataLinkActivity;

public class EditTextNumberRangeValidator extends EditTextValidator {
	
	private Integer minRange;
	private Integer maxRange;
	private String lowerBorderError;
	private String higherBorderError;

	public EditTextNumberRangeValidator(int order, String formatErrorMessage,
			DataLinkActivity<?> activity, EditText control,
			Integer minRange, Integer maxRange, 
			Integer lowerBorderError, Integer higherBorderError) {
		super(order, formatErrorMessage, activity, control);
		
		this.minRange = minRange;
		this.maxRange = maxRange;
		this.lowerBorderError = activity.getString(lowerBorderError);
		this.higherBorderError = activity.getString(higherBorderError);
	}

	@Override
	public void validate(ValidationErrorList errorList) {
		super.validate(errorList);
		
		Integer intValue = null;
		try {
			intValue = Integer.parseInt(getDataProvider().getData());
		} catch (NumberFormatException e) {
			errorList.setError(getControl(), getErrorMessage(), getOrder());
			return;
		}
		
		if (minRange != null && intValue < minRange) {
			errorList.setError(getControl(), lowerBorderError, getOrder());
		}
		
		if (maxRange != null && intValue > maxRange) {
			errorList.setError(getControl(), higherBorderError, getOrder());
		}
	}

}
