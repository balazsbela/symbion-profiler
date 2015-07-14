package org.balazsbela.symbion.visualizer.models;

public class SourceFileMethod {
	public String containingClass;
	public String methodName;
	public long startLine;
	public long endLine;
	
	public SourceFileMethod() {
		
	}
	
	public String getContainingClass() {
		return containingClass;
	}
	public void setContainingClass(String containingClass) {
		this.containingClass = containingClass;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public long getStartLine() {
		return startLine;
	}
	public void setStartLine(long startLine) {
		this.startLine = startLine;
	}
	public long getEndLine() {
		return endLine;
	}
	public void setEndLine(long endLine) {
		this.endLine = endLine;
	}
}
