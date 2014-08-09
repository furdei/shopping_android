package com.furdey.shopping.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.furdey.shopping.R;
import com.furdey.shopping.utils.PreferencesManager.LikeState;
import com.furdey.shopping.utils.PreferencesManager.ShareState;
import com.furdey.social.android.SocialClientsManager;
import com.furdey.social.android.SocialClientsManager.SocialNetwork;

public class FeedbackDialogUtil {

	public interface SocialShareListener {
		void onSocialShare(SocialNetwork socialNetwork);
	}

	public static void showFeedbackDialogsIfNeeded(final Activity activity,
			final SocialShareListener listener) {
		ShareState shareState = PreferencesManager.getShareState(activity);

		if (shareState == ShareState.NOT_SHARED) {
			if (PreferencesManager.isOkToShare(activity)) {
				// Пора делиться
				AlertDialog.Builder builder = new Builder(activity);
				LayoutInflater inflatter = (LayoutInflater) activity
						.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				View confirmView = inflatter.inflate(R.layout.social_icons, null, false);
				ImageView share;

				share = (ImageView) confirmView.findViewById(R.id.aboutAppShareVK);
				if (SocialClientsManager.isClientInstalled(activity, SocialNetwork.VK))
					share.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							onSocialShare(activity, listener, SocialNetwork.VK);
						}
					});
				else
					share.setVisibility(View.GONE);

				share = (ImageView) confirmView.findViewById(R.id.aboutAppShareFB);
				if (SocialClientsManager.isClientInstalled(activity, SocialNetwork.FACEBOOK))
					share.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							onSocialShare(activity, listener, SocialNetwork.FACEBOOK);
						}
					});
				else
					share.setVisibility(View.GONE);

				share = (ImageView) confirmView.findViewById(R.id.aboutAppShareGP);
				if (SocialClientsManager.isClientInstalled(activity, SocialNetwork.GOOGLE_PLUS))
					share.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							onSocialShare(activity, listener, SocialNetwork.GOOGLE_PLUS);

						}
					});
				else
					share.setVisibility(View.GONE);

				share = (ImageView) confirmView.findViewById(R.id.aboutAppShareLI);
				if (SocialClientsManager.isClientInstalled(activity, SocialNetwork.LINKEDIN))
					share.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							onSocialShare(activity, listener, SocialNetwork.LINKEDIN);

						}
					});
				else
					share.setVisibility(View.GONE);

				share = (ImageView) confirmView.findViewById(R.id.aboutAppShareSk);
				if (SocialClientsManager.isClientInstalled(activity, SocialNetwork.SKYPE))
					share.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							onSocialShare(activity, listener, SocialNetwork.SKYPE);
						}
					});
				else
					share.setVisibility(View.GONE);

				share = (ImageView) confirmView.findViewById(R.id.aboutAppShareTw);
				if (SocialClientsManager.isClientInstalled(activity, SocialNetwork.TWITTER))
					share.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							onSocialShare(activity, listener, SocialNetwork.TWITTER);
						}
					});
				else
					share.setVisibility(View.GONE);

				share = (ImageView) confirmView.findViewById(R.id.aboutAppShareEm);
				if (SocialClientsManager.isClientInstalled(activity, SocialNetwork.EMAIL))
					share.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							onSocialShare(activity, listener, SocialNetwork.EMAIL);
						}
					});
				else
					share.setVisibility(View.GONE);

				builder.setTitle(R.string.shareTitle).setView(confirmView)
						.setPositiveButton(activity.getString(R.string.shareLater), new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								PreferencesManager.setShareDelay(activity, System.currentTimeMillis(),
										PreferencesManager.SHARE_LATER_DELAY);
							}
						}).setNegativeButton(activity.getString(R.string.shareCancel), new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								PreferencesManager.setShareState(activity, ShareState.SHARED);
								PreferencesManager.setLikeDelay(activity, System.currentTimeMillis(),
										PreferencesManager.LIKE_DELAY);
							}
						});

				builder.create().show();

			}
		} else {
			// Уже делилсь, возможно, пора лайкать
			LikeState likeState = PreferencesManager.getLikeState(activity);

			if (likeState == LikeState.NOT_LIKED) {
				if (PreferencesManager.isOkToLike(activity)) {
					// Пора лайкать
					AlertDialog.Builder builder = new Builder(activity);
					builder.setTitle(R.string.likeTitle).setMessage(R.string.likeText)
							.setPositiveButton(activity.getString(R.string.likeOk), new OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									PreferencesManager.setLikeState(activity, LikeState.LIKED);
									Uri uri = Uri.parse(activity.getString(R.string.likeUrl));
									Intent intent = new Intent(Intent.ACTION_VIEW, uri);
									activity.startActivity(intent);
								}
							}).setNeutralButton(activity.getString(R.string.likeLater), new OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									PreferencesManager.setLikeDelay(activity, System.currentTimeMillis(),
											PreferencesManager.LIKE_LATER_DELAY);
								}
							}).setNegativeButton(activity.getString(R.string.likeCancel), new OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									PreferencesManager.setLikeState(activity, LikeState.LIKED);
								}
							});

					builder.create().show();

				}
			}
		}
	}

	private static void onSocialShare(final Activity activity, final SocialShareListener listener,
			SocialNetwork socialNetwork) {
		PreferencesManager.setLikeDelay(activity, System.currentTimeMillis(),
				PreferencesManager.LIKE_DELAY);
		listener.onSocialShare(socialNetwork);
	}
}
