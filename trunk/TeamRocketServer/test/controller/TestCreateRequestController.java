package controller;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import model.DLChoice;
import model.DLEvent;
import db.Manager;
import xml.Message;
import junit.framework.TestCase;

/**
 * 
 * @author Nick Bosowski
 *
 */

public class TestCreateRequestController extends TestCase {
	CreateRequestController cont; 
	
	public void setUp(){
		cont = new CreateRequestController(null);
	}
	public void testController(){
		
		String values[] = {"McDonalds", "Wendys", "Burger King", "Sushi Palace"};
		String numChoices = "4";
		String numRounds = "3";
		String question = "where to eat?";
		
		Message.configure("decisionlines.xsd");
		String xmlSource = "<request version='1.0' id='test'><createRequest type='closed' " +
				"question='" + question + "' numChoices='" + numChoices + "' numRounds='" + numRounds + "'>" +
				"<choice value='" + values[0] + "' index='0'/>" +
				"<choice value='" + values[1] + "' index='1'/>" +
				"<choice value='" + values[2] + "' index='2'/>" +
				"<choice value='" + values[3] + "' index='3'/>" +
				"<user name='Nick Bosowski'/></createRequest></request>";
		
		Message request = new Message(xmlSource);
		
		Message response = cont.process(request);
		Node first = response.contents.getFirstChild();
		NamedNodeMap map = first.getAttributes();
		String id = map.getNamedItem("id").getNodeValue();
		
		DLEvent event = Manager.retrieveEvent(cont.testId);
		assertEquals(cont.testId, id);
		assertEquals(cont.testId, event.getID());
		assertEquals(Integer.parseInt(numChoices), event.getNumChoices());
		assertEquals(Integer.parseInt(numRounds), event.getNumRounds());
		assertEquals(Integer.parseInt(numChoices), event.getDLChoice().size());
		assertFalse(event.getIsOpen());
		assertEquals(0,event.getUserList().get(0).getIndex());
		/* NEED TO FIGURE OUT HOW TO FIX THIS and is it a problem or just needs test case manipulation? */
		for (int i = 0; i< Integer.parseInt(numChoices);i++){
			DLChoice d = event.getDLChoice().get(i);
			int index = d.getIndex();
			assertEquals(values[index], d.getName());
		}
		
		Manager.deleteEvent(event.getID());
		
		 xmlSource = "<request version='1.0' id='test'><createRequest type='open' " +
				"question='" + question + "' numChoices='" + numChoices + "' numRounds='" + numRounds + "'>" +
				"<user name='Nick Bosowski'/></createRequest></request>";
		 
		request = new Message(xmlSource);
		response = cont.process(request);
		event = Manager.retrieveEvent(cont.testId);
		
		assertEquals(cont.testId, event.getID());
		assertEquals(Integer.parseInt(numChoices), event.getNumChoices());
		assertEquals(Integer.parseInt(numRounds), event.getNumRounds());
		assertEquals(0, event.getDLChoice().size());
		assertTrue(event.getIsOpen());
		
		Manager.deleteEvent(event.getID());
	}
}
