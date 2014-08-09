package com.furdey.engine.android.validators;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.os.AsyncTask;
import android.view.View;

public class ValidationTask extends AsyncTask<Void, Void, Void> {
	
	private ValidationResultListener resultListener;
	private ValidationErrorList errors;
	private View control;
	private ValidationErrorViewer viewer;
	private ActivityValidatorsHolder validatorsHolder;
	
	public ValidationTask(ValidationResultListener resultListener, ActivityValidatorsHolder validatrosHolder, View control, ValidationErrorViewer viewer) {
		this.resultListener = resultListener;
		this.validatorsHolder = validatrosHolder;
		this.control = control;
		this.viewer = viewer;
		this.errors = new ValidationErrorList();
	}
	
    @Override
    protected void onPostExecute(Void v) {
		List<PrioritizedError> errorsForControl = errors.getErrors(control);
		
		if (errorsForControl != null && errorsForControl.size() > 1) {
			Collections.sort(errorsForControl);
		}
		
		viewer.showError(errorsForControl);
		resultListener.onValidationResult(errors);
    }
    
	@Override
	protected Void doInBackground(Void... params) {
		Iterator<Validator<?>> iterator = validatorsHolder.getValidators().iterator();

		while (iterator.hasNext()) {
			Validator<?> validator = iterator.next();
			validator.validate(errors);
		}
		
		return null;
	}

}
