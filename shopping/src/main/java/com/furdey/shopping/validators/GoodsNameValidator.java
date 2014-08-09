package com.furdey.shopping.validators;

import java.sql.SQLException;

import android.widget.EditText;

import com.furdey.engine.android.activities.DataLinkActivity;
import com.furdey.engine.android.validators.EditTextValidator;
import com.furdey.engine.android.validators.ValidationErrorList;
import com.furdey.shopping.R;
import com.furdey.shopping.dao.db.DatabaseHelper;

public class GoodsNameValidator extends EditTextValidator {

	public GoodsNameValidator(int order, String errorMessage, DataLinkActivity<?> activity,
			EditText control) {
		super(order, errorMessage, activity, control);
	}

	@Override
	public void validate(ValidationErrorList errorList) {
		try {
			if (((DatabaseHelper) getActivity().getDaoHelper()).getGoodsDao().isNameAlreadyExists(
					getDataProvider().getData(), (Long) getControl().getTag())) {
				errorList.setError(getControl(), getErrorMessage(), getOrder());
			}
		} catch (final SQLException e) {
			errorList.setError(getControl(), getActivity().getString(R.string.errorUnknown), getOrder());
		}
	}

}
