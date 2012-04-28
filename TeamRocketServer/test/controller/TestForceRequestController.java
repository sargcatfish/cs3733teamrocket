package controller;

import java.sql.SQLException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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
public class TestForceRequestController extends TestCase {
	ForceRequestController cont;
	AdminSignInRequestController adminSignIn;
	String id = "Apples123";
	int numChoices = 4;
	int numRounds = 3;
	String eventQuestion = "Are llamas evil?";
	boolean isOpen = false;
	boolean acceptingUsers = true;
	String moderator = "superman";
	String choiceName[] = {"Definitely", "Hail the llama king", "I like turtles", "Keanu Reaves"}; 
	DLEvent event1 = new DLEvent(id, moderator, eventQuestion, numChoices, numRounds);
	
	DLEvent event2 = new DLEvent("2", moderator, eventQuestion, numChoices, numRounds);
	DLEvent event3 = new DLEvent("3", moderator, eventQuestion, numChoices, numRounds);
	DLEvent event4 = new DLEvent("4", moderator, eventQuestion, numChoices, numRounds);
	
	String key;
	
	String adminStr = "<request version='1.0' id='fdsfrr4'>" +
	"<adminRequest>" + "<user name='admin' password='password' />" +
	"</adminRequest></request>";
	
	TeamRocketServerModel server;
	
	Message adminMsg;
	
	public void setUp(){
		Message.configure("decisionlines.xsd");
		adminMsg = new Message(this.adminStr);
		cont = new ForceRequestController(null);
		adminSignIn = new AdminSignInRequestController(null);
		adminSignIn.process(adminMsg);
		server = new TeamRocketServerModel().getInstance();
		key = server.getAdmin().getKey();
		
		TeamRocketServerModel.getInstance().getTable().put(id, event1);
		TeamRocketServerModel.getInstance().getTable().put("2", event2);
		TeamRocketServerModel.getInstance().getTable().put("3", event3);
		TeamRocketServerModel.getInstance().getTable().put("4", event4);
		Manager.insertDLEvent(id, numChoices, numRounds, eventQuestion, true, acceptingUsers, moderator);
		Manager.insertDLEvent("2", numChoices, numRounds, eventQuestion, true, acceptingUsers, moderator);
		Manager.insertDLEvent("3", numChoices, numRounds, eventQuestion, false, acceptingUsers, moderator);
		Manager.insertDLEvent("4", numChoices, numRounds, eventQuestion, true, acceptingUsers, moderator);
		
		
		
	}
	
	public void tearDown(){
		Manager.deleteEvent(id);
		Manager.deleteEvent("2");
		Manager.deleteEvent("3");
		Manager.deleteEvent("4");
		TeamRocketServerModel.destroyEvent(id);
		TeamRocketServerModel.destroyEvent("2");
		TeamRocketServerModel.destroyEvent("3");
		TeamRocketServerModel.destroyEvent("4");
	}
	
	public void testForceSingle() {
		String xmlSource = "<request version='1.0' id='test'>" +
				"<forceRequest key ='" + key + "' id = 'Apples123'/></request>";
		
		assertFalse(event1.isComplete()) ;
		Message request = new Message(xmlSource);
		Message response = cont.process(request);
		assertTrue(event1.isComplete()) ;
		
		String xmlSource2 = "<request version='1.0' id='test'>" +
				"<forceRequest key ='" + key + "' id = 'shoe'/></request>";
		
		Message request2 = new Message(xmlSource2);
		Message response2 = cont.process(request2);
		Node check = response2.contents.getFirstChild() ;
		NamedNodeMap checkA = check.getAttributes() ;
		int val = Integer.parseInt(checkA.getNamedItem("numberAffected").getNodeValue()) ;
		assertEquals(0, val) ;
	}
	
	public void testForceDays() throws SQLException{
		String xmlSource = "<request version='1.0' id='test'>" +
				"<forceRequest key ='" + key + "' daysOld = '-1'/></request>";
		
		assertFalse(event1.isComplete()) ;
		Message request = new Message(xmlSource);
		Message response = cont.process(request);
		System.out.println("response: " + response);
		Node check = response.contents.getFirstChild() ;
		NamedNodeMap checkA = check.getAttributes() ;
		int val = Integer.parseInt(checkA.getNamedItem("numberAffected").getNodeValue()) ;
		assertTrue(event1.isComplete()) ;
		assertTrue(val > 3) ;
	}
}
