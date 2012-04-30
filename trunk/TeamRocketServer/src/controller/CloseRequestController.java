package controller;

import server.ClientState;
import xml.Message;
import db.Manager;
import model.DLEvent;
import model.TeamRocketServerModel;

/**
 * Controller to close the event as an administrator
 * @author rhollinger, iplukens
 *
 */
public class CloseRequestController {
	/** Client state: used to get the client id and other information identifying the individual client	 */
	ClientState state;
	
	/**
	 * Controller for the CloseRequestController
	 * @param st ClientState to be set
	 */
	public  CloseRequestController(ClientState st) {
		state = st;
	}

	/**
	 * Processing function to parse the request and generate the response 
	 * @param request The request from the client to respond to
	 * @return The generated response
	 */
	public Message process(Message request) {
		String eventID = request.contents.getFirstChild().getAttributes().getNamedItem("id").getNodeValue();

		DLEvent temp = TeamRocketServerModel.getInstance().getEvent(eventID) ;
		if (temp == null){
			String xmlString = Message.responseHeader(request.id(), "No event found") + "<closeResponse/></response>" ;
			Message response = new Message(xmlString) ;
			return response ;
		}
		if (temp.getUserList().size() < 3){
			String xmlString = Message.responseHeader(request.id(), "Not enough users") + "<closeResponse/></response>" ;
			Message response = new Message(xmlString) ;
			return response ;
		}
		else {
			temp.setAcceptingUsers(false) ;
			temp.forceComplete() ;
			Manager.setClosed(eventID);
			String xmlString = Message.responseHeader(request.id()) + "<closeResponse/></response>";
			Message response = new Message(xmlString);
			return response;
		}
	}
}
