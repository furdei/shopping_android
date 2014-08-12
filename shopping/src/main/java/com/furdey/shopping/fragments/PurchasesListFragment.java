package com.furdey.shopping.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.furdey.shopping.R;
import com.furdey.shopping.adapters.PurchasesListAdapter;
import com.furdey.shopping.content.PurchasesUtils;
import com.furdey.shopping.content.model.Purchase;
import com.furdey.shopping.content.model.Purchase.PurchaseState;
import com.furdey.shopping.contentproviders.PurchasesContentProvider;
import com.furdey.shopping.utils.NetworkUtils;
import com.furdey.shopping.utils.PreferencesManager;
import com.furdey.shopping.utils.PreferencesManager.PurchasesSortOrder;
import com.furdey.social.android.SocialClientsManager;
import com.furdey.social.android.SocialClientsManager.SocialNetwork;

public class PurchasesListFragment extends Fragment {

	public static interface PurchasesListListener {

		void onPurchasesListFragmentReady();

		void onPurchaseClicked(Purchase purchase);

		void onEditPurchase(Purchase purchase);

		void onDeletePurchase(Purchase purchase);

		void onPutPurchaseOff(Purchase purchase);

		void onNewPurchaseMenuSelected();

		void onGoodsMenuSelected();

		void onGoodsCategoriesMenuSelected();

		void onUnitsMenuSelected();

		void onAboutMenuSelected();

		void onShareVkMenuSelected();

		void onShareFbMenuSelected();

		void onShareTwMenuSelected();

		void onShareGpMenuSelected();

		void onShareLiMenuSelected();

		void onShareSkMenuSelected();

		void onShareEmMenuSelected();

		void onPurchasesListSortOrderChanged(PurchasesSortOrder purchasesSortOrder);

		void onSendPurchasesList(SocialNetwork socialNetwork);
	}

	private PurchasesListListener listener;

	private int contextPosition;

	private PurchasesListAdapter adapter;

	private ListView grid;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (PurchasesListListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement "
					+ PurchasesListListener.class.getSimpleName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.purchases_list, container, false);

