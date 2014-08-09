package com.furdey.shopping.controllers;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.furdey.engine.android.activities.DataLinkActivity;
import com.furdey.engine.android.controllers.BaseController;
import com.furdey.engine.android.utils.LogicException;
import com.furdey.shopping.R;
import com.furdey.shopping.activities.SocialMessageActivity;
import com.furdey.shopping.dao.db.DatabaseHelper;
import com.furdey.shopping.listeners.OnFriendsLoadedListener;
import com.furdey.social.SocialConnection;
import com.furdey.social.SocialConnectionsPool;
import com.furdey.social.model.Person;
import com.furdey.social.vk.api.GetFriendsRequest;
import com.furdey.social.vk.api.GetFriendsResponse;
import com.furdey.social.vk.api.MessagesSendRequest;
import com.furdey.social.vk.api.MessagesSendResponse;
import com.furdey.social.vk.connector.VkConnection;

public class SocialController extends BaseController<Void, DatabaseHelper> {

	private static final int MESSAGE_SEND_SLEEP_TIME_MILLIS = 400;

	public static final int REQUEST_MESSAGE = 0;

	public static final String PARAM_SOCIAL_NETWORK = SocialController.class.getCanonicalName()
			.concat(".socialNetwork");
	public static final String SOCIAL_NETWORK_VK = "VK";
	public static final String PARAM_UIDS = SocialController.class.getCanonicalName().concat(".uids");

	public SocialController(DataLinkActivity<DatabaseHelper> activity) {
		super(activity);
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

	public void editSocialMessage(String[] uids, String socialNetwork) {
		Intent intent = new Intent(getActivity(), SocialMessageActivity.class);
		intent.putExtra(PARAM_UIDS, uids);
		intent.putExtra(PARAM_SOCIAL_NETWORK, socialNetwork);
		getActivity().startActivityForResult(intent, REQUEST_MESSAGE);
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
							throw new LogicException(getActivity(), R.string.errorUnknown, error);

						Toast.makeText(getActivity().getApplicationContext(), R.string.socialMessageSent,
								Toast.LENGTH_LONG).show();
						getActivity().setResult(Activity.RESULT_OK);
						getActivity().finish();
					}
				}.execute();

			}
		};
	}
}
