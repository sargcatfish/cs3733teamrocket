package controller;

import junit.framework.TestCase;
import model.DLEvent;
import model.MockClient;
import model.TeamRocketServerModel;

import server.Server;

import xml.Message;
import db.Manager;

/**
 * wanted to isolate testing the broadcast and turn functionality
 * 
 * @author ian
 *
 */
public class TestBroadcast extends TestCase{
	SignInRequestController cont, cont2;
	CreateRequestController create;
	String id = "Apples123";
	int numChoices = 3;
	int numRounds = 3;
	String eventQuestion = "Are llamas evil?";
	boolean isOpen = false;
	boolean acceptingUsers = true;
	String moderator = "superman";
	String choiceName[] = {"Definitely", "Hail the llama king", "I like turtles", "Keanu Reaves"}; 
	DLEvent event = new DLEvent(id, moderator, eventQuestion, numChoices, numRounds);
	MockClient client1, client2, client3 ;
	AddChoiceController tested ;
	AddEdgeController edge ;
	
	
	public void setUp(){
		client1 = new MockClient() ;
		client2 = new MockClient() ;
		client3 = new MockClient() ;
		Server.register(client1.id(), client1) ;
		Server.register(client2.id(), client2) ;
		Server.register(client3.id(), client3) ;
		cont = new SignInRequestController(client2);
		create = new CreateRequestController(client1);
		cont2 = new SignInRequestController(client3) ;
		Message.configure("decisionlines.xsd");
		tested = new AddChoiceController(client1) ;
		edge = new AddEdgeController(client2) ;
	}
	
	public void tearDown(){
		Server.unregister(client1.id()) ;
		Server.unregister(client2.id()) ;
		Server.unregister(client3.id()) ;
		Manager.deleteEvent(id);
		TeamRocketServerModel.getInstance();
		TeamRocketServerModel.destroyEvent(id);
	}

	
	public void testBroadcast(){
		
		String name = "Batman";
		

		String xmlSource = "<request version='1.0' id='test'><createRequest type='open' " +
				"question='" + eventQuestion + "' numChoices='" + numChoices + "' numRounds='" + numRounds + "'>" +
				"<choice value='" + choiceName[0] + "' index='0'/>" +
				"<choice value='" + choiceName[1] + "' index='1'/>" +
				"<user name='Nick Bosowski'/></createRequest></request>";
		
		Message request = new Message(xmlSource);
		create.process(request);
		
		id = create.testId;
		xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='" + name +"'/>" + "</signInRequest></request>";
		
		request = new Message(xmlSource);
		cont.process(request);
		
		xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='Superman'/>" + "</signInRequest></request>";
		cont2.process(request) ;
		
		xmlSource = "<request version='1.0' id='test'><addChoiceRequest id='" + id + "' number='0' choice='Orange'/></request>";
		request = new Message(xmlSource) ;
		tested.process(request) ;
		
		Message c2response = client2.getAndRemoveMessage();
		Message c3response = client3.getAndRemoveMessage();
		assertTrue (c2response.success());
		assertTrue (c3response.success());
		assertEquals ("addChoiceResponse", c2response.contents.getFirstChild().getLocalName());
		assertEquals ("addChoiceResponse", c3response.contents.getFirstChild().getLocalName());
		
		xmlSource = "<request version='1.0' id='test'><addEdgeRequest id='" + id + "' left='1' right='2' height='397'/></request>";
		request = new Message(xmlSource) ;
		edge.process(request) ;
		
		Message c1response = client1.getAndRemoveMessage();
		c3response = client3.getAndRemoveMessage();
		assertTrue (c1response.success());
		assertTrue (c3response.success());
		assertEquals ("addEdgeResponse", c1response.contents.getFirstChild().getLocalName());
		assertEquals ("addEdgeResponse", c3response.contents.getFirstChild().getLocalName());
		
		// it is now client2's turn "again" because it is assumed the first edge is sent by the manager
		// even though in this instance, client2 just sent the message
		c2response = client2.getAndRemoveMessage() ;
		assertEquals ("turnResponse", c2response.contents.getFirstChild().getLocalName()) ;
	}
}
