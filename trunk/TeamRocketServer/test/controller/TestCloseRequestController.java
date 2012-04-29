package controller;

import model.DLEvent;
import model.MockClient;
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
	String id ;
	int numChoices = 4;
	int numRounds = 3;
	String eventQuestion = "Are llamas evil?";
	String choiceName[] = {"Definitely", "Hail the llama king", "I like turtles", "Keanu Reaves"}; 
	TeamRocketServerModel server;
	CreateRequestController test ;
	SignInRequestController more, more2 ;
	MockClient client1, client2, client3 ;
	
	public void setUp(){
		client1 = new MockClient() ;
		client2 = new MockClient() ;
		client3 = new MockClient() ;
		Message.configure("decisionlines.xsd");
		cont = new CloseRequestController(null);
		test = new CreateRequestController(client1) ;
		String xmlSource = "<request version='1.0' id='test'><createRequest type='open' " +
				"question='" + eventQuestion + "' numChoices='" + numChoices + "' numRounds='" + numRounds + "'>" +
				"<choice value='" + choiceName[0] + "' index='0'/>" +
				"<choice value='" + choiceName[1] + "' index='1'/>" +
				"<user name='THEFlash'/></createRequest></request>";
		
		Message request = new Message(xmlSource);
		test.process(request) ;
		id = test.testId;
		more = new SignInRequestController(client2) ;
		more2 = new SignInRequestController(client3) ;
		server = TeamRocketServerModel.getInstance();
	
	}
	
	public void tearDown(){
		Manager.deleteEvent(id);
		TeamRocketServerModel.destroyEvent(id);
	}
	
	public void testFailSingle() {
		String xmlSource = "<request version='1.0' id='test'>" +
				"<closeRequest id = 'Apples123'/></request>";
		
		DLEvent event1 = Manager.retrieveEvent(id) ;
		assertTrue(event1.isAccepting()) ;
		Message request = new Message(xmlSource);
		cont.process(request);
		assertTrue(TeamRocketServerModel.getInstance().getEvent(id).isAccepting());
		
		String xmlSource2 = "<request version='1.0' id='test'>" +
				"<closeRequest id = 'shoe'/></request>";
		
		Message request2 = new Message(xmlSource2);
		cont.process(request2);
	}
	
	public void testSuccess(){
		DLEvent event1 = Manager.retrieveEvent(id) ;

		assertTrue(event1.isAccepting()) ;
		String xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='GreenLantern'/>" + "</signInRequest></request>";
		
		Message request = new Message(xmlSource);
		more.process(request);
		
		xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='Superman'/>" + "</signInRequest></request>";
		
		request = new Message(xmlSource);
		more.process(request);
		
		xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='batman'/>" + "</signInRequest></request>";
		
		request = new Message(xmlSource);
		more2.process(request);
		
		xmlSource = "<request version='1.0' id='test'>" +
				"<closeRequest id = '" + id + "'/></request>";
		
		request = new Message(xmlSource);
		cont.process(request);
		assertFalse(TeamRocketServerModel.getInstance().getEvent(id).isAccepting());
	}
}