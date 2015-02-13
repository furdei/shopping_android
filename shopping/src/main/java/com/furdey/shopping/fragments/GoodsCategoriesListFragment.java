package com.furdey.shopping.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.furdey.shopping.R;
import com.furdey.shopping.ShoppingApplication;
import com.furdey.shopping.adapters.GoodsCategoriesListAdapter;
import com.furdey.shopping.content.GoodsCategoriesUtils;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.contentproviders.GoodsCategoriesContentProvider;
import com.furdey.shopping.fragments.UnitsListFragment.UnitsListListener;

public class GoodsCategoriesListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

	public static GoodsCategoriesListFragment newInstance(Mode mode, String filter) {
		GoodsCategoriesListFragment fragment = new GoodsCategoriesListFragment();
		Bundle args = new Bundle();
		args.putString(MODE_PARAM, mode.toString());
		args.putString(FILTER_PARAM, filter);
		fragment.setArguments(args);
		return fragment;
	}

    public static interface GoodsCategoriesListListener {
		void onEditCategory(GoodsCategory category);

		void onDeleteCategory(GoodsCategory category);
	}

	public enum Mode {
		GRID, LOOKUP
	}

	private static final String MODE_PARAM = "mode";
	private static final String FILTER_PARAM = "filter";
    private static final String CATEGORIES_LIST_LOADER_FILTER = "categoriesFilter";
    private static final int CATEGORIES_LIST_LOADER = 0;

	private GoodsCategoriesListListener listener;

	private int contextPosition;

	private GoodsCategoriesListAdapter adapter;

	private ListView grid;
	private View listHeader;
	private TextView listHeaderName;
	private SearchView searchView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (GoodsCategoriesListListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement "
					+ UnitsListListener.class.getSimpleName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.base_list, container, false);

		listHeader = inflater.inflate(R.layout.goods_categories_li, null, false);
		ImageView headerIcon = (ImageView) listHeader.findViewById(R.id.goodsCategoriesLiIcon);
		headerIcon.setImageResource(R.drawable.content_new_in_list);
		listHeaderName = (TextView) listHeader.findViewById(R.id.goodsCategoriesLiName);
		TextView listHeaderDescr = (TextView) listHeader.findViewById(R.id.goodsCategoriesLiDescr);
		listHeaderDescr.setText(R.string.goodsCategoriesLiNewCategoryDescr);
		listHeaderDescr.setTextColor(Color.RED);

		grid = (ListView) view.findViewById(R.id.baseListGrid);
		grid.addHeaderView(listHeader, null, true);
		adapter = new GoodsCategoriesListAdapter(getActivity());
		grid.setAdapter(adapter);

		String modeStr = getArguments().getString(MODE_PARAM);
		Mode mode = modeStr != null ? Mode.valueOf(modeStr) : Mode.GRID;
		if (mode == Mode.GRID) {
			registerForContextMenu(grid);
		}
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (position > 0) {
					Cursor cursor = (Cursor) adapter.getItem(position - 1);
					listener.onEditCategory(GoodsCategoriesUtils.fromCursor(cursor));
				} else {
					// header item was clicked
					GoodsCategory category = new GoodsCategory();
					category.setName(searchView.getQuery() != null ? searchView.getQuery().toString().trim()
							: null);
					listener.onEditCategory(category);
				}
			}

		});

		setHasOptionsMenu(true);
//		setRetainInstance(true);

		return view;
	}

    @Override
    public void onResume() {
        super.onResume();
        String filter = getArguments().getString(FILTER_PARAM);
        onFillCategoriesList(filter);

        ((ShoppingApplication) getActivity().getApplication())
                .trackViewScreen(GoodsCategoriesListFragment.class);

    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.goods_categories_list, menu);

		MenuItem menuItem = menu.findItem(R.id.searchView);
		searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
		// bug - some SearchView xml attributes don't work at compatibility library
		searchView.setIconified(false);
		searchView.setQueryHint(getString(R.string.menuGoodsCategoriesListSearch));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String arg0) {
				return true;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				onFillCategoriesList(arg0);
				return true;
			}
		});

		String filter = getArguments().getString(FILTER_PARAM);
		searchView.setQuery(filter, false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuGoodsCategoriesListNewRecord:
			listener.onEditCategory(null);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.baseListGrid) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			contextPosition = info.position - 1;

			if (contextPosition >= 0) {
				getActivity().getMenuInflater().inflate(R.menu.goods_categories_list_context, menu);
				Cursor cursor = (Cursor) adapter.getItem(contextPosition);
				int goodsNameInd = cursor.getColumnIndex(GoodsCategoriesContentProvider.Columns.NAME
						.toString());
				menu.setHeaderTitle(String.format(getString(R.string.menuGoodsCategoriesListContextTitle),
						cursor.getString(goodsNameInd)));
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final Cursor cursor = (Cursor) adapter.getItem(contextPosition);

		switch (item.getItemId()) {
		case R.id.menuGoodsCategoriesListEditRecord:
			listener.onEditCategory(GoodsCategoriesUtils.fromCursor(cursor));
			return true;

		case R.id.menuGoodsCategoriesListDeleteRecord:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			final GoodsCategory contextModel = GoodsCategoriesUtils.fromCursor(cursor);
			builder.setTitle(R.string.goodsCategoriesLiConfirmDeleteCaption);
			builder
					.setMessage(
							String.format(getString(R.string.goodsCategoriesLiConfirmDeleteDetails),
									contextModel.getName()))
					.setNegativeButton(R.string.formButtonCancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
						}
					}).setPositiveButton(R.string.formButtonDelete, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							listener.onDeleteCategory(contextModel);
						}
					});
			// Create the AlertDialog object and return it
			builder.create().show();
			return true;
		}

		return super.onContextItemSelected(item);
	}

	public void onCategoriesListReady(Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	public void onCategoriesListReset() {
		adapter.swapCursor(null);
	}

    // /////////////////////////////
    // // LoaderCallbacks<Cursor> //
    // /////////////////////////////

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String filter = args.getString(CATEGORIES_LIST_LOADER_FILTER);
        return GoodsCategoriesUtils.getGoodsCategoriesLoader(getActivity(), filter);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        onCategoriesListReady(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        onCategoriesListReset();
    }

    // /////////////////////////////
    // ////////// private //////////
    // /////////////////////////////

    private void onFillCategoriesList(String filter) {
		listHeaderName.setText(filter);
		listHeaderName.setVisibility(filter == null || filter.trim().length() == 0 ? View.GONE
				: View.VISIBLE);

        Bundle args = new Bundle();
        args.putString(CATEGORIES_LIST_LOADER_FILTER, filter);
        getLoaderManager().restartLoader(CATEGORIES_LIST_LOADER, args, this);

    }

}
