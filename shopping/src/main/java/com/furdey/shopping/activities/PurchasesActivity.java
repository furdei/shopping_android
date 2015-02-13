package com.furdey.shopping.activities;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.furdey.shopping.R;
import com.furdey.shopping.ShoppingApplication;
import com.furdey.shopping.adapters.GoodsListAdapter;
import com.furdey.shopping.content.AlarmUtils;
import com.furdey.shopping.content.PurchasesUtils;
import com.furdey.shopping.content.model.Goods;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.content.model.Purchase;
import com.furdey.shopping.controllers.SocialController;
import com.furdey.shopping.fragments.AlarmTimePickerFragment;
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
import com.furdey.social.android.SocialClient;
import com.furdey.social.android.SocialClientsManager.SocialNetwork;

import java.util.Timer;
import java.util.TimerTask;

public class PurchasesActivity extends BaseActivity implements PurchasesListListener,
		PurchasesFormListener, GoodsListListener, GoodsCategoriesListListener,
		StatusCallback, AlarmTimePickerFragment.AlarmTimePickerListener {

	public static final int REQUEST_SELECT_FRIEND = 1;

    public static final String RESULT_MESSAGE_PARAM_NAME = PurchasesActivity.class.getCanonicalName() + ".messageId";

    /**
     * Modes are used to let user go to the 'Add a purchase' window
     */
    public enum Mode {
        PURCHASES_LIST, ADD_NEW_PURCHASE
    }

    public static final String MODE_PARAMETER = PurchasesActivity.class.getCanonicalName() + ".mode";

	private static final int RESULT_EMPTY = -1;
	private static final String TAG = PurchasesActivity.class.getSimpleName();
    private static final String SAVE_KEEP_SCREEN_ON = "wakeLog";

	private InternetConnectionBroadcastReceiver internetConnectionBroadcastReceiver = null;
	private boolean internetConnectionBroadcastReceiverRegistered = false;
//	private UiLifecycleHelper uiLifecycleHelper;
    private boolean keepScreenOn;

	private static final String PURCHASES_LIST_TAG = "purchasesList";
	private static final String PURCHASES_FORM_TAG = "purchasesForm";
	private static final String GOODS_LIST_TAG = "goodsList";
	private static final String CATEGORIES_LIST_TAG = "categoriesList";
    private static final String ALARM_TIME_PICKER_TAG = "alarmTimePicker";


    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_fragment_activity);

        if (savedInstanceState == null) {
            // we don't need to create a fragment on recreating 'case we already have got one
            goToPurchasesList();

            if (getMode() == Mode.ADD_NEW_PURCHASE) {
                if (getGoodsListFragment() == null) {
                    onNewPurchaseMenuSelected();
                } else {
                    Log.d(TAG, "Already at goods list");
                }
            }
        } else {
            boolean wakeLockOn = savedInstanceState.containsKey(SAVE_KEEP_SCREEN_ON) ?
                    savedInstanceState.getBoolean(SAVE_KEEP_SCREEN_ON) : false;

            if (wakeLockOn) {
                keepScreenOn();
            }
        }

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
                                            SocialController socialController = new SocialController(PurchasesActivity.this);
                                            socialController.createShareActivity(socialNetwork);
										}
									});
						}
					});
				}
			}, 100);
		}
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

		invalidateOptionsMenu();
	}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (getMode() == Mode.ADD_NEW_PURCHASE) {
            if (getGoodsListFragment() == null) {
                onNewPurchaseMenuSelected();
            } else {
                Log.d(TAG, "Already at goods list");
            }
        } else {
            returnToPurchasesList();
        }
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SELECT_FRIEND && resultCode == RESULT_OK) {
			SocialClient.sendVkMessage(this, data.getStringExtra(FriendsListActivity.PERSON_ID),
					data.getStringExtra(FriendsListActivity.MESSAGE_PARAM));
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            outState.putBoolean(SAVE_KEEP_SCREEN_ON, keepScreenOn);
        }
    }

    // /////////////////////////////
	// /// PurchasesListListener ///
	// /////////////////////////////

	@Override
	public void onPurchaseClicked(Purchase purchase) {
		new ToastThrowableAsyncTask<Purchase, Void>(getApplicationContext()) {
			@Override
			protected Void doBackgroundWork(Purchase purchase) throws Exception {
				PurchasesUtils.savePurchase(getApplicationContext(),
                        PurchasesUtils.revertState(purchase));
				return null;
			}
		}.execute(purchase);

        keepScreenOn();
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
    public void onAlarmsMenuSelected() {
        DialogFragment fragment = AlarmTimePickerFragment.newInstance(
                PreferencesManager.getAlarmHour(getApplicationContext()),
                PreferencesManager.getAlarmMinute(getApplicationContext()),
                PreferencesManager.getAlarmRepeat(getApplicationContext())
        );
        fragment.show(getFragmentManager(), ALARM_TIME_PICKER_TAG);
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
        ((ShoppingApplication) getApplication()).trackEvent(
                R.string.analyticsEventCategorySocial,
                R.string.analyticsEventRecommendShopping,
                SocialNetwork.FACEBOOK.name());

        ((ShoppingApplication) getApplication()).trackEvent(
                R.string.analyticsEventCategorySocial,
                R.string.analyticsEventRecommendShopping,
                getString(PreferencesManager.isShareTracked(getApplicationContext()) ?
                        R.string.analyticsEventRepeated : R.string.analyticsEventFirst));
        PreferencesManager.setShareTracked(getApplicationContext(), true);

        SocialController socialController = new SocialController(this);
        socialController.createShareFbActivity();
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

        ((ShoppingApplication) getApplication()).trackEvent(
                R.string.analyticsEventCategorySocial,
                R.string.analyticsEventSendList,
                socialNetwork.name());
        ((ShoppingApplication) getApplication()).trackEvent(
                R.string.analyticsEventCategorySocial,
                R.string.analyticsEventSendList,
                getString(PreferencesManager.isSendListTracked(getApplicationContext()) ?
                        R.string.analyticsEventRepeated : R.string.analyticsEventFirst));
        PreferencesManager.setSendListTracked(getApplicationContext(), true);
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
	public void onEditGoods(final Goods goods) {
        hideKeyboard();

		new ToastThrowableAsyncTask<Goods, Cursor>(getApplicationContext()) {
			@Override
			protected Cursor doBackgroundWork(Goods goods) throws Exception {
				return goods.getId() != null ? PurchasesUtils.getActivePurchasesByGoods(
						getApplicationContext(), goods.getId()) : null;
			}

			@Override
			protected void onSuccess(Cursor purchasesInTheList) {
				boolean thereWasAPurchaseAlready = getFragmentManager().popBackStackImmediate(
						PURCHASES_FORM_TAG, 0);

				if (purchasesInTheList != null && purchasesInTheList.getCount() > 0) {
					// purchase is in the list so let's just edit
					if (thereWasAPurchaseAlready) {
						getFragmentManager().popBackStack();
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

					getFragmentManager().popBackStack();
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

	/**
	 * Here we just select a category to replace the old one at the goods details
	 * form
	 * 
	 * @param category
	 */
	@Override
	public void onEditCategory(GoodsCategory category) {
		getFragmentManager().popBackStack();
		PurchasesFormFragment purchasesFormFragment = getPurchasesFormFragment();
		purchasesFormFragment.setCategory(category);
	}

	@Override
	public void onDeleteCategory(GoodsCategory category) {
		throw new UnsupportedOperationException();
	}

	// ///////////////////////////////
	// /////// StatusCallback ////////
	// ///////////////////////////////

	@Override
	public void call(Session session, SessionState state, Exception exception) {
	}

    // /////////////////////////////
    // // AlarmTimePickerListener //
    // /////////////////////////////

    @Override
    public void onAlarmTimeSet(int hourOfDay, int minute, int repeat) {
        AlarmUtils.setAlarmTime(this, hourOfDay, minute, repeat);

        ((ShoppingApplication) getApplication()).trackEvent(
                R.string.analyticsEventCategoryFeature,
                R.string.analyticsEventAlarmSet,
                getString(PreferencesManager.isAlarmTracked(getApplicationContext()) ?
                        R.string.analyticsEventRepeated : R.string.analyticsEventFirst));
        PreferencesManager.setAlarmTracked(getApplicationContext(), true);
    }

    // ///////////////////////////////
	// ////////// private ////////////
	// ///////////////////////////////

	private GoodsListFragment getGoodsListFragment() {
		return (GoodsListFragment) getFragmentManager().findFragmentByTag(GOODS_LIST_TAG);
	}

	private PurchasesFormFragment getPurchasesFormFragment() {
		return (PurchasesFormFragment) getFragmentManager()
				.findFragmentByTag(PURCHASES_FORM_TAG);
	}

	private GoodsCategoriesListFragment getGoodsCategoriesListFragment() {
		return (GoodsCategoriesListFragment) getFragmentManager().findFragmentByTag(
				CATEGORIES_LIST_TAG);
	}

    private FragmentTransaction replace(FragmentTransaction transaction, int containerId,
                                        Fragment fragment, String tag) {
        return transaction.replace(containerId, fragment, tag);
    }

	private void goToGoodsList(String filter, boolean addToBackStack) {
        GoodsListFragment goodsListFragment = GoodsListFragment.newInstance(GoodsListAdapter.Mode.LOOKUP, filter);
		FragmentTransaction tr = getFragmentManager().beginTransaction();
        replace(tr, R.id.dynamic_fragment_container, goodsListFragment, GOODS_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(GOODS_LIST_TAG);

		tr.commit();
        getFragmentManager().executePendingTransactions();
		setTitle(R.string.goodsLiTitleLookup);
	}

	private void returnToPurchasesList() {
		getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        hideKeyboard();
		setTitle(R.string.appName);
	}

	private void goToPurchaseForm(boolean addToBackStack, Purchase purchase) {
        PurchasesFormFragment purchasesFormFragment = PurchasesFormFragment.newInstance(purchase);
		FragmentTransaction tr = getFragmentManager().beginTransaction();
        replace(tr, R.id.dynamic_fragment_container, purchasesFormFragment, PURCHASES_FORM_TAG);

		if (addToBackStack)
			tr.addToBackStack(PURCHASES_FORM_TAG);

		tr.commit();
        getFragmentManager().executePendingTransactions();
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
		FragmentTransaction tr = getFragmentManager().beginTransaction().add(
				R.id.dynamic_fragment_container, categoriesListFragment, CATEGORIES_LIST_TAG);

		if (addToBackStack)
			tr.addToBackStack(null);

		tr.commit();
        getFragmentManager().executePendingTransactions();
		setTitle(R.string.goodsCategoriesLiTitleLookup);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
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
                    getFragmentManager().removeOnBackStackChangedListener(this);
				}
			}
		});
	}

    private void goToPurchasesList() {
        PurchasesListFragment purchasesListFragment = new PurchasesListFragment();
        FragmentTransaction tr = getFragmentManager().beginTransaction();
        replace(tr, R.id.dynamic_fragment_container, purchasesListFragment, PURCHASES_LIST_TAG);
        tr.commit();
        getFragmentManager().executePendingTransactions();
    }

	private void sendShareMessage(SocialNetwork network) {
		SocialClient.sendMessage(this, network, null, getShareMessage());

        ((ShoppingApplication) getApplication()).trackEvent(
                R.string.analyticsEventCategorySocial,
                R.string.analyticsEventRecommendShopping,
                network.name());

        ((ShoppingApplication) getApplication()).trackEvent(
                R.string.analyticsEventCategorySocial,
                R.string.analyticsEventRecommendShopping,
                getString(PreferencesManager.isShareTracked(getApplicationContext()) ?
                        R.string.analyticsEventRepeated : R.string.analyticsEventFirst));
        PreferencesManager.setShareTracked(getApplicationContext(), true);
    }

	private void sendShareMessageWithTitle(SocialNetwork network) {
		SocialClient.sendMessage(this, network, getShareMessageTitle(), getShareMessage());

        ((ShoppingApplication) getApplication()).trackEvent(
                R.string.analyticsEventCategorySocial,
                R.string.analyticsEventRecommendShopping,
                network.name());

        ((ShoppingApplication) getApplication()).trackEvent(
                R.string.analyticsEventCategorySocial,
                R.string.analyticsEventRecommendShopping,
                getString(PreferencesManager.isShareTracked(getApplicationContext()) ?
                    R.string.analyticsEventRepeated : R.string.analyticsEventFirst));
        PreferencesManager.setShareTracked(getApplicationContext(), true);
	}

	private String getShareMessageTitle() {
		return getString(R.string.socialMessageMsgTitle);
	}

	private String getShareMessage() {
		return getString(R.string.socialMessageBase).concat(" ").concat(
				getString(R.string.socialMessageAddition));
	}

    private Mode getMode() {
        String modeStr = (getIntent() != null && getIntent().getExtras() != null) ?
                getIntent().getExtras().getString(MODE_PARAMETER) : null;

        if (modeStr == null) {
            return Mode.PURCHASES_LIST;
        }

        return Mode.valueOf(modeStr);
    }

    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        keepScreenOn = true;
    }
}
