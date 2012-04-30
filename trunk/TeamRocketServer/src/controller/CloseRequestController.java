package controller;

import server.ClientState;
import xml.Message;
import db.Manager;
import model.DLEvent;
import model.TeamRocketServerModel;

/**
 * Gets the closeRequest, ensures that it is from a moderator, processes it, and sends back the response.
 * 
 * @author rhollinger, iplukens
 *
 */
public class CloseRequestController {

	ClientState state;
	
	public  CloseRequestController(ClientState st) {
		state = st;
	}

	public Message process(Message request) {
		String eventID = request.contents.getFirstChild().getAttributes().getNamedItem("id").getNodeValue();
		int choices = 0 ;

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
