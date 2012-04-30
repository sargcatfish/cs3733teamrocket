package controller;

import model.Admin;
import model.TeamRocketServerModel;
import xml.Message;
import junit.framework.TestCase;
/**
 * 
 * @author Ian Lukens, Wesley Nitinthorn
 *
 */
public class TestAdminSignInRequestController extends TestCase {
		AdminSignInRequestController adminSignIn;
		String key ;
		Message adminMsg ;
		TeamRocketServerModel server ;
		String adminStr = "<request version='1.0' id='fdsfrr4'>" +
				"<adminRequest>" + "<user name='admin' password='pss' />" +
				"</adminRequest></request>" ;
		
		public void testWrongPassword(){
			Message.configure("decisionlines.xsd");
			adminMsg = new Message(this.adminStr);
			adminSignIn = new AdminSignInRequestController(null);
			
			Message response = adminSignIn.process(adminMsg);
			assertFalse(response.success()) ;
	}
}