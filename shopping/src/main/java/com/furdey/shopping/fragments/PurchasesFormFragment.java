package com.furdey.shopping.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.furdey.shopping.R;
import com.furdey.shopping.ShoppingApplication;
import com.furdey.shopping.adapters.UnitsSpinnerAdapter;
import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.content.model.Purchase;
import com.furdey.shopping.content.model.Unit;
import com.furdey.shopping.contentproviders.UnitsContentProvider;
import com.furdey.shopping.listeners.GoodsCountTextChangedListener;
import com.furdey.shopping.listeners.UnitsLoaderCallbacks;
import com.furdey.shopping.utils.DecimalUtils;

import java.math.BigDecimal;

public class PurchasesFormFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	public static PurchasesFormFragment newInstance(Purchase purchase) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(PARAM_PURCHASE, purchase);
		PurchasesFormFragment fragment = new PurchasesFormFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	public static interface PurchasesFormListener {
		void onSavePurchase(Purchase purchase);

		void onCancelPurchaseEdit();

		void onSelectGoods(String filter);

		void onSelectGoodsCategory(String filter);
	}

	private static final String PARAM_PURCHASE = "purchase";
    private static final String SAVE_UNITS_SPINNER = "unitsSpinner";
    private static final String SAVE_SELECTED_GOODS_ID = "selectedGoodsId";
    private static final String SAVE_SELECTED_CATEGORY_ID = "selectedCategoryId";

	private PurchasesFormListener mListener;
    private boolean isFragmentCreated;
    private int unitsSpinnerSelection = Spinner.INVALID_POSITION;

	private static final int UNITS_LOADER = 0;

	private EditText nameEdit;
	private EditText categoryEdit;
	private EditText descrEdit;
	private EditText countEdit;
	private Spinner unitsSpinner;
	private UnitsSpinnerAdapter unitsSpinnerAdapter;
	private UnitsLoaderCallbacks unitsLoaderCallbacks;
	private Button saveButton;
	private Button cancelButton;

    private Long selectedGoodsId;
    private Long selectedCategoryId;

	@Override
	public void onAttach(Activity activity) {
        super.onAttach(activity);
		try {
			mListener = (PurchasesFormListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement "
					+ PurchasesFormListener.class.getSimpleName());
		}
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        isFragmentCreated = false;
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVE_UNITS_SPINNER)) {
                unitsSpinnerSelection = savedInstanceState.getInt(SAVE_UNITS_SPINNER);
                selectedGoodsId = savedInstanceState.getLong(SAVE_SELECTED_GOODS_ID);
                selectedCategoryId = savedInstanceState.getLong(SAVE_SELECTED_CATEGORY_ID);
            }
        }

        ((ShoppingApplication) getActivity().getApplication())
                .trackViewScreen(PurchasesFormFragment.class);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.purchases_form, container, false);
        nameEdit = (EditText) view.findViewById(R.id.purchasesFmNameEdit);
        categoryEdit = (EditText) view.findViewById(R.id.purchasesFmCategoryEdit);
        descrEdit = (EditText) view.findViewById(R.id.purchasesFmDescrEdit);
        countEdit = (EditText) view.findViewById(R.id.purchasesFmCountEdit);
        saveButton = (Button) view.findViewById(R.id.formButtonSave);
        cancelButton = (Button) view.findViewById(R.id.formButtonCancel);
        unitsSpinner = (Spinner) view.findViewById(R.id.purchasesFmUnitsSpinner);

        unitsSpinnerAdapter = new UnitsSpinnerAdapter(getActivity());
        unitsSpinner.setAdapter(unitsSpinnerAdapter);

        nameEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String filter = (nameEdit.getText() != null) ? nameEdit.getText().toString() : null;
                mListener.onSelectGoods(filter);
            }
        });

        categoryEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String filter = (categoryEdit.getText() != null) ? categoryEdit.getText().toString() : null;
                mListener.onSelectGoodsCategory(filter);
            }
        });

        // We do some text formatting here
        countEdit.addTextChangedListener(new GoodsCountTextChangedListener());
        // Reset count of goods when we start to edit it
        countEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    countEdit.setHint(countEdit.getText());
                    countEdit.setText("");
                }
            }
        });

        unitsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                unitsSpinnerSelection = position;
                Cursor cursor = (Cursor) unitsSpinner.getSelectedItem();
                int field = cursor.getColumnIndex(UnitsContentProvider.Columns.DECIMALS.toString());
                BigDecimal dec = BigDecimal.ZERO;

                if (countEdit.getText() != null && countEdit.getText().toString().trim().length() > 0) {
                    dec = new BigDecimal(countEdit.getText().toString().trim());
                } else if (countEdit.getHint() != null && countEdit.getHint().toString().trim().length() > 0) {
                    dec = new BigDecimal(countEdit.getHint().toString().trim());
                }

                countEdit.setText(DecimalUtils.makeFormatString(dec, cursor.getInt(field)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        unitsLoaderCallbacks = new UnitsLoaderCallbacks(getActivity(), unitsSpinnerAdapter);

        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Purchase newModel = constructModelFromUi();
                if (newModel != null)
                    mListener.onSavePurchase(newModel);
            }
        });

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelPurchaseEdit();
            }
        });

        if (isFragmentCreated) {
            return view;
        }

		Purchase model = (Purchase) getArguments().getSerializable(PARAM_PURCHASE);

		if (model.getGoods() != null) {
			setGoods(model.getGoods());
		}

		if (model.getGoods() != null && model.getGoods().getCategory() != null) {
			setCategory(model.getGoods().getCategory());
		}

		descrEdit.setText(model.getDescr());

		if (model.getCount() != null) {
			countEdit.setText(model.getCount().toPlainString());
		}

        isFragmentCreated = true;
