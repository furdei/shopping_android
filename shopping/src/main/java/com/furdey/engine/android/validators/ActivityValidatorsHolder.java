package com.furdey.engine.android.validators;

import java.util.ArrayList;
import java.util.List;

public class ActivityValidatorsHolder {
	
	private ArrayList<Validator<?>> validators;
	
	public ActivityValidatorsHolder() {
		validators = new ArrayList<Validator<?>>();
	}
	
	public void addValidator(Validator<?> validator) {
		validators.add(validator);
	}
	
	public List<Validator<?>> getValidators() {
		return validators;
	}
}
