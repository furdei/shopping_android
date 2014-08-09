package com.furdey.engine.android.activities;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.furdey.engine.android.validators.ActivityValidatorsHolder;
import com.furdey.engine.android.validators.CustomValidatorSetter;
import com.furdey.engine.android.validators.EditTextMinLengthValidatorSetter;
import com.furdey.engine.android.validators.EditTextNumberRangeValidatorSetter;
import com.furdey.engine.android.validators.ValidationErrorList;
import com.furdey.engine.android.validators.ValidationResultListener;
import com.furdey.engine.common.utils.ReflectionUtils;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

/**
 * Base class for the activities, who's UI components need to be bound to the
 * data tier. For example, activities with ListViews. This class extends
 * RoboActivity but also includes all functionality of the OrmLiteBaseActivity
 * class.
 * 
 * <p/>
 * Thanks to Gray for his reply at: <a href=
 * "http://stackoverflow.com/questions/7656539/ormlite-setup-without-using-base-activities"
 * > http://stackoverflow.com/questions/7656539/ormlite-setup-without-using-base
 * -activities</a>
 * 
 * @author Stepan Furdey
 * @author Gray
 */
public class DataLinkActivity<H extends OrmLiteSqliteOpenHelper> extends FragmentActivity implements
		ValidationResultListener {

	private volatile H dbHelper;

	private static Object dbHelperSemaphore = new Object();

	private ActivityValidatorsHolder validatorsHolder;

	private boolean upButtonEnabled = true;

	/**
	 * Use it to get access to your ORMLite DAO layer. Remember that database
	 * operations cost a lot of time and call it only in non-UI threads.
	 * <code>getDaoHelper</code> is a thread-safe method.
	 */
	@SuppressWarnings("unchecked")
	public H getDaoHelper() {
		if (dbHelper == null) {
			synchronized (dbHelperSemaphore) {
				if (dbHelper == null)
					dbHelper = (H) OpenHelperManager.getHelper(this,
							ReflectionUtils.getGenericParameterClass(this.getClass(), DataLinkActivity.class, 0));
			}
		}
		return dbHelper;
	}

	public final ActivityValidatorsHolder getValidatrosHolder() {
		return validatorsHolder;
	}

	@Override
	public void onValidationResult(ValidationErrorList errorList) {
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && upButtonEnabled)
			getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		processValidatorAnnotations();
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		processValidatorAnnotations();
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		super.setContentView(view, params);
		processValidatorAnnotations();
	}

	private void processValidatorAnnotations() {
		// We don't allow to gather validators twice in case when
		// setContentView is called multiple times. Actually this
		// could work, but it needs to be tested first.
		if (validatorsHolder == null) {
			validatorsHolder = new ActivityValidatorsHolder();

			Field[] fields = this.getClass().getDeclaredFields();

			for (int i = 0; i < fields.length; i++) {
				EditTextMinLengthValidatorSetter.addValidatorIfAnnotated(this, fields[i]);
				EditTextNumberRangeValidatorSetter.addValidatorIfAnnotated(this, fields[i]);
				CustomValidatorSetter.addValidatorIfAnnotated(this, fields[i]);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (dbHelper != null) {
			OpenHelperManager.releaseHelper();
			dbHelper = null;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Button "Up" for API level Build.VERSION_CODES.HONEYCOMB
			if (upButtonEnabled) {
				setResult(RESULT_CANCELED);
				finish();
				// NavUtils.navigateUpFromSameTask(this);
				return true;
			}
			break;
		}

		return (super.onOptionsItemSelected(item));
	}

	public boolean isUpButtonEnabled() {
		return upButtonEnabled;
	}

	public void setUpButtonEnabled(boolean upButtonEnabled) {
		this.upButtonEnabled = upButtonEnabled;
	}
}
