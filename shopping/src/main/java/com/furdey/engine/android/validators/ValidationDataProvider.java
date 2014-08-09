package com.furdey.engine.android.validators;

public interface ValidationDataProvider<T> {
	public T getData();
	public void setData(T data);
}
