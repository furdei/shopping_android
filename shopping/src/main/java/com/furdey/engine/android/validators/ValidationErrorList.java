package com.furdey.engine.android.validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;

public class ValidationErrorList {
	
	private Map<View, List<PrioritizedError>> errorList;
	
	public ValidationErrorList() {
		errorList = new HashMap<View, List<PrioritizedError>>();
	}
	
	public void setError(View control, String error, int order) {
		PrioritizedError prioritizedError = new PrioritizedError();
		prioritizedError.setError(error);
		prioritizedError.setPriority(order);
		
		List<PrioritizedError> errorsForControl = errorList.get(control);
		
		if (errorsForControl != null)
			errorsForControl.add(prioritizedError);
		else {
			errorsForControl = new ArrayList<PrioritizedError>();
			errorsForControl.add(prioritizedError);
			errorList.put(control, errorsForControl);
		}
	}
	
	public List<PrioritizedError> getErrors(View control) {
		return errorList.get(control);
	}
	
	public Map<View, List<PrioritizedError>> getErrors() {
		return errorList;
	}
	
}
