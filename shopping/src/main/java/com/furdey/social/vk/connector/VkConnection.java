package com.furdey.social.vk.connector;

import com.furdey.social.SocialConnection;
import com.furdey.social.vk.api.Request;
import com.furdey.social.vk.api.Response;

public class VkConnection extends SocialConnection {

	public VkConnection(VkToken accessToken) {
		super(accessToken);
	}

	public Response callVk(Request request) {
		String url = RequestSerializer.serializeRequest(request, (VkToken) getToken());
		return VkConnectorFactory.getInstance().callVk(url, request.getResponseClass());
	}
}
