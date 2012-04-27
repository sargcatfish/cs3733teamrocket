package controller;

import java.sql.Date;

import model.DLEvent;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;

import xml.Message;
import junit.framework.TestCase;
/**
 * 
 * @author Timothy Kolek
 *
 */
public class TestAddEdgeController extends TestCase {
	AddEdgeController cont;
	
	public void setUp(){
		cont = new AddEdgeController(null);
	}
	
	public void testAddEdgeProcess(){
		DLEvent temp = new DLEvent("test", "tester", "Hello?", 1, 1);
		Date tempDate = new Date(0);
		temp.setDateCreated(tempDate);
		cont.model.addTestDLEvent(temp);
		
		Message.configure("decisionlines.xsd");
		String xmlSource = "<request version='1.0' id='test'><addEdgeRequest id='newEdge' left='1' right='2' height='397'/></request>";
		Message request = new Message(xmlSource);
		
		Message response = cont.process(request);
		
		Node first = response.contents.getFirstChild();
		NamedNodeMap map = first.getAttributes();
		String id = map.getNamedItem("id").getNodeValue();
		String left = map.getNamedItem("left").getNodeValue();
		String right = map.getNamedItem("right").getNodeValue();
		String height = map.getNamedItem("height").getNodeValue();
		
		assertEquals("newEdge", id);
		assertEquals("1", left);
		assertEquals("2", right);
		assertEquals("397", height);
		
		Manager.deleteEdges("newEdge");
		Manager.deleteEvent("test");
	}

}
