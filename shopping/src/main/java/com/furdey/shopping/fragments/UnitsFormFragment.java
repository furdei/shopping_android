package com.furdey.shopping.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.furdey.engine.common.utils.KeyValuePair;
import com.furdey.shopping.R;
import com.furdey.shopping.content.UnitsUtils;
import com.furdey.shopping.content.model.Unit;
import com.furdey.shopping.content.model.Unit.UnitType;
import com.furdey.shopping.utils.TemplateRunnable;

public class UnitsFormFragment extends Fragment {

	/**
	 * @param unit
	 *          can be null for a new record
	 * @return
	 */
	public static UnitsFormFragment newInstance(Unit unit) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(PARAM_UNIT, unit != null ? unit : new Unit());
		UnitsFormFragment fragment = new UnitsFormFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	public static interface UnitsFormListener {
		void onSaveUnit(Unit unit);

		void onCancelUnitEdit();
	}

	private UnitsFormListener listener;

	private static final String PARAM_UNIT = "unit";

	public static class UnitTypeSpinnerArrayAdapter extends
			ArrayAdapter<KeyValuePair<Unit.UnitType, String>> {

		private int resource;

		public UnitTypeSpinnerArrayAdapter(Context context, int resource,
				List<KeyValuePair<UnitType, String>> objects) {
			super(context, resource, objects);
			this.resource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(resource, parent, false);
			}

			TextView text = (TextView) v.findViewById(android.R.id.text1);
			text.setText(getItem(position).getValue());
			return v;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
			}

