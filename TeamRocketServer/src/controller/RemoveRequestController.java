package teamRocket.controller;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import xml.Message;

public class RemoveRequestController {
	
	public Message process(Message request) {
	int result ;
	Message response ;
	Node signInR = request.contents.getFirstChild();
	
	// retrieve ID		
	NamedNodeMap adminAtts = signInR.getFirstChild().getAttributes();
	String key = adminAtts.getNamedItem("key").getNodeValue();
	
	// get meeting object -- have user sign in!
	a = ServerModel.getInstance().getAdmin() ;
	
	if (!a.verify(key)){
		String xmlString = Message.responseHeader(request.id(), "Invalid key") ;
		response = new Message(xmlString);
	}
	else{
		Node iNode = adminAtts.getNamedItem("id");
		String id = null;
		if (iNode != null) {
			id = iNode.getNodeValue();
			result = ServerModel.destroyEvent(id) ;
			String xmlString =  Message.responseHeader(request.id()) + "<removeResponse numberAffected='" + result + "' " + "</removeResponse></response>";
			response = new Message(xmlString);
		}
		else {
			String completed = adminAtts.getNamedItem("completed").getNodeValue();
			String daysOld = adminAtts.getNamedItem("daysOld").getNodeValue();
			// :: TODO remove all these events!
				// destroyEvent returns an int!
			result = ServerModel.destroyEvent(completed, daysOld) ;
			String xmlString =  Message.responseHeader(request.id()) + "<removeResponse numberAffected='" + result + "' " + "</removeResponse></response>";
			response = new Message(xmlString);
		}
	}
	
	return response ;
	}
}
