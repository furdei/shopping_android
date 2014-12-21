package com.furdey.shopping.activities;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.furdey.shopping.R;
import com.furdey.shopping.content.GoodsCategoriesUtils;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.fragments.GoodsCategoriesFormFragment;
import com.furdey.shopping.fragments.GoodsCategoriesFormFragment.GoodsCategoriesFormListener;
import com.furdey.shopping.fragments.GoodsCategoriesListFragment;
import com.furdey.shopping.fragments.GoodsCategoriesListFragment.GoodsCategoriesListListener;
import com.furdey.shopping.fragments.GoodsCategoriesListFragment.Mode;
import com.furdey.shopping.tasks.ToastThrowableAsyncTask;


public class GoodsCategoriesActivity extends BaseActivity implements
		GoodsCategoriesListListener, GoodsCategoriesFormListener {

	private static final String TAG = GoodsCategoriesActivity.class.getCanonicalName();

	private static final String GOODS_CATEGORIES_LIST_TAG = "goodsCategoriesListTag";
	private static final String GOODS_CATEGORIES_FORM_TAG = "goodsCategoriesFormTag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_fragment_activity);
		goToGoodsCategoriesList(false);
	}

	@Override
	public void onEditCategory(GoodsCategory category) {
		goToGoodsCategoriesForm(category, true);
	}

	@Override
	public void onDeleteCategory(GoodsCategory category) {
		new ToastThrowableAsyncTask<GoodsCategory, Void>(getApplicationContext()) {
			@Override
			protected Void doBackgroundWork(GoodsCategory param) throws Throwable {
				GoodsCategoriesUtils.deleteGoodsCategory(getApplicationContext(), param.getId());
				return null;
			}

			@Override
			protected void onSuccess(Void result) {
				Toast.makeText(getApplicationContext(), R.string.goodsCategoriesLiItemDeleted,
						Toast.LENGTH_LONG).show();
			}

		}.execute(category);
	}

	@Override
	public void onSaveCategory(GoodsCategory category) {
		returnToCategoriesList();
		new ToastThrowableAsyncTask<GoodsCategory, Uri>(getApplicationContext()) {
			@Override
			protected Uri doBackgroundWork(GoodsCategory param) throws Throwable {
				return GoodsCategoriesUtils.saveGoodsCategory(getApplicationContext(), param);
			}
		}.execute(category);
	}

	@Override
	public void onCancelCategoryEdit() {
		returnToCategoriesList();
	}

	private void goToGoodsCategoriesList(boolean addToBackStack) {
		GoodsCategoriesListFragment categoriesListFragment = (GoodsCategoriesListFragment) getFragmentManager()
				.findFragmentByTag(GOODS_CATEGORIES_LIST_TAG);

		if (categoriesListFragment != null) {
			Log.d(TAG, "already at the list");
			return;
		}

		categoriesListFragment = GoodsCategoriesListFragment.newInstance(Mode.GRID, null);
		FragmentTransaction tr = getFragmentManager().beginTransaction().add(
				R.id.dynamic_fragment_container, categoriesListFragment, GOODS_CATEGORIES_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
	}

	private void returnToCategoriesList() {
		getFragmentManager().popBackStack();
		setTitle(R.string.goodsCategoriesLiTitle);
	}

	private void goToGoodsCategoriesForm(GoodsCategory category, boolean addToBackStack) {
		GoodsCategoriesFormFragment categoriesFormFragment = (GoodsCategoriesFormFragment) getFragmentManager()
				.findFragmentByTag(GOODS_CATEGORIES_FORM_TAG);

		if (categoriesFormFragment == null)
			categoriesFormFragment = GoodsCategoriesFormFragment.newInstance(category);

		int title = category == null ? R.string.goodsCategoriesFmTitleAdd
				: R.string.goodsCategoriesFmTitleEdit;

		FragmentTransaction tr = getFragmentManager()
				.beginTransaction()
				.replace(R.id.dynamic_fragment_container, categoriesFormFragment, GOODS_CATEGORIES_FORM_TAG)
				.setBreadCrumbTitle(title);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
		setTitle(title);
	}

}
