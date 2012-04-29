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
		//TODO close Event
		//not sure if this is the right way to do this...
		DLEvent temp = TeamRocketServerModel.getInstance().getEvent(eventID) ;
				if (temp != null){
					temp.setAcceptingUsers(false) ;
					choices = temp.getStates().size();
					temp.setNumChoices(choices) ;
				}
		int result = Manager.setClosed(eventID, choices);
		if (result == 0){
			String xmlString = Message.responseHeader(request.id(), "No event found") + "<closeResponse/></response>" ;
			Message response = new Message(xmlString) ;
			return response ;
		}
		else { 
			String xmlString = Message.responseHeader(request.id()) + "<closeResponse/></response>";
			Message response = new Message(xmlString);
			return response;
		}
	}
}
