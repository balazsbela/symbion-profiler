package org.balazsbela.symbion.models;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "functioncalls")
public class FunctionCallListWrapper implements Serializable {	
	
	private List<FunctionCall>  calledMethods = new Vector<FunctionCall>();

	public List<FunctionCall> getCalledMethods() {
		return calledMethods;
	}

	public void setCalledMethods(List<FunctionCall> calledMethods) {
		this.calledMethods = calledMethods;
	}	

}
