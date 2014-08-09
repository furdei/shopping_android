package com.furdey.shopping.utils;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;

public class IconsCacheDispatcher {

	private static Map<String, WeakReference<Bitmap>> cache = new ConcurrentHashMap<String, WeakReference<Bitmap>>();

	public static Bitmap getIconFromCache(String iconName) {
		WeakReference<Bitmap> ref = cache.get(iconName);

		if (ref != null) {
			return ref.get();
		}

		return null;
	}

	public static void putIconToCache(String iconName, Bitmap icon) {
		cache.put(iconName, new WeakReference<Bitmap>(icon));
	}
}
