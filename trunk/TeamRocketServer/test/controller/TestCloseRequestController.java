package controller;

import model.DLEvent;
import model.TeamRocketServerModel;

import xml.Message;
import db.Manager;
import junit.framework.TestCase;

/**
 * 
 * @author Ian Lukens
 *
 */

public class TestCloseRequestController extends TestCase {
	CloseRequestController cont;
	String id = "Apples123";
	int numChoices = 4;
	int numRounds = 3;
	String eventQuestion = "Are llamas evil?";
	boolean isOpen = false;
	boolean acceptingUsers = true;
	String moderator = "superman";
	String choiceName[] = {"Definitely", "Hail the llama king", "I like turtles", "Keanu Reaves"}; 
	DLEvent event1 = new DLEvent(id, moderator, eventQuestion, numChoices, numRounds) ;
	TeamRocketServerModel server;
	
	public void setUp(){
		Message.configure("decisionlines.xsd");
		cont = new CloseRequestController(null);
		server = TeamRocketServerModel.getInstance();
	
		Manager.insertDLEvent(id, numChoices, numRounds, eventQuestion, true, acceptingUsers, moderator);
	}
	
	public void tearDown(){
		Manager.deleteEvent(id);
		TeamRocketServerModel.destroyEvent(id);
	}
	
	public void testForceSingle() {
		String xmlSource = "<request version='1.0' id='test'>" +
				"<closeRequest id = 'Apples123'/></request>";
		
		assertTrue(event1.isAccepting()) ;
		Message request = new Message(xmlSource);
		cont.process(request);
		assertFalse(TeamRocketServerModel.getInstance().getTable().get(id).isAccepting());
		
		String xmlSource2 = "<request version='1.0' id='test'>" +
				"<closeRequest id = 'shoe'/></request>";
		
		Message request2 = new Message(xmlSource2);
		cont.process(request2);
	}
}