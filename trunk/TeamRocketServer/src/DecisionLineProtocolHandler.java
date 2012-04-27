import java.sql.SQLException;

import org.w3c.dom.Node;

import server.ClientState;
import server.IProtocolHandler;
import server.Server;

import xml.*;
import controller.*;

/**
 * @author Nick Bosowski
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
	/*
	 * TODO:
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
			try {
				return removeController.process(request);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} else if(localName.equals("reportRequest")){
			// More message handling
			System.out.println("Trying to make report.\n");
			reportController = new ReportRequestController(st);
			return	reportController.process(request);
			
			
		} else if(localName.equals("signInRequest")){
			// More message handling
			System.out.println("Trying to sign in.\n");
			signInController = new SignInRequestController(st);
			return signInController.process(request);
			
		} 

		// unknown? no idea what to do
		System.err.println("Unable to handle message:" + request);
		return null;
	} 
}
