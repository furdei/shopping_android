package com.furdey.shopping.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
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
import android.widget.ListView;

import com.furdey.shopping.R;
import com.furdey.shopping.ShoppingApplication;
import com.furdey.shopping.adapters.UnitsListAdapter;
import com.furdey.shopping.content.UnitsUtils;
import com.furdey.shopping.content.model.Unit;
import com.furdey.shopping.contentproviders.UnitsContentProvider;

public class UnitsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	public static interface UnitsListListener {
		void onEditUnit(Unit unit);

		void onDeleteUnit(Unit unit);
	}

    private static final int UNITS_LIST_LOADER = 0;

    private UnitsListListener listener;

	private int contextPosition;

	private UnitsListAdapter adapter;

	private ListView grid;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (UnitsListListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement "
					+ UnitsListListener.class.getSimpleName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.base_list, container, false);

		grid = (ListView) view.findViewById(R.id.baseListGrid);
		adapter = new UnitsListAdapter(getActivity());
		grid.setAdapter(adapter);
		registerForContextMenu(grid);
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Cursor cursor = (Cursor) adapter.getItem(position);
				listener.onEditUnit(UnitsUtils.fromCursor(cursor));
			}

		});

		setHasOptionsMenu(true);
		return view;
	}

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(UNITS_LIST_LOADER, null, this);

        ((ShoppingApplication) getActivity().getApplication())
                .trackViewScreen(UnitsListFragment.class);

    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.units_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuUnitsListNewRecord:
			listener.onEditUnit(null);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.baseListGrid) {
			getActivity().getMenuInflater().inflate(R.menu.units_list_context, menu);
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			contextPosition = info.position;

			Cursor cursor = (Cursor) adapter.getItem(contextPosition);
			int goodsNameInd = cursor.getColumnIndex(UnitsContentProvider.Columns.NAME.toString());
			menu.setHeaderTitle(String.format(getString(R.string.menuUnitsListContextTitle),
					cursor.getString(goodsNameInd)));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final Cursor cursor = (Cursor) adapter.getItem(contextPosition);

		switch (item.getItemId()) {
		case R.id.menuUnitsListEditRecord:
			listener.onEditUnit(UnitsUtils.fromCursor(cursor));
			return true;

		case R.id.menuUnitsListDeleteRecord:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			final Unit contextModel = UnitsUtils.fromCursor(cursor);
			builder.setTitle(R.string.unitsLiConfirmDeleteCaption);
			builder
					.setMessage(
							String.format(getString(R.string.unitsLiConfirmDeleteDetails), contextModel.getName()))
					.setNegativeButton(R.string.formButtonCancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
						}
					}).setPositiveButton(R.string.formButtonDelete, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							listener.onDeleteUnit(contextModel);
						}
					});
			// Create the AlertDialog object and return it
			builder.create().show();
			return true;
		}

		return super.onContextItemSelected(item);
	}

	public void onUnitsListReady(Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	public void onUnitsListReset() {
		adapter.swapCursor(null);
	}

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return UnitsUtils.getUnitsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        onUnitsListReady(arg1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        onUnitsListReset();
    }

}
