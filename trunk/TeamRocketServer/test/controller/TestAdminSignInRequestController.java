package controller;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import model.TeamRocketServerModel;
import xml.Message;
import junit.framework.TestCase;

/**
 * 
 * @author Ian Lukens, Wesley Nitinthorn, Greg McConnell
 *
 */
public class TestAdminSignInRequestController extends TestCase {
		AdminSignInRequestController adminSignIn;
		Message adminMsg ;
		TeamRocketServerModel server ;
		String adminStrFail = Message.requestHeader()+"<adminRequest><user name='admin' password='pss'/></adminRequest></request>";
		
		String adminStrPass = Message.requestHeader()+"<adminRequest><user name='admin' password='password'/></adminRequest></request>";
		
		public void testWrongPassword(){
			// setup the system
			Message.configure("decisionlines.xsd");
			adminMsg = new Message(this.adminStrFail);
			System.out.println(adminMsg);
			adminSignIn = new AdminSignInRequestController(null);
			
			// process message
			Message response = adminSignIn.process(adminMsg);
			// retrieve contents
			String reason = response.contents.getFirstChild().getAttributes().getNamedItem("reason").getNodeValue();
			String success = response.contents.getFirstChild().getAttributes().getNamedItem("success").getNodeValue();
			
			// check if the expected response and the received are equal
			assertEquals("Invalid credential", reason);
			assertEquals("false", success);
		}
		
		public void testCorrectPassword(){
			// setup the system
			Message.configure("decisionlines.xsd");
			adminMsg = new Message(this.adminStrPass);
			adminSignIn = new AdminSignInRequestController(null);
			
			// configure the expected response
			String key = "temp";
			String xmlresponse = Message.responseHeader(adminMsg.id()) + "<adminResponse key =\"" + key + "\" /></response>" ;
			Message responseMsg = new Message(xmlresponse);
			System.out.println(responseMsg.toString());
			
			// check if the expected response and the received are equal
			assertFalse(!responseMsg.toString().equals(adminSignIn.process(adminMsg).toString()));
		}
}
