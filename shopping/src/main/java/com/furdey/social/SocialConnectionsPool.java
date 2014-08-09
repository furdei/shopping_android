package com.furdey.social;

import java.util.HashMap;
import java.util.Map;

public class SocialConnectionsPool {

	private static volatile SocialConnectionsPool instance;

	private Map<Class<? extends SocialConnection>, SocialConnection> pool;

	private SocialConnectionsPool() {
		pool = new HashMap<Class<? extends SocialConnection>, SocialConnection>();
	}

	public static SocialConnectionsPool getInstance() {
		if (instance == null) {
			synchronized (SocialConnectionsPool.class) {
				if (instance == null) {
					instance = new SocialConnectionsPool();
				}
			}
		}

		return instance;
	}

	public void put(Class<? extends SocialConnection> key, SocialConnection connection) {
		pool.put(key, connection);
	}

	public SocialConnection get(Class<? extends SocialConnection> key) {
		return pool.get(key);
	}
}
