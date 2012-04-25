package controller;

import java.util.UUID;

import model.DLEvent;
import model.TeamRocketServerModel;
import model.Admin;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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
		Admin a;
		a = TeamRocketServerModel.getInstance().getAdmin() ;
		
		if (!a.verify(key)){
			String xmlString = Message.responseHeader(request.id(), "Invalid key") ;
			Message response = new Message(xmlString);
		}
		/*
	    <xs:attribute name='id'          type='xs:string'  use='required'/>
	    <xs:attribute name='type'        type='eventType'  use='required'/>
	    <xs:attribute name='question'    type='xs:string'  use='required'/>
	    <xs:attribute name='numChoices'  type='xs:integer' use='required'/>
	    <xs:attribute name='numRounds'   type='xs:integer' use='required'/>
	    <xs:attribute name='created'     type='xs:string'  use='required'/>
	    <xs:attribute name='completed'   type='xs:boolean' use='required'/>
	    */
		else {
			if (eventType.equals("open")){
				//:: TODO generate report of all open events
				DLEvent m;
				m = TeamRocketServerModel.getInstance().retriveEvent("open") ;

				String xmlString =  Message.responseHeader(request.id()) + "<reportResponse id='" + m.meetingID + "' " + 
					    "type = 'open' " + 
						"question = '" + m.question + "' " +
						"numChoices = '" + m.numChoices + "' " +
						"numRounds = '" + m.numRounds + "' " + 
						"created = '" + m.created + "'" +
						"completed = '" + m.completed + "'" ; //  + "</signInResponse></response>";
				Message response = new Message(xmlString);

			}
			else {
				//:: TODO generate report of all closed events
				Message response = new Message(xmlString);

			}
		}
		return response ;
	}
}
