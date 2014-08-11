package com.furdey.shopping.listeners;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

import com.furdey.shopping.content.UnitsUtils;

public class UnitsLoaderCallbacks implements LoaderCallbacks<Cursor> {

	private Context context;
	private CursorAdapter adapter;

	public UnitsLoaderCallbacks(Context context, CursorAdapter adapter) {
		super();
		this.context = context;
		this.adapter = adapter;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return UnitsUtils.getUnitsLoader(context);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		adapter.swapCursor(arg1);
	}

}
