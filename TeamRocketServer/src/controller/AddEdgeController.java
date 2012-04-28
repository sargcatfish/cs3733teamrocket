package controller;

import server.ClientState;
import server.Server;
import xml.Message;

import model.DLEvent;
import model.Edge;
import model.TeamRocketServerModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;

/**
 * 
 * @author Timothy Kolek, Nick Bosowski
 *
 */

public class AddEdgeController {
	ClientState state;
	TeamRocketServerModel model;
	
	public AddEdgeController(ClientState cs){
		this.state = cs;
		this.model = TeamRocketServerModel.getInstance();
	}
	
	public Message process(Message request){
		
		Node first = request.contents.getFirstChild();
		NamedNodeMap map = first.getAttributes();
		String id = map.getNamedItem("id").getNodeValue();
		String left = map.getNamedItem("left").getNodeValue();
		String right = map.getNamedItem("right").getNodeValue();
		String height = map.getNamedItem("height").getNodeValue();
		
		int leftNum = Integer.parseInt(left);
		int rightNum = Integer.parseInt(right);
		int heightNum = Integer.parseInt(height);
		
		Manager.insertEdge(id, leftNum, rightNum, heightNum);
		
		//add edge to local
		Edge edge = new Edge(leftNum, rightNum, heightNum);
		// ian
		// using new function
		// error handling if passed an invalid id
		DLEvent temp = model.getEvent(id) ;
		if (temp == null){
			String xml = Message.responseHeader(request.id(), "No event") + "<addEdgeResponse id='" + id + "' left='0' right='0' height='0'/></response>" ;
			Message response = new Message(xml) ;
			return response ;
		}
		else temp.addEdge(edge);
		
		String xml = Message.responseHeader(request.id()) + "<addEdgeResponse id='" + id + "' left='" + left + "' right='" + right+ "' height='" + height + "'/></response>";
		Message response = new Message(xml);
		
		/* This supposedly sends to all the clients */
		for (String threadID : Server.ids()) {
			ClientState cs = Server.getState(threadID);
			if (id.equals(cs.getData())) {
				// make sure not to send to requesting client TWICE
				if (!cs.id().equals(state.id())) {
					cs.sendMessage(response);
				}
			}
		}	
		new TurnResponseController().process(id);
		return response;
	}

}
