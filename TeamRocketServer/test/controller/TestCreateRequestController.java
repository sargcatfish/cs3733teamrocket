package controller;

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
		
		Message.configure("decisionlines.xsd");
		String xmlSource = "<request version='1.0' id='test'><createRequest type='closed' " +
				"question='where to eat?' numChoices='4' numRounds='3'>" +
				"<choice value='McDonalds' index='0'/>" +
				"<choice value='Wendys' index='1'/>" +
				"<choice value='Burger King' index='2'/>" +
				"<choice value='Sushi Palace' index='3'/>" +
				"<user name='Nick Bosowski'/></createRequest></request>";
		
		Message request = new Message(xmlSource);
		
		Message response = cont.process(request);
		
		DLEvent event = Manager.retrieveEvent(cont.testId);
		
		assertEquals(cont.testId, event.getID());
		assertEquals(4, event.getNumChoices());
		assertEquals(3, event.getNumRounds());
		assertEquals(4, event.getDLChoice().size());
		assertFalse(event.getIsOpen());
		/* NEED TO FIGURE OUT HOW TO FIX THIS and is it a problem or just needs test case manipulation? */
	//	assertEquals("Wendy's", event.getDLChoice().get(0).ge
		
		Manager.deleteEvent(event.getID());
		
		 xmlSource = "<request version='1.0' id='test'><createRequest type='open' " +
				"question='where to eat?' numChoices='4' numRounds='3'>" +
				"<user name='Nick Bosowski'/></createRequest></request>";
		 
		request = new Message(xmlSource);
		response = cont.process(request);
		event = Manager.retrieveEvent(cont.testId);
		
		assertEquals(cont.testId, event.getID());
		assertEquals(4, event.getNumChoices());
		assertEquals(3, event.getNumRounds());
		assertEquals(0, event.getDLChoice().size());
		assertTrue(event.getIsOpen());
	}
}
