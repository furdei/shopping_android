package com.furdey.social.vk.api;

public class UsersGetResponse extends Response {

	private PersonDetails[] response;

	public PersonDetails[] getResponse() {
		return response;
	}

	public void setResponse(PersonDetails[] response) {
		this.response = response;
	}

}
