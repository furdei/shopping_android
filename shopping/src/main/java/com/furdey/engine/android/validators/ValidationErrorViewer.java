package com.furdey.engine.android.validators;

import java.util.List;

public interface ValidationErrorViewer {

	/**
	 * Show error message on the corresponding control. This method is called from
	 * the UI thread after the validation finishes and only if the validation
	 * event occured on the corresponding control. 
	 * 
	 * <p/>This method is called even if there is no any error. In this case
	 * <code>errorList</code> parameter is null and you should remove any errors
	 * from the control appearence.
	 */
	public void showError(List<PrioritizedError> errorList);	
	
}
