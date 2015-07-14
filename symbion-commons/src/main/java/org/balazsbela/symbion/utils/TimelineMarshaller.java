package org.balazsbela.symbion.utils;

import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import org.balazsbela.symbion.models.FunctionCallListWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringSource;

public class TimelineMarshaller implements Marshaller {

	@Autowired
	private Jaxb2Marshaller marshaller;
	
	public TimelineMarshaller() {
		
	}
	
	
	@Override
	public FunctionCallListWrapper decode(String xml) {	
		xml = xml.trim();
		return (FunctionCallListWrapper) marshaller.unmarshal(new StringSource(xml.trim()));		
	}

	@Override
	public String encode(FunctionCallListWrapper o) {
		StringWriter sw = new StringWriter();
		marshaller.marshal(o, new StreamResult(sw));
		return sw.toString();
	}


	public void setMarshaller(Jaxb2Marshaller marshaller) {
		this.marshaller = marshaller;
	}

}
