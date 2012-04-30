package controller;

import java.util.Iterator;

import model.DLChoice;
import model.DLEvent;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;
import server.ClientState;
import xml.Message;
import model.TeamRocketServerModel;

/**
 * Gets the signInRequest, processes it, and sends back the response.
 * 
 * @author Timothy Kolek, Nick Bosowski
 *
 */

public class TurnResponseController {
	int nextPosition;
	TeamRocketServerModel model = TeamRocketServerModel.getInstance(); // retrieve the singleton


	public Message process(String id){
		//int master = model.getTable().get(id).getCurrentMaster();
		/* This isnt done but i need to go now need to figure out how to retrieve the proper client state, keep a list of them in the event,
		 * or do that in the protocol handler?
		 * 
		 * probably in the protocol handler. all thats in here is deciding which message it should send. 
		 * The one saying the event is completed because the number of edges has been reached or
		 * just for the next person. But we will probably also have to check in the protocol handler to send 
		 * it to the right people. Because the completion is sent to everyone right?
		 */
		Message response;
		DLEvent event = model.getEvent(id);
		if (event == null){
			System.out.println("Event does not exist");
		}
		String xml;
		int size = event.getNumEdges();
//		if (event.getEdgeList() == null){
//			size = 0;
//		}
//		else{
//			size = event.getEdgeList().size();
//		}
		int numEdges = event.getUserList().size() * event.getNumRounds();
		if (size == numEdges){
			xml = Message.responseHeader(id) + "<turnResponse completed=\"true\" /></response>";
			event.setIsComplete(true);
			Manager.setCompletion(id);
			response = new Message(xml);
			
			Iterator<ClientState> cs = TeamRocketServerModel.getInstance().getEvent(id).getStates().iterator();
			while(cs.hasNext()){
				ClientState next = cs.next();
				if(next != null){
						next.sendMessage(response);	
				}
			}
			// TODO: set completed in database and local
		}
		else{
			xml = Message.responseHeader(id) + "<turnResponse/></response>";		
			response = new Message(xml);
			if(event.getClientState()!= null){
				event.getClientState().sendMessage(response);
				System.out.print("sent to" + event.getClientState().id() +"\n");
				event.incrementCurrentMaster();
			}
		}

		return response;
	}
}