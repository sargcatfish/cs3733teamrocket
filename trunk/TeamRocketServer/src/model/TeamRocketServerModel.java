package model;


import java.util.Hashtable;

import db.Manager;
/** Singleton model with a table of the locally stored event
 * 
 * @author Wesley
 *
 */
public class TeamRocketServerModel {
	
	static TeamRocketServerModel instance;
	
	Hashtable<String, DLEvent> table = new Hashtable<String, DLEvent>();
	
	public TeamRocketServerModel(){}
	
	public static TeamRocketServerModel getInstance() {
		if (instance == null) {
			instance = new TeamRocketServerModel();
		}
		
		return instance;
	}
	
	/*You can create an event first as try to add or you can do this messy way pick?*/
	public String addDLEvent(String name, String question, int numChoices, int numRounds){
		String id = Manager.generateMeetingID(); //generate ID for the event
		DLEvent newEvent = null;
		if (!Manager.insertDLEvent(name, question, numChoices, numRounds)) {
			System.err.println("FAIL TO INSERT");
		}
		
		else {
			newEvent = new DLEvent(id, name, question, numChoices, numRounds);
			table.put(id, newEvent);
		}
		return id;
	}

}
