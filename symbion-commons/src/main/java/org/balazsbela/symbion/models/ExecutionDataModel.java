package org.balazsbela.symbion.models;

import java.io.Serializable;
import java.util.List;

import org.balazsbela.symbion.models.FunctionCall;

public class ExecutionDataModel implements Serializable {
	public List<FunctionCall> functionCalls;
	public String sourcePath;
	
	public ExecutionDataModel() {
		
	}
	
	public List<FunctionCall> getFunctionCalls() {
		return functionCalls;
	}
	public void setFunctionCalls(List<FunctionCall> functionCalls) {
		this.functionCalls = functionCalls;
	}
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	
}
