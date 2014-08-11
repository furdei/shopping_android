package com.furdey.shopping.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.furdey.shopping.R;
import com.furdey.shopping.adapters.GoodsListAdapter.Mode;
import com.furdey.shopping.content.GoodsCategoriesUtils;
import com.furdey.shopping.content.GoodsUtils;
import com.furdey.shopping.content.UnitsUtils;
import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.fragments.GoodsCategoriesListFragment;
import com.furdey.shopping.fragments.GoodsCategoriesListFragment.GoodsCategoriesListListener;
import com.furdey.shopping.fragments.GoodsFormFragment;
import com.furdey.shopping.fragments.GoodsFormFragment.GoodsFormListener;
import com.furdey.shopping.fragments.GoodsListFragment;
import com.furdey.shopping.fragments.GoodsListFragment.GoodsListListener;
import com.furdey.shopping.tasks.ToastThrowableAsyncTask;

public class GoodsActivity extends ActionBarActivity implements GoodsListListener,
		GoodsFormListener, GoodsCategoriesListListener, LoaderCallbacks<Cursor> {

	private static final String TAG = GoodsActivity.class.getCanonicalName();

	private static final String GOODS_LIST_TAG = "goodsListTag";
	private static final String GOODS_FORM_TAG = "goodsFormTag";
	private static final String CATEGORIES_LIST_TAG = "categoriesListTag";

	private static final int GOODS_LIST_LOADER = 0;
	private static final int GOODS_HEADER_LOADER = 1;
	private static final int UNITS_LIST_LOADER = 2;
	private static final int CATEGORIES_LIST_LOADER = 3;
	private static final String GOODS_LOADER_FILTER = "goodsFilter";
	private static final String CATEGORIES_LIST_LOADER_FILTER = "categoriesFilter";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_fragment_activity);
		goToGoodsList(false);
	}

	// /////////////////////////////
	// ///// GoodsListListener /////
	// /////////////////////////////

	@Override
	public void onFillGoodsList(String filter) {
		Bundle bundle = new Bundle();
		bundle.putString(GOODS_LOADER_FILTER, filter);
		getSupportLoaderManager().restartLoader(GOODS_LIST_LOADER, bundle, this);
		getSupportLoaderManager().restartLoader(GOODS_HEADER_LOADER, bundle, this);
	}

	@Override
	public void onEditGoods(Goods goods) {
		goToGoodsForm(goods, true);
	}

	@Override
	public void onDeleteGoods(Goods goods) {
		new ToastThrowableAsyncTask<Goods, Void>(getApplicationContext()) {
			@Override
			protected Void doBackgroundWork(Goods param) throws Throwable {
				GoodsUtils.deleteGoods(getApplicationContext(), param.getId());
				return null;
			}

			@Override
			protected void onSuccess(Void result) {
				Toast.makeText(getApplicationContext(), R.string.goodsLiItemDeleted, Toast.LENGTH_LONG)
						.show();
			}

		}.execute(goods);
	}

	// /////////////////////////////
	// ///// GoodsFormListener /////
	// /////////////////////////////

	@Override
	public void onGoodsFormReady() {
		getSupportLoaderManager().initLoader(UNITS_LIST_LOADER, null, this);
	}

	@Override
	public void onSaveGoods(Goods goods) {
		returnToGoodsList();
		new ToastThrowableAsyncTask<Goods, Uri>(getApplicationContext()) {
			@Override
			protected Uri doBackgroundWork(Goods param) throws Exception {
				return GoodsUtils.saveGoods(getApplicationContext(), param);
			}
		}.execute(goods);
	}

	@Override
	public void onCancelGoodsEdit() {
		returnToGoodsList();
	}

	@Override
	public void onSelectGoodsCategory(String filter) {
		goToCategoriesList(filter, true);
	}

	// /////////////////////////////////
	// // GoodsCategoriesListListener //
	// /////////////////////////////////

	@Override
	public void onFillCategoriesList(String filter) {
		Bundle args = new Bundle();
		args.putString(CATEGORIES_LIST_LOADER_FILTER, filter);
		getSupportLoaderManager().restartLoader(CATEGORIES_LIST_LOADER, args, this);
	}

	/**
	 * Here we just select a category to replace the old one at the goods details
	 * form
	 * 
	 * @param category
	 */
	@Override
	public void onEditCategory(GoodsCategory category) {
		getSupportFragmentManager().popBackStack();
		GoodsFormFragment goodsFormFragment = getGoodsFormFragment();
		goodsFormFragment.setCategory(category);
	}

	@Override
	public void onDeleteCategory(GoodsCategory category) {
		throw new UnsupportedOperationException();
	}

	// /////////////////////////////
	// ////// LoaderCallbacks //////
	// /////////////////////////////

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		switch (loaderId) {
		case GOODS_LIST_LOADER:
			String filter = args.getString(GOODS_LOADER_FILTER);
			return GoodsUtils.getGoodsLoader(this, filter);

		case GOODS_HEADER_LOADER:
			filter = args.getString(GOODS_LOADER_FILTER);
			return GoodsUtils.getExactGoodsLoader(this, filter);

		case UNITS_LIST_LOADER:
			return UnitsUtils.getUnitsLoader(this);

		case CATEGORIES_LIST_LOADER:
			filter = args.getString(CATEGORIES_LIST_LOADER_FILTER);
			return GoodsCategoriesUtils.getGoodsCategoriesLoader(this, filter);
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		case GOODS_LIST_LOADER:
			GoodsListFragment goodsListFragment = getGoodsListFragment();

			if (goodsListFragment != null)
				goodsListFragment.onGoodsListReady(cursor);
			else
				Log.e(TAG, "GoodsListFragment was expected but wasn't found");
			break;

		case GOODS_HEADER_LOADER:
			goodsListFragment = getGoodsListFragment();

			if (goodsListFragment != null) {
				Goods goods = cursor.moveToFirst() ? GoodsUtils.fromCursor(cursor) : null;
				goodsListFragment.onExactGoodsFound(goods);
			} else
				Log.e(TAG, "GoodsListFragment was expected but wasn't found");
			break;

		case UNITS_LIST_LOADER:
			GoodsFormFragment goodsFormFragment = getGoodsFormFragment();

			if (goodsFormFragment != null)
				goodsFormFragment.setUnitsCursor(cursor);
			else
				Log.e(TAG, "GoodsFormFragment was expected but wasn't found");
			break;

		case CATEGORIES_LIST_LOADER:
			GoodsCategoriesListFragment goodsCategoriesListFragment = getGoodsCategoriesListFragment();

			if (goodsCategoriesListFragment != null)
				goodsCategoriesListFragment.onCategoriesListReady(cursor);
			else
				Log.e(TAG, "GoodsCategoriesListFragment was expected but wasn't found");
			break;

		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
		case GOODS_LIST_LOADER:
			GoodsListFragment goodsListFragment = getGoodsListFragment();

			if (goodsListFragment != null)
				goodsListFragment.onGoodsListReset();
			else
				Log.e(TAG, "GoodsListFragment was expected but wasn't found");
			break;

		case UNITS_LIST_LOADER:
			GoodsFormFragment goodsFormFragment = getGoodsFormFragment();

			if (goodsFormFragment != null)
				goodsFormFragment.setUnitsCursor(null);
			else
				Log.e(TAG, "GoodsFormFragment was expected but wasn't found");
			break;

		case CATEGORIES_LIST_LOADER:
			GoodsCategoriesListFragment goodsCategoriesListFragment = getGoodsCategoriesListFragment();

			if (goodsCategoriesListFragment != null)
				goodsCategoriesListFragment.onCategoriesListReset();
			else
				Log.e(TAG, "GoodsCategoriesListFragment was expected but wasn't found");
			break;
		}
	}

	// /////////////////////////////
	// ////////// private //////////
	// /////////////////////////////

	private GoodsListFragment getGoodsListFragment() {
		return (GoodsListFragment) getSupportFragmentManager().findFragmentByTag(GOODS_LIST_TAG);
	}

	private void goToGoodsList(boolean addToBackStack) {
		GoodsListFragment goodsListFragment = getGoodsListFragment();

		if (goodsListFragment != null) {
			Log.d(TAG, "already at the list");
			return;
		}

		goodsListFragment = GoodsListFragment.newInstance(Mode.GRID, null);
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction().add(
				R.id.dynamic_fragment_container, goodsListFragment, GOODS_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
	}

	private void returnToGoodsList() {
		getSupportFragmentManager().popBackStack();
		setTitle(R.string.goodsLiTitleGrid);
	}

	private GoodsFormFragment getGoodsFormFragment() {
		return (GoodsFormFragment) getSupportFragmentManager().findFragmentByTag(GOODS_FORM_TAG);
	}

	private void goToGoodsForm(Goods goods, boolean addToBackStack) {
		GoodsFormFragment goodsFormFragment = getGoodsFormFragment();

		if (goodsFormFragment == null)
			goodsFormFragment = GoodsFormFragment.newInstance(goods);

		int title = getGoodsFormTitle(goods);

		FragmentTransaction tr = getSupportFragmentManager().beginTransaction()
				.replace(R.id.dynamic_fragment_container, goodsFormFragment, GOODS_FORM_TAG)
				.setBreadCrumbTitle(title);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
		setTitle(title);
	}

	private int getGoodsFormTitle(Goods goods) {
		return goods == null ? R.string.goodsFmTitleAdd : R.string.goodsFmTitleEdit;
	}

	private GoodsCategoriesListFragment getGoodsCategoriesListFragment() {
		return (GoodsCategoriesListFragment) getSupportFragmentManager().findFragmentByTag(
				CATEGORIES_LIST_TAG);
	}

	private void goToCategoriesList(String filter, boolean addToBackStack) {
		GoodsCategoriesListFragment categoriesListFragment = getGoodsCategoriesListFragment();

		if (categoriesListFragment != null) {
			Log.d(TAG, "already at the list");
			return;
		}

		categoriesListFragment = GoodsCategoriesListFragment.newInstance(
				GoodsCategoriesListFragment.Mode.LOOKUP, filter);
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction().add(
				R.id.dynamic_fragment_container, categoriesListFragment, CATEGORIES_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
		setTitle(R.string.goodsCategoriesLiTitleLookup);

		getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
			public void onBackStackChanged() {
				GoodsCategoriesListFragment categoriesListFragment = getGoodsCategoriesListFragment();

				if (categoriesListFragment == null) {
					// category pick up window is closed
					GoodsFormFragment goodsFormFragment = getGoodsFormFragment();

					if (goodsFormFragment == null) {
						Log.e(TAG, "GoodsFormFragment was expected but wasn't found");
						return;
					}

					Goods goods = goodsFormFragment.getParameterGoods();
					int title = getGoodsFormTitle(goods);
					setTitle(title);
					getSupportFragmentManager().removeOnBackStackChangedListener(this);
				}
			}
		});
	}

}
