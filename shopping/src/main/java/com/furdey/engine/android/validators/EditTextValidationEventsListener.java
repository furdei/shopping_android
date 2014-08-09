package com.furdey.engine.android.validators;

import java.util.ArrayList;
import java.util.Iterator;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class EditTextValidationEventsListener implements TextWatcher, OnFocusChangeListener, ValidationResultListener {
	
	private ArrayList<EditTextValidator> validators;
	private ValidationResultListener resultListener;
	private View control;
	private ValidationErrorViewer viewer;
	private Boolean validationInProgress;
	private Boolean validateAgain;
	private ActivityValidatorsHolder validatorsHolder;
	
	public EditTextValidationEventsListener(ValidationResultListener resultListener, ActivityValidatorsHolder validatorsHolder, EditText control) {
		this.resultListener = resultListener; 
		this.validatorsHolder = validatorsHolder;
		this.control = control;
		validators = new ArrayList<EditTextValidator>();
		viewer = new EditTextValidationErrorViewer(control);
		validationInProgress = false;
	}
	
	public void addValidator(EditTextValidator validator) {
		validators.add(validator);
		validatorsHolder.addValidator(validator);
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		Iterator<EditTextValidator> iterator = validators.iterator();
		
		while (iterator.hasNext()) {
			EditTextValidator validator = iterator.next();
			validator.getDataProvider().setData(arg0.toString());
		}

		validate();
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onFocusChange(View view, boolean focus) {
		if (!focus)
			validate();
	}

	private void validate() {
		synchronized (validationInProgress) {
			if (!validationInProgress) {
				validationInProgress = true;
				validateAgain = false;
				new ValidationTask(this, validatorsHolder, control, viewer).execute();
			} else
				validateAgain = true;
		}
	}

	@Override
	public void onValidationResult(ValidationErrorList errorList) {
		resultListener.onValidationResult(errorList);
		
		synchronized (validationInProgress) {
			validationInProgress = false;
		}
		
		if (validateAgain)
			validate();
	}
}
