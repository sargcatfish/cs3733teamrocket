package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import model.DLEvent;
import model.TeamRocketServerModel;
import model.User;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import server.ClientState;
import server.Server;

import db.Manager;

import xml.Message;


/**
 * Gets the signInRequest, processes it, and sends back the response.
 * 
 * @author rhollinger, Nick Bosowski
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
	
		
		NamedNodeMap userAtts = signInR.getFirstChild().getAttributes();
		String user = userAtts.getNamedItem("name").getNodeValue();
		Node pNode = userAtts.getNamedItem("password");
		String password = null;
		if (pNode != null) {
			password = pNode.getNodeValue();
		}
		
		// get event object -- have user sign in!
		DLEvent m = Manager.retrieveEvent(eventID);
		//Added by Wesley trying to handle the event doesn't exist
		if (m == null){
			
			String xmlString = Message.responseHeader(request.id(), "The event doesnt exist") +
					"<signInResponse id=\"" + eventID + "\" " + 
					"type = \"open\" " +
					"question = \"1\" " +
					"numChoices = \"1\" " + 
					"numRounds = \"1\" " +
					"position = \"1\"/></response>";
//			System.out.print(xmlString);
			Message response = new Message(xmlString);
			return response;
		}
//		if(TeamRocketServerModel.getInstance().getTable().get(eventID) == null){
//			TeamRocketServerModel.getInstance().getTable().put(eventID, m);
//		}
		TeamRocketServerModel.getInstance().getEvent(eventID).addClientState(state); // add the client state to the local list
		int position = m.getNextPosition(user);
		//try to sign in as already existent user
		boolean Accepted = true;
		String failedReason = null;
		if (!m.signIn(user, password)) {
//			System.err.println ("Can't sign in #1");
			//add as a new user
			if(!m.isAccepting()){
				//TODO: REJECT USER THEY CANNOT JOIN
				failedReason = "No longer accepting users";
				Accepted = false;
			}
			else if (!Manager.signin(eventID, user, password, false, position)) {
				// TODO: What if can't sign in
				System.err.println ("Can't sign in #2");
				failedReason = "Invalid Password";
				Accepted = false;
			}
		}
			
		m.addUser(new User(user, password, false, position));
		TeamRocketServerModel.getInstance().getTable().get(eventID).addUser(new User(user, password, false, position));
		if(m.getUserList().size() == m.getNumChoices())
			Manager.setacceptingUsers(m.getID());
		StringBuffer choices = new StringBuffer();
		
		for (int i=0; i<m.getDLChoice().size(); i++ ) {
			String choice = m.getDLChoice().get(i).getName();
			int index = m.getDLChoice().get(i).getIndex();
			// append into entry section
			choices.append("<choice value=\"" + choice + "\" index=\"" + index + "\"/>");
		}
		
		String type = new String("open");
		if (!m.getIsOpen()) {
			type = "closed";
		}
			
			
			// TODO: Error Checking! May have typed in invalid meeting id
		if (Accepted) {
			String xmlString =  Message.responseHeader(request.id()) + "<signInResponse id=\"" + eventID + "\" " + 
//			    "id = \"" + eventID + "\" " + 
				"type = \"" + type + "\" " +
				"question = \"" + m.getEventQuestion() + "\" " +
				"numChoices = \"" + m.getNumChoices() + "\" " + 
				"numRounds = \"" + m.getNumRounds() + "\" " +
				"position = \"" + position + "\">" + choices.toString() + "</signInResponse></response>";
			
			Message response = new Message(xmlString);
			
//			// Must be sure to send refreshResponse to everyone else.
//			xmlString = Message.responseHeader(request.id()) + "<refreshResponse id='" + eventID + "' " +
//				"user = '" + user + "'/></response>";
//			Message broadcast = new Message (xmlString);
//			
//			// send to all clients for this same meeting.
//			// Now send response to all connected clients associated with same meeting ID.
//			for (String threadID : Server.ids()) {
//				ClientState cs = Server.getState(threadID);
//				if (eventID.equals(cs.getData())) {
//					// make sure not to send to requesting client TWICE
//					if (!cs.id().equals(state.id())) {
//						cs.sendMessage(broadcast);
//					}
//				}
//			}
			return response;
		}
		

		String xmlString = Message.responseHeader(request.id(), failedReason);
		xmlString +=  "<signInResponse id=\"" + eventID + "\" " + 
				"type = \"" + type + "\" " +
				"question = \"" + m.getEventQuestion() + "\" " +
				"numChoices = \"" + m.getNumChoices() + "\" " + 
				"numRounds = \"" + m.getNumRounds() + "\" " +
				"position = \"" + position + "\">" + choices.toString() + "</signInResponse></response>";

		Message response = new Message(xmlString);

		// Must be sure to send refreshResponse to everyone else.
//		xmlString = Message.responseHeader(request.id()) + "<refreshResponse id='" + eventID + "' " +
//				"user = '" + user + "'/></response>";
//		Message broadcast = new Message (xmlString);
//
//		// send to all clients for this same meeting.
//		// Now send response to all connected clients associated with same meeting ID.
//		for (String threadID : Server.ids()) {
//			ClientState cs = Server.getState(threadID);
//			if (eventID.equals(cs.getData())) {
//				// make sure not to send to requesting client TWICE
//				if (!cs.id().equals(state.id())) {
//					cs.sendMessage(broadcast);
//				}
//			}
//		}
//	
		
		// make sure to send back to originating client the signInResponse
		return response;
	}

}