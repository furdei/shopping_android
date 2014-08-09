package com.furdey.engine.android.validators;

import android.widget.EditText;

import com.furdey.engine.android.activities.DataLinkActivity;

public abstract class EditTextValidator extends BaseValidator<String> {
	
	private static final String CONTROL_CAN_NOT_BE_NULL = "Control for validation can not be null";
	
	private EditText control;
	private EditTextValidationDataProvider dataProvider;

	public EditTextValidator(int order, String errorMessage, DataLinkActivity<?> activity, EditText control) {
		super(order, errorMessage, activity);
		
		if (control == null)
			throw new IllegalStateException(CONTROL_CAN_NOT_BE_NULL);

		this.control = control;
		this.dataProvider = new EditTextValidationDataProvider(control);
	}

	protected EditText getControl() {
		return control;
	}
	
	@Override
	public void validate(ValidationErrorList errorList) {
		if (control == null)
			throw new IllegalStateException(CONTROL_CAN_NOT_BE_NULL);
	}
	
	@Override
	public ValidationDataProvider<String> getDataProvider() {
		return dataProvider;
	}

	@Override
	public void setDataProvider(ValidationDataProvider<String> dataProvider) {
		throw new UnsupportedOperationException("Method setDataProvider is not supported for EditTextValidator");
	}

}
