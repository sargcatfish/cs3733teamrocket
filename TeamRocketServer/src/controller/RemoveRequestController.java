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
 
/**
 * Controller for administrator to remove an event
 * @author Ian Lukens, Wesley Nitinthorn
 *
 */
public class RemoveRequestController {
	/** Client state: used to get the client id and other information identifying the individual client	 */
	ClientState state;
	
	/** 
	 * Constructor for RemoveRequestController
	 * @param st ClientState to be set
	 */
	public RemoveRequestController(ClientState st){
		state = st;
	}
	
	/**
	 * Processing function to parse the request and generate the response 
	 * @param request The request from the client to respond to
	 * @return The generated response
	 */
	public Message process(Message request) {
	int result ;
	Message response ;
	Node signInR = request.contents.getFirstChild();
	
	/** retrieve ID	 */
	NamedNodeMap adminAtts = signInR.getAttributes();
	String key = adminAtts.getNamedItem("key").getNodeValue();
	String xmlString;
	
	/** get meeting object -- have user sign in! */
	Admin a = TeamRocketServerModel.getInstance().getAdmin() ;
	
	
	if (!a.verify(key)){
		xmlString = Message.responseHeader(request.id(), "Invalid key") + "<removeResponse numberAffected=\"0\" /></response>" ;
		response = new Message(xmlString);
	}
	else{
		Node iNode = adminAtts.getNamedItem("id");
		String id = null;
		if (adminAtts.getNamedItem("daysOld") == null) {
			id = iNode.getNodeValue();
			result = TeamRocketServerModel.destroyEvent(id);
			
			xmlString =  Message.responseHeader(request.id()) + "<removeResponse numberAffected=\"" + result + "\" " + " /></response>";
			response = new Message(xmlString);
		}
		else {
			String completed = adminAtts.getNamedItem("completed").getNodeValue();
			String daysOld = adminAtts.getNamedItem("daysOld").getNodeValue();
			int i = Integer.parseInt(daysOld) ;
			result = TeamRocketServerModel.destroyEvent(completed, i) ;
			xmlString =  Message.responseHeader(request.id()) + "<removeResponse numberAffected=\"" + result + "\" " + " /></response>";
			response = new Message(xmlString);
		}
	}
	
	return response ;
	}
}
