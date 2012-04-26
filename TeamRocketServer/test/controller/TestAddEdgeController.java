package controller;

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
		Manager.deleteEdges("newEdge");
		Message.configure("decisionlines.xsd");
		String xmlSource = "<request version='1.0' id='123'><addEdgeRequest id='newEdge' left='1' right='2' height='397'/></request>";
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
	}

}
