package com.furdey.shopping.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.furdey.shopping.R;
import com.furdey.shopping.content.UnitsUtils;
import com.furdey.shopping.content.model.Unit;

public class UnitsListAdapter extends CursorAdapter {

	public UnitsListAdapter(Context context) {
		super(context, null, 0);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Unit model = UnitsUtils.fromCursor(cursor);

		TextView name = (TextView) view.findViewById(R.id.unitsLiName);
		name.setText(model.getName());

		TextView descr = (TextView) view.findViewById(R.id.unitsLiDescr);
		descr.setText(model.getDescr());
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.units_li, parent, false);
		bindView(v, context, cursor);
		return v;
	}

}
