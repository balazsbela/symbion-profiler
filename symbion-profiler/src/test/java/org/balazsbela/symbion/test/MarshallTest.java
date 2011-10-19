package org.balazsbela.symbion.test;
import java.io.BufferedOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Assert;

import org.balazsbela.symbion.models.Info;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for login service
 * 
 * @author Balazs Bela
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/balazsbela/symbion/test/context/testContext.xml" })
public class MarshallTest {

	@Autowired
	@Qualifier("marshaller")
	Jaxb2Marshaller marshaller;
	
	@Test
	public void testMarshalling () throws XmlMappingException, IOException{	
		Assert.assertNotNull(marshaller);
		Info addr = new Info();		
		addr.setCity("Cluj");
		
		marshaller.marshal(addr,new StreamResult(new FileWriter("object.xml")));
		
		Info addr2 = (Info) marshaller.unmarshal(new StreamSource(new FileReader("object.xml")));
		Assert.assertEquals(addr.getCity(),addr2.getCity());
	}
}
