package org.balazsbela.symbion.models;

import java.io.Serializable;

public class FunctionCall implements Serializable {
	private ThreadModel parentThread;
	private Function parent;
	private Function target;
	private long lineNumber;
	
	public FunctionCall() {
		
	}
	
	public Function getParent() {
		return parent;
	}
	public void setParent(Function parent) {
		this.parent = parent;
	}
	public Function getTarget() {
		return target;
	}
	public void setTarget(Function target) {
		this.target = target;
	}
	public long getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	public ThreadModel getParentThread() {
		return parentThread;
	}

	public void setParentThread(ThreadModel parentThread) {
		this.parentThread = parentThread;
	}
}
