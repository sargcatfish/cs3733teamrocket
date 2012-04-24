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
	public String addDLEvent(DLEvent d){
		String id = Manager.generateMeetingID(); //generate ID for the event
		table.put(id, d);	// add DLEvent to table
		if (!Manager.insertDLEvent(id, d)) {
			System.err.println("FAIL TO INSERT IN DB");
		}
		return id;
	}

}