//		setRetainInstance(true);
		return view;
	}

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(UNITS_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            outState.putInt(SAVE_UNITS_SPINNER, unitsSpinnerSelection);
            outState.putLong(SAVE_SELECTED_GOODS_ID, selectedGoodsId);
            outState.putLong(SAVE_SELECTED_CATEGORY_ID, selectedCategoryId);
        }
    }

    @Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return unitsLoaderCallbacks.onCreateLoader(arg0, arg1);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        unitsLoaderCallbacks.onLoadFinished(arg0, arg1);
		Purchase model = (Purchase) getArguments().getSerializable(PARAM_PURCHASE);

		if (unitsSpinner.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
            if (unitsSpinnerSelection != Spinner.INVALID_POSITION) {
                unitsSpinner.setSelection(unitsSpinnerSelection);
            } else {
                setUnit(model.getUnits());
            }
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		unitsLoaderCallbacks.onLoaderReset(arg0);
	}

	public Purchase getParameterPurchase() {
		return (Purchase) getArguments().getSerializable(PARAM_PURCHASE);
	}

	public void setGoods(Goods goods) {
        nameEdit.setText(goods != null ? goods.getName() : null);
        selectedGoodsId = goods != null ? goods.getId() : null;
	}

	public void setCategory(GoodsCategory category) {
        categoryEdit.setText(category != null ? category.getName() : null);
		selectedCategoryId = category != null ? category.getId() : null;
	}

	public void setUnit(Unit unit) {
		if (unit != null && unit.getId() != null) {
			int index = unitsSpinnerAdapter.getIndexById(unit.getId());
			unitsSpinner.setSelection(index);
		} else
			unitsSpinner.setSelection(0);
	}

	private Purchase constructModelFromUi() {
		Purchase purchase = (Purchase) getArguments().getSerializable(PARAM_PURCHASE);

		if (descrEdit.getText() != null && descrEdit.getText().toString() != null
				&& descrEdit.getText().toString().trim().length() > 0) {
            purchase.setDescr(descrEdit.getText().toString().trim());
        } else {
            purchase.setDescr(null);
        }

		if (countEdit.getText() != null && countEdit.getText().toString() != null
				&& countEdit.getText().toString().trim().length() > 0)
			purchase.setCount(getCount(countEdit.getText().toString()));
		else
			purchase.setCount(getCount(countEdit.getHint().toString()));

		int unitsSpinnerPos = unitsSpinner.getSelectedItemPosition();
		if (unitsSpinnerPos == Spinner.INVALID_POSITION) {
			return null;
		}

		purchase.setUnits(new Unit());
		Long selectedUnitId = unitsSpinner.getSelectedItemId();
		purchase.getUnits().setId(selectedUnitId);

		if (nameEdit.getText() == null || nameEdit.getText().toString() == null
				|| nameEdit.getText().toString().trim().length() < 1) {
			nameEdit.setError(getString(R.string.purchasesFmErrorSpecifyName));
			return null;
		}

		purchase.setGoods(new Goods());
		purchase.getGoods().setId(selectedGoodsId);
		purchase.getGoods().setName(nameEdit.getText().toString().trim());

		if (categoryEdit.getText() == null || categoryEdit.getText().toString() == null
				|| categoryEdit.getText().toString().trim().length() < 1) {
			categoryEdit.setError(getString(R.string.purchasesFmErrorSpecifyCategory));
			return null;
		}

		purchase.getGoods().setCategory(new GoodsCategory());
		purchase.getGoods().getCategory().setId(selectedCategoryId);
		purchase.getGoods().getCategory().setName(categoryEdit.getText().toString().trim());

		return purchase;
	}

    private BigDecimal getCount(String count) {
        try {
            return new BigDecimal(count.trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
