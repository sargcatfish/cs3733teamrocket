package model;


import java.sql.SQLException;
import java.util.Hashtable;

import db.Manager;
/** Singleton model with a table of the locally stored event
 * 
 * @author Wesley
 *
 */
public class TeamRocketServerModel {
	
	static TeamRocketServerModel instance = null;
	
	private Hashtable<String, DLEvent> table = new Hashtable<String, DLEvent>();
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
				d.getIsOpen(), d.isAccepting(), d.getModerator())) {
			System.err.println("FAIL TO INSERT IN DB");
			return "";
		}
		else{
			return id;
		}
	}
	
	/**
	 * Function for testing purposes
	 * @param d DLEvent to add only to the internal HashTable
	 */
	public void addTestDLEvent(DLEvent d){
		table.put(d.getID(), d);	// add DLEvent to table
	}
	
	public Admin getAdmin(){
		return this.admin;
	}
	
	public Hashtable<String, DLEvent> getTable(){
		return this.table;
	}
	
	/***
	 * changes completion status of event with given id
	 * @param id
	 * @return int, the number of affected events
	 */
	public static int forceCompleteEvent(String id){
		
		getInstance().getTable().get(id).setComplete() ;
		if (Manager.setCompletion(id)){
			return 1;
		}
		else return 0;
	}
	
	/**
	 * changes completion status of events older than given days
	 * @param daysOld
	 * @return int, number of affected events
	 */
	public static int forceCompleteEvent(int daysOld){
		
		return Manager.setCompletion(daysOld) ;
	}
	
	public static int destroyEvent(String id){
		getInstance().getTable().remove(id);
		if (Manager.deleteEvent(id)){
			return 1;
		}
		else return 0;
	}
	//TODO make it work
	public static int destroyEvent(String isComplete, int daysOld) throws SQLException{
		boolean a ;
		//getInstance().getTable().remove(id);
		if (isComplete.equals(true)){
			a = true ;
		}
		else a = false ;
		
		return Manager.deleteEvents(a, daysOld) ;
	}

}
