package controller;

import org.w3c.dom.NamedNodeMap;

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
			adminMsg = new Message(adminStrFail);
			adminSignIn = new AdminSignInRequestController(null);
			
			// process message
			Message response = adminSignIn.process(adminMsg);

			// retrieve contents
			NamedNodeMap signInR = response.contents.getAttributes();
			String reason = signInR.getNamedItem("reason").getNodeValue();
			String success = signInR.getNamedItem("success").getNodeValue();
			
			// check if the expected response and the received are equal
			assertEquals("Invalid credential", reason);
			assertEquals("false", success);
		}
		
		public void testCorrectPassword(){
			// setup the system
			Message.configure("decisionlines.xsd");
			adminMsg = new Message(adminStrPass);
			adminSignIn = new AdminSignInRequestController(null);
			
			// process message
			Message response = adminSignIn.process(adminMsg);

			// retrieve contents
			NamedNodeMap signInR = response.contents.getAttributes();
			String success = signInR.getNamedItem("success").getNodeValue();
			
			// check if the expected response and the received are equal
			assertEquals("true", success);
		}
}
