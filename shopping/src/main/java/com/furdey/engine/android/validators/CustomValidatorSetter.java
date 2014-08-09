package com.furdey.engine.android.validators;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import android.widget.EditText;

import com.furdey.engine.android.activities.DataLinkActivity;
import com.furdey.engine.android.annotations.CustomValidator;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class CustomValidatorSetter {
	
	public static void addValidatorIfAnnotated(DataLinkActivity<? extends OrmLiteSqliteOpenHelper> activity, Field field) {
    	CustomValidator an = field.getAnnotation(CustomValidator.class);
    	
        if (an != null && EditText.class.equals(field.getType())) {
        	try {
        		field.setAccessible(true);
				EditText control = (EditText)field.get(activity);
				EditTextValidationEventsListener listener = 
						new EditTextValidationEventsListener(activity, activity.getValidatrosHolder(), control);
				Constructor<?> ctor = an.validatorClass().getConstructor(int.class, String.class, DataLinkActivity.class, EditText.class);
				listener.addValidator((EditTextValidator)ctor.newInstance(an.order(), activity.getString(an.errorMessageId()), activity, control));
				control.addTextChangedListener(listener);
				control.setOnFocusChangeListener(listener);
			} catch (Exception e) {
				throw new RuntimeException("Error in adding validator", e);
			}
        }
	}

}
