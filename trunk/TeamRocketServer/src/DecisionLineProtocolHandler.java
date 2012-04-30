import org.w3c.dom.Node;

import server.ClientState;
import server.IProtocolHandler;
import server.Server;

import xml.*;
import controller.*;

/**
 * Protocol Handler class: determines which controller gets called based on the mxl request
 * @author Nick Bosowski
 */
public class DecisionLineProtocolHandler implements IProtocolHandler {

	/** Protocol handler knows the server in question. */
	Server server;
	/** Controller for adding choices	 */
	AddChoiceController choiceController;
	/** Controller for adding edges	 */
	AddEdgeController  edgeController;
	/** Controller for the administrator to sign in */
	AdminSignInRequestController adminController;
	/** Controller for closing an event */
	CloseRequestController closeController;
	/** Controller for creating an event */
	CreateRequestController createController;
	/** Controller for  forcing an event to close */
	ForceRequestController forceController;
	/** Controller for the administrator to remove an event  */
	RemoveRequestController removeController;
	/** Controller for the administrator to generate a report */
	ReportRequestController reportController;
	/** Controller for a user to sign in */
	SignInRequestController signInController;
	
	

	/** Associate a server object with this handler. */
	public void setServer (Server s) {
		this.server = s;
	}
	
	/**
	 * to send to just one thing you do st.sendMessage of the return
	 * to send to multiple need to figure out how to determine which ids are part of the 
	 * event and those that arent, but you can get an iterator of the ids to retrieve each individual client
	 * state
	 */
	public synchronized Message process (ClientState st, Message request) {
		Node child = request.contents.getFirstChild();
		String localName = child.getLocalName();
		
		if (localName.equals ("connectRequest")) {
			System.out.println("Connected");
		} else if(localName.equals("addChoiceRequest")){
			// More message handling
			System.out.println("Trying to add Choice.\n");
			choiceController = new AddChoiceController(st);
			return choiceController.process(request);
			
			
		} else if(localName.equals("addEdgeRequest")){
			// More message handling
			System.out.println("Trying to add Edge.\n");
			edgeController = new AddEdgeController(st);
			return edgeController.process(request);
			
			
		} else if(localName.equals("adminRequest")){
			// More message handling
			System.out.println("Trying to process adminRequest.\n");
			adminController = new AdminSignInRequestController(st);
			return adminController.process(request);
			
			
		} else if(localName.equals("closeRequest")){
			// More message handling
			System.out.println("Trying to process closeRequest.\n");
			closeController = new CloseRequestController(st);
			return closeController.process(request);
			
			
		} else if(localName.equals("createRequest")){
			// More message handling
			System.out.println("Trying to process createRequest.\n");
			createController = new CreateRequestController(st);
			return createController.process(request);
			
			
		} else if(localName.equals("forceRequest")){
			// More message handling
			System.out.println("Trying process forceRequest.\n");
			forceController = new ForceRequestController(st);
			return forceController.process(request);
			
			
		} else if(localName.equals("removeRequest")){
			// More message handling
			System.out.println("Trying to remove.\n");
			removeController = new RemoveRequestController(st);				
			return removeController.process(request);
			
		} else if(localName.equals("reportRequest")){
			// More message handling
			System.out.println("Trying to make report.\n");
			reportController = new ReportRequestController(st);
			return	reportController.process(request);
			
			
		} else if(localName.equals("signInRequest")){
			// More message handling
			System.out.println("Trying to sign in.\n");
			System.out.println("state entered:" + st.id());
			signInController = new SignInRequestController(st);
			return signInController.process(request);
			
		} 

		/**
		 * If the message is not known, prints out an error message
		 */
		System.err.println("Unable to handle message:" + request);
		return null;
	} 
}
