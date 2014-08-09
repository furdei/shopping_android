package com.furdey.social.vk.connector;

public class VkConnectorFactory {

	private static volatile VkConnector instance;

	public static VkConnector getInstance() {
		if (instance == null)
			synchronized (VkConnector.class) {
				if (instance == null)
					instance = new JsonHttpConnector();
			}

		return instance;
	}

}
