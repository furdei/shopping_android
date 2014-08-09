package com.furdey.shopping.tasks;

import android.os.AsyncTask;

public abstract class ThrowableAsyncTask<Params, Result> extends AsyncTask<Params, Void, Result> {

	Throwable e = null;

	@Override
	protected final Result doInBackground(Params... params) {
		try {
			return doBackgroundWork(params != null && params.length > 0 ? params[0] : null);
		} catch (Throwable e) {
			e.printStackTrace();
			this.e = e;
			return null;
		}
	}

	protected abstract Result doBackgroundWork(Params param) throws Throwable;

	protected abstract void onSuccess(Result result);

	protected abstract void onFail(Throwable e);

	@Override
	protected void onPostExecute(Result result) {
		if (e == null)
			onSuccess(result);
		else
			onFail(e);
	}

}
