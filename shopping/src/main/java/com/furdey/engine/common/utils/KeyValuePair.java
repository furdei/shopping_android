package com.furdey.engine.common.utils;

public class KeyValuePair<K, V> {
	private K key;
	private V value;
	
	public KeyValuePair(K key, V value) {
		setKey(key);
		setValue(value);
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
}
