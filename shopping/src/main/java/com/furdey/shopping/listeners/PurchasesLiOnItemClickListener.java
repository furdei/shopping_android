package com.furdey.shopping.listeners;

import android.database.Cursor;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;

import com.furdey.engine.android.controllers.OnModelLoadListener;
import com.furdey.shopping.R;
import com.furdey.shopping.content.model.Purchase;
import com.furdey.shopping.content.model.Purchase.PurchaseState;
import com.furdey.shopping.controllers.PurchasesController;

public class PurchasesLiOnItemClickListener implements OnItemClickListener {

	private PurchasesController controller;
	private boolean sync = false;

	public PurchasesLiOnItemClickListener(PurchasesController controller) {
		this.controller = controller;
	}

	@Override
	public void onItemClick(final AdapterView<?> adapterView, final View v, int position, long id) {
		System.out.println("onItemClick adapterView.class: "
				+ adapterView.getClass().getCanonicalName());
		final CheckBox check = (CheckBox) v.findViewById(R.id.purchasesLiCheck);
		final Cursor cursor = (Cursor) ((CursorAdapter) adapterView.getAdapter()).getItem(position);
		int stateInd = cursor.getColumnIndex("state");
		PurchaseState state = PurchaseState.valueOf(cursor.getString(stateInd));

		if (check.isChecked()) {
			state = PurchaseState.ENTERED;
		} else {
			state = PurchaseState.ACCEPTED;
		}

		int idInd = cursor.getColumnIndex(Purchase.ID_FIELD_NAME);
		OnModelLoadListener<Purchase> finishListener = new OnModelLoadListener<Purchase>() {
			@Override
			public void onLoadComplete(Purchase model) {
				View strike = v.findViewById(R.id.purchasesLiStrike);

				if (strike.getVisibility() == View.VISIBLE) {
					strike.setVisibility(View.GONE);
					check.setChecked(false);
				} else {
					check.setChecked(true);
					strike.setVisibility(View.VISIBLE);
					strike.startAnimation(AnimationUtils.loadAnimation(controller.getActivity(),
							R.anim.striking));
				}

				controller.refreshCursor();
			}
		};

		if (sync)
			controller.updatePurchaseStateSync(cursor.getInt(idInd), state, finishListener);
		else
			controller.updatePurchaseState(cursor.getInt(idInd), state, finishListener);
	}

	public void onItemClickSync(final AdapterView<?> adapterView, final View v, int position, long id) {
		sync = true;
		onItemClick(adapterView, v, position, id);
		sync = false;
	}

}
