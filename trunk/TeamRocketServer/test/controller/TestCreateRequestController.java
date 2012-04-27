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
				"<choice value='McDonald's' index='0'/>" +
				"<choice value='Wendy's' index='1'/>" +
				"<choice value='Burger King' index='2'/>" +
				"<choice value='Sushi Palace' index='3'/>" +
				"<user name='Nick Bosowski'/></createRequest></request>";
		
		Message request = new Message(xmlSource);
		
		Message response = cont.process(request);
		
		DLEvent event = Manager.retrieveEvent("createRequest");
		
		assertEquals("createRequest", event.getID());
		assertEquals(4, event.getNumChoices());
		assertEquals(3, event.getNumRounds());
		assertEquals(4, event.getDLChoice().size());
		assertEquals("Wendy's", event.getDLChoice().get(0).getName());
	}
}
