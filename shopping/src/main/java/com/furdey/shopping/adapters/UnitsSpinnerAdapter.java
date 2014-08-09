package com.furdey.shopping.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.LongSparseArray;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.furdey.shopping.contentproviders.UnitsContentProvider;

public class UnitsSpinnerAdapter extends CursorAdapter {

	// private ViewHolder viewHolder;
	private boolean dropDown = false;
	private LongSparseArray<Integer> unitsPositions;

	public UnitsSpinnerAdapter(Context context) {
		super(context, null, 0);
		unitsPositions = new LongSparseArray<Integer>();
	}

	public UnitsSpinnerAdapter(Context context, Cursor cursor) {
		super(context, cursor, 0);
		unitsPositions = new LongSparseArray<Integer>(cursor.getCount());
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();

		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.nameInd = cursor.getColumnIndex(UnitsContentProvider.Columns.NAME.toString());
			viewHolder.descrInd = cursor.getColumnIndex(UnitsContentProvider.Columns.DESCR.toString());
			viewHolder.text = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(viewHolder);
		}

		// Unit model = UnitsDao.fromCursor(cursor);
		String name = cursor.getString(viewHolder.nameInd);
		String descr = cursor.getString(viewHolder.descrInd);

		if (dropDown)
			viewHolder.text.setText(descr.concat(", ").concat(name));
		else
			viewHolder.text.setText(name);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
		dropDown = false;
		return v;
	}

	@Override
	public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
		dropDown = true;
		return v;
	}

	private static class ViewHolder {
		public int nameInd;
		public int descrInd;
		public TextView text;
	}

	public int getIndexById(long id) {
		return unitsPositions.get(id);
	}

	@Override
	public Cursor swapCursor(Cursor newCursor) {
		unitsPositions.clear();

		if (newCursor != null) {
			newCursor.moveToPosition(-1);
			int idInd = newCursor.getColumnIndex(UnitsContentProvider.Columns._id.toString());

			while (newCursor.moveToNext()) {
				Long id = newCursor.getLong(idInd);
				unitsPositions.put(id, newCursor.getPosition());
			}

			newCursor.moveToPosition(-1);
		}

		return super.swapCursor(newCursor);
	}

};
