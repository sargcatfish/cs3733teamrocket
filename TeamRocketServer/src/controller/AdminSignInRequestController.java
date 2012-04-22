package teamRocket.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import server.ClientState;
import server.Server;
import xml.Message;

public class AdminSignInRequestController {

	ClientState state;
	
	public AdminSignInRequestController(ClientState st) {
		this.state = st;
	}

	/** When given a SignInRequest, need to generate SignInResponse. */
	public Message process(Message request) {
		Node signInR = request.contents.getFirstChild();
		
		// retrieve ID		
		NamedNodeMap adminAtts = signInR.getFirstChild().getAttributes();
		String admin = adminAtts.getNamedItem("name").getNodeValue();
		String pword = adminAtts.getNamedItem("password").getNodeValue();
		
		String adminKey = UUID.randomUUID().toString();
		adminKey = adminKey.substring(0, 13);
		
		// get meeting object -- have user sign in!
		a = ServerModel.getInstance().getAdmin() ;
		
		if (a == null){
			System.err.println("User name doesn't match") ;
		}
		
		if (!a.signIn(admin, pword)) {
			System.err.println ("Can't sign in");
			adminKey = "INVALID" ;
		}
		// client should recognize this!
		else //:: TODO update data base with new admin key!
			;
		
		String xmlString =  Message.responseHeader(request.id()) + "<adminResponse key =" + adminKey + "</adminResponse></response>" ;
		
		Message response = new Message(xmlString);	
		
		// make sure to send back to originating client the adminResponse
		return response;
	}
}
