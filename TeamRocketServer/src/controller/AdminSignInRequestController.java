package controller;

import java.util.UUID;

import model.Admin;
import model.TeamRocketServerModel;

import org.w3c.dom.Node;

import server.ClientState;
import xml.Message;

public class AdminSignInRequestController {

	ClientState state;
	
	public AdminSignInRequestController(ClientState st) {
		this.state = st;
	}

	/** When given a SignInRequest, need to generate SignInResponse. */
	public Message process(Message request) {
		System.out.println(request);
		Node signInR = request.contents.getFirstChild();
		
		// retrieve ID
		System.out.println(signInR);
		System.out.println(request.contents.getFirstChild().getFirstChild().getAttributes().getNamedItem("id"));
		String admin = signInR.getFirstChild().getAttributes().getNamedItem("name").getNodeValue();
		String pword = signInR.getFirstChild().getAttributes().getNamedItem("password").getNodeValue();
		
		String adminKey = UUID.randomUUID().toString();
		adminKey = adminKey.substring(0, 13);
		String xmlString = "";
		
		Admin a = TeamRocketServerModel.getInstance().getAdmin() ;
		
		
		if (!a.signIn(admin, pword)) {
			xmlString =  Message.responseHeader(request.id(), "Invalid credential")+"<adminResponse key =\"bad\" /></response>"; //valid xml??
		}
		// client should recognize this!
		else {
			a.setKey(adminKey);
			xmlString =  Message.responseHeader(request.id()) + "<adminResponse key =\"" + adminKey + "\" /></response>" ;
		}
				
		
		Message response = new Message(xmlString);	
		
		// make sure to send back to originating client the adminResponse
		System.out.println(response.toString());
		return response;
	}
}
