package com.furdey.shopping.controllers;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.ShareDialogBuilder;
import com.furdey.engine.android.activities.DataLinkActivity;
import com.furdey.engine.android.controllers.BaseController;
import com.furdey.engine.android.controllers.CursorLoader;
import com.furdey.engine.android.controllers.OnModelLoadListener;
import com.furdey.engine.android.controllers.OnSaveCompleteListener;
import com.furdey.engine.android.utils.LogicException;
import com.furdey.engine.android.utils.Settings;
import com.furdey.shopping.R;
import com.furdey.shopping.activities.AboutAppActivity;
import com.furdey.shopping.activities.BaseActivity;
import com.furdey.shopping.activities.GoodsActivity;
import com.furdey.shopping.activities.GoodsCategoriesActivity;
import com.furdey.shopping.activities.PurchasesActivity;
import com.furdey.shopping.activities.UnitsActivity;
import com.furdey.shopping.activities.VkLoginActivity;
import com.furdey.shopping.content.PurchasesUtils;
import com.furdey.shopping.content.model.CategoriesStatistics;
import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.content.model.GoodsStatistics;
import com.furdey.shopping.content.model.Purchase;
import com.furdey.shopping.content.model.Purchase.PurchaseState;
import com.furdey.shopping.content.model.Unit;
import com.furdey.shopping.contentproviders.PurchasesContentProvider;
import com.furdey.shopping.dao.BaseDao;
import com.furdey.shopping.dao.GoodsDao;
import com.furdey.shopping.dao.UnitsDao;
import com.furdey.shopping.dao.db.DatabaseHelper;
import com.furdey.shopping.widgets.ShoppingListWidgetProvider;
import com.furdey.social.SocialConnectionsPool;
import com.furdey.social.android.SocialClient;
import com.furdey.social.android.SocialClientsManager.SocialNetwork;
import com.furdey.social.vk.api.MessagesSendRequest;
import com.furdey.social.vk.api.MessagesSendResponse;
import com.furdey.social.vk.connector.VkConnection;
import com.j256.ormlite.misc.TransactionManager;

public class PurchasesController extends BaseController<Purchase, DatabaseHelper> {

	public static final String RESULT_MESSAGE_PARAM_NAME = "com.furdey.shopping.activities.PurchasesController.messageId";

	public static final int REQUEST_ADD = 0;
	public static final int REQUEST_EDIT = 1;
	public static final int REQUEST_SELECT_GOOD = 2;
	public static final int REQUEST_SELECT_GOODSCATEGORY = 3;
	public static final int REQUEST_SELECT_FRIEND = 4;

	private static final String TAG = PurchasesController.class.getSimpleName();

	private String purchasesListToSend;

	public PurchasesController(DataLinkActivity<DatabaseHelper> activity) {
		super(activity);

		// State by default if calling activity didn't supply any state.
		if (getState() == STATE_UNSTATED)
			setState(STATE_GRID);
	}

	// @Override
	// protected Cursor onSelect() throws SQLException {
	// return getActivity().getDaoHelper().getPurchasesDao()
	// .fetchAll(PreferencesManager.getPurchasesSortOrder(getActivity()));
	// }
	@Override
	protected Purchase onBeforeInsert() {
		Purchase model = null; // ((PurchasesFormActivity)
														// getActivity()).createFromUi();
		model.setStrdate(new Date());
		model.setFindate(BaseDao.getInfinity());
		return model;
	}

