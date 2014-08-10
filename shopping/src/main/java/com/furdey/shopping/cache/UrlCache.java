package com.furdey.shopping.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Caches an URL resource and/or gets it from the cache
 * 
 * @author Stepan Furdey
 */
public abstract class UrlCache extends AsyncTask<String, Void, InputStream> {

	private static final int IO_BUFFER_SIZE = 4096;
	private Context context;

	public UrlCache(Context context) {
		this.context = context;
	}

	@Override
	protected InputStream doInBackground(String... urls) {
		String url = urls[0];
		File cachedFile = getCachedFile(url);

		if (!cachedFile.exists())
			// There is no cached file, load it and save
			try {
				InputStream in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);
				OutputStream out = new FileOutputStream(cachedFile);
				byte[] buffer = new byte[IO_BUFFER_SIZE];
				int read;

				while ((read = in.read(buffer)) > 0) {
					out.write(buffer, 0, read);
				}

				out.flush();
				in.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Error downloading file " + url, e);
			}

		try {
			return new FileInputStream(cachedFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading cache " + cachedFile.getAbsolutePath(), e);
		}
	}

	@Override
	protected abstract void onPostExecute(InputStream result);

	/**
	 * Transforms URL to a valid file name for cache
	 */
	private String urlToCacheName(String url) {
		final char[] reservedChars = { '|', '\\', '?', '*', '<', '\"', ':', '>', '+', '/', ',', ';',
				'.' };
		String res = url;

		for (int i = 0; i < reservedChars.length; i++)
			res = res.replace(reservedChars[i], '_');

		return res;
	}

	/**
	 * Returns a File object from cache that corresponds to the given URL
	 */
	protected File getCachedFile(String url) {
		String basePath;

		if (context.getExternalCacheDir() != null)
			basePath = context.getExternalCacheDir().getAbsolutePath();
		else
			basePath = context.getCacheDir().getAbsolutePath();

		if (!basePath.endsWith("/"))
			basePath = basePath.concat("/");

		return new File(basePath.concat(urlToCacheName(url)));
	}
}
