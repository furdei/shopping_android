package com.furdey.engine.android.validators;

import com.furdey.engine.android.activities.DataLinkActivity;

/**
 * Validates something in the specified order using specified method.
 *
 * @author Stepan Furdey
 */
public abstract class BaseValidator<T> implements /*Comparable<BaseValidator<T>>,*/ Validator<T> {
	
	private int order;
	private String errorMessage;
	private DataLinkActivity<?> activity;

	public BaseValidator(int order, String errorMessage, DataLinkActivity<?> activity) {
		this.order = order;
		this.errorMessage = errorMessage;
		this.activity = activity;
	}
	
/*	@Override
	public int compareTo(BaseValidator<T> arg0) {
		return arg0.order - this.order;
	}*/

	protected String getErrorMessage() {
		return errorMessage;
	}

	protected DataLinkActivity<?> getActivity() {
		return activity;
	}

	protected int getOrder() {
		return order;
	}
}
