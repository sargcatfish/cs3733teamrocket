package controller;

import model.DLChoice;
import model.DLEvent;
import model.TeamRocketServerModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;
import server.ClientState;
import server.Server;
import xml.Message;
/**
 * 
 * @author Timothy Kolek, Nick Bosowski, Wesley Nitinthorn
 *
 */

public class AddChoiceController {
	ClientState state;
	TeamRocketServerModel model;
	
	public AddChoiceController(ClientState cs){
		this.state = cs;
		this.model = TeamRocketServerModel.getInstance();
	}
	
	public Message process(Message request){
		Node first = request.contents.getFirstChild();
		NamedNodeMap map = first.getAttributes();
		String id = map.getNamedItem("id").getNodeValue();
		String number = map.getNamedItem("number").getNodeValue();
		String choice = map.getNamedItem("choice").getNodeValue();
		
		
		
		int choiceNum = Integer.parseInt(number);
		
		Manager.insertChoice(id, choiceNum, choice);
		
		//add choice to local
		//@Wesley I dont think we add a new event to local so just doing a check here
		//if not local then go to DB
		DLChoice dlc = new DLChoice(choiceNum, choice); 
		
		// Ian
		// I made I function in the model to do this.  GetInstance already makes a model, don't know why you're making another
		// also error handling for if it doesn't exist
		DLEvent temp = model.getEvent(id);
		if (temp == null){
			String xml = Message.responseHeader(request.id(), "No event") + "<addChoiceResponse id='" + id + "' number='0' choice='0'/></response>" ;
			Message response = new Message(xml) ;
			return response ;
		}
		temp.addDLChoice(dlc);
		String xml = Message.responseHeader(request.id()) + "<addChoiceResponse id='" + id + "' number='" + number + "' choice='" + choice + "'/></response>";
		Message response = new Message(xml);
		
		/* This supposedly sends to all the clients */
		for (String threadID : Server.ids()) {
			ClientState cs = Server.getState(threadID);
			if (id.equals(cs.getData())) {
				// make sure not to send to requesting client TWICE
				if (!cs.id().equals(state.id())) {
					cs.sendMessage(response);
				}
			}
		}	
		
		return response;
	}

}
