package com.furdey.shopping.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.ShareDialogBuilder;
import com.furdey.shopping.R;
import com.furdey.shopping.adapters.GoodsListAdapter.Mode;
import com.furdey.shopping.content.GoodsCategoriesUtils;
import com.furdey.shopping.content.GoodsUtils;
import com.furdey.shopping.content.PurchasesUtils;
import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.content.model.Purchase;
import com.furdey.shopping.content.model.Purchase.PurchaseState;
import com.furdey.shopping.fragments.GoodsCategoriesListFragment;
import com.furdey.shopping.fragments.GoodsCategoriesListFragment.GoodsCategoriesListListener;
import com.furdey.shopping.fragments.GoodsListFragment;
import com.furdey.shopping.fragments.GoodsListFragment.GoodsListListener;
import com.furdey.shopping.fragments.PurchasesFormFragment;
import com.furdey.shopping.fragments.PurchasesFormFragment.PurchasesFormListener;
import com.furdey.shopping.fragments.PurchasesListFragment;
import com.furdey.shopping.fragments.PurchasesListFragment.PurchasesListListener;
import com.furdey.shopping.receivers.InternetConnectionBroadcastReceiver;
import com.furdey.shopping.tasks.ToastThrowableAsyncTask;
import com.furdey.shopping.utils.FeedbackDialogUtil;
import com.furdey.shopping.utils.FeedbackDialogUtil.SocialShareListener;
import com.furdey.shopping.utils.NetworkUtils;
import com.furdey.shopping.utils.PreferencesManager;
import com.furdey.shopping.utils.PreferencesManager.PurchasesSortOrder;
import com.furdey.social.android.SocialClient;
import com.furdey.social.android.SocialClientsManager.SocialNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PurchasesActivity extends ActionBarActivity implements PurchasesListListener,
		PurchasesFormListener, GoodsListListener, GoodsCategoriesListListener, LoaderCallbacks<Cursor>,
		StatusCallback {

	public static final int REQUEST_EDIT = 1;
	public static final int REQUEST_SELECT_FRIEND = 2;

	private static final int RESULT_EMPTY = -1;
	// public static final int PURCHASES_LIST_LOADER = 0;
	private static final String TAG = PurchasesActivity.class.getSimpleName();
	// private static final String GRID_STATE = "gridState";
    public static final String RESULT_MESSAGE_PARAM_NAME = "com.furdey.shopping.activities.PurchasesController.messageId";

	// private Purchase contextModel;
	// private PurchasesController controller;
	// private Parcelable gridState;
	private InternetConnectionBroadcastReceiver internetConnectionBroadcastReceiver = null;
	private boolean internetConnectionBroadcastReceiverRegistered = false;
	private PurchasesListFragment purchasesListFragment;
	private UiLifecycleHelper uiLifecycleHelper;

	// private PurchasesListAdapter adapter;

	// private ListView grid;

	// public Cursor getCursor() {
	// if (grid != null)
	// if (grid.getAdapter() != null)
	// return ((CursorAdapter) grid.getAdapter()).getCursor();
	//
	// return null;
	// }

	private static final String PURCHASES_LIST_TAG = "purchasesList";
	private static final String PURCHASES_FORM_TAG = "purchasesForm";
	private static final String GOODS_LIST_TAG = "goodssList";
	private static final String CATEGORIES_LIST_TAG = "categoriesList";

	private static final int GOODS_LIST_LOADER = 0;
	private static final int CATEGORIES_LIST_LOADER = 1;
	private static final int PURCHASES_LIST_LOADER = 2;
	private static final String GOODS_LIST_LOADER_FILTER = "goodsFilter";
	private static final String CATEGORIES_LIST_LOADER_FILTER = "categoriesFilter";


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// setUpButtonEnabled(false); // This is for the top-level activity only
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_fragment_activity);
		purchasesListFragment = new PurchasesListFragment();
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
		tr.add(R.id.dynamic_fragment_container, purchasesListFragment, PURCHASES_LIST_TAG);
		tr.commit();

		// (PurchasesListFragment) getSupportFragmentManager().findFragmentById(
		// R.id.purchases_list_fragment);
		// purchasesListFragment.setPurchasesListFragmentListener(this);

		// grid = (ListView) findViewById(R.id.baseListGrid);
		// adapter = new PurchasesListAdapter(this);
		// grid.setAdapter(adapter);

		// controller = new PurchasesController(this);
		// getSupportLoaderManager().initLoader(PURCHASES_LIST_LOADER, null,
		// new PurchasesLoaderCallbacks(this, adapter));

		// registerForContextMenu(grid);
		// grid.setOnItemClickListener(new
		// PurchasesLiOnItemClickListener(controller));

		internetConnectionBroadcastReceiver = new InternetConnectionBroadcastReceiver();

		if (NetworkUtils.isNetworkAvailable(this)) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							FeedbackDialogUtil.showFeedbackDialogsIfNeeded(PurchasesActivity.this,
									new SocialShareListener() {
										@Override
										public void onSocialShare(SocialNetwork socialNetwork) {
											switch (socialNetwork) {
											case EMAIL:
												SocialClient.sendMessage(
														PurchasesActivity.this,
														SocialNetwork.EMAIL,
														getString(R.string.socialMessageMsgTitle),
														getString(R.string.socialMessageBase).concat(" ").concat(
																getString(R.string.socialMessageAddition)));
												break;

											case FACEBOOK:
												// start Facebook Login
												Session session = Session.getActiveSession();
												if (session != null && session.isOpened()) {
													sendFbShareMessage(session);
												} else {
													List<String> permissions = new ArrayList<String>();
													permissions.add("public_profile");
													permissions.add("user_friends");
													// permissions.add("publish_actions");
													Session.openActiveSession(PurchasesActivity.this, true, permissions,
															new Session.StatusCallback() {
																// callback when session changes state
																@Override
																public void call(Session session, SessionState state,
																		Exception exception) {
																	sendFbShareMessage(session);
																}
															});
												}
												break;

											case GOOGLE_PLUS:
												SocialClient.sendMessage(
														PurchasesActivity.this,
														SocialNetwork.GOOGLE_PLUS,
														null,
														getString(R.string.socialMessageBase).concat(" ").concat(
																getString(R.string.socialMessageAddition)));
												break;

											case LINKEDIN:
												SocialClient.sendMessage(
														PurchasesActivity.this,
														SocialNetwork.LINKEDIN,
														null,
														getString(R.string.socialMessageBase).concat(" ").concat(
																getString(R.string.socialMessageAddition)));
												break;

											case SKYPE:
												SocialClient.sendMessage(
														PurchasesActivity.this,
														SocialNetwork.SKYPE,
														null,
														getString(R.string.socialMessageBase).concat(" ").concat(
																getString(R.string.socialMessageAddition)));
												break;

											case TWITTER:
												SocialClient.sendMessage(
														PurchasesActivity.this,
														SocialNetwork.TWITTER,
														null,
														getString(R.string.socialMessageBase).concat(" ").concat(
																getString(R.string.socialMessageAddition)));
												break;

											case VK:
												SocialClient.sendMessage(
														PurchasesActivity.this,
														SocialNetwork.VK,
														null,
														getString(R.string.socialMessageBase).concat(" ").concat(
																getString(R.string.socialMessageAddition)));
												break;
											}
										}
									});
						}
					});
				}
			}, 100);
		}

		uiLifecycleHelper = new UiLifecycleHelper(this, this);
		uiLifecycleHelper.onCreate(savedInstanceState);
	}

	/**
	 * It's called when the user adds or edits a purchase
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent data = getIntent();

		if (data != null) {
			Integer messageId = data.getIntExtra(RESULT_MESSAGE_PARAM_NAME,
					RESULT_EMPTY);

			if (messageId != RESULT_EMPTY) {
				Toast.makeText(this, messageId, Toast.LENGTH_LONG).show();
				data.putExtra(RESULT_MESSAGE_PARAM_NAME, RESULT_EMPTY);
			}
		}

		if (!internetConnectionBroadcastReceiverRegistered) {
			registerReceiver(internetConnectionBroadcastReceiver, new IntentFilter(
					"android.net.conn.CONNECTIVITY_CHANGE"));
			internetConnectionBroadcastReceiver.setActivity(this);
			internetConnectionBroadcastReceiverRegistered = true;
		}

		ActivityCompat.invalidateOptionsMenu(this);
	}

	@Override
	protected void onPause() {
		if (internetConnectionBroadcastReceiverRegistered) {
			unregisterReceiver(internetConnectionBroadcastReceiver);
			internetConnectionBroadcastReceiver.setActivity(null);
			internetConnectionBroadcastReceiverRegistered = false;
		}

		super.onPause();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.purchases_list, menu);
	// PurchasesSortOrder sortOrder =
	// PreferencesManager.getPurchasesSortOrder(this);
	// int sortMenuId;
	//
	// switch (sortOrder) {
	// case ORDER_BY_NAME:
	// sortMenuId = R.id.menuPurchasesListSortOrderName;
	// break;
	// case ORDER_BY_ADD_TIME:
	// sortMenuId = R.id.menuPurchasesListSortOrderAddTime;
	// break;
	// default:
	// sortMenuId = R.id.menuPurchasesListSortOrderCategory;
	// }
	//
	// MenuItem sortMenu = menu.findItem(sortMenuId);
	// if (sortMenu != null)
	// sortMenu.setChecked(true);
	//
	// if (isNetworkAvailable()) {
	// if (!SocialClientsManager.isClientInstalled(this, SocialNetwork.VK)) {
	// MenuItem social = menu.findItem(R.id.menuPurchasesListShareVK);
	// social.setVisible(false);
	// social = menu.findItem(R.id.menuPurchasesListSendListVK);
	// social.setVisible(false);
	// }
	//
	// if (!SocialClientsManager.isClientInstalled(this, SocialNetwork.FACEBOOK))
	// {
	// MenuItem social = menu.findItem(R.id.menuPurchasesListShareFB);
	// social.setVisible(false);
	// // social = menu.findItem(R.id.menuPurchasesListSendListFB);
	// // social.setVisible(false);
	// }
	//
	// if (!SocialClientsManager.isClientInstalled(this, SocialNetwork.TWITTER)) {
	// MenuItem social = menu.findItem(R.id.menuPurchasesListShareTw);
	// social.setVisible(false);
	// // social = menu.findItem(R.id.menuPurchasesListSendListTw);
	// // social.setVisible(false);
	// }
	//
	// if (!SocialClientsManager.isClientInstalled(this,
	// SocialNetwork.GOOGLE_PLUS)) {
	// MenuItem social = menu.findItem(R.id.menuPurchasesListShareGP);
	// social.setVisible(false);
	// social = menu.findItem(R.id.menuPurchasesListSendListGP);
	// social.setVisible(false);
	// }
	//
	// if (!SocialClientsManager.isClientInstalled(this, SocialNetwork.LINKEDIN))
	// {
	// MenuItem social = menu.findItem(R.id.menuPurchasesListShareLI);
	// social.setVisible(false);
	// // social = menu.findItem(R.id.menuPurchasesListSendListLI);
	// // social.setVisible(false);
	// }
	//
	// if (!SocialClientsManager.isClientInstalled(this, SocialNetwork.SKYPE)) {
	// MenuItem social = menu.findItem(R.id.menuPurchasesListShareSk);
	// social.setVisible(false);
	// social = menu.findItem(R.id.menuPurchasesListSendListSk);
	// social.setVisible(false);
	// }
	//
	// if (!SocialClientsManager.isClientInstalled(this, SocialNetwork.EMAIL)) {
	// MenuItem social = menu.findItem(R.id.menuPurchasesListShareEm);
	// social.setVisible(false);
	// social = menu.findItem(R.id.menuPurchasesListSendListEm);
	// social.setVisible(false);
	// }
	// } else {
	// MenuItem social = menu.findItem(R.id.menuPurchasesListSendList);
	// social.setVisible(false);
	// social.getSubMenu().setGroupVisible(R.id.menuPurchasesListSendListGroup,
	// false);
	// social = menu.findItem(R.id.menuPurchasesListShare);
	// social.setVisible(false);
	// social.getSubMenu().setGroupVisible(R.id.menuPurchasesListShareGroup,
	// false);
	// }
	//
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.menuGoods:
	// startActivity(new Intent(this, GoodsActivity.class));
	// return true;
	// case R.id.menuGoodsCategories:
	// controller.createGoodsCategoriesListActivity();
	// return true;
	// case R.id.menuUnits:
	// controller.createUnitsListActivity();
	// return true;
	// case R.id.menuPurchasesListNewRecord:
	// goToGoodsList(true);
	// return true;
	// case R.id.menuPurchasesListAbout:
	// controller.createAboutActivity();
	// return true;
	// case R.id.menuPurchasesListShareVK:
	// controller.createShareVkActivity();
	// return true;
	// case R.id.menuPurchasesListShareFB:
	// controller.createShareFbActivity();
	// return true;
	// case R.id.menuPurchasesListShareTw:
	// controller.createShareTwActivity();
	// return true;
	// case R.id.menuPurchasesListShareGP:
	// controller.createShareGPActivity();
	// return true;
	// case R.id.menuPurchasesListShareLI:
	// controller.createShareLIActivity();
	// return true;
	// case R.id.menuPurchasesListShareSk:
	// controller.createShareSkActivity();
	// return true;
	// case R.id.menuPurchasesListShareEm:
	// controller.createShareEmActivity();
	// return true;
	// case R.id.menuPurchasesListSortOrderName:
	// if (!item.isChecked()) {
	// item.setChecked(true);
	// PreferencesManager.setPurchasesSortOrder(this,
	// PurchasesSortOrder.ORDER_BY_NAME);
	// controller.refreshCursor();
	// }
	// return true;
	// case R.id.menuPurchasesListSortOrderAddTime:
	// if (!item.isChecked()) {
	// item.setChecked(true);
	// PreferencesManager.setPurchasesSortOrder(this,
	// PurchasesSortOrder.ORDER_BY_ADD_TIME);
	// controller.refreshCursor();
	// }
	// return true;
	// case R.id.menuPurchasesListSortOrderCategory:
	// if (!item.isChecked()) {
	// item.setChecked(true);
	// PreferencesManager.setPurchasesSortOrder(this,
	// PurchasesSortOrder.ORDER_BY_CATEGORY);
	// controller.refreshCursor();
	// }
	// return true;
	// // case R.id.menuPurchasesListSendListFB:
	// // controller.sendList(SocialNetwork.FACEBOOK);
	// // return true;
	// case R.id.menuPurchasesListSendListGP:
	// controller.sendList(SocialNetwork.GOOGLE_PLUS);
	// return true;
	// // case R.id.menuPurchasesListSendListLI:
	// // controller.sendList(SocialNetwork.LINKEDIN);
	// // return true;
	// case R.id.menuPurchasesListSendListSk:
	// controller.sendList(SocialNetwork.SKYPE);
	// return true;
	// // case R.id.menuPurchasesListSendListTw:
	// // controller.sendList(SocialNetwork.TWITTER);
	// // return true;
	// case R.id.menuPurchasesListSendListVK:
	// controller.sendList(SocialNetwork.VK);
	// return true;
	// case R.id.menuPurchasesListSendListEm:
	// controller.sendList(SocialNetwork.EMAIL);
	// return true;
	// }
	//
	// return super.onOptionsItemSelected(item);
	// }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// if (v.getId() == R.id.baseListGrid) {
		// getMenuInflater().inflate(R.menu.purchases_list_context, menu);
		//
		// AdapterView.AdapterContextMenuInfo info =
		// (AdapterView.AdapterContextMenuInfo) menuInfo;
		// Cursor cursor = (Cursor) ((PurchasesListAdapter)
		// grid.getAdapter()).getItem(info.position);
		// contextModel = PurchasesDao.fromCursor(cursor);
		//
		// CheckBox check = (CheckBox)
		// info.targetView.findViewById(R.id.purchasesLiCheck);
		//
		// if (check.isChecked()) {
		// MenuItem item = menu.findItem(R.id.menuPurchasesListDelayRecord);
		//
		// if (item != null)
		// item.setEnabled(false);
		//
		// item = menu.findItem(R.id.menuPurchasesListEditRecord);
		//
		// if (item != null)
		// item.setEnabled(false);
		// }
		//
		// int goodsNameInd =
		// cursor.getColumnIndex(PurchasesDao.GOODS_NAME_FIELD_NAME);
		// menu.setHeaderTitle(String.format(getString(R.string.menuPurchasesListContextTitle),
		// cursor.getString(goodsNameInd)));
		//
		// CheckBox checked = (CheckBox)
		// info.targetView.findViewById(R.id.purchasesLiCheck);
		// if (checked != null) {
		// contextModel.setState(checked.isChecked() ? PurchaseState.ACCEPTED :
		// PurchaseState.ENTERED);
		// } else
		// Log.d(TAG, "onCreateContextMenu: checked is null");
		// }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// final Context context = this;
		//
		// switch (item.getItemId()) {
		// case R.id.menuPurchasesListEditRecord:
		// controller.createEditForm(contextModel);
		// return true;
		//
		// case R.id.menuPurchasesListDeleteRecord:
		// if (contextModel.getState() == PurchaseState.ACCEPTED) {
		// AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(R.string.purchasesLiConfirmDeleteCaption);
		// builder.setMessage(R.string.purchasesLiConfirmDeleteDetails)
		// .setNegativeButton(R.string.formButtonCancel, new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// // User cancelled the dialog
		// }
		// }).setPositiveButton(R.string.formButtonDelete, new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// controller.delete(contextModel.getId());
		// Toast.makeText(context, R.string.purchasesLiItemDeleted,
		// Toast.LENGTH_LONG).show();
		// }
		// });
		// // Create the AlertDialog object and return it
		// builder.create().show();
		// } else {
		// controller.delete(contextModel.getId());
		// Toast.makeText(context, R.string.purchasesLiItemDeleted,
		// Toast.LENGTH_LONG).show();
		// }
		// return true;
		//
		// case R.id.menuPurchasesListDelayRecord:
		// controller.delayPurchase(contextModel.getId(), new
		// OnModelLoadListener<Purchase>() {
		// @Override
		// public void onLoadComplete(Purchase model) {
		// controller.refreshCursor();
		// Toast.makeText(context, R.string.purchasesLiItemDelayed,
		// Toast.LENGTH_LONG).show();
		// }
		// });
		// return true;
		// }
		//
		return super.onContextItemSelected(item);
	}

	// @Override
	// protected void onRestoreInstanceState(Bundle state) {
	// super.onRestoreInstanceState(state);
	// gridState = state.getParcelable(GRID_STATE);
	// }

	// @Override
	// protected void onSaveInstanceState(Bundle state) {
	// super.onSaveInstanceState(state);
	// gridState = grid.onSaveInstanceState();
	// state.putParcelable(GRID_STATE, gridState);
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SELECT_FRIEND && resultCode == RESULT_OK) {
			SocialClient.sendVkMessage(this, data.getStringExtra(FriendsListActivity.PERSON_ID),
					data.getStringExtra(FriendsListActivity.MESSAGE_PARAM));
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// /////////////////////////////
	// /// PurchasesListListener ///
	// /////////////////////////////

	@Override
	public void onPurchasesListFragmentReady() {
		getSupportLoaderManager().initLoader(PURCHASES_LIST_LOADER, null, this);
	}

	@Override
	public void onPurchaseClicked(Purchase purchase) {
		purchase.setState(purchase.getState() == PurchaseState.ENTERED ? PurchaseState.ACCEPTED
				: PurchaseState.ENTERED);

		new ToastThrowableAsyncTask<Purchase, Void>(getApplicationContext()) {
			@Override
			protected Void doBackgroundWork(Purchase purchase) throws Exception {
				PurchasesUtils.savePurchase(getApplicationContext(), purchase);
				return null;
			}
		}.execute(purchase);
	}

	@Override
	public void onEditPurchase(Purchase purchase) {
		goToPurchaseForm(true, purchase);
	}

	@Override
	public void onDeletePurchase(Purchase purchase) {
		new ToastThrowableAsyncTask<Purchase, Void>(getApplicationContext()) {
			@Override
			protected Void doBackgroundWork(Purchase purchase) throws Exception {
				PurchasesUtils.deletePurchase(getApplicationContext(), purchase.getId());
				return null;
			}
		}.execute(purchase);
	}

	@Override
	public void onPutPurchaseOff(Purchase purchase) {
		new ToastThrowableAsyncTask<Purchase, Uri>(getApplicationContext()) {
			@Override
			protected Uri doBackgroundWork(Purchase purchase) throws Exception {
				return PurchasesUtils.savePurchase(getApplicationContext(),
						PurchasesUtils.putPurchaseOff(purchase));
			}
		}.execute(purchase);
	}

	@Override
	public void onNewPurchaseMenuSelected() {
		goToGoodsList(null, true);
	}

	@Override
	public void onGoodsMenuSelected() {
		startActivity(new Intent(this, GoodsActivity.class));
	}

	@Override
	public void onGoodsCategoriesMenuSelected() {
		Intent intent = new Intent(this, GoodsCategoriesActivity.class);
		startActivity(intent);
	}

	@Override
	public void onUnitsMenuSelected() {
		startActivity(new Intent(this, UnitsActivity.class));
	}

	@Override
	public void onAboutMenuSelected() {
		startActivity(new Intent(this, AboutAppActivity.class));
	}

	@Override
	public void onShareVkMenuSelected() {
		sendShareMessage(SocialNetwork.VK);
	}

	@Override
	public void onShareFbMenuSelected() {
		// start Facebook Login
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			sendFbShareMessage(session);
		} else {
			List<String> permissions = new ArrayList<String>();
			permissions.add("public_profile");
			permissions.add("user_friends");
			Session.openActiveSession(this, true, permissions, new Session.StatusCallback() {
				// callback when session changes state
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					sendFbShareMessage(session);
				}
			});
		}
	}

	@Override
	public void onShareTwMenuSelected() {
		sendShareMessage(SocialNetwork.TWITTER);
	}

	@Override
	public void onShareGpMenuSelected() {
		sendShareMessage(SocialNetwork.GOOGLE_PLUS);
	}

	@Override
	public void onShareLiMenuSelected() {
		sendShareMessage(SocialNetwork.LINKEDIN);
	}

	@Override
	public void onShareSkMenuSelected() {
		sendShareMessage(SocialNetwork.SKYPE);
	}

	@Override
	public void onShareEmMenuSelected() {
		sendShareMessageWithTitle(SocialNetwork.EMAIL);
	}

	@Override
	public void onPurchasesListSortOrderChanged(PurchasesSortOrder purchasesSortOrder) {
		PreferencesManager.setPurchasesSortOrder(this, purchasesSortOrder);
		getSupportLoaderManager().restartLoader(PURCHASES_LIST_LOADER, null, this);
	}

	@Override
	public void onSendPurchasesList(SocialNetwork socialNetwork) {
		new ToastThrowableAsyncTask<SocialNetwork, Void>(getApplicationContext()) {
			@Override
			protected Void doBackgroundWork(SocialNetwork socialNetwork) throws Exception {
				String purchasesList = PurchasesUtils.getPurchasesListString(getApplicationContext());

				switch (socialNetwork) {
				case VK:
					Intent intent = new Intent(PurchasesActivity.this, VkLoginActivity.class);
					intent.putExtra(FriendsListActivity.MESSAGE_PARAM, purchasesList);
					startActivityForResult(intent, REQUEST_SELECT_FRIEND);
					break;
				default:
					SocialClient.sendMessage(PurchasesActivity.this, socialNetwork,
							getString(R.string.appName), purchasesList);
				}

				return null;
			}
		}.execute(socialNetwork);
	}

	// /////////////////////////////
	// /// PurchasesFormListener ///
	// /////////////////////////////

	@Override
	public void onSavePurchase(Purchase purchase) {
		returnToPurchasesList();
		new ToastThrowableAsyncTask<Purchase, Uri>(getApplicationContext()) {
			@Override
			protected Uri doBackgroundWork(Purchase purchase) throws Exception {
				return PurchasesUtils.savePurchase(getApplicationContext(), purchase);
			}
		}.execute(purchase);
	}

	@Override
	public void onCancelPurchaseEdit() {
		returnToPurchasesList();
	}

	@Override
	public void onSelectGoods(String filter) {
		goToGoodsList(filter, true);
	}

	@Override
	public void onSelectGoodsCategory(String filter) {
		goToCategoriesList(filter, true);
	}

	// /////////////////////////////
	// ///// GoodsListListener /////
	// /////////////////////////////

	@Override
	public void onFillGoodsList(String filter) {
		Bundle bundle = new Bundle();
		bundle.putString(GOODS_LIST_LOADER_FILTER, filter);
		getSupportLoaderManager().restartLoader(GOODS_LIST_LOADER, bundle, this);
	}

	@Override
	public void onEditGoods(final Goods goods) {
		new ToastThrowableAsyncTask<Goods, Cursor>(getApplicationContext()) {
			@Override
			protected Cursor doBackgroundWork(Goods goods) throws Exception {
				return goods.getId() != null ? PurchasesUtils.getActivePurchasesByGoods(
						getApplicationContext(), goods.getId()) : null;
			}

			@Override
			protected void onSuccess(Cursor purchasesInTheList) {
				boolean thereWasAPurchaseAlready = getSupportFragmentManager().popBackStackImmediate(
						PURCHASES_FORM_TAG, 0);

				if (purchasesInTheList != null && purchasesInTheList.getCount() > 0) {
					// purchase is in the list so let's just edit
					if (thereWasAPurchaseAlready) {
						getSupportFragmentManager().popBackStack();
					}

					purchasesInTheList.moveToFirst();
					Purchase purchase = PurchasesUtils.fromCursor(purchasesInTheList);
					goToPurchaseForm(true, purchase);
				} else if (thereWasAPurchaseAlready) {
					// there was a purchase being edited
					// we'll replace it's goods, category and units
					// but leave count and description
					PurchasesFormFragment purchasesFormFragment = getPurchasesFormFragment();

					Purchase oldPurchase = purchasesFormFragment.getParameterPurchase();
					Purchase newPurchase = new Purchase();

					newPurchase.setGoods(goods);
					newPurchase.setUnits(goods.getDefaultUnits());

					if (oldPurchase != null) {
						newPurchase.setCount(oldPurchase.getCount());
						newPurchase.setDescr(oldPurchase.getDescr());
					}

					getSupportFragmentManager().popBackStack();
					goToPurchaseForm(true, newPurchase);
				} else {
					// there wasn't any purchase being edited
					// so create a new one
					Purchase purchase = new Purchase();
					purchase.setGoods(goods);
					purchase.setUnits(goods.getDefaultUnits());
					goToPurchaseForm(true, purchase);
				}

				if (purchasesInTheList != null) {
					purchasesInTheList.close();
				}
			}

		}.execute(goods);

	}

	@Override
	public void onDeleteGoods(Goods goods) {
		throw new UnsupportedOperationException();
	}

	// /////////////////////////////////
	// // GoodsCategoriesListListener //
	// /////////////////////////////////

	@Override
	public void onFillCategoriesList(String filter) {
		Bundle args = new Bundle();
		args.putString(CATEGORIES_LIST_LOADER_FILTER, filter);
		getSupportLoaderManager().restartLoader(CATEGORIES_LIST_LOADER, args, this);
	}

	/**
	 * Here we just select a category to replace the old one at the goods details
	 * form
	 * 
	 * @param category
	 */
	@Override
	public void onEditCategory(GoodsCategory category) {
		getSupportFragmentManager().popBackStack();
		PurchasesFormFragment purchasesFormFragment = getPurchasesFormFragment();
		purchasesFormFragment.setCategory(category);
	}

	@Override
	public void onDeleteCategory(GoodsCategory category) {
		throw new UnsupportedOperationException();
	}

	// /////////////////////////////
	// // LoaderCallbacks<Cursor> //
	// /////////////////////////////

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		switch (loaderId) {
		case GOODS_LIST_LOADER:
			String filter = args.getString(GOODS_LIST_LOADER_FILTER);
			return GoodsUtils.getGoodsLoader(this, filter);

		case CATEGORIES_LIST_LOADER:
			filter = args.getString(CATEGORIES_LIST_LOADER_FILTER);
			return GoodsCategoriesUtils.getGoodsCategoriesLoader(this, filter);

		case PURCHASES_LIST_LOADER:
			String sortOrder = PurchasesUtils.getPurchasesSortOrder(this);
			return PurchasesUtils.getPurchasesLoader(this, sortOrder);
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		case GOODS_LIST_LOADER:
			GoodsListFragment goodsListFragment = getGoodsListFragment();

			if (goodsListFragment != null)
				goodsListFragment.onGoodsListReady(cursor);
			else
				Log.e(TAG, "GoodsListFragment was expected but wasn't found");
			break;

		case CATEGORIES_LIST_LOADER:
			GoodsCategoriesListFragment goodsCategoriesListFragment = getGoodsCategoriesListFragment();

			if (goodsCategoriesListFragment != null)
				goodsCategoriesListFragment.onCategoriesListReady(cursor);
			else
				Log.e(TAG, "GoodsCategoriesListFragment was expected but wasn't found");
			break;

		case PURCHASES_LIST_LOADER:
			PurchasesListFragment purchasesListFragment = getPurchasesListFragment();

			if (purchasesListFragment != null)
				purchasesListFragment.onPurchasesListReady(cursor);
			else
				Log.e(TAG, "PurchasesListFragment was expected but wasn't found");
			break;

		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
		case GOODS_LIST_LOADER:
			GoodsListFragment goodsListFragment = getGoodsListFragment();

			if (goodsListFragment != null)
				goodsListFragment.onGoodsListReset();
			else
				Log.e(TAG, "GoodsListFragment was expected but wasn't found");
			break;

		case CATEGORIES_LIST_LOADER:
			GoodsCategoriesListFragment goodsCategoriesListFragment = getGoodsCategoriesListFragment();

			if (goodsCategoriesListFragment != null)
				goodsCategoriesListFragment.onCategoriesListReset();
			else
				Log.e(TAG, "GoodsCategoriesListFragment was expected but wasn't found");
			break;

		case PURCHASES_LIST_LOADER:
			PurchasesListFragment purchasesListFragment = getPurchasesListFragment();

			if (purchasesListFragment != null)
				purchasesListFragment.onPurchasesListReset();
			else
				Log.e(TAG, "PurchasesListFragment was expected but wasn't found");
			break;
		}
	}

	// ///////////////////////////////
	// /////// StatusCallback ////////
	// ///////////////////////////////

	@Override
	public void call(Session session, SessionState state, Exception exception) {
	}

	// ///////////////////////////////
	// ////////// private ////////////
	// ///////////////////////////////

	private GoodsListFragment getGoodsListFragment() {
		return (GoodsListFragment) getSupportFragmentManager().findFragmentByTag(GOODS_LIST_TAG);
	}

	private PurchasesFormFragment getPurchasesFormFragment() {
		return (PurchasesFormFragment) getSupportFragmentManager()
				.findFragmentByTag(PURCHASES_FORM_TAG);
	}

	private PurchasesListFragment getPurchasesListFragment() {
		return (PurchasesListFragment) getSupportFragmentManager()
				.findFragmentByTag(PURCHASES_LIST_TAG);
	}

	private GoodsCategoriesListFragment getGoodsCategoriesListFragment() {
		return (GoodsCategoriesListFragment) getSupportFragmentManager().findFragmentByTag(
				CATEGORIES_LIST_TAG);
	}

	private void goToGoodsList(String filter, boolean addToBackStack) {
		GoodsListFragment goodsListFragment = getGoodsListFragment();

		goodsListFragment = GoodsListFragment.newInstance(Mode.LOOKUP, filter);
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction().replace(
				R.id.dynamic_fragment_container, goodsListFragment, GOODS_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(GOODS_LIST_TAG);

		tr.commit();
		setTitle(R.string.goodsLiTitleLookup);
	}

	private void returnToPurchasesList() {
		System.out.println("PurchasesListActivity.returnToPurchasesList()");
		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		setTitle(R.string.appName);
	}

	private void goToPurchaseForm(boolean addToBackStack, Purchase purchase) {
		PurchasesFormFragment purchasesFormFragment = getPurchasesFormFragment();

		// if (purchasesFormFragment != null) {
		// Log.d(TAG, "already at the purchases form");
		// return;
		// }

		purchasesFormFragment = PurchasesFormFragment.newInstance(purchase);
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction().replace(
				R.id.dynamic_fragment_container, purchasesFormFragment, PURCHASES_FORM_TAG);

		if (addToBackStack)
			tr.addToBackStack(PURCHASES_FORM_TAG);

		tr.commit();
		setTitle(getPurchaseFormTitle(purchase));
	}

	private int getPurchaseFormTitle(Purchase purchase) {
		return purchase.getId() == null ? R.string.purchasesFmTitleAdd : R.string.purchasesFmTitleEdit;
	}

	private void goToCategoriesList(String filter, boolean addToBackStack) {
		GoodsCategoriesListFragment categoriesListFragment = getGoodsCategoriesListFragment();

		if (categoriesListFragment != null) {
			Log.d(TAG, "already at the list");
			return;
		}

		categoriesListFragment = GoodsCategoriesListFragment.newInstance(
				GoodsCategoriesListFragment.Mode.LOOKUP, filter);
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction().add(
				R.id.dynamic_fragment_container, categoriesListFragment, CATEGORIES_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
		setTitle(R.string.goodsCategoriesLiTitleLookup);

		getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
			public void onBackStackChanged() {
				GoodsCategoriesListFragment categoriesListFragment = getGoodsCategoriesListFragment();

				if (categoriesListFragment == null) {
					// category pick up window is closed
					PurchasesFormFragment purchasesFormFragment = getPurchasesFormFragment();

					if (purchasesFormFragment == null) {
						Log.e(TAG, "PurchasesFormFragment was expected but wasn't found");
						return;
					}

					Purchase purchase = purchasesFormFragment.getParameterPurchase();
					int title = getPurchaseFormTitle(purchase);
					setTitle(title);
					getSupportFragmentManager().removeOnBackStackChangedListener(this);
				}
			}
		});
	}

	private void sendFbShare(Session session, String message, String link, String name, String iconUrl) {
		if (session.isOpened()) {
			FacebookDialog.ShareDialogBuilder builder = new ShareDialogBuilder(this);
			builder = builder.setApplicationName(getString(R.string.appName));

			if (message != null)
				builder = builder.setDescription(getString(R.string.socialMessageBase));
			// .setCaption("Test message caption")

			if (link != null)
				builder = builder.setLink(getString(R.string.socialMessageAddition));

			if (name != null)
				builder = builder.setName(getString(R.string.socialMessageMsgTitle));
			// .setRef("Test ref")

			if (iconUrl != null)
				builder = builder.setPicture(getString(R.string.socialMessageIconUrl));

			try {
				uiLifecycleHelper.trackPendingDialogCall(builder.build().present());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sendFbShareMessage(Session session) {
		sendFbShare(session, getString(R.string.socialMessageBase),
				getString(R.string.socialMessageAddition), getString(R.string.socialMessageMsgTitle),
				getString(R.string.socialMessageIconUrl));
	}

	private void sendShareMessage(SocialNetwork network) {
		SocialClient.sendMessage(this, network, null, getShareMessage());
	}

	private void sendShareMessageWithTitle(SocialNetwork network) {
		SocialClient.sendMessage(this, network, getShareMessageTitle(), getShareMessage());
	}

	private String getShareMessageTitle() {
		return getString(R.string.socialMessageMsgTitle);
	}

	private String getShareMessage() {
		return getString(R.string.socialMessageBase).concat(" ").concat(
				getString(R.string.socialMessageAddition));
	}

}
