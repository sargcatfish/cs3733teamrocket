package controller;

import java.util.Iterator;

import model.DLEvent;
import model.TeamRocketServerModel;
import server.ClientState;
import xml.Message;
import db.Manager;


/***
 * force an early close
 * @author ian
 *
 */
public class AdminTurnResponseController {
	int nextPosition;
	TeamRocketServerModel model = TeamRocketServerModel.getInstance(); // retrieve the singleton

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