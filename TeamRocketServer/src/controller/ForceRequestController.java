package controller;

import model.Admin;
import model.TeamRocketServerModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import xml.Message;

public class ForceRequestController {

	public Message process(Message request) {
		int result ;
		Message response ;
		Node signInR = request.contents.getFirstChild();

		// retrieve ID		
		NamedNodeMap adminAtts = signInR.getFirstChild().getAttributes();
		String key = adminAtts.getNamedItem("key").getNodeValue();

		// get meeting object -- have user sign in!
		Admin a = TeamRocketServerModel.getInstance().getAdmin() ;
		String xmlString = "";

		if (!a.verify(key)){
			xmlString = Message.responseHeader(request.id(), "Invalid key") ;
			response = new Message(xmlString);
		}
		else{
			// ForceRequesetController
			//Force completion
			Node iNode = adminAtts.getNamedItem("id");
			String id = null;
			if (iNode != null) {
				id = iNode.getNodeValue();
				result = TeamRocketServerModel.forceCompleteEvent(id) ;
				xmlString =  Message.responseHeader(request.id()) + "<forceResponse numberAffected='" + result + "' " + "></forceResponse></response>";
				response = new Message(xmlString);
			}
			else {
				String daysOld = adminAtts.getNamedItem("daysOld").getNodeValue();
				int i = Integer.parseInt(daysOld) ;
				result = TeamRocketServerModel.forceCompleteEvent(i) ;
				xmlString =  Message.responseHeader(request.id()) + "<forceResponse numberAffected='" + result + "' " + "></forceResponse></response>";
				response = new Message(xmlString);
			}
		}

		return response ;
	}
}
