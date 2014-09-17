package com.furdey.shopping.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.furdey.shopping.R;
import com.furdey.shopping.content.GoodsCategoriesUtils;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.tasks.LoadIconTask;

public class GoodsCategoriesListAdapter extends CursorAdapter {

	public GoodsCategoriesListAdapter(Context context) {
		super(context, null, 0);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		GoodsCategory model = GoodsCategoriesUtils.fromCursor(cursor);

		TextView name = (TextView) view.findViewById(R.id.goodsCategoriesLiName);
		name.setText(model.getName());

		TextView descr = (TextView) view.findViewById(R.id.goodsCategoriesLiDescr);
		if (model.getDescr() != null && model.getDescr().trim().length() > 0) {
			descr.setText(model.getDescr());
			descr.setVisibility(View.VISIBLE);
		} else
			descr.setVisibility(View.GONE);

		ImageView icon = (ImageView) view.findViewById(R.id.goodsCategoriesLiIcon);
		String iconName = model.getIcon();
		if (iconName != null) {
			LoadIconTask task = new LoadIconTask();
			task.loadIcon(icon, iconName, 1, true, context.getApplicationContext());
		} else {
			icon.setImageBitmap(null);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.goods_categories_li, parent, false);
		bindView(view, context, cursor);
		return view;
	}

	@Override
	public int getItemViewType(int position) {
		return IGNORE_ITEM_VIEW_TYPE;
	}

}