	@Override
	protected Intent onInsert(final Purchase model) throws SQLException {
		TransactionManager.callInTransaction(getActivity().getDaoHelper().getConnectionSource(),
				new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						// Create goods category if needed
						if (model.getGoods().getCategory().getId() == null)
							getActivity().getDaoHelper().getGoodsCategoriesDao()
									.create(model.getGoods().getCategory());

						// Create good if needed
						if (model.getGoods().getId() == null)
							getActivity().getDaoHelper().getGoodsDao().create(model.getGoods());
						else {
							Goods good = getActivity().getDaoHelper().getGoodsDao()
									.queryForId(model.getGoods().getId());

							// Change good's default units if needed
							// Change good's category if needed
							if (good != null
									&& (good.getDefaultUnits().getId() != model.getGoods().getDefaultUnits().getId() || good
											.getCategory().getId() != model.getGoods().getCategory().getId())) {
								good.setDefaultUnits(model.getGoods().getDefaultUnits());
								good.setCategory(model.getGoods().getCategory());
								getActivity().getDaoHelper().getGoodsDao().update(good);
							}
						}

						// And finally create a purchase
						getActivity().getDaoHelper().getPurchasesDao().create(model);
						return null;
					}
				});

		Intent result = new Intent();
		result.putExtra(RESULT_MESSAGE_PARAM_NAME, R.string.purchasesFmResultAdded);
		return result;
	}

	@Override
	protected Purchase onBeforeUpdate() {
		Purchase model = null;// ((PurchasesFormActivity)
													// getActivity()).createFromUi();
		model.setId(getModel().getId());
		model.setState(getModel().getState());
		model.setStrdate(new Date());
		model.setFindate(BaseDao.getInfinity());
		return model;
	}

	@Override
	protected Intent onUpdate(final Purchase model) throws SQLException {
		TransactionManager.callInTransaction(getActivity().getDaoHelper().getConnectionSource(),
				new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						// Create goods category if needed
						if (model.getGoods().getCategory().getId() == null)
							getActivity().getDaoHelper().getGoodsCategoriesDao()
									.create(model.getGoods().getCategory());

						// Create good if needed
						if (model.getGoods().getId() == null)
							getActivity().getDaoHelper().getGoodsDao().create(model.getGoods());
						else {
							Goods good = getActivity().getDaoHelper().getGoodsDao()
									.queryForId(model.getGoods().getId());

							// Change good's default units if needed
							// Change good's category if needed
							if (good != null
									&& (good.getDefaultUnits().getId() != model.getGoods().getDefaultUnits().getId() || good
											.getCategory().getId() != model.getGoods().getCategory().getId())) {
								good.setDefaultUnits(model.getGoods().getDefaultUnits());
								good.setCategory(model.getGoods().getCategory());
								getActivity().getDaoHelper().getGoodsDao().update(good);
							}
						}

						// And finally update a purchase
						getActivity().getDaoHelper().getPurchasesDao().update(model);
						return null;
					}
				});

		Intent result = new Intent();
		result.putExtra(RESULT_MESSAGE_PARAM_NAME, R.string.purchasesFmResultEdited);
		return result;
	}

	@Override
	protected void onDelete(final Integer id) throws SQLException {
		getActivity().getDaoHelper().getPurchasesDao().softDelete(id);
	}

	public CursorLoader getUnitsLoader() {
		return new CursorLoader() {
			@Override
			public Cursor loadCursor() throws SQLException {
				return getActivity().getDaoHelper().getUnitsDao().fetchAll();
			}

		};
	}

	// public void createEditForm(Purchase model) {
	// Intent intent = new Intent(getActivity(), PurchasesFormActivity.class);
	// intent.putExtra(BaseController.STATE_PARAM_NAME,
	// BaseController.STATE_FORM_EDIT);
	// intent.putExtra(BaseController.OBJECT_TO_EDIT_PARAM_NAME, model);
	// getActivity().startActivityForResult(intent, REQUEST_EDIT);
	// }

	public void createGoodsListActivity() {
		Intent intent = new Intent(getActivity(), GoodsActivity.class);
		intent.putExtra(BaseController.STATE_PARAM_NAME, BaseController.STATE_GRID);
		getActivity().startActivity(intent);
	}

	public void createGoodsCategoriesListActivity() {
		Intent intent = new Intent(getActivity(), GoodsCategoriesActivity.class);
		intent.putExtra(BaseController.STATE_PARAM_NAME, BaseController.STATE_GRID);
		getActivity().startActivity(intent);
	}

	public void createGoodsCategoriesListActivity(String filter) {
		Intent intent = new Intent(getActivity(), null/*
																									 * GoodsCategoriesListActivity.
																									 * class
																									 */);

		if (filter != null)
			intent.putExtra(BaseFilterController.FILTER_PARAM_NAME, filter);

		intent.putExtra(BaseController.STATE_PARAM_NAME, BaseFilterController.STATE_LOOKUP);
		getActivity().startActivityForResult(intent, REQUEST_SELECT_GOODSCATEGORY);
	}

	public void createUnitsListActivity() {
		Intent intent = new Intent(getActivity(), UnitsActivity.class);
		getActivity().startActivity(intent);
	}

	public final void refreshPurchase(Integer id, final OnModelLoadListener<Purchase> onRefreshed) {
		new AsyncTask<Integer, Void, Purchase>() {

			@Override
			protected Purchase doInBackground(Integer... params) {
				try {
					return getActivity().getDaoHelper().getPurchasesDao().queryForId(params[0]);
				} catch (SQLException e) {
					throw new LogicException(getActivity(), Settings.getInstance().getUnknownError(), e);
				}
			}

			@Override
			protected void onPostExecute(Purchase result) {
				onRefreshed.onLoadComplete(result);
			}
		}.execute(id);
	}

	// public final void refreshGood(Integer id, final OnModelLoadListener<Goods>
	// onRefreshed) {
	// new AsyncTask<Integer, Void, Goods>() {
	//
	// @Override
	// protected Goods doInBackground(Integer... params) {
	// try {
	// return getActivity().getDaoHelper().getGoodsDao().queryForId(params[0]);
	// } catch (SQLException e) {
	// throw new LogicException(getActivity(),
	// Settings.getInstance().getUnknownError(), e);
	// }
	// }
	//
	// @Override
	// protected void onPostExecute(Goods result) {
	// onRefreshed.onLoadComplete(result);
	// }
	// }.execute(id);
	// }

	public final void refreshCategory(Integer id, final OnModelLoadListener<GoodsCategory> onRefreshed) {
		new AsyncTask<Integer, Void, GoodsCategory>() {

			@Override
			protected GoodsCategory doInBackground(Integer... params) {
				try {
					return getActivity().getDaoHelper().getGoodsCategoriesDao().queryForId(params[0]);
				} catch (SQLException e) {
					throw new LogicException(getActivity(), Settings.getInstance().getUnknownError(), e);
				}
			}

			@Override
			protected void onPostExecute(GoodsCategory result) {
				onRefreshed.onLoadComplete(result);
			}
		}.execute(id);
	}

	private Purchase updatePurchaseStateInternal(final Integer id, final Purchase.PurchaseState state) {
		try {
			Purchase model = getActivity().getDaoHelper().getPurchasesDao().queryForId(id);
			Purchase prvPurchase = null;

			switch (state) {
			case ENTERED:
				// Purchase was previously accepted, and now it's being unaccepted
				model.setFindate(BaseDao.getInfinity());

				// Find the previous latest purchase...
				prvPurchase = getActivity().getDaoHelper().getPurchasesDao().getLatestPurchase(model);

				// Update buy statistics
				updateStatistics(prvPurchase, model, -1);
				break;

			case ACCEPTED:
				// Purchase was previously entered, and now it's being accepted
				model.setFindate(new Date());

				// Find the latest purchase...
				prvPurchase = getActivity().getDaoHelper().getPurchasesDao().getLatestPurchase(null);

				// Update buy statistics
				updateStatistics(prvPurchase, model, 1);

				break;
			}

			model.setState(state);
			model.setChanged(new Date());
			getActivity().getDaoHelper().getPurchasesDao().update(model);
			return model;
		} catch (SQLException e) {
			throw new LogicException(getActivity(), Settings.getInstance().getUnknownError(), e);
		}
	}

	public void updatePurchaseStateSync(final Integer id, final Purchase.PurchaseState state,
			final OnModelLoadListener<Purchase> listener) {
		listener.onLoadComplete(updatePurchaseStateInternal(id, state));
	}

	public void updatePurchaseState(final Integer id, final Purchase.PurchaseState state,
			final OnModelLoadListener<Purchase> listener) {
		new AsyncTask<Void, Void, Purchase>() {

			@Override
			protected Purchase doInBackground(Void... params) {
				return updatePurchaseStateInternal(id, state);
			}

			@Override
			protected void onPostExecute(Purchase result) {
				listener.onLoadComplete(result);
			}
		}.execute();
	}

	@SuppressLint("SimpleDateFormat")
	private void updateStatistics(Purchase prvPurchase, Purchase nextPurchase, int inc)
			throws SQLException {
		Log.d(TAG, "Purchase inc: " + inc);
		SimpleDateFormat sdf = new SimpleDateFormat(BaseDao.DATE_FORMAT);

		// Retrieve goods details for statistics purposes
		Goods nextGood = getActivity().getDaoHelper().getGoodsDao()
				.queryForId(nextPurchase.getGoods().getId());

		// If this is NOT the first purchase of the day
		if (prvPurchase != null
				&& sdf.format(prvPurchase.getChanged()).compareTo(sdf.format(new Date())) == 0) {
			Log.d(TAG, "This is NOT the first purchase of the day");

			// Retrieve goods details
			Goods prvGood = getActivity().getDaoHelper().getGoodsDao()
					.queryForId(prvPurchase.getGoods().getId());

			if (prvGood.getCategory().getId() != nextGood.getCategory().getId()) {
				// Previous and new purchases are of different categories
				Log.d(TAG, "Previous and new purchases are of different categories");
				CategoriesStatistics catStat = getActivity().getDaoHelper().getCategoriesStatisticsDao()
						.getStatistics(prvGood.getCategory(), nextGood.getCategory());
				catStat.setBuyCount(catStat.getBuyCount() + inc);
				catStat.setChanged(new Date());
				getActivity().getDaoHelper().getCategoriesStatisticsDao().createOrUpdate(catStat);

				GoodsStatistics goodStat = getActivity().getDaoHelper().getGoodsStatisticsDao()
						.getStatistics(null, nextGood);
				goodStat.setBuyCount(goodStat.getBuyCount() + inc);
				goodStat.setChanged(new Date());
				getActivity().getDaoHelper().getGoodsStatisticsDao().createOrUpdate(goodStat);
			} else {
				// Previous and new purchases are of the same category
				Log.d(TAG, "Previous and new purchases are of the same category");

				if (prvGood.getId().compareTo(nextGood.getId()) != 0) {
					Log.d(TAG, "Good is not the same as previous. Prv: " + prvGood.getId() + " Nxt: "
							+ nextGood.getId());

					GoodsStatistics goodStat = getActivity().getDaoHelper().getGoodsStatisticsDao()
							.getStatistics(prvGood, nextGood);
					goodStat.setBuyCount(goodStat.getBuyCount() + inc);
					goodStat.setChanged(new Date());
					getActivity().getDaoHelper().getGoodsStatisticsDao().createOrUpdate(goodStat);
				}
			}
		} else {
			// The first purchase of the day
			Log.d(TAG, "The first purchase of the day");
			CategoriesStatistics catStat = getActivity().getDaoHelper().getCategoriesStatisticsDao()
					.getStatistics(null, nextGood.getCategory());
			catStat.setBuyCount(catStat.getBuyCount() + inc);
			catStat.setChanged(new Date());
			getActivity().getDaoHelper().getCategoriesStatisticsDao().createOrUpdate(catStat);

			GoodsStatistics goodStat = getActivity().getDaoHelper().getGoodsStatisticsDao()
					.getStatistics(null, nextGood);
			goodStat.setBuyCount(goodStat.getBuyCount() + inc);
			goodStat.setChanged(new Date());
			getActivity().getDaoHelper().getGoodsStatisticsDao().createOrUpdate(goodStat);
		}
	}

	public void delayPurchase(final Integer id, final OnModelLoadListener<Purchase> listener) {
		new AsyncTask<Void, Void, Purchase>() {

			@Override
			protected Purchase doInBackground(Void... params) {
				try {
					Purchase model = getActivity().getDaoHelper().getPurchasesDao().queryForId(id);

					Calendar c = Calendar.getInstance();
					c.setTime(new Date());
					c.add(Calendar.DATE, 1);
					model.setStrdate(c.getTime());

					getActivity().getDaoHelper().getPurchasesDao().update(model);
					return model;
				} catch (SQLException e) {
					throw new LogicException(getActivity(), Settings.getInstance().getUnknownError(), e);
				}
			}

			@Override
			protected void onPostExecute(Purchase result) {
				listener.onLoadComplete(result);
			}
		}.execute();
	}

	public OnSaveCompleteListener getOnSaveCompleteListener() {
		return new OnSaveCompleteListener() {
			@Override
			public void onSaveComplete(Intent result) {
				Intent intent = new Intent(getActivity(), PurchasesActivity.class);
				intent.putExtra(BaseController.STATE_PARAM_NAME, BaseController.STATE_GRID);
				intent.putExtras(result);
				getActivity().startActivity(intent);
				getActivity().finish();
				ShoppingListWidgetProvider.updateWidgets(getActivity());
			}
		};
	}

	public void createAboutActivity() {
		Intent intent = new Intent(getActivity(), AboutAppActivity.class);
		getActivity().startActivity(intent);
	}

	public void createShareVkActivity() {
		SocialClient.sendMessage(
				getActivity(),
				SocialNetwork.VK,
				null,
				getActivity().getString(R.string.socialMessageBase).concat(" ")
						.concat(getActivity().getString(R.string.socialMessageAddition)));
		/*
		 * Intent intent = new Intent(getActivity(), VkLoginActivity.class);
		 * getActivity().startActivity(intent);
		 */
	}

	public void createShareFbActivity() {
		/*
		 * SocialClient.sendMessage( getActivity(), SocialNetwork.FACEBOOK, null,
		 * getActivity().getString(R.string.socialMessageBase).concat(" ")
		 * .concat(getActivity().getString(R.string.socialMessageAddition)));
		 */

		// start Facebook Login
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			sendFbShareMessage(session);
		} else {
			List<String> permissions = new ArrayList<String>();
			permissions.add("public_profile");
			permissions.add("user_friends");
			// permissions.add("publish_actions");
			Session.openActiveSession(getActivity(), true, permissions, new Session.StatusCallback() {
				// callback when session changes state
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					sendFbShareMessage(session);
				}
			});
		}
	}

	private void sendFbShare(Session session, String message, String link, String name, String iconUrl) {
		if (session.isOpened()) {
			FacebookDialog.ShareDialogBuilder builder = new ShareDialogBuilder(getActivity());
			builder = builder.setApplicationName(getActivity().getString(R.string.appName));

			if (message != null)
				builder = builder.setDescription(getActivity().getString(R.string.socialMessageBase));
			// .setCaption("Test message caption")

			if (link != null)
				builder = builder.setLink(getActivity().getString(R.string.socialMessageAddition));

			if (name != null)
				builder = builder.setName(getActivity().getString(R.string.socialMessageMsgTitle));
			// .setRef("Test ref")

			if (iconUrl != null)
				builder = builder.setPicture(getActivity().getString(R.string.socialMessageIconUrl));

			try {
				((BaseActivity) getActivity()).getUiLifecycleHelper().trackPendingDialogCall(
						builder.build().present());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sendFbShareMessage(Session session) {
		sendFbShare(session, getActivity().getString(R.string.socialMessageBase), getActivity()
				.getString(R.string.socialMessageAddition),
				getActivity().getString(R.string.socialMessageMsgTitle),
				getActivity().getString(R.string.socialMessageIconUrl));
	}

	public void createShareTwActivity() {
		SocialClient.sendMessage(
				getActivity(),
				SocialNetwork.TWITTER,
				null,
				getActivity().getString(R.string.socialMessageBase).concat(" ")
						.concat(getActivity().getString(R.string.socialMessageAddition)));
	}

	public void createShareGPActivity() {
		SocialClient.sendMessage(
				getActivity(),
				SocialNetwork.GOOGLE_PLUS,
				null,
				getActivity().getString(R.string.socialMessageBase).concat(" ")
						.concat(getActivity().getString(R.string.socialMessageAddition)));
	}

	public void createShareLIActivity() {
		SocialClient.sendMessage(
				getActivity(),
				SocialNetwork.LINKEDIN,
				null,
				getActivity().getString(R.string.socialMessageBase).concat(" ")
						.concat(getActivity().getString(R.string.socialMessageAddition)));
	}

	public void createShareSkActivity() {
		SocialClient.sendMessage(
				getActivity(),
				SocialNetwork.SKYPE,
				null,
				getActivity().getString(R.string.socialMessageBase).concat(" ")
						.concat(getActivity().getString(R.string.socialMessageAddition)));
	}

	public void createShareEmActivity() {
		SocialClient.sendMessage(
				getActivity(),
				SocialNetwork.EMAIL,
				getActivity().getString(R.string.socialMessageMsgTitle),
				getActivity().getString(R.string.socialMessageBase).concat(" ")
						.concat(getActivity().getString(R.string.socialMessageAddition)));
	}

	private static final String ERROR_OCURED = "#error#";

	public void sendList(final SocialNetwork socialNetwork) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					String[] projection = new String[] { PurchasesContentProvider.Columns._id.toString(),
							PurchasesContentProvider.Columns.STATE.toString(),
							PurchasesContentProvider.Columns.GOODS_NAME.toString(),
							PurchasesContentProvider.Columns.DESCR.toString(),
							PurchasesContentProvider.Columns.COUNT.toString(),
							PurchasesContentProvider.Columns.UNIT_NAME.toString(),
							PurchasesContentProvider.Columns.GOODSCATEGORY_ICON.toString() };
					Cursor data = getActivity().getContentResolver().query(
							PurchasesContentProvider.PURCHASES_URI, projection, null, null, null);

					Cursor unitsCursor = getActivity().getDaoHelper().getUnitsDao().fetchAll();
					Map<Long, Unit> unitsMap = new HashMap<Long, Unit>();
					while (!unitsCursor.isAfterLast()) {
						Unit unit = UnitsDao.fromCursor(unitsCursor);
						unitsMap.put(unit.getId(), unit);
						unitsCursor.moveToNext();
					}

					String listStr = "";
					String purchasesLiSendListItemsDelimeter = getActivity().getString(
							R.string.purchasesLiSendListItemsDelimeter);
					String purchasesLiSendListCounterDelimeter = getActivity().getString(
							R.string.purchasesLiSendListCounterDelimeter);
					String purchasesLiSendListDescrFormat = getActivity().getString(
							R.string.purchasesLiSendListDescrFormat);
					data.moveToFirst();
					GoodsDao goodsDao = getActivity().getDaoHelper().getGoodsDao();

					while (!data.isAfterLast()) {
						Purchase purchase = PurchasesUtils.fromCursor(data);

						if (purchase.getState() == PurchaseState.ENTERED) {
							if (listStr.length() > 0) {
								listStr = listStr.concat(purchasesLiSendListItemsDelimeter);
							}

							Goods good = goodsDao.queryForId(purchase.getGoods().getId());
							listStr = listStr.concat(good.getName()).concat(purchasesLiSendListCounterDelimeter)
									.concat(purchase.getCount().toString())
									.concat(purchasesLiSendListCounterDelimeter)
									.concat(unitsMap.get(purchase.getUnits().getId()).getName());

							String descr = purchase.getDescr();
							if (descr != null)
								if (descr.length() > 0) {
									listStr = listStr.concat(String.format(purchasesLiSendListDescrFormat, descr));
								}
						}

						data.moveToNext();
					}

					return getActivity().getString(R.string.purchasesLiSendListHeader).concat(listStr);
				} catch (Exception e) {
					e.printStackTrace();
					return ERROR_OCURED;
				}
			}

			@Override
			protected void onPostExecute(final String result) {
				if (ERROR_OCURED.compareTo(result) == 0) {
					Toast.makeText(getActivity(), getActivity().getString(R.string.purchasesLiFailedToSend),
							Toast.LENGTH_LONG).show();
				} else if (result.length() == 0) {
					Toast.makeText(getActivity().getApplicationContext(),
							getActivity().getString(R.string.purchasesLiNoPurchasesForSend), Toast.LENGTH_LONG)
							.show();
				} else {
					switch (socialNetwork) {
					// case FACEBOOK:
					// // start Facebook Login
					// Session session = Session.getActiveSession();
					// if (session != null && session.isOpened())
					// sendFbShare(session, result, null, null, null);
					// else {
					// List<String> permissions = new ArrayList<String>();
					// permissions.add("public_profile");
					// permissions.add("user_friends");
					// Session.openActiveSession(getActivity(), true, permissions,
					// new Session.StatusCallback() {
					// // callback when session changes state
					// @Override
					// public void call(Session session, SessionState state,
					// Exception
					// exception) {
					// sendFbShare(session, result, null,
					// getActivity().getString(R.string.socialMessageMsgTitle),
					// getActivity()
					// .getString(R.string.socialMessageIconUrl));
					// }
					// });
					// }
					// break;
					case VK:
						purchasesListToSend = result;
						Intent intent = new Intent(getActivity(), VkLoginActivity.class);
						getActivity().startActivityForResult(intent, REQUEST_SELECT_FRIEND);
						break;
					default:
						SocialClient.sendMessage(getActivity(), socialNetwork,
								getActivity().getString(R.string.appName), result);
					}
				}
			}
		}.execute();
	}

	public void onFriendSelected(final String personId) {
		if (purchasesListToSend != null && personId != null) {
			new AsyncTask<Void, Void, MessagesSendResponse>() {
				@Override
				protected MessagesSendResponse doInBackground(Void... params) {
					VkConnection vkConn = (VkConnection) SocialConnectionsPool.getInstance().get(
							VkConnection.class);

					MessagesSendRequest vkMsg = new MessagesSendRequest();
					vkMsg.setTitle(getActivity().getString(R.string.appName));
					vkMsg.setMessage(purchasesListToSend);
					vkMsg.setUid(Long.parseLong(personId));
					vkMsg.setChat_id(Long.parseLong(personId));
					MessagesSendResponse vkResp = (MessagesSendResponse) vkConn.callVk(vkMsg);
					return vkResp;
				}

				@Override
				protected void onPostExecute(MessagesSendResponse result) {
					if (result != null) {
						Toast.makeText(getActivity(), getActivity().getString(R.string.socialMessageSent),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(),
								getActivity().getString(R.string.purchasesLiFailedToSend), Toast.LENGTH_LONG)
								.show();
					}
				}
			}.execute();
		} else {
			Toast.makeText(getActivity(), getActivity().getString(R.string.purchasesLiFailedToSend),
					Toast.LENGTH_LONG).show();
		}
	}
}
