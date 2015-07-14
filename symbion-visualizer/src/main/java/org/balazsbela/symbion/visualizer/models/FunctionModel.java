package org.balazsbela.symbion.visualizer.models;

import java.util.HashSet;
import java.util.Set;

import org.balazsbela.symbion.models.Function;

public class FunctionModel {
	private Set<FunctionModel> targets = new HashSet<FunctionModel>();
	private Function function;
	private Set<FunctionModel> parents = new HashSet<FunctionModel>();
	private String fullMethodName;
	
	public FunctionModel() {
		
	}
	
	public String getClassName() {
		int index = fullMethodName.lastIndexOf(".");
		if(index>-1) {
			String s= fullMethodName.substring(0,index);;			
			return s;
		}
		else {
			return "Start node";
		}
	}
	
	public FunctionModel(String fullMethodName) {
		this.fullMethodName = fullMethodName;
	}	
	
	public Set<FunctionModel> getTargets() {
		return targets;
	}
	public void setTargets(Set<FunctionModel> targets) {
		this.targets = targets;
	}
	public Function getFunction() {
		return function;
	}
	public void setFunction(Function func) {
		this.function = func;
	}
	public String getFullMethodName() {
		return fullMethodName;
	}
	public void setFullMethodName(String fullMethodName) {
		this.fullMethodName = fullMethodName;
	}

	public  Set<FunctionModel> getParents() {
		return parents;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fullMethodName == null) ? 0 : fullMethodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionModel other = (FunctionModel) obj;
		if (fullMethodName == null) {
			if (other.fullMethodName != null)
				return false;
		} else if (!fullMethodName.equals(other.fullMethodName))
			return false;
		return true;
	}

}
