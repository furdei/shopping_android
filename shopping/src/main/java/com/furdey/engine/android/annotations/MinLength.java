package com.furdey.engine.android.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Minimal length validation for the input field EditText
 *
 * @author Stepan Furdey
 */
@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface MinLength {
	
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
	 * Minimal permitted field length. Not required, default is 1
	 */
	int minLength() default 1;
}
