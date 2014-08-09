package com.furdey.shopping.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.furdey.engine.android.utils.LogicException;
import com.furdey.shopping.R;
import com.furdey.social.SocialConnectionsPool;
import com.furdey.social.vk.connector.VkConnection;
import com.furdey.social.vk.connector.VkToken;

public class VkLoginActivity extends BaseActivity {

	private static final String TAG = VkLoginActivity.class.getSimpleName();
	private static final String AUTHORISE_URL = "http://oauth.vk.com/authorize?client_id=%s&scope=%s&redirect_uri=%s&display=%s&response_type=%s";
	private static final String CLIENT_ID = "3568366";
	private static final String SCOPE = "friends,messages,offline";
	private static final String REDIRECT_URI = "http://oauth.vk.com/blank.html";
	private static final String DISPLAY = "touch";
	private static final String RESPONSE_TYPE = "token";

	private WebView webView;

	// private SocialController controller;

	private static final int REQUEST_FRIENDS = 1;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) throws RuntimeException {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vk_login);
		webView = (WebView) findViewById(R.id.vkLoginWeb);

		// webView.setWebChromeClient(new WebChromeClient());
		// webView.getSettings().setPluginState(PluginState.ON);
		// webView.getSettings().setJavaScriptEnabled(true);
		// webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d(TAG, url);

				if (url.startsWith(REDIRECT_URI)) {
					String[] authResult = url.split("#");
					if (authResult.length != 2)
						throw new LogicException(view.getContext(), R.string.vkLoginLoginFailed);

					VkToken token = new VkToken(authResult[1]);
					VkConnection api = new VkConnection(token);
					SocialConnectionsPool.getInstance().put(VkConnection.class, api);

					view.setVisibility(View.GONE);

					Intent intent = new Intent(view.getContext(), FriendsListActivity.class);
					intent.putExtra(FriendsListActivity.MESSAGE_PARAM,
							getIntent().getStringExtra(FriendsListActivity.MESSAGE_PARAM));
					startActivityForResult(intent, REQUEST_FRIENDS);

					// controller = new SocialController(VkLoginActivity.this);
					// controller.getFriendsList(VkConnection.class, new
					// OnFriendsLoadedListener() {
					// @Override
					// public void onFriendsLoaded(List<Person> friends) {
					// String[] uids = new String[friends.size()];
					// for (int i = 0; i < friends.size(); i++) {
					// uids[i] = friends.get(i).getId();
					// }
					// controller.editSocialMessage(uids,
					// SocialController.SOCIAL_NETWORK_VK);
					// finish();
					// }
					// });
				} else
					view.loadUrl(url);

				return false; // then it is not handled by default action
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.i(TAG, "Finished loading URL: " + url);
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Log.e(TAG, "Error: " + description + " code: " + errorCode + " failingUrl: " + failingUrl);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});

		String url = String.format(AUTHORISE_URL, CLIENT_ID, SCOPE, REDIRECT_URI, DISPLAY,
				RESPONSE_TYPE);
		/*
		 * ConnectionFactoryRegistry connectionFactoryRegistry = new
		 * ConnectionFactoryRegistry();
		 * 
		 * FacebookConnectionFactory facebookConnectionFactory; String appId =
		 * "285472548253698"; String appSecret = "0acb8e6326b7d272e7bb244218162245";
		 * facebookConnectionFactory = new FacebookConnectionFactory(appId,
		 * appSecret);
		 * connectionFactoryRegistry.addConnectionFactory(facebookConnectionFactory
		 * );
		 * 
		 * FacebookConnectionFactory connectionFactory; connectionFactory =
		 * (FacebookConnectionFactory) connectionFactoryRegistry
		 * .getConnectionFactory(Facebook.class); String redirectUri =
		 * "https://www.facebook.com/connect/login_success.html"; String scope =
		 * "publish_stream,offline_access,read_stream,user_about_me";
		 * 
		 * OAuth2Parameters parameters = new OAuth2Parameters();
		 * parameters.setRedirectUri(redirectUri); parameters.setScope(scope);
		 * parameters.add("display", "touch");
		 * 
		 * OAuth2Operations oauth = connectionFactory.getOAuthOperations(); String
		 * url = oauth.buildAuthorizeUrl(GrantType.IMPLICIT_GRANT, parameters);
		 */
		webView.loadUrl(url);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_FRIENDS && resultCode == RESULT_OK) {
			setResult(RESULT_OK, data);
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
