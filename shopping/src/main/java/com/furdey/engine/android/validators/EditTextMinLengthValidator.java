package com.furdey.engine.android.validators;

import android.widget.EditText;

import com.furdey.engine.android.activities.DataLinkActivity;

public class EditTextMinLengthValidator extends EditTextValidator {
	
	private int minLength;

	public EditTextMinLengthValidator(int order, String errorMessage, DataLinkActivity<?> activity, EditText control, int minLength) {
		super(order, errorMessage, activity, control);
		this.minLength = minLength;
	}

	@Override
	public void validate(ValidationErrorList errorList) {
		super.validate(errorList);
		
		if (getDataProvider().getData().trim().length() < minLength) {
			errorList.setError(getControl(), getErrorMessage(), getOrder());
		}
	}

}
