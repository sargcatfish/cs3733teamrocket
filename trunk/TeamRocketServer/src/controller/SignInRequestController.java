package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import model.DLEvent;
import model.MockClient;
import model.TeamRocketServerModel;
import model.User;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import server.ClientState;
import server.Server;

import db.Manager;

import xml.Message;


/**
 * Controller for a user to sign in
 * 
 * @author rhollinger, Nick Bosowski, Wesley N.
 *
 */
public class SignInRequestController {
	/** Client state: used to get the client id and other information identifying the individual client	 */
	ClientState state;
	
	/**
	 * Constructor for SignInRequestController
	 * @param st ClientState to be set
	 */
	public SignInRequestController(ClientState st) {
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
		String eventID = signInR.getAttributes().getNamedItem("id").getNodeValue();
	
		
		NamedNodeMap userAtts = signInR.getFirstChild().getAttributes();
		String user = userAtts.getNamedItem("name").getNodeValue();
		Node pNode = userAtts.getNamedItem("password");
		String password = null;
		if (pNode != null) {
			password = pNode.getNodeValue();
		}
		
		/**  get event object -- have user sign in! */
		DLEvent m = Manager.retrieveEvent(eventID);
		if (m == null){
			
			String xmlString = Message.responseHeader(request.id(), "The event doesnt exist") +
					"<signInResponse id=\"" + eventID + "\" " + 
					"type = \"open\" " +
					"question = \"1\" " +
					"numChoices = \"1\" " + 
					"numRounds = \"1\" " +
					"position = \"1\"/></response>";
			Message response = new Message(xmlString);
			return response;
		}

		boolean isModerator = true;
			if(!TeamRocketServerModel.getInstance().getEvent(eventID).getStates().isEmpty()){
				isModerator = false;
				if(!TeamRocketServerModel.getInstance().getEvent(eventID).getStates().get(0).id().equals(state.id())){
					TeamRocketServerModel.getInstance().getEvent(eventID).addClientState(state); // add the client state to the local list
				}
			}
		int position = m.getNextPosition(user);
		/** try to sign in as already existent user */
		boolean Accepted = true;
		String failedReason = null;
		if (!m.signIn(user, password)) {
			/** add as a new user */
			if(!m.isAccepting()){
				failedReason = "No longer accepting users";
				Accepted = false;
			}
			else if (!Manager.signin(eventID, user, password, isModerator, position)) {
				// TODO: What if can't sign in
				System.err.println ("Can't sign in #2");
				failedReason = "Invalid Password";
				Accepted = false;
			}
		}
		if (Accepted){
		m.addUser(new User(user, password, isModerator, position));
		TeamRocketServerModel.getInstance().getEvent(eventID).addUser(new User(user, password, isModerator, position));
		if(m.getUserList().size() == m.getNumChoices())
			Manager.setacceptingUsers(m.getID());
		}
		StringBuffer choices = new StringBuffer();
		
		for (int i=0; i<m.getDLChoice().size(); i++ ) {
			String choice = m.getDLChoice().get(i).getName();
			int index = m.getDLChoice().get(i).getIndex();
			/** append into entry section */
			choices.append("<choice value=\"" + choice + "\" index=\"" + index + "\"/>");
		}
		
		String type = new String("open");
		if (!m.getIsOpen()) {
			type = "closed";
		}
			
			
		if (Accepted) {
			String xmlString =  Message.responseHeader(request.id()) + "<signInResponse id=\"" + eventID + "\" " + 
				"type = \"" + type + "\" " +
				"question = \"" + m.getEventQuestion() + "\" " +
				"numChoices = \"" + m.getNumChoices() + "\" " + 
				"numRounds = \"" + m.getNumRounds() + "\" " +
				"position = \"" + position + "\">" + choices.toString() + "</signInResponse></response>";
			
			Message response = new Message(xmlString);
			if(m.getUserList().size() == m.getNumChoices() && !(state instanceof MockClient) && !m.getIsOpen()){
				new TurnResponseController().process(eventID);
			}
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
		
		return response;
	}

}