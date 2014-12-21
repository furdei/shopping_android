package com.furdey.shopping.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.furdey.shopping.R;
import com.furdey.shopping.adapters.GoodsListAdapter.Mode;
import com.furdey.shopping.content.GoodsUtils;
import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.fragments.GoodsCategoriesListFragment;
import com.furdey.shopping.fragments.GoodsCategoriesListFragment.GoodsCategoriesListListener;
import com.furdey.shopping.fragments.GoodsFormFragment;
import com.furdey.shopping.fragments.GoodsFormFragment.GoodsFormListener;
import com.furdey.shopping.fragments.GoodsListFragment;
import com.furdey.shopping.fragments.GoodsListFragment.GoodsListListener;
import com.furdey.shopping.tasks.ToastThrowableAsyncTask;

public class GoodsActivity extends BaseActivity implements GoodsListListener,
		GoodsFormListener, GoodsCategoriesListListener {

	private static final String TAG = GoodsActivity.class.getCanonicalName();

	private static final String GOODS_LIST_TAG = "goodsListTag";
	private static final String GOODS_FORM_TAG = "goodsFormTag";
	private static final String CATEGORIES_LIST_TAG = "categoriesListTag";

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

	/**
	 * Here we just select a category to replace the old one at the goods details
	 * form
	 * 
	 * @param category
	 */
	@Override
	public void onEditCategory(GoodsCategory category) {
        getFragmentManager().popBackStack();
		GoodsFormFragment goodsFormFragment = getGoodsFormFragment();
		goodsFormFragment.setCategory(category);
	}

	@Override
	public void onDeleteCategory(GoodsCategory category) {
		throw new UnsupportedOperationException();
	}

	// /////////////////////////////
	// ////////// private //////////
	// /////////////////////////////

	private GoodsListFragment getGoodsListFragment() {
		return (GoodsListFragment) getFragmentManager().findFragmentByTag(GOODS_LIST_TAG);
	}

	private void goToGoodsList(boolean addToBackStack) {
		GoodsListFragment goodsListFragment = getGoodsListFragment();

		if (goodsListFragment != null) {
			Log.d(TAG, "already at the list");
			return;
		}

		goodsListFragment = GoodsListFragment.newInstance(Mode.GRID, null);
		FragmentTransaction tr = getFragmentManager().beginTransaction().add(
				R.id.dynamic_fragment_container, goodsListFragment, GOODS_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
	}

	private void returnToGoodsList() {
		getFragmentManager().popBackStack();
		setTitle(R.string.goodsLiTitleGrid);
	}

	private GoodsFormFragment getGoodsFormFragment() {
		return (GoodsFormFragment) getFragmentManager().findFragmentByTag(GOODS_FORM_TAG);
	}

	private void goToGoodsForm(Goods goods, boolean addToBackStack) {
		GoodsFormFragment goodsFormFragment = getGoodsFormFragment();

		if (goodsFormFragment == null)
			goodsFormFragment = GoodsFormFragment.newInstance(goods);

		int title = getGoodsFormTitle(goods);

		FragmentTransaction tr = getFragmentManager().beginTransaction()
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
		return (GoodsCategoriesListFragment) getFragmentManager().findFragmentByTag(
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
		FragmentTransaction tr = getFragmentManager().beginTransaction().add(
				R.id.dynamic_fragment_container, categoriesListFragment, CATEGORIES_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
		setTitle(R.string.goodsCategoriesLiTitleLookup);

		getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
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
					getFragmentManager().removeOnBackStackChangedListener(this);
				}
			}
		});
	}

}
