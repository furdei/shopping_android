package com.furdey.engine.android.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Number range validation for the number input field EditText
 *
 * @author Stepan Furdey
 */
@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface NumberRange {
	
	/**
	 * Id of the error message string resource. This message is shown
	 * if the number format is wrong. Required.
	 */
	int formatErrorMessageId();
	
	/**
	 * Id of the error message string resource. This message is shown
	 * if the number is less than the lower bound. Required.
	 */
	int lowerErrorMessageId();
	
	/**
	 * Id of the error message string resource. This message is shown
	 * if the number is greater than the higher bound. Required.
	 */
	int higherErrorMessageId();
	
	/**
	 * Order of this validator to check. Not required, default is 0
	 */
	int order() default 0;
	
	/**
	 * Minimal permitted field value. Required
	 */
	int minRange();

	/**
	 * Maximal permitted field value. Required
	 */
	int maxRange();

}
