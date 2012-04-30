package controller;

import java.util.UUID;

import model.Admin;
import model.TeamRocketServerModel;

import org.w3c.dom.Node;

import server.ClientState;
import xml.Message;
/**
 * Controller for the administrator to sign in
 * @author Ian Lukens, Wesley Nitinthorn
 *
 */
public class AdminSignInRequestController {
	/** Client state: used to get the client id and other information identifying the individual client	 */
	ClientState state;
	
	/**
	 * Constructor for the AdminSingInRequestController
	 * @param st ClientState to be set
	 */
	public AdminSignInRequestController(ClientState st) {
		this.state = st;
	}

	/**
	 * Processing function to parse the request and generate the response 
	 * @param request The request from the client to respond to
	 * @return The generated response
	 */
	public Message process(Message request) {
		Node signInR = request.contents.getFirstChild();
		
		/** retrieve ID*/
		String admin = signInR.getFirstChild().getAttributes().getNamedItem("name").getNodeValue();
		String pword = signInR.getFirstChild().getAttributes().getNamedItem("password").getNodeValue();
		String adminKey = UUID.randomUUID().toString();
		adminKey = adminKey.substring(0, 13);
		String xmlString = "";
		
		Admin a = TeamRocketServerModel.getInstance().getAdmin() ;
		
		
		if (!a.signIn(admin, pword)) {
			xmlString =  Message.responseHeader(request.id(), "Invalid credential")+"<adminResponse key ='bad'/></response>";
		}
		else {
			a.setKey(adminKey);
			xmlString =  Message.responseHeader(request.id())+"<adminResponse key ='"+ adminKey +"'/></response>";
		}
				
		
		Message response = new Message(xmlString);	
		
		return response;
	}
}
