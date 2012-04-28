package controller;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import model.DLEvent;
import model.TeamRocketServerModel;
import xml.Message;
import controller.SignInRequestController;
import db.Manager;
import junit.framework.TestCase;

/**
 * 
 * @author Nick Bosowski
 *
 */
public class TestSignInRequestController extends TestCase {
	SignInRequestController cont;
	CreateRequestController create;
	String id = "Apples123";
	int numChoices = 4;
	int numRounds = 3;
	String eventQuestion = "Are llamas evil?";
	boolean isOpen = false;
	boolean acceptingUsers = true;
	String moderator = "superman";
	String choiceName[] = {"Definitely", "Hail the llama king", "I like turtles", "Keanu Reaves"}; 
	DLEvent event = new DLEvent(id, moderator, eventQuestion, numChoices, numRounds);
	
	
	public void setUp(){
		cont = new SignInRequestController(null);
		create = new CreateRequestController(null);
		String id = "Apples123";
		Message.configure("decisionlines.xsd");
	//	TeamRocketServerModel.getInstance().getTable().put(id, event);
	}
	
	public void tearDown(){
		Manager.deleteEvent(id);
		TeamRocketServerModel.getInstance();
		TeamRocketServerModel.destroyEvent(id);
	}
	public void testClosedController(){
				
		String name = "Batman";

//		Manager.insertDLEvent(id, numChoices, numRounds, eventQuestion, isOpen, acceptingUsers, moderator);
//		Manager.signin(id, moderator, "", true, 0);
//		for(int i = 0; i < numChoices; i++){
//			Manager.insertChoice(id, i, choiceName[i]);
//		}
//		Message.configure("decisionlines.xsd");
		String xmlSource = "<request version='1.0' id='test'><createRequest type='closed' " +
				"question='" + eventQuestion + "' numChoices='" + numChoices + "' numRounds='" + numRounds + "'>" +
				"<choice value='" + choiceName[0] + "' index='0'/>" +
				"<choice value='" + choiceName[1] + "' index='1'/>" +
				"<choice value='" + choiceName[2] + "' index='2'/>" +
				"<choice value='" + choiceName[3] + "' index='3'/>" +
				"<user name='Nick Bosowski'/></createRequest></request>";
		
		Message request = new Message(xmlSource);
		create.process(request);
		id = create.testId;
		 xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='" + name +"'/>" + "</signInRequest></request>";
		
		request = new Message(xmlSource);
		Message response = cont.process(request);
		//System.out.print(response.toString());
		Node first = response.contents.getFirstChild(); // this should be the signInResponse
		System.out.print(response);
		NamedNodeMap map = first.getAttributes();
		
		assertEquals(numChoices, Integer.parseInt(map.getNamedItem("numChoices").getNodeValue()));
		assertEquals(numRounds, Integer.parseInt(map.getNamedItem("numRounds").getNodeValue()));
		assertEquals(eventQuestion, map.getNamedItem("question").getNodeValue());
		assertEquals(id, map.getNamedItem("id").getNodeValue());
		assertEquals(1, Integer.parseInt(map.getNamedItem("position").getNodeValue())); 
		NodeList children = first.getChildNodes();
		for(int i = 0; i < children.getLength(); i++){
			Node child = children.item(i);
			String value = child.getAttributes().getNamedItem("value").getNodeValue();
			int index = Integer.parseInt(child.getAttributes().getNamedItem("index").getNodeValue());
			
			assertEquals(index, i);
			assertEquals(choiceName[i], value);
			
		}
	}
	
