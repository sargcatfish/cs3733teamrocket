package controller;

import server.ClientState;
import xml.Message;

import model.Edge;
import model.TeamRocketServerModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;

/**
 * 
 * @author Timothy Kolek
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
		model.getTable().get(request.id()).addEdge(edge);
		
		String xml = Message.responseHeader(request.id()) + "<addEdgeResponse id='" + id + "' left='" + left + "' right='" + right+ "' height='" + height + "'/></response>";
		Message response = new Message(xml);
		
		new TurnResponseController().process(id);
		return response;
	}

}
