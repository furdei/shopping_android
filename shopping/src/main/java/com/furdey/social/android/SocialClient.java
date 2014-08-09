package com.furdey.social.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.furdey.shopping.R;
import com.furdey.social.SocialConnectionsPool;
import com.furdey.social.android.SocialClientsManager.SocialNetwork;
import com.furdey.social.vk.api.MessagesSendRequest;
import com.furdey.social.vk.api.MessagesSendResponse;
import com.furdey.social.vk.connector.VkConnection;

public final class SocialClient {

	public static final boolean sendMessage(Context context, SocialNetwork socialNetwork,
			String subject, String message) {
		ComponentName componentName = SocialClientsManager.getComponentName(context, socialNetwork);

		if (componentName == null)
			return false;

		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);

		switch (socialNetwork) {
		case EMAIL:
			shareIntent.setType("message/rfc822");
			break;
		default:
			shareIntent.setType("text/plain");
			shareIntent.setComponent(componentName);
		}

		if (subject != null)
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);

		if (message != null) {
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		}

		// shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		try {
			context.startActivity(shareIntent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void sendVkMessage(final Context context, final String personId,
			final String message) {
		if (message != null && personId != null) {
			new AsyncTask<Void, Void, MessagesSendResponse>() {
				@Override
				protected MessagesSendResponse doInBackground(Void... params) {
					VkConnection vkConn = (VkConnection) SocialConnectionsPool.getInstance().get(
							VkConnection.class);

					MessagesSendRequest vkMsg = new MessagesSendRequest();
					vkMsg.setTitle(context.getString(R.string.appName));
					vkMsg.setMessage(message);
					vkMsg.setUid(Long.parseLong(personId));
					vkMsg.setChat_id(Long.parseLong(personId));
					MessagesSendResponse vkResp = (MessagesSendResponse) vkConn.callVk(vkMsg);
					return vkResp;
				}

				@Override
				protected void onPostExecute(MessagesSendResponse result) {
					if (result != null) {
						Toast.makeText(context, context.getString(R.string.socialMessageSent),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(context, context.getString(R.string.purchasesLiFailedToSend),
								Toast.LENGTH_LONG).show();
					}
				}
			}.execute();
		} else {
			Toast.makeText(context, context.getString(R.string.purchasesLiFailedToSend),
					Toast.LENGTH_LONG).show();
		}
	}

}
