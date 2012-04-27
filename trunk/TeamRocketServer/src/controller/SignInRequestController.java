package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import model.DLEvent;
import model.TeamRocketServerModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import server.ClientState;
import server.Server;

import db.Manager;

import xml.Message;


/**
 * Gets the signInRequest, processes it, and sends back the response.
 * 
 * @author rhollinger
 *
 */
public class SignInRequestController {
	ClientState state;
	
	public SignInRequestController(ClientState st) {
		this.state = st;
	}

	/** When given a SignInRequest, need to generate SignInResponse. */
	public Message process(Message request) {
		Node signInR = request.contents.getFirstChild();
		
		// retrieve ID
		String eventID = signInR.getAttributes().getNamedItem("id").getNodeValue();
		TeamRocketServerModel.getInstance().getTable().get(eventID).addClientState(state); // add the client state to the local list
		
		NamedNodeMap userAtts = signInR.getFirstChild().getAttributes();
		String user = userAtts.getNamedItem("name").getNodeValue();
		Node pNode = userAtts.getNamedItem("password");
		String password = null;
		if (pNode != null) {
			password = pNode.getNodeValue();
		}
		
		// get event object -- have user sign in!
		DLEvent m = Manager.retrieveEvent(eventID);
		int position = m.getNextPosition(user);
		//try to sign in as already existent user
		boolean Accepted = true;
		String failedReason = null;
		if (!m.signIn(user, password)) {
			System.err.println ("Can't sign in #1");
			//add as a new user
			if(!m.isAccepting()){
				//TODO: REJECT USER THEY CANNOT JOIN
				failedReason = "No longer accpting users";
				Accepted = false;
			}
			else if (!Manager.signin(eventID, user, password, false, position)) {
				// TODO: What if can't sign in
				System.err.println ("Can't sign in #2");
				failedReason = "Invalid Password";
				Accepted = false;
			}
		}
		
		StringBuffer choices = new StringBuffer();
		
		for (int i=1; i<m.getNumChoices(); i++ ) {
			String choice = m.getDLChoice().get(i-1).getName();
			// append into entry section
			choices.append("<choice value='" + choice + "' index='" + i + "'/>");
		}
		
		String type = new String("open");
		if (!m.getIsOpen()) {
			type = "closed";
		}
			
			
			// TODO: Error Checking! May have typed in invalid meeting id
		if (Accepted) {
			String xmlString =  Message.responseHeader(request.id()) + "<signInResponse id='" + eventID + "' " + 
			    "id = '" + eventID + "' " + 
				"type = '" + type + "' " +
				"question = '" + m.getEventQuestion() + "' " +
				"numChoices = '" + m.getNumChoices() + "' " + 
				"numRows = '" + m.getNumRounds() + "' " +
				"position = '" + position + "'>" + choices.toString() + "</signInResponse></response>";
			
			Message response = new Message(xmlString);
			
			// Must be sure to send refreshResponse to everyone else.
			xmlString = Message.responseHeader(request.id()) + "<refreshResponse id='" + eventID + "' " +
				"user = '" + user + "'/></response>";
			Message broadcast = new Message (xmlString);
			
			// send to all clients for this same meeting.
			// Now send response to all connected clients associated with same meeting ID.
			for (String threadID : Server.ids()) {
				ClientState cs = Server.getState(threadID);
				if (eventID.equals(cs.getData())) {
					// make sure not to send to requesting client TWICE
					if (!cs.id().equals(state.id())) {
						cs.sendMessage(broadcast);
					}
				}
			}
			return response;
		}
		

		String xmlString = Message.responseHeader(request.id(), failedReason);
		xmlString +=  "<signInResponse id='" + eventID + "' " + 
				"id = '" + eventID + "' " + 
				"type = '" + type + "' " +
				"question = '" + m.getEventQuestion() + "' " +
				"numChoices = '" + m.getNumChoices() + "' " + 
				"numRows = '" + m.getNumRounds() + "' " +
				"position = '" + position + "'>" + choices.toString() + "</signInResponse></response>";

		Message response = new Message(xmlString);

		// Must be sure to send refreshResponse to everyone else.
		xmlString = Message.responseHeader(request.id()) + "<refreshResponse id='" + eventID + "' " +
				"user = '" + user + "'/></response>";
		Message broadcast = new Message (xmlString);

		// send to all clients for this same meeting.
		// Now send response to all connected clients associated with same meeting ID.
		for (String threadID : Server.ids()) {
			ClientState cs = Server.getState(threadID);
			if (eventID.equals(cs.getData())) {
				// make sure not to send to requesting client TWICE
				if (!cs.id().equals(state.id())) {
					cs.sendMessage(broadcast);
				}
			}
		}
	
		
		// make sure to send back to originating client the signInResponse
		return response;
	}

}