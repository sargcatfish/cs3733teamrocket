package controller;

import model.DLChoice;
import model.TeamRocketServerModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;
import server.ClientState;
import xml.Message;
/**
 * 
 * @author Timothy Kolek
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
		DLChoice dlc = new DLChoice(choiceNum, choice); 
		model.getTable().get(request.id()).addDLChoice(dlc);

		String xml = Message.responseHeader(request.id()) + "<addChoiceResponse id='" + id + "' number='" + number + "' choice='" + choice + "'/></response>";
		Message response = new Message(xml);
		
		return response;
	}

}