		grid = (ListView) view.findViewById(R.id.baseListGrid);
		adapter = new PurchasesListAdapter(getActivity());
		grid.setAdapter(adapter);
        grid.setEmptyView(view.findViewById(R.id.shoppingListGridEmpty));
		registerForContextMenu(grid);

		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) adapter.getItem(position);
				listener.onPurchaseClicked(PurchasesUtils.fromCursor(cursor));
			}
		});

		setHasOptionsMenu(true);
		setRetainInstance(true);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listener.onPurchasesListFragmentReady();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final Cursor cursor = (Cursor) adapter.getItem(contextPosition);

		switch (item.getItemId()) {
		case R.id.menuPurchasesListEditRecord:
			listener.onEditPurchase(PurchasesUtils.fromCursor(cursor));
			return true;

		case R.id.menuPurchasesListDeleteRecord:
			PurchaseState purchaseState = PurchaseState.valueOf(cursor.getString(cursor
					.getColumnIndex(PurchasesContentProvider.Columns.STATE.toString())));
			if (purchaseState == PurchaseState.ACCEPTED) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.purchasesLiConfirmDeleteCaption);
				builder.setMessage(R.string.purchasesLiConfirmDeleteDetails)
						.setNegativeButton(R.string.formButtonCancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						}).setPositiveButton(R.string.formButtonDelete, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								listener.onDeletePurchase(PurchasesUtils.fromCursor(cursor));
								Toast.makeText(getActivity().getApplicationContext(),
										R.string.purchasesLiItemDeleted, Toast.LENGTH_LONG).show();
							}
						});
				// Create the AlertDialog object and return it
				builder.create().show();
			} else {
				listener.onDeletePurchase(PurchasesUtils.fromCursor(cursor));
				Toast.makeText(getActivity().getApplicationContext(), R.string.purchasesLiItemDeleted,
						Toast.LENGTH_LONG).show();
			}
			return true;

		case R.id.menuPurchasesListDelayRecord:
			listener.onPutPurchaseOff(PurchasesUtils.fromCursor(cursor));
			return true;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if (v.getId() == R.id.baseListGrid) {
			getActivity().getMenuInflater().inflate(R.menu.purchases_list_context, menu);

			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			contextPosition = info.position;
			CheckBox check = (CheckBox) info.targetView.findViewById(R.id.purchasesLiCheck);

			if (check.isChecked()) {
				MenuItem item = menu.findItem(R.id.menuPurchasesListDelayRecord);

				if (item != null)
					item.setEnabled(false);

				item = menu.findItem(R.id.menuPurchasesListEditRecord);

				if (item != null)
					item.setEnabled(false);
			}

			Cursor cursor = (Cursor) adapter.getItem(contextPosition);
			int goodsNameInd = cursor.getColumnIndex(PurchasesContentProvider.Columns.GOODS_NAME
					.toString());
			menu.setHeaderTitle(String.format(getString(R.string.menuPurchasesListContextTitle),
					cursor.getString(goodsNameInd)));
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.purchases_list, menu);

		PurchasesSortOrder sortOrder = PreferencesManager.getPurchasesSortOrder(getActivity());
		int sortMenuId;

		switch (sortOrder) {
		case ORDER_BY_NAME:
			sortMenuId = R.id.menuPurchasesListSortOrderName;
			break;
		case ORDER_BY_ADD_TIME:
			sortMenuId = R.id.menuPurchasesListSortOrderAddTime;
			break;
		default:
			sortMenuId = R.id.menuPurchasesListSortOrderCategory;
		}

		MenuItem sortMenu = menu.findItem(sortMenuId);
		if (sortMenu != null)
			sortMenu.setChecked(true);

		if (NetworkUtils.isNetworkAvailable(getActivity())) {
			if (!SocialClientsManager.isClientInstalled(getActivity(), SocialNetwork.VK)) {
				MenuItem social = menu.findItem(R.id.menuPurchasesListShareVK);
				social.setVisible(false);
				social = menu.findItem(R.id.menuPurchasesListSendListVK);
				social.setVisible(false);
			}

			if (!SocialClientsManager.isClientInstalled(getActivity(), SocialNetwork.FACEBOOK)) {
				MenuItem social = menu.findItem(R.id.menuPurchasesListShareFB);
				social.setVisible(false);
			}

			if (!SocialClientsManager.isClientInstalled(getActivity(), SocialNetwork.TWITTER)) {
				MenuItem social = menu.findItem(R.id.menuPurchasesListShareTw);
				social.setVisible(false);
			}

			if (!SocialClientsManager.isClientInstalled(getActivity(), SocialNetwork.GOOGLE_PLUS)) {
				MenuItem social = menu.findItem(R.id.menuPurchasesListShareGP);
				social.setVisible(false);
				social = menu.findItem(R.id.menuPurchasesListSendListGP);
				social.setVisible(false);
			}

			if (!SocialClientsManager.isClientInstalled(getActivity(), SocialNetwork.LINKEDIN)) {
				MenuItem social = menu.findItem(R.id.menuPurchasesListShareLI);
				social.setVisible(false);
			}

			if (!SocialClientsManager.isClientInstalled(getActivity(), SocialNetwork.SKYPE)) {
				MenuItem social = menu.findItem(R.id.menuPurchasesListShareSk);
				social.setVisible(false);
				social = menu.findItem(R.id.menuPurchasesListSendListSk);
				social.setVisible(false);
			}

			if (!SocialClientsManager.isClientInstalled(getActivity(), SocialNetwork.EMAIL)) {
				MenuItem social = menu.findItem(R.id.menuPurchasesListShareEm);
				social.setVisible(false);
				social = menu.findItem(R.id.menuPurchasesListSendListEm);
				social.setVisible(false);
			}
		} else {
			MenuItem social = menu.findItem(R.id.menuPurchasesListSendList);
			social.setVisible(false);
			social.getSubMenu().setGroupVisible(R.id.menuPurchasesListSendListGroup, false);
			social = menu.findItem(R.id.menuPurchasesListShare);
			social.setVisible(false);
			social.getSubMenu().setGroupVisible(R.id.menuPurchasesListShareGroup, false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuGoods:
			listener.onGoodsMenuSelected();
			return true;

		case R.id.menuGoodsCategories:
			listener.onGoodsCategoriesMenuSelected();
			return true;

		case R.id.menuUnits:
			listener.onUnitsMenuSelected();
			return true;

		case R.id.menuPurchasesListNewRecord:
			listener.onNewPurchaseMenuSelected();
			return true;

		case R.id.menuPurchasesListAbout:
			listener.onAboutMenuSelected();
			return true;

		case R.id.menuPurchasesListShareVK:
			listener.onShareVkMenuSelected();
			return true;

		case R.id.menuPurchasesListShareFB:
			listener.onShareFbMenuSelected();
			return true;

		case R.id.menuPurchasesListShareTw:
			listener.onShareTwMenuSelected();
			return true;

		case R.id.menuPurchasesListShareGP:
			listener.onShareGpMenuSelected();
			return true;

		case R.id.menuPurchasesListShareLI:
			listener.onShareLiMenuSelected();
			return true;

		case R.id.menuPurchasesListShareSk:
			listener.onShareSkMenuSelected();
			return true;

		case R.id.menuPurchasesListShareEm:
			listener.onShareEmMenuSelected();
			return true;

		case R.id.menuPurchasesListSortOrderName:
			if (!item.isChecked()) {
				item.setChecked(true);
				listener.onPurchasesListSortOrderChanged(PurchasesSortOrder.ORDER_BY_NAME);
			}
			return true;

		case R.id.menuPurchasesListSortOrderAddTime:
			if (!item.isChecked()) {
				item.setChecked(true);
				listener.onPurchasesListSortOrderChanged(PurchasesSortOrder.ORDER_BY_ADD_TIME);
			}
			return true;

		case R.id.menuPurchasesListSortOrderCategory:
			if (!item.isChecked()) {
				item.setChecked(true);
				listener.onPurchasesListSortOrderChanged(PurchasesSortOrder.ORDER_BY_CATEGORY);
			}
			return true;

		case R.id.menuPurchasesListSendListGP:
			listener.onSendPurchasesList(SocialNetwork.GOOGLE_PLUS);
			return true;

		case R.id.menuPurchasesListSendListSk:
			listener.onSendPurchasesList(SocialNetwork.SKYPE);
			return true;

		case R.id.menuPurchasesListSendListVK:
			listener.onSendPurchasesList(SocialNetwork.VK);
			return true;

		case R.id.menuPurchasesListSendListEm:
			listener.onSendPurchasesList(SocialNetwork.EMAIL);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onPurchasesListReady(Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	public void onPurchasesListReset() {
		adapter.swapCursor(null);
	}

}
