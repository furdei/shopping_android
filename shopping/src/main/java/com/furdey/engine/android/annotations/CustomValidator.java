package com.furdey.engine.android.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation for the input field EditText
 *
 * @author Stepan Furdey
 */
@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface CustomValidator {
	
	/**
	 * Id of the error message string resource. This message is shown
	 * if the rule is broken. Required.
	 */
	int errorMessageId();
	
	/**
	 * Order of this validator to check. Not required, default is 0
	 */
	int order() default 0;
	
	/**
	 * Class that extends BaseAsyncValidator to perform heavy validation 
	 * in a separate thread
	 */
	Class<?> validatorClass();
}
