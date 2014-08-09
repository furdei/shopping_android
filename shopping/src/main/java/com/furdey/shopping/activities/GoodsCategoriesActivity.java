package com.furdey.shopping.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
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

public class GoodsCategoriesActivity extends ActionBarActivity implements
		GoodsCategoriesListListener, GoodsCategoriesFormListener, LoaderCallbacks<Cursor> {

	private static final String TAG = GoodsCategoriesActivity.class.getCanonicalName();

	private static final String GOODS_CATEGORIES_LIST_TAG = "goodsCategoriesListTag";
	private static final String GOODS_CATEGORIES_FORM_TAG = "goodsCategoriesFormTag";

	private static final int GOODS_CATEGORIES_LIST_LOADER = 0;
	private static final String GOODS_CATEGORIES_LIST_LOADER_FILTER = "categoriesFilter";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_fragment_activity);
		goToGoodsCategoriesList(false);
	}

	@Override
	public void onFillCategoriesList(String filter) {
		Bundle bundle = new Bundle();
		bundle.putString(GOODS_CATEGORIES_LIST_LOADER_FILTER, filter);
		getSupportLoaderManager().restartLoader(GOODS_CATEGORIES_LIST_LOADER, bundle, this);
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

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String filter = arg1.getString(GOODS_CATEGORIES_LIST_LOADER_FILTER);
		return GoodsCategoriesUtils.getGoodsCategoriesLoader(this, filter);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		GoodsCategoriesListFragment categoriesListFragment = (GoodsCategoriesListFragment) getSupportFragmentManager()
				.findFragmentByTag(GOODS_CATEGORIES_LIST_TAG);

		if (categoriesListFragment != null)
			categoriesListFragment.onCategoriesListReady(arg1);
		else
			Log.e(TAG, "GoodsCategoriesListFragment was expected but wasn't found");
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		GoodsCategoriesListFragment categoriesListFragment = (GoodsCategoriesListFragment) getSupportFragmentManager()
				.findFragmentByTag(GOODS_CATEGORIES_LIST_TAG);

		if (categoriesListFragment != null)
			categoriesListFragment.onCategoriesListReset();
		else
			Log.e(TAG, "GoodsCategoriesListFragment was expected but wasn't found");
	}

	private void goToGoodsCategoriesList(boolean addToBackStack) {
		GoodsCategoriesListFragment categoriesListFragment = (GoodsCategoriesListFragment) getSupportFragmentManager()
				.findFragmentByTag(GOODS_CATEGORIES_LIST_TAG);

		if (categoriesListFragment != null) {
			Log.d(TAG, "already at the list");
			return;
		}

		categoriesListFragment = GoodsCategoriesListFragment.newInstance(Mode.GRID, null);
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction().add(
				R.id.dynamic_fragment_container, categoriesListFragment, GOODS_CATEGORIES_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
	}

	private void returnToCategoriesList() {
		getSupportFragmentManager().popBackStack();
		setTitle(R.string.goodsCategoriesLiTitle);
	}

	private void goToGoodsCategoriesForm(GoodsCategory category, boolean addToBackStack) {
		GoodsCategoriesFormFragment categoriesFormFragment = (GoodsCategoriesFormFragment) getSupportFragmentManager()
				.findFragmentByTag(GOODS_CATEGORIES_FORM_TAG);

		if (categoriesFormFragment == null)
			categoriesFormFragment = GoodsCategoriesFormFragment.newInstance(category);

		int title = category == null ? R.string.goodsCategoriesFmTitleAdd
				: R.string.goodsCategoriesFmTitleEdit;

		FragmentTransaction tr = getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.dynamic_fragment_container, categoriesFormFragment, GOODS_CATEGORIES_FORM_TAG)
				.setBreadCrumbTitle(title);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
		setTitle(title);
	}

}