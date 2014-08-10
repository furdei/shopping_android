package com.furdey.shopping.activities;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.furdey.shopping.R;
import com.furdey.shopping.controllers.SocialController;
import com.furdey.social.android.SocialClientsManager;
import com.furdey.social.android.SocialClientsManager.SocialNetwork;

public class AboutAppActivity extends BaseActivity {

	private TextView version;
	private ImageView shareVK;
	private ImageView shareFB;
	private ImageView shareGP;
	private ImageView shareLI;
	private ImageView shareSk;
	private ImageView shareTw;
	private ImageView shareEm;
	private View share;
	private View shareGr1;
	private View shareGr2;

	private SocialController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutapp);

		version = (TextView) findViewById(R.id.aboutAppVersion);
		shareVK = (ImageView) findViewById(R.id.aboutAppShareVK);
		shareFB = (ImageView) findViewById(R.id.aboutAppShareFB);
		shareGP = (ImageView) findViewById(R.id.aboutAppShareGP);
		shareLI = (ImageView) findViewById(R.id.aboutAppShareLI);
		shareSk = (ImageView) findViewById(R.id.aboutAppShareSk);
		shareTw = (ImageView) findViewById(R.id.aboutAppShareTw);
		shareEm = (ImageView) findViewById(R.id.aboutAppShareEm);
		share = findViewById(R.id.aboutAppShare);
		shareGr1 = findViewById(R.id.aboutAppSocialIcons);
		shareGr2 = findViewById(R.id.aboutAppSocialIcons2);

		try {
			version.setText(getString(R.string.aboutAppVersion,
					getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to retreive app version", e);
		}
		controller = new SocialController(this);

		if (isNetworkAvailable()) {
			if (SocialClientsManager.isClientInstalled(this, SocialNetwork.VK))
				shareVK.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						controller.createShareVkActivity();
					}
				});
			else
				shareVK.setVisibility(View.GONE);

			if (SocialClientsManager.isClientInstalled(this, SocialNetwork.FACEBOOK))
				shareFB.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						controller.createShareFbActivity();
					}
				});
			else
				shareFB.setVisibility(View.GONE);

			if (SocialClientsManager.isClientInstalled(this, SocialNetwork.GOOGLE_PLUS))
				shareGP.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						controller.createShareGPActivity();
					}
				});
			else
				shareGP.setVisibility(View.GONE);

			if (SocialClientsManager.isClientInstalled(this, SocialNetwork.LINKEDIN))
				shareLI.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						controller.createShareLIActivity();
					}
				});
			else
				shareLI.setVisibility(View.GONE);

			if (SocialClientsManager.isClientInstalled(this, SocialNetwork.SKYPE))
				shareSk.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						controller.createShareSkActivity();
					}
				});
			else
				shareSk.setVisibility(View.GONE);

			if (SocialClientsManager.isClientInstalled(this, SocialNetwork.TWITTER))
				shareTw.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						controller.createShareTwActivity();
					}
				});
			else
				shareTw.setVisibility(View.GONE);

			if (SocialClientsManager.isClientInstalled(this, SocialNetwork.EMAIL))
				shareEm.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						controller.createShareEmActivity();
					}
				});
			else
				shareEm.setVisibility(View.GONE);
		} else {
			share.setVisibility(View.GONE);
			shareGr1.setVisibility(View.GONE);
			shareGr2.setVisibility(View.GONE);
		}
	}
}
