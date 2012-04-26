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
	Admin admin = new Admin();
	
	public TeamRocketServerModel(){}
	
	public static TeamRocketServerModel getInstance() {
		if (instance == null) {
			instance = new TeamRocketServerModel();
		}
		
		return instance;
	}
	/*adding this comment so it recommits the proper file! */
	/*You can create an event first as try to add or you can do this messy way pick?*/
	public String addDLEvent(DLEvent d){
		String id = Manager.generateEventID(); //generate ID for the event
		table.put(id, d);	// add DLEvent to table
		if (!Manager.insertDLEvent(d.getID(), d.getNumChoices(), d.getNumEdges(), d.getEventQuestion(),
				d.getDateCreated(), d.getIsOpen(), d.isAccepting(), d.getModerator())) {
			System.err.println("FAIL TO INSERT IN DB");
			return "";
		}
		else{
			return id;
		}
	}
	
	public Admin getAdmin(){
		return this.admin;
	}
	
	public Hashtable<String, DLEvent> getTable(){
		return this.table;
	}
	//TODO make it iterate through database and remove the one with the given year
	public static int forceCompleteEvent(String id){
		
		if (getInstance().getTable().get(id).setComplete() && Manager.setCompletion(id)){
			return 1;
		}
		else return 0;
	}
	
	public static int destroyEvent(String id){
		getInstance().getTable().remove(id);
		if (Manager.deleteEvent(id)){
			return 1;
		}
		else return 0;
	}
	//TODO make it work
	public static int destroyEvent(String isComplete, String daysOld){
		//getInstance().getTable().remove(id);
		if (Manager.deleteEvent("")){
			return 1;
		}
		else return 0;
	}

}
