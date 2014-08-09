package com.furdey.social;

public class SocialConnection {

	private SocialToken token;

	public SocialConnection(SocialToken token) {
		setToken(token);
	}

	public SocialToken getToken() {
		return token;
	}

	private void setToken(SocialToken token) {
		this.token = token;
	}

}
