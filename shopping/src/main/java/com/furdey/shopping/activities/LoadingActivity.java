package com.furdey.shopping.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.furdey.shopping.R;
import com.furdey.shopping.contentproviders.DatabaseHelper;
import com.furdey.shopping.utils.LogicException;
import com.furdey.shopping.utils.PreferencesManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadingActivity extends Activity {

	private static final String TAG = LoadingActivity.class.getSimpleName();
	private static final long LOADING_MIN_DURATION = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final long start = System.currentTimeMillis();
		final Activity activity = this;

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				PreferencesManager.setRunCount(LoadingActivity.this,
						PreferencesManager.getRunCount(LoadingActivity.this) + 1);
				File db = new File(getDatabasePath(DatabaseHelper.DATABASE_NAME).getPath());

				if (!db.exists()) {
					try {
						DatabaseHelper.copyDataBase(activity);
					} catch (Exception e) {
						Log.e(TAG, "Error while creating database", e);
						throw new LogicException(activity, R.string.errorUnknown, e);
					}
				}

				Date date = new Date();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String currentDate = df.format(date);

				String lastRunDate = PreferencesManager.getLastRunDate(LoadingActivity.this);
				long now = System.currentTimeMillis();

				if (now - start < LOADING_MIN_DURATION && currentDate.compareTo(lastRunDate) != 0) {
					try {
						Thread.sleep(LOADING_MIN_DURATION - now + start);
					} catch (InterruptedException e) {
						// If there is exception during SLEEPPING there's nothing
						// to worry about
						e.printStackTrace();
					}
				}

				PreferencesManager.setLastRunDate(LoadingActivity.this, currentDate);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Intent intent = new Intent(activity, PurchasesActivity.class);
				startActivity(intent);
				activity.finish();
			}
		}.execute();
	}
}
