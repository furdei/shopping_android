package com.furdey.shopping.tasks;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.furdey.shopping.adapters.IconsGalleryAdapter;
import com.furdey.shopping.utils.IconsCacheDispatcher;

public class LoadIconTask extends AsyncTask<String, Void, Bitmap> {

	private Context context;
	private int sampleSize;
	private ImageView imageView;
	private boolean loadForList;

	@SuppressLint("NewApi")
	public void loadIcon(ImageView imageView, String iconName, int sampleSize, boolean loadForList,
			Context context) {

		Bitmap icon = IconsCacheDispatcher.getIconFromCache(iconName);

		if (icon != null) {
			imageView.setImageBitmap(icon);
		} else {
			this.context = context;
			this.sampleSize = sampleSize;
			this.imageView = imageView;
			this.loadForList = loadForList;

			if (android.os.Build.VERSION.SDK_INT >= 11)
				executeOnExecutor(THREAD_POOL_EXECUTOR, iconName);
			else
				execute(iconName);
		}
	}

	@Override
	protected Bitmap doInBackground(String... arg0) {
		Options bmpOpts = new Options();
		bmpOpts.inSampleSize = sampleSize;
		try {
			Bitmap icon = BitmapFactory.decodeStream(
					context.getAssets().open(
							((loadForList) ? IconsGalleryAdapter.LIST_ICONS_FOLDER
									: IconsGalleryAdapter.ICONS_FOLDER) + "/" + arg0[0]), null, bmpOpts);
			IconsCacheDispatcher.putIconToCache(arg0[0], icon);
			return icon;
		} catch (IOException e) {
			// don't use an icon if loading breaks
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (imageView != null)
			try {
				imageView.setImageBitmap(result);
			} catch (Exception e) {
			}
	}

}
