package controller;

import java.util.Iterator;

import model.DLChoice;
import model.DLEvent;
import model.MockClient;
import model.TeamRocketServerModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;
import server.ClientState;
import server.Server;
import xml.Message;
/**
 * Controller for adding choices
 * @author Timothy Kolek, Nick Bosowski, Wesley Nitinthorn
 *
 */

public class AddChoiceController {
	/** Client state: used to get the client id and other information identifying the individual client	 */
	ClientState state;
	/** Model containing all of the information for events users, edges and choices locally  */
	TeamRocketServerModel model;
	
	/**
	 * Constructor for the AddChoiceController
	 * @param cs ClientState to be set
	 */
	public AddChoiceController(ClientState cs){
		this.state = cs;
		this.model = TeamRocketServerModel.getInstance();
	}
	
	/**
	 * Processing function to parse the request and generate the response 
	 * @param request The request from the client to respond to
	 * @return The generated response
	 */
	public Message process(Message request){
		Node first = request.contents.getFirstChild();
		NamedNodeMap map = first.getAttributes();
		String id = map.getNamedItem("id").getNodeValue();
		String number = map.getNamedItem("number").getNodeValue();
		String choice = map.getNamedItem("choice").getNodeValue();
		
		
		
		int choiceNum = Integer.parseInt(number);
		
		/** add choice to local*/
		DLChoice dlc = new DLChoice(choiceNum, choice); 
		
		DLEvent temp = model.getEvent(id);
		if (temp == null){
			String xml = Message.responseHeader(request.id(), "No event") + "<addChoiceResponse id=\"" + id + "\" number=\"0\" choice='" + choice + "'/></response>" ;
			Message response = new Message(xml) ;
			return response ;
		}
		Manager.insertChoice(id, choiceNum, choice);
		temp.addDLChoice(dlc);
		String xml = Message.responseHeader(request.id()) + "<addChoiceResponse id=\"" + id + "\" number=\"" + number + "\" choice=\"" + choice + "\"/></response>";
		Message response = new Message(xml);
		
		int choicesAdded = temp.getDLChoice().size();
		int needed = temp.getNumChoices();
		Iterator<ClientState> cs = TeamRocketServerModel.getInstance().getEvent(id).getStates().iterator();
		while(cs.hasNext()){
			ClientState next = cs.next();
			if(next != null){
				if(!next.id().equals(state.id()) ||(choicesAdded == needed && temp.getIsOpen()))
					next.sendMessage(response);	
			}
		}
		if(choicesAdded == needed && !(state instanceof MockClient)){
			new TurnResponseController().process(id);
		}
		return response;
	}

}
