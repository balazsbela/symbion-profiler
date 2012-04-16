package org.balazsbela.symbion.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExecutionTimeline implements Serializable {

	private static final long serialVersionUID = -6723810247973303887L;
	public static List<String>  calledMethods = new ArrayList<String>();

	public ExecutionTimeline() {
	}
	
	public List<String> getCalledMethods() {
		return calledMethods;
	}

	public void setCalledMethods(List<String> calledMethods) {
		this.calledMethods = calledMethods;
	}
}
