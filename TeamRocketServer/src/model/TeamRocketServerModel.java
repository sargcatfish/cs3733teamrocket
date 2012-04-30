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
	//	String id = Manager.generateEventID(); //generate ID for the event
		table.put(d.getID(), d);	// add DLEvent to table //Nick 4/28/12
		if (!Manager.insertDLEvent(d.getID(), d.getNumChoices(), d.getNumEdges(), d.getEventQuestion(),
				d.getIsOpen(), d.isAccepting(), d.getModerator())) {
			System.err.println("FAIL TO INSERT IN DB");
			return "";
		}
		else{
			return d.getID();
		}
	}
	
	// ian
	/**
	 * Gets an event either from the database or table if present, adds to the table if not
	 * @param id
	 * @return the event w/ specified id
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
	 * Function for testing purposes
	 * @param d DLEvent to add only to the internal HashTable
	 */
	public void addTestDLEvent(DLEvent d){
		table.put(d.getID(), d);	// add DLEvent to table
	}
	
	public Admin getAdmin(){
		if (this.admin == null){
			return admin = new Admin();
		}
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
		
	if (getInstance().getTable().containsKey(id)){
		getInstance().getTable().get(id).setComplete() ;
	}
		return (Manager.setCompletion(id)) ;
	}
	
	/**
	 * changes completion status of events older than given days
	 * @param daysOld
	 * @return int, number of affected events
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
	 * destroys single event
	 * @author ian
	 * @param id
	 * @return
	 */
	public static int destroyEvent(String id){
		getInstance().getTable().remove(id);
		if (Manager.deleteEvent(id)){
			return 1;
		}
		else return 0;
	}

	/**
	 * destroys specified event
	 * @author ian, wesley
	 * @param isComplete
	 * @param daysOld
	 * @return number affected
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
