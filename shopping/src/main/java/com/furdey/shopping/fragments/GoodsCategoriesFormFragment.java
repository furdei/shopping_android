package com.furdey.shopping.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.furdey.shopping.R;
import com.furdey.shopping.ShoppingApplication;
import com.furdey.shopping.adapters.IconsGalleryAdapter;
import com.furdey.shopping.content.GoodsCategoriesUtils;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.tasks.LoadIconTask;
import com.furdey.shopping.utils.TemplateRunnable;

public class GoodsCategoriesFormFragment extends Fragment {
	/**
	 * @param category
	 *          can be null for a new record
	 * @return
	 */
	public static GoodsCategoriesFormFragment newInstance(GoodsCategory category) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(PARAM_CATEGORY, category != null ? category : new GoodsCategory());
		GoodsCategoriesFormFragment fragment = new GoodsCategoriesFormFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	public static interface GoodsCategoriesFormListener {
		void onSaveCategory(GoodsCategory category);

		void onCancelCategoryEdit();
	}

	private GoodsCategoriesFormListener listener;

	private static final String PARAM_CATEGORY = "goodsCategory";
	private static final String SAVE_SELECTED_ICON = "selectedIcon";

	private EditText nameEdit;
	private EditText descrEdit;
	private ImageView icon;
	private GridView iconsGallery;
	private Button saveButton;
	private Button cancelButton;

    private boolean isFragmentCreated;
    private String selectedIcon;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (GoodsCategoriesFormListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement "
					+ GoodsCategoriesFormListener.class.getSimpleName());
		}
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFragmentCreated = false;

        if (savedInstanceState != null) {
            selectedIcon = savedInstanceState.getString(SAVE_SELECTED_ICON);
            isFragmentCreated = true;
        }

        ((ShoppingApplication) getActivity().getApplication())
                .trackViewScreen(GoodsCategoriesFormFragment.class);

    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.goods_categories_form, container, false);

		nameEdit = (EditText) view.findViewById(R.id.goodsCategoriesFmNameEdit);
		descrEdit = (EditText) view.findViewById(R.id.goodsCategoriesFmDescrEdit);
		icon = (ImageView) view.findViewById(R.id.goodsCategoriesFmIcon);
		iconsGallery = (GridView) view.findViewById(R.id.goodsCategoriesFmIconsGallery);
		saveButton = (Button) view.findViewById(R.id.formButtonSave);
		cancelButton = (Button) view.findViewById(R.id.formButtonCancel);

		GoodsCategory model = (GoodsCategory) getArguments().getSerializable(PARAM_CATEGORY);
		nameEdit.setText(model.getName());

		if (model.getId() == null) {
			// adding a new category
			nameEdit.requestFocus();
		} else {
			// editing an existing category
			descrEdit.setText(model.getDescr());

            if (!isFragmentCreated) {
                selectedIcon = model.getIcon();
            }

			if (model.getIcon() != null) {
				LoadIconTask task = new LoadIconTask();
				task.loadIcon(icon, selectedIcon, 1, false, getActivity().getApplicationContext());
			} else {
				icon.setImageResource(R.drawable.nothing);
			}

			cancelButton.requestFocus();
		}

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onCancelCategoryEdit();
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				constructModelFromUi(new TemplateRunnable<GoodsCategory>() {
					@Override
					public void run(GoodsCategory newModel) {
						listener.onSaveCategory(newModel);
					}
				});
			}
		});

		iconsGallery.setAdapter(new IconsGalleryAdapter(getActivity().getApplicationContext()));
		iconsGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String iconName = (String) iconsGallery.getAdapter().getItem(arg2);
                selectedIcon = iconName;
				LoadIconTask task = new LoadIconTask();
				task.loadIcon(icon, iconName, 1, false, getActivity().getApplicationContext());
			}
		});

//		setRetainInstance(true);
        isFragmentCreated = true;
		return view;
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            outState.putString(SAVE_SELECTED_ICON, selectedIcon);
        }
    }

    @SuppressWarnings("unchecked")
	private void constructModelFromUi(final TemplateRunnable<GoodsCategory> callback) {
		final GoodsCategory category = new GoodsCategory();
		GoodsCategory paramCategory = (GoodsCategory) getArguments().getSerializable(PARAM_CATEGORY);
		category.setId(paramCategory.getId());

		if (nameEdit.getText() != null && nameEdit.getText().toString() != null
				&& nameEdit.getText().toString().trim().length() > 0) {
			category.setName(nameEdit.getText().toString().trim());
		} else {
			nameEdit.setError(getString(R.string.goodsCategoriesFmErrorSpecifyName));
			return;
		}

		if (descrEdit.getText() != null && descrEdit.getText().toString() != null
				&& descrEdit.getText().toString().trim().length() > 0) {
			category.setDescr(descrEdit.getText().toString().trim());
		}

		category.setIcon(selectedIcon);

		// check if a category already exists
		new AsyncTask<GoodsCategory, Void, GoodsCategory>() {
			@Override
			protected GoodsCategory doInBackground(GoodsCategory... params) {
				Cursor cursor = GoodsCategoriesUtils.getGoodsCategoriesByName(getActivity(),
						params[0].getName());
                GoodsCategory goodsCategory = null;

                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        goodsCategory = GoodsCategoriesUtils.fromCursor(cursor);
                    }

                    cursor.close();
                }

                return goodsCategory;
			}

			@Override
			protected void onPostExecute(GoodsCategory result) {
				if (result == null || (category.getId() != null && result.getId().equals(category.getId()))) {
					// there are no same categories found - success
					// or maybe the found one is the same category that we're going to save
                    callback.run(category);
				} else {
					nameEdit.setError(getString(R.string.goodsCategoriesFmErrorAlreadyExists));
				}
			}

		}.execute(category);
	}

}
