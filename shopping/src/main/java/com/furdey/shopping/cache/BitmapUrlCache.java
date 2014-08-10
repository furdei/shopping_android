package com.furdey.shopping.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class BitmapUrlCache extends UrlCache {

	ImageView resultView;

	public BitmapUrlCache(Context context) {
		super(context);
	}

	@Override
	protected void onPostExecute(InputStream result) {
		Bitmap image = BitmapFactory.decodeStream(result);
		resultView.setImageBitmap(image);
		try {
			result.close();
		} catch (IOException e) {
			// Yep, just swallow it
			e.printStackTrace();
		}
	}

	/**
	 * Loads a bitmap from cache into view. If there is no bitmap in the cache, it
	 * loads it into the cache asynchronously.
	 * 
	 * @param url
	 *          - URL of the original bitmap
	 * @param resultView
	 *          - View where to place the bitmap
	 */
	public void loadBitmap(String url, ImageView resultView) {
		this.resultView = resultView;

		File cachedFile = getCachedFile(url);
		if (cachedFile.exists()) {
			try {
				onPostExecute(new FileInputStream(cachedFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				execute(url);
			}
		} else {
			execute(url);
		}
	}

}
