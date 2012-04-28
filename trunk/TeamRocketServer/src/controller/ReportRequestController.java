package controller;

import java.sql.ResultSet;
import java.sql.SQLException;



import model.TeamRocketServerModel;
import model.Admin;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


import db.Manager;

import server.ClientState;
import xml.Message;

public class ReportRequestController {

	ClientState state;

	public ReportRequestController(ClientState st) {
		this.state = st;
	}

	public Message process(Message request) {
		Node signInR = request.contents.getFirstChild();
		
		
		NamedNodeMap adminAtts = signInR.getAttributes();
		String key = adminAtts.getNamedItem("key").getNodeValue();
		String eventType = adminAtts.getNamedItem("type").getNodeValue() ;

		
		Admin a = TeamRocketServerModel.getInstance().getAdmin() ;
		String xmlString="";
		Message response;
		ResultSet m;

		if (!a.verify(key)){
			xmlString = Message.responseHeader(request.id(), "Invalid key") ;
			response = new Message(xmlString);
		}
		else {
			if (eventType.equals("open")){
				m = Manager.retrieveEvent(true) ;
				try {
					//The meaning of id???
					xmlString =  Message.responseHeader(request.id()) + "<reportResponse>";
					while(m.next()){
						xmlString = xmlString +
								"<entry>"+
								"id='" + m.getString("id") + "' " +
								"type = 'open' " + 
								"question = '" + m.getString("eventQuestion") + "' " +
								"numChoices = '" + m.getInt("numChoices") + "' " +
								"numRounds = '" + m.getInt("numRounds") + "' " + 
								"created = '" + m.getDate("dateCreated") + "'" +
								"completed = '" + m.getBoolean("isComplete") + "'" + //  + "</reportRequestResponse></response>";
								"</entry>";

					}
				}catch (SQLException e) {
					e.printStackTrace();
				}

				xmlString = xmlString + "</reportResponse></response>";
				response = new Message(xmlString);
			}
			
			else {
				m = Manager.retrieveEvent(false) ;
				try {
					//The meaning of id???
					xmlString =  Message.responseHeader(request.id()) + "<reportResponse>";
					while(m.next()){
						xmlString = xmlString +
								"<entry "+
								"id='" + m.getString("id") + "' " +
								"type = 'open' " + 
								"question = '" + m.getString("eventQuestion") + "' " +
								"numChoices = '" + m.getInt("numChoices") + "' " +
								"numRounds = '" + m.getInt("numRounds") + "' " + 
								"created = '" + m.getDate("dateCreated") + "' " +
								"completed = '" + m.getBoolean("isComplete") + "' " + //  + "</reportRequestResponse></response>";
								"/>";
						//Message response = new Message(xmlString);

					}
				}catch (SQLException e) {
					e.printStackTrace();
				}

				xmlString = xmlString + "</reportResponse></response>";
				response = new Message(xmlString);
			}
		}
		return response ;
	}
}
