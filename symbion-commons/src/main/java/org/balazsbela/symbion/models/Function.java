package org.balazsbela.symbion.models;

import java.io.Serializable;

public class Function implements Serializable {
	private String containingClassName;
	private String methodName;
	private String methodSignature="";
	private long lineNumber;
	
	public Function() {
		
	}
	
	public String getContainingClassName() {		
		return containingClassName;
	}
	public void setContainingClassName(String containingClass) {
		this.containingClassName = containingClass;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getMethodSignature() {
		return methodSignature;
	}
	public void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
	}
	public long getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((containingClassName == null) ? 0 : containingClassName.hashCode());
		result = prime * result + (int) (lineNumber ^ (lineNumber >>> 32));
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((methodSignature == null) ? 0 : methodSignature.hashCode());
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
		Function other = (Function) obj;
		if (containingClassName == null) {
			if (other.containingClassName != null)
				return false;
		} else if (!containingClassName.equals(other.containingClassName))
			return false;	
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (methodSignature == null) {
			if (other.methodSignature != null)
				return false;
		} else if (!methodSignature.equals(other.methodSignature))
			return false;
		return true;
	}
	
	
}
