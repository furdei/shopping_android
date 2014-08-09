package com.furdey.engine.android.validators;

import java.lang.reflect.Field;

import android.widget.EditText;

import com.furdey.engine.android.activities.DataLinkActivity;
import com.furdey.engine.android.annotations.NumberRange;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class EditTextNumberRangeValidatorSetter {
	
	public static void addValidatorIfAnnotated(DataLinkActivity<? extends OrmLiteSqliteOpenHelper> activity, Field field) {
    	NumberRange an = field.getAnnotation(NumberRange.class);
    	
        if (an != null && EditText.class.equals(field.getType())) {
        	try {
        		field.setAccessible(true);
				EditText control = (EditText)field.get(activity);
				EditTextValidationEventsListener listener = 
						new EditTextValidationEventsListener(activity, activity.getValidatrosHolder(), control);
				listener.addValidator(new EditTextNumberRangeValidator(
						an.order(), 
						activity.getString(an.formatErrorMessageId()), 
						activity,
						control, 
						an.minRange(),
						an.maxRange(),
						an.lowerErrorMessageId(),
						an.higherErrorMessageId()));
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
