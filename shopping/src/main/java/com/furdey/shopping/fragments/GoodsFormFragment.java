package com.furdey.shopping.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.furdey.shopping.R;
import com.furdey.shopping.adapters.UnitsSpinnerAdapter;
import com.furdey.shopping.content.GoodsUtils;
import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.content.model.Unit;
import com.furdey.shopping.utils.TemplateRunnable;

public class GoodsFormFragment extends Fragment {

	public static GoodsFormFragment newInstance(Goods goods) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(PARAM_GOODS, goods != null ? goods : new GoodsCategory());
		GoodsFormFragment fragment = new GoodsFormFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	public static interface GoodsFormListener {
		void onGoodsFormReady();

		void onSaveGoods(Goods goods);

		void onCancelGoodsEdit();

		void onSelectGoodsCategory(String filter);
	}

	private GoodsFormListener listener;

	private static final String PARAM_GOODS = "goods";

	private EditText nameEdit;
	private EditText categoryEdit;
	private Spinner unitsSpinner;
	private UnitsSpinnerAdapter unitsSpinnerAdapter;
	private Button saveButton;
	private Button cancelButton;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (GoodsFormListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement "
					+ GoodsFormListener.class.getSimpleName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.goods_form, container, false);

		nameEdit = (EditText) view.findViewById(R.id.goodsFmNameEdit);
		categoryEdit = (EditText) view.findViewById(R.id.goodsFmCategoryEdit);
		unitsSpinner = (Spinner) view.findViewById(R.id.goodsFmUnitsSpinner);
		saveButton = (Button) view.findViewById(R.id.formButtonSave);
		cancelButton = (Button) view.findViewById(R.id.formButtonCancel);

		Goods model = (Goods) getArguments().getSerializable(PARAM_GOODS);
		nameEdit.setText(model.getName());
		unitsSpinnerAdapter = new UnitsSpinnerAdapter(getActivity());
		unitsSpinner.setAdapter(unitsSpinnerAdapter);

		categoryEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String filter = (categoryEdit.getText() != null) ? categoryEdit.getText().toString() : null;
				listener.onSelectGoodsCategory(filter);
			}
		});

		if (model.getId() == null) {
			// adding a new goods
			nameEdit.requestFocus();
		} else {
			// editing an existing goods
			nameEdit.setTag(model.getId());

			if (model.getCategory() != null) {
				setCategory(model.getCategory());
			}

			unitsSpinner.setTag(model.getDefaultUnits());
			cancelButton.requestFocus();
		}

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onCancelGoodsEdit();
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				constructModelFromUi(new TemplateRunnable<Goods>() {
					@Override
					public void run(Goods newModel) {
						listener.onSaveGoods(newModel);
					}
				});
			}
		});

		setRetainInstance(true);
		listener.onGoodsFormReady();
		return view;
	}

	public void setUnitsCursor(Cursor unitsCursor) {
		Cursor oldCursor = unitsSpinnerAdapter.swapCursor(unitsCursor);

		if (oldCursor == null) {
			Goods model = (Goods) getArguments().getSerializable(PARAM_GOODS);

			if (model != null && model.getDefaultUnits() != null
					&& model.getDefaultUnits().getId() != null) {
				int index = unitsSpinnerAdapter.getIndexById(model.getDefaultUnits().getId());
				unitsSpinner.setSelection(index);
			}
		}
	}

	public Goods getParameterGoods() {
		return (Goods) getArguments().getSerializable(PARAM_GOODS);
	}

	public void setCategory(GoodsCategory category) {
		categoryEdit.setText(category.getName());
		categoryEdit.setTag(category.getId());
	}

	private void constructModelFromUi(final TemplateRunnable<Goods> callback) {
		final Goods goods = new Goods();
		Goods paramGoods = (Goods) getArguments().getSerializable(PARAM_GOODS);
		goods.setId(paramGoods.getId());

		if (nameEdit.getText() != null && nameEdit.getText().toString() != null
				&& nameEdit.getText().toString().trim().length() > 0) {
			goods.setName(nameEdit.getText().toString().trim());
		} else {
			nameEdit.setError(getString(R.string.goodsFmErrorSpecifyName));
			return;
		}

		GoodsCategory category = new GoodsCategory();

		if (categoryEdit.getText() != null && categoryEdit.getText().toString() != null
				&& categoryEdit.getText().toString().trim().length() > 0) {
			category.setName(categoryEdit.getText().toString().trim());
		}

		if (categoryEdit.getTag() != null) {
			category.setId((Long) categoryEdit.getTag());
		}

		goods.setCategory(category);

		Unit unit = null;
		Long selectedUnitId = unitsSpinner.getSelectedItemId();

		if (selectedUnitId != AdapterView.INVALID_ROW_ID) {
			unit = new Unit();
			unit.setId(selectedUnitId);
		}

		goods.setDefaultUnits(unit);

		// check if a goods already exists
		new AsyncTask<Goods, Void, Goods>() {
			@Override
			protected Goods doInBackground(Goods... params) {
				Cursor cursor = GoodsUtils.getGoodsByName(getActivity(), null, params[0].getName(), null);
                Goods goods = null;

                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        goods = GoodsUtils.fromCursor(cursor);
                    }

                    cursor.close();
                }

                return goods;
			}

			@Override
			protected void onPostExecute(Goods result) {
				if (result == null || (goods.getId() != null && result.getId().equals(goods.getId()))) {
					// there are no same units found - success
					// or maybe the found one is the same unit that we're going to save
                    callback.run(result);
				} else {
					nameEdit.setError(getString(R.string.goodsFmErrorAlreadyExists));
				}
			}

		}.execute(goods);
	}

}
