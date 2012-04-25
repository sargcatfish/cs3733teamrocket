package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import model.DLEvent;
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

		// retrieve ID		
		NamedNodeMap adminAtts = signInR.getFirstChild().getAttributes();
		String key = adminAtts.getNamedItem("key").getNodeValue();
		String eventType = adminAtts.getNamedItem("type").getNodeValue() ;

		// get meeting object -- have user sign in!
		Admin a = TeamRocketServerModel.getInstance().getAdmin() ;
		String xmlString="";
		Message response = new Message(Message.responseHeader(request.id(), "Something went horribly wrong on the server or maybe there is no open of type"));
		ResultSet m;

		if (!a.verify(key)){
			xmlString = Message.responseHeader(request.id(), "Invalid key") ;
			response = new Message(xmlString);
		}
		else {
			if (eventType.equals("open")){
				//:: TODO generate report of all open events
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
						//Message response = new Message(xmlString);

					}
				}catch (SQLException e) {
					e.printStackTrace();
				}

				//:: TODO generate report of all closed events
				xmlString = xmlString + "</reportRequestResponse></response>";
				response = new Message(xmlString);
			}
		}
		return response ;
	}
}
