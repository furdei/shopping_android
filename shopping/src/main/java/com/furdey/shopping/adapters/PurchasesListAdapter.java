package com.furdey.shopping.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.furdey.shopping.R;
import com.furdey.shopping.content.model.Purchase.PurchaseState;
import com.furdey.shopping.contentproviders.PurchasesContentProvider;
import com.furdey.shopping.tasks.LoadIconTask;

public class PurchasesListAdapter extends CursorAdapter {
	private ViewHolder viewHolder;

	public PurchasesListAdapter(Context context) {
		super(context, null, 0);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.stateInd = cursor
					.getColumnIndex(PurchasesContentProvider.Columns.STATE.toString());
			viewHolder.goodsNameInd = cursor.getColumnIndex(PurchasesContentProvider.Columns.GOODS_NAME
					.toString());
			viewHolder.descrInd = cursor
					.getColumnIndex(PurchasesContentProvider.Columns.DESCR.toString());
			viewHolder.countInd = cursor
					.getColumnIndex(PurchasesContentProvider.Columns.COUNT.toString());
			viewHolder.unitsNameInd = cursor.getColumnIndex(PurchasesContentProvider.Columns.UNIT_NAME
					.toString());
			viewHolder.categoryIconInd = cursor
					.getColumnIndex(PurchasesContentProvider.Columns.GOODSCATEGORY_ICON.toString());
		}

		String iconName = cursor.getString(viewHolder.categoryIconInd);
		ImageView icon = (ImageView) view.findViewById(R.id.purchasesLiIcon);
		if (iconName != null) {
			LoadIconTask task = new LoadIconTask();
			task.loadIcon(icon, iconName, 1, true, context.getApplicationContext());
		} else {
			icon.setImageBitmap(null);
		}

		TextView name = (TextView) view.findViewById(R.id.purchasesLiName);
		name.setText(cursor.getString(viewHolder.goodsNameInd));

		String strDescr = cursor.getString(viewHolder.descrInd);
		TextView descr = (TextView) view.findViewById(R.id.purchasesLiDescr);
		if (strDescr != null && strDescr.length() > 0) {
			descr.setText(cursor.getString(viewHolder.descrInd));
			descr.setVisibility(View.VISIBLE);
		} else
			descr.setVisibility(View.GONE);

		TextView countAndUnits = (TextView) view.findViewById(R.id.purchasesLiCountAndUnits);
		countAndUnits.setText(cursor.getString(viewHolder.countInd).concat(" ")
				.concat(cursor.getString(viewHolder.unitsNameInd)));

		PurchaseState state = PurchaseState.valueOf(cursor.getString(viewHolder.stateInd));

		if (state == PurchaseState.ACCEPTED) {
			CheckBox check = (CheckBox) view.findViewById(R.id.purchasesLiCheck);
			check.setChecked(true);
			View strike = view.findViewById(R.id.purchasesLiStrike);
			strike.setVisibility(View.VISIBLE);
		} else {
			CheckBox check = (CheckBox) view.findViewById(R.id.purchasesLiCheck);
			check.setChecked(false);
			View strike = view.findViewById(R.id.purchasesLiStrike);
			strike.setVisibility(View.GONE);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.purchases_li, parent, false);
		bindView(v, context, cursor);
		return v;
	}

	@Override
	public int getItemViewType(int position) {
		return IGNORE_ITEM_VIEW_TYPE;
	}

	private static class ViewHolder {
		public int stateInd;
		public int goodsNameInd;
		public int descrInd;
		public int countInd;
		public int unitsNameInd;
		public int categoryIconInd;
	}

}
