package com.furdey.shopping.controllers;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.FacebookDialog;
import com.furdey.shopping.R;
import com.furdey.shopping.activities.BaseActivity;
import com.furdey.shopping.activities.SocialMessageActivity;
import com.furdey.shopping.listeners.OnFriendsLoadedListener;
import com.furdey.social.SocialConnection;
import com.furdey.social.SocialConnectionsPool;
import com.furdey.social.android.SocialClient;
import com.furdey.social.android.SocialClientsManager;
import com.furdey.social.model.Person;
import com.furdey.social.vk.api.GetFriendsRequest;
import com.furdey.social.vk.api.GetFriendsResponse;
import com.furdey.social.vk.api.MessagesSendRequest;
import com.furdey.social.vk.api.MessagesSendResponse;
import com.furdey.social.vk.connector.VkConnection;

import java.util.ArrayList;
import java.util.List;

public class SocialController {

	private static final int MESSAGE_SEND_SLEEP_TIME_MILLIS = 400;

	public static final String PARAM_SOCIAL_NETWORK = SocialController.class.getCanonicalName()
			.concat(".socialNetwork");
	public static final String SOCIAL_NETWORK_VK = "VK";
	public static final String PARAM_UIDS = SocialController.class.getCanonicalName().concat(".uids");
    private Activity activity;

    public SocialController(Activity activity) {
		this.activity = activity;
	}

	@SuppressWarnings("unchecked")
	public void getFriendsList(Class<? extends SocialConnection> src,
			final OnFriendsLoadedListener listener) {
		new AsyncTask<Class<? extends SocialConnection>, Void, List<Person>>() {
			@Override
			protected List<Person> doInBackground(Class<? extends SocialConnection>... params) {
				if (VkConnection.class.equals(params[0]))
					return getVkFriends();

				return null;
			}

			@Override
			protected void onPostExecute(List<Person> result) {
				listener.onFriendsLoaded(result);
			}
		}.execute(src);
	}

	private List<Person> getVkFriends() {
		VkConnection conn = (VkConnection) SocialConnectionsPool.getInstance().get(VkConnection.class);

		GetFriendsRequest request = new GetFriendsRequest();
		request.setFields(new String[] { GetFriendsRequest.FIELD_UID,
				GetFriendsRequest.FIELD_FIRST_NAME, GetFriendsRequest.FIELD_LAST_NAME,
				GetFriendsRequest.FIELD_PHOTO });
		GetFriendsResponse response = (GetFriendsResponse) conn.callVk(request);

		return response.toSocial();
	}

	public String constructMessage(String srcMessage) {
		String src = srcMessage.trim();

		if (!src.endsWith(".") && !src.endsWith("!") && !src.endsWith("?") && !src.endsWith(",")
				&& src.length() > 0)
			src = src.concat(".");

		String socialMessageAddition = getActivity().getString(R.string.socialMessageAddition);
		return (src.concat(" ").concat(socialMessageAddition)).trim();
	}

	public OnClickListener getSendButtonOnClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setEnabled(false);

				final String networkClassName = getActivity().getIntent().getExtras()
						.getString(PARAM_SOCIAL_NETWORK);

				if (networkClassName == null)
					throw new IllegalArgumentException("Parameter ".concat(PARAM_SOCIAL_NETWORK).concat(
							" is required"));

				final String[] uids = getActivity().getIntent().getExtras().getStringArray(PARAM_UIDS);

				if (uids == null)
					throw new IllegalArgumentException("Parameter ".concat(PARAM_UIDS).concat(" is required"));

