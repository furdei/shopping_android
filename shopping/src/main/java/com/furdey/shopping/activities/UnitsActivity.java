package com.furdey.shopping.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
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

public class UnitsActivity extends Activity implements UnitsListListener, UnitsFormListener {

	private static final String TAG = UnitsActivity.class.getCanonicalName();

	private static final String UNITS_LIST_TAG = "unitsListTag";
	private static final String UNITS_FORM_TAG = "unitsFormTag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_fragment_activity);
		goToUnitsList(false);
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

	private void goToUnitsList(boolean addToBackStack) {
		UnitsListFragment unitsListFragment = (UnitsListFragment) getFragmentManager()
				.findFragmentByTag(UNITS_LIST_TAG);

		if (unitsListFragment != null) {
			Log.d(TAG, "already at the list");
			return;
		}

		unitsListFragment = new UnitsListFragment();
		FragmentTransaction tr = getFragmentManager().beginTransaction().add(
				R.id.dynamic_fragment_container, unitsListFragment, UNITS_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
	}

	private void returnToUnitsList() {
        getFragmentManager().popBackStack();
		setTitle(R.string.unitsLiTitle);
	}

	private void goToUnitsForm(Unit unit, boolean addToBackStack) {
		UnitsFormFragment unitsFormFragment = (UnitsFormFragment) getFragmentManager()
				.findFragmentByTag(UNITS_FORM_TAG);

		if (unitsFormFragment == null)
			unitsFormFragment = UnitsFormFragment.newInstance(unit);

		int title = unit == null ? R.string.unitsFmTitleAdd : R.string.unitsFmTitleEdit;

		FragmentTransaction tr = getFragmentManager().beginTransaction()
				.add(R.id.dynamic_fragment_container, unitsFormFragment, UNITS_FORM_TAG)
				.hide(getFragmentManager().findFragmentByTag(UNITS_LIST_TAG))
				.setBreadCrumbTitle(title);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
		setTitle(title);
	}
}
