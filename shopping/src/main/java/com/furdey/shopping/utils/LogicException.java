package com.furdey.shopping.utils;

import android.content.Context;

public class LogicException extends RuntimeException {
	
	private static final long serialVersionUID = -5234200517242459673L;

	public LogicException(Context context, int messageId) {
		super(context.getString(messageId));
	}

	public LogicException(Context context, int messageId, Throwable e) {
		super(context.getString(messageId), e);
	}
	
}
