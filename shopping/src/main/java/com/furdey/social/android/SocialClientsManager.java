package com.furdey.social.android;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public final class SocialClientsManager {

	public enum SocialNetwork {
		TWITTER, FACEBOOK, VK, GOOGLE_PLUS, LINKEDIN, SKYPE, EMAIL
	};

	private static Map<SocialNetwork, ComponentName> installedClients;

	// com.twitter.android.composer.TextFirstComposerActivity
	private static final String PACKAGE_PATTERN_TWITTER = "^com\\.twitter\\..*";

	// com.facebook.katana.activity.composer.ImplicitShareIntentHandler
	private static final String PACKAGE_PATTERN_FACEBOOK = "^com\\.facebook\\..*";

	// com.vkontakte.android.SendActivity
	private static final String PACKAGE_PATTERN_VK = "^com\\.vkontakte\\..*";

	// com.google.android.apps.plus.phone.SignOnActivity
	private static final String PACKAGE_PATTERN_GOOGLE_PLUS = "^com\\.google\\.android\\.apps\\.plus\\..*";

	// com.linkedin.android.home.v2.ShareSwitchActivity
	private static final String PACKAGE_PATTERN_LINKEDIN = "^com\\.linkedin\\..*";

	// com.skype.android.app.main.SplashActivity
	private static final String PACKAGE_PATTERN_SKYPE = "^com\\.skype\\..*";

	public static final Set<SocialNetwork> getInstalledClients(Context context) {
		if (installedClients == null) {
			synchronized (SocialClientsManager.class) {
				if (installedClients == null) {
					installedClients = new HashMap<SocialNetwork, ComponentName>();

					Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					PackageManager pm = context.getPackageManager();
					List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);

					for (final ResolveInfo app : activityList) {
						ComponentName component = new ComponentName(app.activityInfo.packageName,
								app.activityInfo.name);

						if (app.activityInfo.name.matches(PACKAGE_PATTERN_TWITTER)) {
							installedClients.put(SocialNetwork.TWITTER, component);
						} else if (app.activityInfo.name.matches(PACKAGE_PATTERN_FACEBOOK)) {
							installedClients.put(SocialNetwork.FACEBOOK, component);
						} else if (app.activityInfo.name.matches(PACKAGE_PATTERN_VK)) {
							installedClients.put(SocialNetwork.VK, component);
						} else if (app.activityInfo.name.matches(PACKAGE_PATTERN_GOOGLE_PLUS)) {
							installedClients.put(SocialNetwork.GOOGLE_PLUS, component);
						} else if (app.activityInfo.name.matches(PACKAGE_PATTERN_LINKEDIN)) {
							installedClients.put(SocialNetwork.LINKEDIN, component);
						} else if (app.activityInfo.name.matches(PACKAGE_PATTERN_SKYPE)) {
							installedClients.put(SocialNetwork.SKYPE, component);
						}
					}

					shareIntent = new Intent(android.content.Intent.ACTION_SEND);
					shareIntent.setType("message/rfc822");
					activityList = pm.queryIntentActivities(shareIntent, 0);

					if (activityList != null && activityList.size() > 0) {
						ResolveInfo app = activityList.get(0);
						ComponentName component = new ComponentName(app.activityInfo.packageName,
								app.activityInfo.name);
						installedClients.put(SocialNetwork.EMAIL, component);
					}
				}
			}
		}

		return installedClients.keySet();
	}

	public static final boolean isClientInstalled(Context context, SocialNetwork socialClient) {
		if (installedClients == null)
			getInstalledClients(context);

		return installedClients.containsKey(socialClient);
	}

	protected static final ComponentName getComponentName(Context context, SocialNetwork socialClient) {
		if (!isClientInstalled(context, socialClient))
			return null;

		return installedClients.get(socialClient);
	}
}
