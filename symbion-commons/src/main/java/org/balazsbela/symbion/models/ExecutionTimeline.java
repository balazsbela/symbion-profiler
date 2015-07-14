package org.balazsbela.symbion.models;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExecutionTimeline implements Serializable {

	private static final long serialVersionUID = -6723810247973303887L;
	private volatile FunctionCallListWrapper calledMethods = new FunctionCallListWrapper();
	private volatile Set<String> matchedClasses = Collections.synchronizedSet(new HashSet<String>());
	private volatile Set<ThreadModel> threads = Collections.synchronizedSet(new HashSet<ThreadModel>());

	public ExecutionTimeline() {
	}


	public synchronized FunctionCallListWrapper getCalledMethodsWrapper() {
		return calledMethods;
	}

	public synchronized List<FunctionCall> getCalledMethods() {
		return calledMethods.getCalledMethods();
	}

	public synchronized Set<ThreadModel> getThreads() {
		return threads;
	}

	public synchronized Set<String> getMatchedclasses() {
		return matchedClasses;
	}

	public synchronized FunctionCallListWrapper getCalledmethods() {
		return calledMethods;
	}
}
