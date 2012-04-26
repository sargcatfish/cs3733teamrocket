package controller;

import model.DLChoice;

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
		
		if (model.getTable().get(id) == null){
			System.out.println("Im a stupid cuntbag!!!");
		}
		String xml;
		int size;
		if (model.getTable().get(id).getEdgeList() == null){
			size = 0;
		}
		else{
			size = model.getTable().get(id).getEdgeList().size();
		}
		int numEdges = model.getTable().get(id).getNumChoices() * model.getTable().get(id).getNumRounds();
		if (size == numEdges){
			xml = Message.responseHeader(id) + "<turnResponse completed='true' /></response>";
		}
		else{
			xml = Message.responseHeader(id) + "<turnResponse/></response>";
		}
		
		Message response = new Message(xml);
		
		return response;
	}
}