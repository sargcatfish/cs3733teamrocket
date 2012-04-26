package controller;

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
		Manager.deleteChoices("newChoice");
		Message.configure("decisionlines.xsd");
		String xmlSource = "<request version='1.0' id='123'><addChoiceRequest id='newChoice' number='5' choice='Orange'/></request>";
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
		
		//DLEvent test = Manager.retrieveEvent("123");
		//assertEquals("Orange", test.getDLChoice().get(0).getName());
		Manager.deleteChoices("newChoice");
	}

}
