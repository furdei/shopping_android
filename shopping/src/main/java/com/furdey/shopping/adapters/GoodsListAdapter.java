package com.furdey.shopping.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.furdey.shopping.R;
import com.furdey.shopping.content.GoodsUtils;
import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.contentproviders.GoodsContentProvider;
import com.furdey.shopping.tasks.LoadIconTask;

/**
 * Used to show filtered list when it isn't empty. When empty see
 * NewRecordAdapter.
 * 
 * @author Stepan Furdey
 */
public class GoodsListAdapter extends CursorAdapter {

	public enum Mode {
		GRID, LOOKUP
	}

	private Mode mode;

	public GoodsListAdapter(Context context, Mode mode) {
		super(context, null, false);
		this.mode = mode;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Goods model = GoodsUtils.fromCursor(cursor);

		TextView name = (TextView) view.findViewById(R.id.goodsLiName);
		name.setText(model.getName());

		TextView descr = (TextView) view.findViewById(R.id.goodsLiDescr);
		int fieldPurchase = cursor.getColumnIndex(GoodsContentProvider.Columns.PURCHASE_ID.toString());

		if (cursor.isNull(fieldPurchase) || mode == Mode.GRID) {
			// int fieldDescr =
			// cursor.getColumnIndex(GoodsDao.CATEGORY_OF_GOODS_FIELD_NAME);
			// descr.setText(cursor.getString(fieldDescr));
			descr.setText(model.getCategory().getName());
			descr.setTextAppearance(context, R.style.goodsLiDescrNormalStyle);
		} else {
			int fieldDelayed = cursor.getColumnIndex(GoodsContentProvider.Columns.PURCHASE_DELAYED
					.toString());

			if (cursor.getInt(fieldDelayed) == 0) {
				descr.setText(R.string.goodsLiAlreadyInTheList);
			} else {
				descr.setText(R.string.goodsLiAlreadyInTheListDelayed);
			}
			descr.setTextAppearance(context, R.style.goodsLiDescrAlreadyInTheListStyle);
		}

		// int fieldIcon = cursor.getColumnIndex(GoodsDao.CATEGORY_ICON_FIELD_NAME);
		// String iconName = cursor.getString(fieldIcon);
		String iconName = model.getCategory().getIcon();
		ImageView icon = (ImageView) view.findViewById(R.id.goodsLiIcon);
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
		View v = inflater.inflate(R.layout.goods_li, parent, false);
		bindView(v, context, cursor);
		return v;
	}

	@Override
	public int getItemViewType(int position) {
		return IGNORE_ITEM_VIEW_TYPE;
	}

}