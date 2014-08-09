package com.furdey.engine.android.validators;

import java.util.List;

import android.widget.EditText;

public class EditTextValidationErrorViewer implements ValidationErrorViewer {

	private static final String CONTROL_CAN_NOT_BE_NULL = "Control for validation result viewer can not be null";
	
	private EditText control;
	
	public EditTextValidationErrorViewer(EditText control) {
		if (control == null)
			throw new IllegalStateException(CONTROL_CAN_NOT_BE_NULL);
		
		this.control = control;
	}

	@Override
	public void showError(List<PrioritizedError> errorList) {
		if (control == null)
			throw new IllegalStateException(CONTROL_CAN_NOT_BE_NULL);
		
		if (errorList != null && errorList.size() > 0)
			control.setError(errorList.get(0).getError());
		else
			control.setError(null);		
	}

}
