package com.furdey.shopping.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import com.furdey.shopping.R;
import com.furdey.shopping.content.UnitsUtils;
import com.furdey.shopping.content.model.Unit;
import com.furdey.shopping.fragments.UnitsFormFragment;
import com.furdey.shopping.fragments.UnitsFormFragment.UnitsFormListener;
import com.furdey.shopping.fragments.UnitsListFragment;
import com.furdey.shopping.fragments.UnitsListFragment.UnitsListListener;
import com.furdey.shopping.tasks.ToastThrowableAsyncTask;

public class UnitsActivity extends FragmentActivity implements UnitsListListener,
		UnitsFormListener, LoaderCallbacks<Cursor> {

	private static final String TAG = UnitsActivity.class.getCanonicalName();

	private static final String UNITS_LIST_TAG = "unitsListTag";
	private static final String UNITS_FORM_TAG = "unitsFormTag";

	private static final int UNITS_LIST_LOADER = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_fragment_activity);
		goToUnitsList(false);
	}

	@Override
	public void onUnitsListFragmentCreated() {
		getSupportLoaderManager().initLoader(UNITS_LIST_LOADER, null, this);
	}

	@Override
	public void onEditUnit(Unit unit) {
		goToUnitsForm(unit, true);
	}

	@Override
	public void onDeleteUnit(Unit unit) {
		new ToastThrowableAsyncTask<Unit, Void>(getApplicationContext()) {
			@Override
			protected Void doBackgroundWork(Unit param) throws Throwable {
				UnitsUtils.deleteUnit(getApplicationContext(), param.getId());
				return null;
			}

			@Override
			protected void onSuccess(Void result) {
				Toast.makeText(getApplicationContext(), R.string.unitsLiItemDeleted, Toast.LENGTH_LONG)
						.show();
			}

		}.execute(unit);
	}

	@Override
	public void onSaveUnit(Unit unit) {
		returnToUnitsList();
		new ToastThrowableAsyncTask<Unit, Uri>(getApplicationContext()) {
			@Override
			protected Uri doBackgroundWork(Unit param) throws Throwable {
				return UnitsUtils.saveUnit(getApplicationContext(), param);
			}
		}.execute(unit);
	}

	@Override
	public void onCancelUnitEdit() {
		returnToUnitsList();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return UnitsUtils.getUnitsLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		UnitsListFragment unitsListFragment = (UnitsListFragment) getSupportFragmentManager()
				.findFragmentByTag(UNITS_LIST_TAG);

		if (unitsListFragment != null)
			unitsListFragment.onUnitsListReady(arg1);
		else
			Log.e(TAG, "UnitsListFragment was expected but wasn't found");
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		UnitsListFragment unitsListFragment = (UnitsListFragment) getSupportFragmentManager()
				.findFragmentByTag(UNITS_LIST_TAG);

		if (unitsListFragment != null)
			unitsListFragment.onUnitsListReset();
		else
			Log.e(TAG, "UnitsListFragment was expected but wasn't found");
	}

	private void goToUnitsList(boolean addToBackStack) {
		UnitsListFragment unitsListFragment = (UnitsListFragment) getSupportFragmentManager()
				.findFragmentByTag(UNITS_LIST_TAG);

		if (unitsListFragment != null) {
			Log.d(TAG, "already at the list");
			return;
		}

		unitsListFragment = new UnitsListFragment();
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction().add(
				R.id.dynamic_fragment_container, unitsListFragment, UNITS_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
	}

	private void returnToUnitsList() {
		getSupportFragmentManager().popBackStack();
		setTitle(R.string.unitsLiTitle);
	}

	private void goToUnitsForm(Unit unit, boolean addToBackStack) {
		UnitsFormFragment unitsFormFragment = (UnitsFormFragment) getSupportFragmentManager()
				.findFragmentByTag(UNITS_FORM_TAG);

		if (unitsFormFragment == null)
			unitsFormFragment = UnitsFormFragment.newInstance(unit);

		int title = unit == null ? R.string.unitsFmTitleAdd : R.string.unitsFmTitleEdit;

		FragmentTransaction tr = getSupportFragmentManager().beginTransaction()
				.add(R.id.dynamic_fragment_container, unitsFormFragment, UNITS_FORM_TAG)
				.hide(getSupportFragmentManager().findFragmentByTag(UNITS_LIST_TAG))
				.setBreadCrumbTitle(title);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
		setTitle(title);
	}
}
