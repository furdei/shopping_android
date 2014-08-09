package com.furdey.social.vk.connector;

import com.furdey.social.SocialToken;

/**
 * VK access token
 * 
 * @author Stepan Furdey
 */
public class VkToken implements SocialToken {

	private static final String ACCESS_TOKEN = "access_token";
	private static final String EXPIRES_IN = "expires_in";
	private static final String USER_ID = "user_id";

	private String accessToken;
	private String expiresIn;
	private Long userId;

	public VkToken(String parse) {
		String[] authParams = parse.split("&");

		for (int i = 0; i < authParams.length; i++) {
			String[] param = authParams[i].split("=");

			if (param.length != 2)
				throw new RuntimeException("Wrong token parameter format: " + authParams[i]);

			if (ACCESS_TOKEN.compareTo(param[0]) == 0)
				accessToken = param[1];
			else if (EXPIRES_IN.compareTo(param[0]) == 0)
				expiresIn = param[1];
			else if (USER_ID.compareTo(param[0]) == 0)
				userId = Long.parseLong(param[1]);
			else
				throw new RuntimeException("Unknown token parameter: " + param[0]);
		}

		if (accessToken == null)
			throw new RuntimeException("Token parameter " + ACCESS_TOKEN + " wasn't found");

		if (expiresIn == null)
			throw new RuntimeException("Token parameter " + EXPIRES_IN + " wasn't found");

		if (userId == null)
			throw new RuntimeException("Token parameter " + USER_ID + " wasn't found");
	}

	@Override
	public String getAccessToken() {
		return accessToken;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public Long getUserId() {
		return userId;
	}

}