				new AsyncTask<Void, Void, Void>() {
					Exception error = null;

					@Override
					protected Void doInBackground(Void... params) {
						try {
							if (SOCIAL_NETWORK_VK.compareTo(networkClassName) == 0) {
								VkConnection vkConn = (VkConnection) SocialConnectionsPool.getInstance().get(
										VkConnection.class);

								for (int i = 0; i < uids.length; i++) {
									MessagesSendRequest vkMsg = new MessagesSendRequest();
									vkMsg.setTitle(getActivity().getString(R.string.socialMessageMsgTitle));
									vkMsg.setMessage(((SocialMessageActivity) getActivity()).getMessage());
									vkMsg.setUid(Long.parseLong(uids[i]));
									vkMsg.setChat_id(Long.parseLong(uids[i]));
									MessagesSendResponse vkResp = (MessagesSendResponse) vkConn.callVk(vkMsg);

									if (vkResp.getResponse() == null)
										throw new IllegalStateException("VK response is null");

									Thread.sleep(MESSAGE_SEND_SLEEP_TIME_MILLIS);
								}
							} else
								throw new IllegalArgumentException("Social network ".concat(networkClassName)
										.concat(" is unknown"));
						} catch (Exception e) {
							error = e;
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						if (error != null)
							throw new IllegalStateException(getActivity().getString(R.string.errorUnknown), error);

						Toast.makeText(getActivity().getApplicationContext(), R.string.socialMessageSent,
								Toast.LENGTH_LONG).show();
						getActivity().setResult(Activity.RESULT_OK);
						getActivity().finish();
					}
				}.execute();

			}
		};
	}

    public void createShareActivity(SocialClientsManager.SocialNetwork socialNetwork) {
        switch (socialNetwork) {
            case EMAIL:
                createShareEmActivity();
                break;

            case FACEBOOK:
                createShareFbActivity();
                break;

            case GOOGLE_PLUS:
                createShareGPActivity();
                break;

            case LINKEDIN:
                createShareLIActivity();
                break;

            case SKYPE:
                createShareSkActivity();
                break;

            case TWITTER:
                createShareTwActivity();
                break;

            case VK:
                createShareVkActivity();
                break;
        }
    }

    public void createShareVkActivity() {
        SocialClient.sendMessage(
                getActivity(),
                SocialClientsManager.SocialNetwork.VK,
                null,
                getActivity().getString(R.string.socialMessageBase).concat(" ")
                        .concat(getActivity().getString(R.string.socialMessageAddition))
        );
    }

    public void createShareFbActivity() {
        // start Facebook Login
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            sendFbShareMessage(session);
        } else {
            List<String> permissions = new ArrayList<String>();
            permissions.add("public_profile");
            permissions.add("user_friends");
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
            FacebookDialog.ShareDialogBuilder builder = new FacebookDialog.ShareDialogBuilder(getActivity());
            builder = builder.setApplicationName(getActivity().getString(R.string.appName));

            if (message != null)
                builder = builder.setDescription(getActivity().getString(R.string.socialMessageBase));

            if (link != null)
                builder = builder.setLink(getActivity().getString(R.string.socialMessageAddition));

            if (name != null)
                builder = builder.setName(getActivity().getString(R.string.socialMessageMsgTitle));

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
                SocialClientsManager.SocialNetwork.TWITTER,
                null,
                getActivity().getString(R.string.socialMessageBase).concat(" ")
                        .concat(getActivity().getString(R.string.socialMessageAddition)));
    }

    public void createShareGPActivity() {
        SocialClient.sendMessage(
                getActivity(),
                SocialClientsManager.SocialNetwork.GOOGLE_PLUS,
                null,
                getActivity().getString(R.string.socialMessageBase).concat(" ")
                        .concat(getActivity().getString(R.string.socialMessageAddition)));
    }

    public void createShareLIActivity() {
        SocialClient.sendMessage(
                getActivity(),
                SocialClientsManager.SocialNetwork.LINKEDIN,
                null,
                getActivity().getString(R.string.socialMessageBase).concat(" ")
                        .concat(getActivity().getString(R.string.socialMessageAddition)));
    }

    public void createShareSkActivity() {
        SocialClient.sendMessage(
                getActivity(),
                SocialClientsManager.SocialNetwork.SKYPE,
                null,
                getActivity().getString(R.string.socialMessageBase).concat(" ")
                        .concat(getActivity().getString(R.string.socialMessageAddition)));
    }

    public void createShareEmActivity() {
        SocialClient.sendMessage(
                getActivity(),
                SocialClientsManager.SocialNetwork.EMAIL,
                getActivity().getString(R.string.socialMessageMsgTitle),
                getActivity().getString(R.string.socialMessageBase).concat(" ")
                        .concat(getActivity().getString(R.string.socialMessageAddition)));
    }

    private Activity getActivity() {
        return activity;
    }
}
