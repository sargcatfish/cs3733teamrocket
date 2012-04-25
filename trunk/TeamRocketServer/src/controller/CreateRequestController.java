package controller;

import java.sql.Date;

import model.DLEvent;
import model.TeamRocketServerModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;

import server.ClientState;
import xml.Message;

/**
 * Gets the createRequest, processes it, and sends back the response.
 * 
 * @author rhollinger
 *
 */
public class CreateRequestController {
	ClientState state;
	
	public CreateRequestController(ClientState st) {
		state = st;
	}

	public Message process(Message request) {
		Node first = request.contents.getFirstChild();
		NamedNodeMap map = first.getAttributes();
		
		
		String numChoices = map.getNamedItem("numChoices").getNodeValue();
		String numRounds = map.getNamedItem("numRounds").getNodeValue();
		String eventQuestion = map.getNamedItem("question").getNodeValue();
		String eventType = map.getNamedItem("type").getNodeValue();
		Date dateCreated = request.getSentDate();
		String id = Manager.generateEventID();
		boolean isOpen = false;
		if (eventType.equals(new String("open"))) {
			isOpen = true;
		}
		String moderator = map.getNamedItem("name").getNodeValue();
		
		Manager.insertDLEvent(id, Integer.getInteger(numChoices), Integer.getInteger(numRounds), 
				eventQuestion, dateCreated, isOpen, moderator);
	    //TODO: parse out choice names and make them into DLChoice objects (in database)
		
		
		String xmlString = Message.responseHeader(request.id()) + "<createResponse id='" + id + "'/></response>";
		Message resp = new Message(xmlString);
		
		return resp;
	}
}
