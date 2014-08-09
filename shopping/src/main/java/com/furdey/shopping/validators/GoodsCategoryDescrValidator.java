package com.furdey.shopping.validators;

import java.sql.SQLException;

import android.widget.EditText;

import com.furdey.engine.android.activities.DataLinkActivity;
import com.furdey.engine.android.validators.EditTextValidator;
import com.furdey.engine.android.validators.ValidationErrorList;
import com.furdey.shopping.R;
import com.furdey.shopping.dao.db.DatabaseHelper;

public class GoodsCategoryDescrValidator extends EditTextValidator {

	public GoodsCategoryDescrValidator(int order, String errorMessage,
			DataLinkActivity<?> activity, EditText control) {
		super(order, errorMessage, activity, control);
	}

	@Override
	public void validate(ValidationErrorList errorList) {
		try {
			if (getDataProvider().getData() != null && getDataProvider().getData().length() > 0 &&
					((DatabaseHelper)getActivity().getDaoHelper()).getGoodsCategoriesDao().isDescrAlreadyExists(
							getDataProvider().getData(),
							(Integer)getControl().getTag()	)) {
				errorList.setError(getControl(), getErrorMessage(), getOrder());
			}
		} catch (final SQLException e) {
			errorList.setError(getControl(), getActivity().getString(R.string.errorUnknown), getOrder());
		}
	}

}
