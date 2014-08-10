package com.furdey.shopping.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.bugsense.trace.BugSenseHandler;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class BaseActivity extends ActionBarActivity implements StatusCallback {

	private static final String APIKEY = "YOURAPIKEY";
	private UiLifecycleHelper uiLifecycleHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, APIKEY);

		uiLifecycleHelper = new UiLifecycleHelper(this, this);
		uiLifecycleHelper.onCreate(savedInstanceState);
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
	}

	@Override
	protected void onDestroy() {
		uiLifecycleHelper.onDestroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		uiLifecycleHelper.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		uiLifecycleHelper.onResume();
		super.onResume();
	}

	@Override
	protected void onStop() {
		uiLifecycleHelper.onStop();
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		uiLifecycleHelper.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		uiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	public UiLifecycleHelper getUiLifecycleHelper() {
		return uiLifecycleHelper;
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case R.id.menu_units: Log.d("TAG",
	 * "R.id.menu_units selected"); Intent intent = new Intent(this,
	 * UnitsListActivity.class); startActivity(intent); return true;
	 * 
	 * default: return super.onOptionsItemSelected(item); } }
	 */

}
