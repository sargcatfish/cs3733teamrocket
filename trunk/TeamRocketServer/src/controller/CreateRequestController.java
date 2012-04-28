package controller;

import java.sql.Date;

import model.DLEvent;
import model.TeamRocketServerModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.servicetag.SystemEnvironment;

import db.Manager;

import server.ClientState;
import xml.Message;

/**
 * Gets the createRequest, processes it, and sends back the response.
 * 
 * @author rhollinger, Nick Bosowski, Wesley Nitinthorn
 *
 */
public class CreateRequestController {
	ClientState state;
	public String testId;
	
	public CreateRequestController(ClientState st) {
		state = st;
	}

	public Message process(Message request) {
		Node first = request.contents.getFirstChild();
		NamedNodeMap map = first.getAttributes();
		NodeList next = first.getChildNodes();
		
		//parse out all the event information
		String numChoices = map.getNamedItem("numChoices").getNodeValue();
		String numRounds = map.getNamedItem("numRounds").getNodeValue();
		String eventQuestion = map.getNamedItem("question").getNodeValue();
		String eventType = map.getNamedItem("type").getNodeValue();
		
		String id = Manager.generateEventID();
		testId = id;
		boolean isOpen = false;
		if (eventType.equals(new String("open"))) {
			isOpen = true;
		}
		//int numItems = next.getLength();
		//get moderator names
		String moderator = null;
		String pswd = "";
		for(int i = 0; i < next.getLength();i++){
			if(next.item(i).getLocalName().equals("user")){
			//	NodeList children = next.item(i).getChildNodes();
				NamedNodeMap child = next.item(i).getAttributes();

				moderator = child.getNamedItem("name").getNodeValue();
				if(child.getLength() > 1)
					pswd = child.getNamedItem("password").getNodeValue();
				else
					pswd = "";
				
			}
		
		}
		if(moderator == null){
			throw new IllegalArgumentException("No moderator was found");
		}
//		if (numItems - Integer.getInteger(numChoices) > 1){
//			moderator = next.item(numItems-2).getNodeValue();
//			pswd = next.item(numItems-1).getNodeValue();
//		}
//		else{
//			moderator = next.item(numItems-1).getNodeValue();
//			pswd = new String("");
//			}
		//add the event to the database

		Manager.insertDLEvent(id, Integer.parseInt(numChoices), Integer.parseInt(numRounds), 
				eventQuestion, isOpen, true, moderator);
		//get the event from database after adding it.
		DLEvent e = Manager.retrieveEvent(id); // create local event
		e.addClientState(state); // add moderator clientstate
		TeamRocketServerModel.getInstance().getTable().put(id, e); // add local event to table
		//get choice names	
		if(!isOpen){
			for (int i = 0; i < Integer.parseInt(numChoices); i++){
			NamedNodeMap child = next.item(i).getAttributes();
			int index = Integer.parseInt(child.getNamedItem("index").getNodeValue());
			String value = child.getNamedItem("value").getNodeValue();
				Manager.insertChoice(id, index, value);
			}
		}
		//Sign in the moderator
		Manager.signin(id, moderator, pswd, true, 0);
	
		
		String xmlString = Message.responseHeader(request.id()) + "<createResponse id='" + id + "'/></response>";
		Message resp = new Message(xmlString);
		return resp;
	}
}
