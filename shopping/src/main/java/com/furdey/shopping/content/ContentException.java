package com.furdey.shopping.content;

import android.content.Context;

public class ContentException extends Exception {

	private static final long serialVersionUID = -2483725905734822802L;

	public ContentException(Context context, int errorResId) {
		super(context.getString(errorResId));
	}

	public ContentException(Context context, int errorResId, Throwable e) {
		super(context.getString(errorResId), e);
	}
}