	public void testOpenController(){
//		isOpen = true;
		
		String name = "Batman";
		
//		Manager.insertDLEvent(id, numChoices, numRounds, eventQuestion, isOpen, acceptingUsers, moderator);
//		for(int i = 0; i < 2; i++){
//			Manager.insertChoice(id, i, choiceName[i]);
//		}
		
//		Message.configure("decisionlines.xsd");
		String xmlSource = "<request version='1.0' id='test'><createRequest type='open' " +
				"question='" + eventQuestion + "' numChoices='" + numChoices + "' numRounds='" + numRounds + "'>" +
				"<choice value='" + choiceName[0] + "' index='0'/>" +
				"<choice value='" + choiceName[1] + "' index='1'/>" +
//				"<choice value='" + choiceName[2] + "' index='2'/>" +
//				"<choice value='" + choiceName[3] + "' index='3'/>" +
				"<user name='Nick Bosowski'/></createRequest></request>";
		
		Message request = new Message(xmlSource);
		create.process(request);
		id = create.testId;
		xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='" + name +"'/>" + "</signInRequest></request>";
		
		request = new Message(xmlSource);
		Message response = cont.process(request);
		//System.out.print(response.toString());
		Node first = response.contents.getFirstChild(); // this should be the signInResponse
		NamedNodeMap map = first.getAttributes();
		
		assertEquals(numChoices, Integer.parseInt(map.getNamedItem("numChoices").getNodeValue()));
		assertEquals(numRounds, Integer.parseInt(map.getNamedItem("numRounds").getNodeValue()));
		assertEquals(eventQuestion, map.getNamedItem("question").getNodeValue());
		assertEquals(id, map.getNamedItem("id").getNodeValue());
//		assertEquals(1, map.getNamedItem("position").getNodeValue()); need to figure out internal protocol for this
		NodeList children = first.getChildNodes();
//		System.out.print(response);
		for(int i = 0; i < children.getLength(); i++){
			Node child = children.item(i);
			String value = child.getAttributes().getNamedItem("value").getNodeValue();
			int index = Integer.parseInt(child.getAttributes().getNamedItem("index").getNodeValue());
			
			assertEquals(index, i);
			assertEquals(choiceName[i], value);
			
		}
	}
	
	public void testBadID(){
		
		String xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + "ianlukens123" + "'>" +
				"<user name='" + "ted" +"'/>" + "</signInRequest></request>";
		
		Message request = new Message(xmlSource);
		Message response = cont.process(request);
		assertFalse(Boolean.parseBoolean(response.contents.getAttributes().getNamedItem("success").getNodeValue()));
	}
	
	public void testNotAcceptingEvent(){
		Manager.insertDLEvent(id, numChoices, numRounds, eventQuestion, isOpen, false, moderator);
		for(int i = 0; i < numChoices; i++){
			Manager.insertChoice(id, i, choiceName[i]);
		}
		String xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='" + "BOB" +"'/>" + "</signInRequest></request>";
		
		Message request = new Message(xmlSource);
		Message response = cont.process(request);
		assertFalse(Boolean.parseBoolean(response.contents.getAttributes().getNamedItem("success").getNodeValue()));
	}
	
	public void testTooManyUsers(){
		
		String xmlSource = "<request version='1.0' id='test'><createRequest type='open' " +
				"question='" + eventQuestion + "' numChoices='" + numChoices + "' numRounds='" + numRounds + "'>" +
				"<choice value='" + choiceName[0] + "' index='0'/>" +
				"<choice value='" + choiceName[1] + "' index='1'/>" +
//				"<choice value='" + choiceName[2] + "' index='2'/>" +
//				"<choice value='" + choiceName[3] + "' index='3'/>" +
				"<user name='Nick Bosowski'/></createRequest></request>";
		
		Message request = new Message(xmlSource);
		create.process(request);
		id = create.testId;
		
		xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='" + "bill" +"'/>" + "</signInRequest></request>";
		request = new Message(xmlSource);
		cont.process(request);
		
		xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='" + "ted" +"'/>" + "</signInRequest></request>";
		request = new Message(xmlSource);
		cont.process(request);
		
		xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='" + "frank" +"'/>" + "</signInRequest></request>";
		request = new Message(xmlSource);
		cont.process(request);
		
		xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='" + "sam" +"'/>" + "</signInRequest></request>";
		request = new Message(xmlSource);
		Message response = cont.process(request);
//		System.out.print(response);
		
		NamedNodeMap map = response.contents.getAttributes();
		boolean success = Boolean.parseBoolean(map.getNamedItem("success").getNodeValue());
		assertFalse(success);
		
	}
}
