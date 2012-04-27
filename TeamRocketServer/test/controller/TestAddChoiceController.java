package controller;

import java.sql.Date;

import model.DLEvent;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;

import server.ClientState;
import xml.Message;
import junit.framework.TestCase;
/**
 * 
 * @author Timothy Kolek
 *
 */
public class TestAddChoiceController extends TestCase {
	AddChoiceController cont;
	
	public void setUp(){
		cont = new AddChoiceController(null);
	}
	
	public void testAddChoiceProcess(){
		DLEvent temp = new DLEvent("test", "tester", "Hello?", 1, 1);
		Date tempDate = new Date(0);
		temp.setDateCreated(tempDate);
		cont.model.addTestDLEvent(temp);
		
		Message.configure("decisionlines.xsd");
		String xmlSource = "<request version='1.0' id='test'><addChoiceRequest id='newChoice' number='5' choice='Orange'/></request>";
		Message request = new Message(xmlSource);
		
		Message response = cont.process(request);
		
		Node first = response.contents.getFirstChild();
		NamedNodeMap map = first.getAttributes();
		String id = map.getNamedItem("id").getNodeValue();
		String number = map.getNamedItem("number").getNodeValue();
		String choice = map.getNamedItem("choice").getNodeValue();
		
		assertEquals("newChoice", id);
		assertEquals("5", number);
		assertEquals("Orange", choice);
		
//		DLEvent test = Manager.retrieveEvent("123");
//		assertEquals("Orange", test.getDLChoice().get(0).getName());
		Manager.deleteChoices("newChoice");
		Manager.deleteEvent("test");
		
	}

}
