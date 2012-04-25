package controller;
/**
 * Gets the closeRequest, ensures that it is from a moderator, processes it, and sends back the response.
 * 
 * @author rhollinger
 *
 */
public class CloseRequestController {
	public CloseEventContoller(ClientState st) {
		// TODO Auto-generated constructor stub
	}

	public Message process(Message request) {
		String EventID = request.contents.getFirstChild().getAttributes().getNamedItem("id").getNodeValue();

		//TODO close Event
		
		String xmlString = Message.responseHeader(request.id()) + "<closeResponse/></response>";
		Message response = new Message(xmlString);
		return response;
	}
}