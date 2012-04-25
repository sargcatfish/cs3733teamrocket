package controller;

import server.ClientState;
import xml.Message;
import db.Manager;
import model.DLEvent;
import model.TeamRocketServerModel;

/**
 * Gets the closeRequest, ensures that it is from a moderator, processes it, and sends back the response.
 * 
 * @author rhollinger
 *
 */
public class CloseRequestController {
	public void CloseRequestContoller(ClientState st) {
		// TODO Auto-generated constructor stub
	}

	public Message process(Message request) {
		String eventID = request.contents.getFirstChild().getAttributes().getNamedItem("id").getNodeValue();

		//TODO close Event
		//not sure if this is the right way to do this...
		DLEvent m = Manager.retrieveEvent(eventID); 
		Manager.setCompletion(eventID);
		
		String xmlString = Message.responseHeader(request.id()) + "<closeResponse/></response>";
		Message response = new Message(xmlString);
		return response;
	}
}
