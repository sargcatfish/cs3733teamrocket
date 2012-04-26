import org.w3c.dom.Node;

import server.*;
import xml.*;
import controller.*;

/**
 * Sample implementation of a protocol handler to respond to messages received from clients.
 * You should follow this template when designing YOUR protocol handler.
 */
public class DecisionLineProtocolHandler implements IProtocolHandler {

	/** Protocol handler knows the server in question. */
	Server server;
	AddChoiceController choiceController;
	AddEdgeController  edgeController;
	AdminSignInRequestController adminController;
	CloseRequestController closeController;
	CreateRequestController createController;
	ForceRequestController forceController;
	RemoveRequestController removeController;
	ReportRequestController reportController;
	SignInRequestController signInController;
	
	

	/** Associate a server object with this handler. */
	public void setServer (Server s) {
		this.server = s;
	}

	public synchronized Message process (ClientState st, Message request) {
		Node child = request.contents.getFirstChild();
		String localName = child.getLocalName();
		
		if (localName.equals ("connectRequest")) {
			System.out.println("Connected");
			
		} else if(localName.equals("addChoiceRequest")){
			// More message handling
			choiceController = new AddChoiceController(st);
			choiceController.process(request);
			System.out.println("Trying to add Choice.\n");
			
		} else if(localName.equals("addEdgeRequest")){
			// More message handling
			edgeController = new AddEdgeController(st);
			edgeController.process(request);
			System.out.println("Trying to add Edge.\n");
			
		} else if(localName.equals("adminRequest")){
			// More message handling
			adminController = new AdminSignInRequestController(st);
			adminController.process(request);
			System.out.println("Trying to process adminRequest.\n");
			
		} else if(localName.equals("closeRequest")){
			// More message handling
			closeController = new CloseRequestController(st);
			closeController.process(request);
			System.out.println("Trying to process closeRequest.\n");
			
		} else if(localName.equals("createRequest")){
			// More message handling
			createController = new CreateRequestController(st);
			createController.process(request);
			System.out.println("Trying to process createRequest.\n");
			
		} else if(localName.equals("forceRequest")){
			// More message handling
			forceController = new ForceRequestController(st);
			forceController.process(request);
			System.out.println("Trying process forceRequest.\n");
			
		} else if(localName.equals("removeRequest")){
			// More message handling
			removeController = new RemoveRequestController(st);
			removeController.process(request);
			System.out.println("Trying to remove.\n");
			
		} else if(localName.equals("reportRequest")){
			// More message handling
			reportController = new ReportRequestController(st);
			reportController.process(request);
			System.out.println("Trying to make report.\n");
			
		} else if(localName.equals("signInRequest")){
			// More message handling
			signInController = new SignInRequestController(st);
			signInController.process(request);
			System.out.println("Trying to sign in.\n");
		}

		// unknown? no idea what to do
		System.err.println("Unable to handle message:" + request);
		return null;
	} 
}
