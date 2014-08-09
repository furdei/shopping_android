package com.furdey.shopping.tasks;

import android.content.Context;
import android.widget.Toast;

public abstract class ToastThrowableAsyncTask<Params, Result> extends
		ThrowableAsyncTask<Params, Result> {

	private Context context;

	public ToastThrowableAsyncTask(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected void onSuccess(Result result) {
	}

	@Override
	protected void onFail(Throwable e) {
		Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
	}

}
