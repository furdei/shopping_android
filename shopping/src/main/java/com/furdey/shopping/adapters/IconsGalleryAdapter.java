package com.furdey.shopping.adapters;

import java.io.IOException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.furdey.shopping.R;
import com.furdey.shopping.tasks.LoadIconTask;

public class IconsGalleryAdapter extends BaseAdapter {

	public static final String ICONS_FOLDER = "icons";
	public static final String LIST_ICONS_FOLDER = "icons-list";

	private Context mContext;
	private String[] iconFiles;

	public IconsGalleryAdapter(Context сontext) {
		mContext = сontext;

		try {
			iconFiles = сontext.getAssets().list(ICONS_FOLDER);
		} catch (IOException e) {
			e.printStackTrace();
			iconFiles = new String[0];
		}
	}

	@Override
	public int getCount() {
		return iconFiles.length;
	}

	@Override
	public Object getItem(int arg0) {
		if (arg0 >= iconFiles.length)
			return null;

		return iconFiles[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg0 >= iconFiles.length)
			return null;

		LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// View iconView = li.inflate(R.layout.icons_gallery_li, arg2, false);
		// ImageView icon = (ImageView)
		// iconView.findViewById(R.id.icons_gallery_item);
		ImageView icon = (ImageView) li.inflate(R.layout.icons_gallery_li, arg2, false);
		LoadIconTask task = new LoadIconTask();
		task.loadIcon(icon, iconFiles[arg0], 1, true, mContext);
		// try {
		// Options bmpOpts = new Options();
		// bmpOpts.inSampleSize = 2;
		// Bitmap bmp = BitmapFactory.decodeStream(
		// mContext.getAssets().open(ICONS_FOLDER + "/" + iconFiles[arg0]), null,
		// bmpOpts);
		// icon.setImageBitmap(bmp);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		return icon;
	}

}
