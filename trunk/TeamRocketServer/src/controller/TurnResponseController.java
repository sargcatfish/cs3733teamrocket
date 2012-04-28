package controller;

import model.DLChoice;
import model.DLEvent;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;
import server.ClientState;
import xml.Message;
import model.TeamRocketServerModel;

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
		int numEdges = event.getNumChoices() * event.getNumRounds();
		if (size == numEdges){
			xml = Message.responseHeader(id) + "<turnResponse completed=\"true\" /></response>";
		}
		else{
			xml = Message.responseHeader(id) + "<turnResponse/></response>";
		}
		
		
		Message response = new Message(xml);
		event.incrementCurrentMaster();
		if(event.getClientState()!= null)
		event.getClientState().sendMessage(response);

		return response;
	}
}