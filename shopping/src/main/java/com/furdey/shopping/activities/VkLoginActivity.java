package com.furdey.shopping.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.furdey.shopping.utils.LogicException;
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
