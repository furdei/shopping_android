package com.furdey.engine.android.validators;

import java.lang.reflect.Field;

import android.widget.EditText;

import com.furdey.engine.android.activities.DataLinkActivity;
import com.furdey.engine.android.annotations.MinLength;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class EditTextMinLengthValidatorSetter {
	
	public static void addValidatorIfAnnotated(DataLinkActivity<? extends OrmLiteSqliteOpenHelper> activity, Field field) {
    	MinLength an = field.getAnnotation(MinLength.class);
    	
        if (an != null && EditText.class.equals(field.getType())) {
        	try {
        		field.setAccessible(true);
				EditText control = (EditText)field.get(activity);
				EditTextValidationEventsListener listener = 
						new EditTextValidationEventsListener(activity, activity.getValidatrosHolder(), control);
				listener.addValidator(new EditTextMinLengthValidator(
						an.order(), 
						activity.getString(an.errorMessageId()), 
						activity,
						control, 
						an.minLength()));
				control.addTextChangedListener(listener);
				control.setOnFocusChangeListener(listener);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Error in adding validator", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Error in adding validator", e);
			}
        }
	}

}
