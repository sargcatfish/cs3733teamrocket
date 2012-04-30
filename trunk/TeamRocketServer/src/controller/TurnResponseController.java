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
 * Controller to determine which users turn it is, sends only to the current turn user
 * 
 * @author Timothy Kolek, Nick Bosowski
 *
 */

public class TurnResponseController {
	int nextPosition;
	TeamRocketServerModel model = TeamRocketServerModel.getInstance(); // retrieve the singleton


	/**
	 * Processing function to parse the request and generate the response
	 * @param id The id of the event
	 * @return The generated response
	 */
	public Message process(String id){
		
		Message response;
		DLEvent event = model.getEvent(id);
		if (event == null){
			System.out.println("Event does not exist");
		}
		String xml;
		int size = event.getNumEdges();
		
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