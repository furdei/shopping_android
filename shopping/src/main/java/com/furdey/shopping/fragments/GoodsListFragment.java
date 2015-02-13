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
import com.furdey.shopping.adapters.GoodsListAdapter;
import com.furdey.shopping.adapters.GoodsListAdapter.Mode;
import com.furdey.shopping.content.GoodsUtils;
import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.contentproviders.GoodsContentProvider;

public class GoodsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	public static GoodsListFragment newInstance(GoodsListAdapter.Mode mode, String filter) {
		GoodsListFragment fragment = new GoodsListFragment();
		Bundle args = new Bundle();
		args.putString(MODE_PARAM, mode.toString());
		args.putString(FILTER_PARAM, filter);
		fragment.setArguments(args);
		return fragment;
	}

    public static interface GoodsListListener {
		void onEditGoods(Goods goods);

		void onDeleteGoods(Goods goods);
	}

	private static final String MODE_PARAM = "mode";
	private static final String FILTER_PARAM = "filter";
    private static final int GOODS_LIST_LOADER = 0;
    private static final int GOODS_HEADER_LOADER = 1;
    private static final String GOODS_LIST_LOADER_FILTER = "goodsFilter";

	private GoodsListListener listener;
	private int contextPosition;
	private GoodsListAdapter adapter;
	private GoodsListAdapter.Mode mode;
	private Goods exactGoodsFound;
	private String filter;
    private boolean isSearchingNow = false;
    private String searchAgainFilter = null;

	private ListView grid;
	private View listHeader;
	private TextView listHeaderName;
	private SearchView searchView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (GoodsListListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement "
					+ GoodsListListener.class.getSimpleName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.base_list, container, false);
		mode = Mode.valueOf(getArguments().getString(MODE_PARAM));

		View listHeaderEnvelope = inflater.inflate(R.layout.goods_header_li, null, false);
		listHeader = listHeaderEnvelope.findViewById(R.id.goodsLi);
		ImageView headerIcon = (ImageView) listHeader.findViewById(R.id.goodsLiIcon);
		headerIcon.setImageResource(R.drawable.content_new_in_list);
		listHeaderName = (TextView) listHeader.findViewById(R.id.goodsLiName);
		TextView listHeaderDescr = (TextView) listHeader.findViewById(R.id.goodsLiDescr);
		listHeaderDescr.setText(R.string.goodsLiNewGoodDescr);
		listHeaderDescr.setTextColor(Color.RED);

		grid = (ListView) view.findViewById(R.id.baseListGrid);
		grid.addHeaderView(listHeaderEnvelope, null, true);
		adapter = new GoodsListAdapter(getActivity(), mode);
		grid.setAdapter(adapter);

		if (mode == Mode.GRID) {
			// we don't need a context menu in LOOKUP mode
			registerForContextMenu(grid);
		}

		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (position > 0) {
					Cursor cursor = (Cursor) adapter.getItem(position - 1);
					listener.onEditGoods(GoodsUtils.fromCursor(cursor));
				} else {
					// header item was clicked
					onNewGoods();
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
        onFillGoodsList(filter);

        ((ShoppingApplication) getActivity().getApplication())
                .trackViewScreen(GoodsListFragment.class);

    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.goods_list, menu);

		MenuItem menuItem = menu.findItem(R.id.searchView);
		searchView = (SearchView) menuItem.getActionView();
		// bug - some SearchView xml attributes don't work at compatibility library
		searchView.setIconified(false);
		searchView.setQueryHint(getString(R.string.menuGoodsListSearch));
        String filter = getArguments().getString(FILTER_PARAM);
        searchView.setQuery(filter, false);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String arg0) {
				return true;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				onFillGoodsList(arg0);
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuGoodsListNewRecord:
			onNewGoods();
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
				getActivity().getMenuInflater().inflate(R.menu.goods_list_context, menu);
				Cursor cursor = (Cursor) adapter.getItem(contextPosition);
				int goodsNameInd = cursor.getColumnIndex(GoodsContentProvider.Columns.NAME.toString());
				menu.setHeaderTitle(String.format(getString(R.string.menuGoodsListContextTitle),
						cursor.getString(goodsNameInd)));
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final Cursor cursor = (Cursor) adapter.getItem(contextPosition);

		switch (item.getItemId()) {
		case R.id.menuGoodsListEditRecord:
			listener.onEditGoods(GoodsUtils.fromCursor(cursor));
			return true;

		case R.id.menuGoodsListDeleteRecord:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			final Goods contextModel = GoodsUtils.fromCursor(cursor);
			builder.setTitle(R.string.goodsLiConfirmDeleteCaption);
			builder
					.setMessage(
							String.format(getString(R.string.goodsLiConfirmDeleteDetails), contextModel.getName()))
					.setNegativeButton(R.string.formButtonCancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
						}
					}).setPositiveButton(R.string.formButtonDelete, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							listener.onDeleteGoods(contextModel);
						}
					});
			// Create the AlertDialog object and return it
			builder.create().show();
			return true;
		}

		return super.onContextItemSelected(item);
	}

	public void onGoodsListReady(Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	public void onGoodsListReset() {
		adapter.swapCursor(null);
	}

	public void onExactGoodsFound(Goods goods) {
		exactGoodsFound = goods;
		updateHeaderVisibility();
	}

    // /////////////////////////////
    // // LoaderCallbacks<Cursor> //
    // /////////////////////////////

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        String filter = args.getString(GOODS_LIST_LOADER_FILTER);

        switch (loaderId) {
            case GOODS_LIST_LOADER:
                return GoodsUtils.getGoodsLoader(getActivity(), filter);
            case GOODS_HEADER_LOADER:
                return GoodsUtils.getExactGoodsLoader(getActivity(), filter);
        }
        return null;
    }

    @Override
    public synchronized void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case GOODS_LIST_LOADER:
                isSearchingNow = false;
                onGoodsListReady(cursor);
                searchAgainIfNeeded();
                break;
            case GOODS_HEADER_LOADER:
                Goods goods = cursor != null && cursor.moveToFirst() ?
                        GoodsUtils.fromCursor(cursor) : null;
                onExactGoodsFound(goods);
                break;
        }

        searchAgainIfNeeded();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        onGoodsListReset();
    }

    // /////////////////////////////
    // ////////// private //////////
    // /////////////////////////////

    private synchronized void onFillGoodsList(String filter) {
        if (!isSearchingNow) {
            isSearchingNow = true;
            searchAgainFilter = null;
            this.filter = filter;
            listHeaderName.setText(filter);
            updateHeaderVisibility();
            Bundle bundle = new Bundle();
            bundle.putString(GOODS_LIST_LOADER_FILTER, filter);
            getLoaderManager().restartLoader(GOODS_LIST_LOADER, bundle, this);
            getLoaderManager().restartLoader(GOODS_HEADER_LOADER, bundle, this);
        } else {
            // wait until current search is finished and repeat the search
            searchAgainFilter = filter;
        }
	}

	private void updateHeaderVisibility() {
		listHeader.setVisibility(filter == null || filter.trim().length() == 0
				|| exactGoodsFound != null ? View.GONE : View.VISIBLE);
	}

	private void onNewGoods() {
		Goods goods = new Goods();
		goods.setName(searchView.getQuery() != null ? searchView.getQuery().toString().trim() : null);
		listener.onEditGoods(goods);
	}

    private void searchAgainIfNeeded() {
        if (searchAgainFilter != null) {
            String filter = searchAgainFilter;
            searchAgainFilter = null;
            onFillGoodsList(filter);
        }
    }
}