			TextView text = (TextView) v.findViewById(android.R.id.text1);
			text.setText(getItem(position).getValue());
			return v;
		}
	};

	private EditText nameEdit;
	private EditText descrEdit;
	private EditText decimalsEdit;

	private static final int DECIMALS_MIN = 0;
	private static final int DECIMALS_MAX = 3;

	private Spinner unitTypeSpinner;
	private CheckBox isDefaultCheckBox;
	private Button saveButton;
	private Button cancelButton;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (UnitsFormListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement "
					+ UnitsFormListener.class.getSimpleName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.units_form, container, false);

		nameEdit = (EditText) view.findViewById(R.id.unitsFmNameEdit);
		descrEdit = (EditText) view.findViewById(R.id.unitsFmDescrEdit);
		decimalsEdit = (EditText) view.findViewById(R.id.unitsFmDecimalsEdit);
		unitTypeSpinner = (Spinner) view.findViewById(R.id.unitsFmUnitType);
		isDefaultCheckBox = (CheckBox) view.findViewById(R.id.unitsFmIsDefault);
		saveButton = (Button) view.findViewById(R.id.formButtonSave);
		cancelButton = (Button) view.findViewById(R.id.formButtonCancel);

		// Create unitType spinner values
		ArrayList<KeyValuePair<Unit.UnitType, String>> unitTypes = new ArrayList<KeyValuePair<Unit.UnitType, String>>();
		unitTypes.add(new KeyValuePair<Unit.UnitType, String>(Unit.UnitType.MASS,
				getString(R.string.unitsFmUnitTypeMass)));
		unitTypes.add(new KeyValuePair<Unit.UnitType, String>(Unit.UnitType.DISTANCE,
				getString(R.string.unitsFmUnitTypeDistance)));
		unitTypes.add(new KeyValuePair<Unit.UnitType, String>(Unit.UnitType.SQUARE,
				getString(R.string.unitsFmUnitTypeSquare)));
		unitTypes.add(new KeyValuePair<Unit.UnitType, String>(Unit.UnitType.VOLUME,
				getString(R.string.unitsFmUnitTypeVolume)));
		unitTypes.add(new KeyValuePair<Unit.UnitType, String>(Unit.UnitType.COUNT,
				getString(R.string.unitsFmUnitTypeCount)));

		UnitTypeSpinnerArrayAdapter adapter = new UnitTypeSpinnerArrayAdapter(getActivity(),
				android.R.layout.simple_spinner_item, unitTypes);
		unitTypeSpinner.setAdapter(adapter);

		Unit model = (Unit) getArguments().getSerializable(PARAM_UNIT);

		if (model.getId() == null) {
			// adding a new unit
			unitTypeSpinner.setSelection(Unit.UnitType.COUNT.getValue());
			nameEdit.requestFocus();
		} else {
			// editing an existing unit
			nameEdit.setText(model.getName());
			nameEdit.setTag(model.getId());
			descrEdit.setText(model.getDescr());
			descrEdit.setTag(model.getId());
			decimalsEdit.setText(Integer.toString(model.getDecimals()));
			unitTypeSpinner.setSelection(model.getUnitType().getValue());
			isDefaultCheckBox.setChecked(model.getIsDefault());
			// When a unit is default then we don't allow to make it not default
			// by unchecking the checkbox. User must set another unit to default
			// instead.
			isDefaultCheckBox.setEnabled(!model.getIsDefault());
			unitTypeSpinner.setEnabled(!model.getIsDefault());

			cancelButton.requestFocus();
		}

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onCancelUnitEdit();
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				constructModelFromUi(new TemplateRunnable<Unit>() {
					@Override
					public void run(Unit newModel) {
						listener.onSaveUnit(newModel);
					}
				});
			}
		});

		isDefaultCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isDefaultCheckBox.isChecked()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
					builder.setTitle(R.string.unitsFmConfirmDefaultCaption);
					builder
							.setMessage(
									String.format(getString(R.string.unitsFmConfirmDefaultDetails), nameEdit
											.getText().toString()))
							.setNegativeButton(R.string.formButtonCancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									isDefaultCheckBox.setChecked(false);
								}
							}).setPositiveButton(R.string.formButtonOk, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									isDefaultCheckBox.setClickable(false);
								}
							});
					// Create the AlertDialog object and return it
					builder.create().show();
				}
			}
		});

		setRetainInstance(true);
		return view;
	}

	@SuppressWarnings("unchecked")
	private void constructModelFromUi(final TemplateRunnable<Unit> callback) {
		final Unit unit = new Unit();
		Unit paramUnit = (Unit) getArguments().getSerializable(PARAM_UNIT);
		unit.setId(paramUnit.getId());

		if (nameEdit.getText() != null && nameEdit.getText().toString() != null
				&& nameEdit.getText().toString().trim().length() > 0) {
			unit.setName(nameEdit.getText().toString().trim());
		} else {
			nameEdit.setError(getString(R.string.unitsFmErrorSpecifyName));
			return;
		}

		if (descrEdit.getText() != null && descrEdit.getText().toString() != null
				&& descrEdit.getText().toString().trim().length() > 0) {
			unit.setDescr(descrEdit.getText().toString().trim());
		} else {
			descrEdit.setError(getString(R.string.unitsFmErrorSpecifyDescr));
			return;
		}

		if (decimalsEdit.getText() != null && decimalsEdit.getText().toString() != null
				&& decimalsEdit.getText().toString().trim().length() > 0) {
			try {
				unit.setDecimals(Integer.parseInt(decimalsEdit.getText().toString().trim()));

				if (unit.getDecimals() < DECIMALS_MIN) {
					decimalsEdit.setError(getString(R.string.unitsFmErrorDecimalsLowerBorder));
					return;
				}

				if (unit.getDecimals() > DECIMALS_MAX) {
					decimalsEdit.setError(getString(R.string.unitsFmErrorDecimalsHigherBorder));
					return;
				}
			} catch (NumberFormatException e) {
				decimalsEdit.setError(getString(R.string.unitsFmErrorDecimalsWrongFormat));
				return;
			}
		} else {
			decimalsEdit.setError(getString(R.string.unitsFmErrorSpecifyDecimals));
			return;
		}

		ArrayAdapter<KeyValuePair<UnitType, String>> arrayAdapter = (ArrayAdapter<KeyValuePair<Unit.UnitType, String>>) unitTypeSpinner
				.getAdapter();
		unit.setUnitType(arrayAdapter.getItem(unitTypeSpinner.getSelectedItemPosition()).getKey());
		unit.setIsDefault(isDefaultCheckBox.isChecked());

		// check if a unit already exists
		new AsyncTask<Unit, Void, Unit>() {
			@Override
			protected Unit doInBackground(Unit... params) {
				Cursor cursor = UnitsUtils.getUnitsByNameOrDescr(getActivity(), params[0].getName(),
						params[0].getDescr());

				if (cursor.moveToNext())
					return UnitsUtils.fromCursor(cursor);
				else
					return null;
			}

			@Override
			protected void onPostExecute(Unit result) {
				if (result == null || (unit.getId() != null && result.getId().equals(unit.getId()))) {
					// there are no same units found - success
					// or maybe the found one is the same unit that we're going to save
					listener.onSaveUnit(unit);
				} else {
					if (result.getName() != null && unit.getName() != null
							&& result.getName().equals(unit.getName())) {
						nameEdit.setError(getString(R.string.unitsFmErrorAlreadyExists));
					} else {
						descrEdit.setError(getString(R.string.unitsFmErrorAlreadyExists));
					}
				}
			}

		}.execute(unit);
	}
}
