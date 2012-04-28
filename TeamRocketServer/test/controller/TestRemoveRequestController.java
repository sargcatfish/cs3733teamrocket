package controller;

import java.sql.SQLException;

import javax.xml.ws.Response;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;
import model.DLEvent;
import model.TeamRocketServerModel;
import xml.Message;
import junit.framework.TestCase;
/**
 * 
 * @author Timothy Kolek
 *
 */
public class TestRemoveRequestController extends TestCase {
	RemoveRequestController cont;
	
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
		cont = new RemoveRequestController(null);
		adminSignIn = new AdminSignInRequestController(null);
		Message.configure("decisionlines.xsd");
		adminMsg = new Message(this.adminStr);
		adminSignIn.process(adminMsg);
		server = new TeamRocketServerModel().getInstance();
		key = server.getAdmin().getKey();
		Manager.deleteEvent(id);
		Manager.deleteEvent("2");
		Manager.deleteEvent("3");
		Manager.deleteEvent("4");
		TeamRocketServerModel.getInstance();
		TeamRocketServerModel.destroyEvent(id);
		TeamRocketServerModel.destroyEvent("2");
		TeamRocketServerModel.destroyEvent("3");
		TeamRocketServerModel.destroyEvent("4");
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
		TeamRocketServerModel.getInstance();
		TeamRocketServerModel.destroyEvent(id);
		TeamRocketServerModel.destroyEvent("2");
		TeamRocketServerModel.destroyEvent("3");
		TeamRocketServerModel.destroyEvent("4");
	}
	public void testControllerSingle(){
		String xmlSource = "<request version='1.0' id='fdsfdgfdgdfrr4'>" +
						"<removeRequest key='" + key + "' id='" + id + "'/></request>";
		Message request = new Message(xmlSource);
		try {
			Message response = cont.process(request);
			Node first = response.contents.getFirstChild();
			NamedNodeMap map = first.getAttributes();
			
			int affected = Integer.parseInt(map.getNamedItem("numberAffected").getNodeValue());
			assertEquals(1,affected);
			assertEquals(null, Manager.retrieveEvent(id));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void testControllerMultiple(){
		String xmlSource = "<request version='1.0' id='fdsfdgfdgdfrr4'>" +
				"<removeRequest key='" + key + "' id='" + id + "' " +
						"completed='false' daysOld='-1'/></request>";
		Message request = new Message(xmlSource);
		try {
			Message response = cont.process(request);
			Node first = response.contents.getFirstChild();
			NamedNodeMap map = first.getAttributes();
			
			int affected = Integer.parseInt(map.getNamedItem("numberAffected").getNodeValue());
			assertEquals(4,affected);
			assertEquals(null, Manager.retrieveEvent(id));
//			assertEquals(null, Manager.retrieveEvent("2"));
//			assertEquals(null, Manager.retrieveEvent("3"));
//			assertEquals(null, Manager.retrieveEvent("4"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
