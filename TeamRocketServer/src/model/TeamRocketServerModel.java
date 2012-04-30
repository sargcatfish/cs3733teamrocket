package model;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import controller.AdminTurnResponseController;


import db.Manager;
/** Singleton model with a table of the locally stored event
 * 
 * @author Wesley, Ian
 *
 */
public class TeamRocketServerModel {
	/** Instance so there will only be one TeamRocketServerModel ever */
	static TeamRocketServerModel instance = null;
	/** Hashtable of the DLEvents  */
	private Hashtable<String, DLEvent> table = new Hashtable<String, DLEvent>();
	/** Creates the admin */
	Admin admin = new Admin();
	
	/**
	 * Gets the instance of the model
	 * @return the instance
	 */
	public static TeamRocketServerModel getInstance() {
		if (instance == null) {
			instance = new TeamRocketServerModel();
		}
		return instance;
	}
	
	
	/*You can create an event first as try to add or you can do this messy way pick?*/
	/**
	 * Add a DLEvent to the hashtable
	 * @param d the event to add
	 * @return
	 */
	public String addDLEvent(DLEvent d){
		/** add DLEvent to table */
		table.put(d.getID(), d);
		if (!Manager.insertDLEvent(d.getID(), d.getNumChoices(), d.getNumEdges(), d.getEventQuestion(),
				d.getIsOpen(), d.isAccepting(), d.getModerator())) {
			System.err.println("FAIL TO INSERT IN DB");
			return "";
		}
		else{
			return d.getID();
		}
	}
	
	/**
	 * Gets an event either from the database or table if present, adds to the table if not
	 * @param id id of the event to get
	 * @return the event w/ specified id if there is one, otherwise null
	 */
	public DLEvent getEvent(String id){
		DLEvent d = table.get(id);
		if (d != null) {
			return d;
		}
		
		d = Manager.retrieveEvent(id);
		if (d == null) {
			return null;
		}
		
		table.put(id, d);
		return d;
	}
	
	/**
	 * Function for testing purposes: adds DLEvent only to model
	 * @param d DLEvent to add only to the internal HashTable
	 */
	public void addTestDLEvent(DLEvent d){
		table.put(d.getID(), d);
	}
	
	/**
	 * Getter for the administrator
	 * @return the Admin object
	 */
	public Admin getAdmin(){
		if (this.admin == null){
			return admin = new Admin();
		}
		return this.admin;
	}
	
	/**
	 * Getter for the hash table
	 * @return The hash table
	 */
	public Hashtable<String, DLEvent> getTable(){
		return this.table;
	}
	
	/***
	 * Changes completion status of event with given id
	 * @param id string of the event id to force to complete
	 * @return int, the number of affected events
	 */
	public static int forceCompleteEvent(String id){
		
	if (getInstance().getTable().containsKey(id)){
		getInstance().getTable().get(id).setComplete() ;
	}
		return (Manager.setCompletion(id)) ;
	}
	
	/**
	 * Changes completion status of events older than given days
	 * @param daysOld given number of days old
	 * @return int, the number of affected events
	 * @throws SQLException 
	 */
	public static int forceCompleteEvent(int daysOld) {
		try{ ResultSet result = Manager.getEventsDays(daysOld) ;
		
		while(result.next()){
			AdminTurnResponseController turn = new AdminTurnResponseController() ;
			turn.process(result.getString("id")) ;
			if(getInstance().getTable().containsKey(result.getString("id"))){
			getInstance().getTable().get(result.getString("id")).setComplete();
			}
		}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		return Manager.setCompletion(daysOld) ;
	}
	
	/**
	 * Destroys single event with the given id
	 * @param id string id of the event to be deleted
	 * @return 1 if event is deleted, 0 otherwise
	 */
	public static int destroyEvent(String id){
		getInstance().getTable().remove(id);
		if (Manager.deleteEvent(id)){
			return 1;
		}
		else return 0;
	}

	
	/**
	 * Destroys specified event depending on if it is completed and how many days old
	 * @param isComplete string if the event is complete or not
	 * @param daysOld integer of the number of days old
	 * @return WHAT IS THIS??!!!
	 */
	public static int destroyEvent(String isComplete, int daysOld) {
		boolean a ;
		ResultSet result1;
		ResultSet result2;
		if (isComplete.equals("true")){
			a = true ;
		}
		else a = false ;
		result1 = Manager.getEventsDays(a, daysOld) ;
		result2 = Manager.getEventsDays(a, daysOld);
		
		try {
			while(result1.next()){
				if(getInstance().getTable().containsKey(result1.getString("id"))){
				getInstance().getTable().remove(result1.getString("id"));
				}
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		return Manager.deleteEvent(result2);
	}
}
