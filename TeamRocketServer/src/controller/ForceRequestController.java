package controller;

import java.sql.SQLException;

import model.Admin;
import model.TeamRocketServerModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import server.ClientState;

import xml.Message;

/**
 * Controller to force an event to close
 * @author Ian Lukens, Wesley Nitinthorn
 *
 */
public class ForceRequestController {
	/** Client state: used to get the client id and other information identifying the individual client	 */
	ClientState state;
	
	/**
	 * Constructor for ForceRequestController
	 * @param st ClientState to be set
	 */
	public ForceRequestController(ClientState st){
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

		/** retrieve ID*/
		NamedNodeMap adminAtts = signInR.getAttributes();
		String key = adminAtts.getNamedItem("key").getNodeValue();

		/** get meeting object -- have user sign in!*/
		Admin a = TeamRocketServerModel.getInstance().getAdmin() ;
		String xmlString = "";

		if (!a.verify(key)){
			xmlString = Message.responseHeader(request.id(), "Invalid key") + "<forceResponse numberAffected=\"0\"/></response>";
			response = new Message(xmlString);
		}
		else{
			Node iNode = adminAtts.getNamedItem("id");
			String id = null;
			if (iNode != null) {
				id = iNode.getNodeValue();
				result = TeamRocketServerModel.forceCompleteEvent(id) ;
				xmlString =  Message.responseHeader(request.id()) + "<forceResponse numberAffected=\"" + result + "\" " + "></forceResponse></response>";
				response = new Message(xmlString);
			}
			else {
				String daysOld = adminAtts.getNamedItem("daysOld").getNodeValue();
				int i = Integer.parseInt(daysOld) ;
				result = TeamRocketServerModel.forceCompleteEvent(i) ;
				xmlString =  Message.responseHeader(request.id()) + "<forceResponse numberAffected=\"" + result + "\" " + " /></response>";
				response = new Message(xmlString);
			}
		}

		return response ;
	}
}
