package com.furdey.social.vk.api;

public abstract class Request {

	public abstract String getRestUrl();

	public abstract Class<? extends Response> getResponseClass();

}
