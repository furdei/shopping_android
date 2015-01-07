package com.furdey.shopping.activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.furdey.shopping.R;
import com.furdey.shopping.content.GoodsUtils;
import com.furdey.shopping.content.SearchUtils;
import com.furdey.shopping.contentproviders.DatabaseHelper;
import com.furdey.shopping.contentproviders.GoodsContentProvider;
import com.furdey.shopping.fragments.ProgressDialogFragment;
import com.furdey.shopping.tasks.RebuildIndicesTask;
import com.furdey.shopping.utils.IntentUtils;
import com.furdey.shopping.utils.LogicException;
import com.furdey.shopping.utils.PreferencesManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoadingActivity extends Activity {

	private static final String TAG = LoadingActivity.class.getSimpleName();
	private static final long LOADING_MIN_DURATION = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.language);

        final TextView version = (TextView) findViewById(R.id.languageFormVersion);
        final View controls = findViewById(R.id.languageControls);
        final Spinner languagesSpinner = (Spinner) findViewById(R.id.languageList);
        final Button selectButton = (Button) findViewById(R.id.languageSelect);

        try {
            version.setText(getString(R.string.aboutAppVersion,
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve app version", e);
        }

		final long start = System.currentTimeMillis();
        final File db = new File(getDatabasePath(DatabaseHelper.DST_DATABASE_NAME).getPath());

        if (!db.exists()) {
            controls.setVisibility(View.VISIBLE);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.languages, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            languagesSpinner.setAdapter(adapter);

            final String[] locales = getResources().getStringArray(R.array.locales);
            Locale locale = Locale.getDefault();
            String language = locale.getLanguage().substring(0, 2);

            languagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position < 0 || position >= locales.length) {
                        position = 0;
                    }

                    setLocale(locales[position], true);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            boolean found = false;
            for (int i = 0; i < locales.length && !found; i++) {
                if (locales[i].compareTo(language) == 0) {
                    found = true;
                    languagesSpinner.setSelection(i);
                }
            }

            if (!found) {
                languagesSpinner.setSelection(0);
            }

            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        DatabaseHelper.copyDataBase(LoadingActivity.this);
                    } catch (Exception e) {
                        Log.e(TAG, "Error while creating database", e);
                        throw new LogicException(LoadingActivity.this, R.string.errorUnknown, e);
                    }
                    PreferencesManager.setLastRunDate(LoadingActivity.this, getCurrentDate());

                    String locale = locales[languagesSpinner.getSelectedItemPosition()];
                    PreferencesManager.setLanguage(getBaseContext(), locale);

                    finishStartUp();
                }
            });
        } else {
            setLocale(PreferencesManager.getLanguage(getBaseContext()), false);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... arg0) {
                    PreferencesManager.setRunCount(LoadingActivity.this,
                            PreferencesManager.getRunCount(LoadingActivity.this) + 1);

                    String currentDate = getCurrentDate();

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
                    finishStartUp();
                }
            }.execute();
        }
	}

    private static final String PROGRESS_TAG = "progressDialog";

    private void finishStartUp() {
        if (!SearchUtils.isSearchFieldExist(getApplicationContext(),
                GoodsContentProvider.Columns.NAME_LOWER.toString(),
                new SearchUtils.ObjectsProvider() {
                    @Override
                    public Cursor getObjects(Context context, String[] projection, String selection) {
                        return GoodsUtils.getGoods(context, projection, selection, null, null);
                    }
                })) {

            ProgressDialogFragment fragment = ProgressDialogFragment.createInstance(
                    getString(R.string.rebuildIndicesTitle),
                    null, 100);
            getFragmentManager().beginTransaction().add(fragment, PROGRESS_TAG).commit();

            new RebuildIndicesTask(fragment).execute(this);
        } else {
            runPurchasesActivity();
        }
    }

    private void runPurchasesActivity() {
        startActivity(IntentUtils.purchaseActivityIntent(this));
        finish();
    }

    private String getCurrentDate() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    private void setLocale(String locale, boolean recreate) {
        Locale oldLocale = Locale.getDefault();

        if (!oldLocale.getLanguage().substring(0, 2).equals(locale)) {
            Locale newLocale = new Locale(locale);
            Locale.setDefault(newLocale);
            Configuration configuration = new Configuration(
                    getBaseContext().getResources().getConfiguration());
            configuration.locale = newLocale;
            getBaseContext().getResources().updateConfiguration(configuration,
                    getBaseContext().getResources().getDisplayMetrics());

            if (recreate) {
                recreate();
            }
        }
    }
}
