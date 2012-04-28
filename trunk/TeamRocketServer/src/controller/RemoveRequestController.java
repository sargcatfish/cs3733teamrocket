package controller;

import java.sql.SQLException;
import java.util.Hashtable;

import model.Admin;
import model.DLEvent;
import model.TeamRocketServerModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import server.ClientState;

import xml.Message;

public class RemoveRequestController {
	ClientState state;
	
	public RemoveRequestController(ClientState st){
		state = st;
	}
	public Message process(Message request) {
	int result ;
	Message response ;
	Node signInR = request.contents.getFirstChild();
	
	// retrieve ID		
	NamedNodeMap adminAtts = signInR.getAttributes();
	String key = adminAtts.getNamedItem("key").getNodeValue();
	String xmlString;
	
	// get meeting object -- have user sign in!
	Admin a = TeamRocketServerModel.getInstance().getAdmin() ;
	
	
	if (!a.verify(key)){
		xmlString = Message.responseHeader(request.id(), "Invalid key") + "<removeResponse numberAffected='0' /></response>" ;
		response = new Message(xmlString);
	}
	else{
		Node iNode = adminAtts.getNamedItem("id");
		String id = null;
		if (adminAtts.getNamedItem("daysOld") == null) {
			id = iNode.getNodeValue();
			System.out.print("here1\n");
			result = TeamRocketServerModel.destroyEvent(id);
			
			xmlString =  Message.responseHeader(request.id()) + "<removeResponse numberAffected='" + result + "' " + " /></response>";
			response = new Message(xmlString);
		}
		else {
			String completed = adminAtts.getNamedItem("completed").getNodeValue();
			String daysOld = adminAtts.getNamedItem("daysOld").getNodeValue();
			int i = Integer.parseInt(daysOld) ;
			result = TeamRocketServerModel.destroyEvent(completed, i) ;
			xmlString =  Message.responseHeader(request.id()) + "<removeResponse numberAffected='" + result + "' " + " /></response>";
			response = new Message(xmlString);
		}
	}
	
	return response ;
	}
}
