package controller;

import java.util.Iterator;

import model.DLEvent;
import model.TeamRocketServerModel;
import server.ClientState;
import xml.Message;
import db.Manager;


/***
 * Controller to force an early close as administrator, sends completed message to all users
 * @author Ian Lukens
 *
 */
public class AdminTurnResponseController {
	/** Model containing all of the information for events users, edges and choices locally  */
	TeamRocketServerModel model = TeamRocketServerModel.getInstance();

	/**
	 * Processing function to use the event ID to generate the response 
	 * @param id The ID of the event to be closed by the administrator
	 * @return Generated response to all users
	 */
	public Message process(String id){

		Message response;
		DLEvent event = model.getEvent(id);
		if (event == null){
			System.out.println("Event does not exist");
		}
		String xml;

			xml = Message.responseHeader(id) + "<turnResponse completed=\"true\" /></response>";
			response = new Message(xml);
			
			Iterator<ClientState> cs = TeamRocketServerModel.getInstance().getEvent(id).getStates().iterator();
			while(cs.hasNext()){
				ClientState next = cs.next();
				if(next != null){
						next.sendMessage(response);	
				}
			}
		return response;
	}
}