package com.furdey.engine.android.validators;

public class PrioritizedError implements Comparable<PrioritizedError> {
	private int priority;
	private String error;
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	@Override
	public int compareTo(PrioritizedError error) {
		return error.getPriority() - this.getPriority();
	}	
	
}
