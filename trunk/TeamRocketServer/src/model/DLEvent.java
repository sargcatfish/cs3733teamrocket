package model;

import java.util.ArrayList;
import java.sql.Date;

import server.ClientState;

/** Class that stores references to information for a decisionLine event
 * @author Wesley, gdmcconnell, nbosowski
 * */
public class DLEvent {

	/** id of the event */
	private String id;
	/** name of the event moderator */
	private String moderatorName;
	/** question the event will decide on */
	private String eventQuestion;
	/** the date the event was created */
	private Date dateCreated;
	/** true for an open event, false otherwise */
	private boolean isOpen;
	/** integer - number of choices in the event */
	private int numChoices;
	/** integer - number of rounds in the event */
	private int numRounds;
	/** number of edges in the event */
	private int numEdges;
	/** the user whose turn it is to add an egde */
	private int currentMaster; 
	/** is the event complete */
	private boolean isComplete;
	/** is the event accepting users */
	private boolean acceptingUsers;
	/** forced complete boolean */
	private boolean forcedComplete;

	/** array list of users */
	ArrayList<User> userList = new ArrayList<User>();
	/** array list of edges */
	ArrayList<Edge> edgeList = new ArrayList<Edge>();
	/** array list of choices */
	ArrayList<DLChoice> choiceList = new ArrayList<DLChoice>();
	/** array list of client states */
	ArrayList<ClientState> stateList = new ArrayList<ClientState>();

	/**
	 * Constructor for the event
	 * @param id the id of the event
	 * @param moderator the name of the moderator
	 * @param question the event question
	 * @param numChoices number of choices
	 * @param numRounds Number of rounds
	 */
	public DLEvent(String id, String moderator, String question, int numChoices, int numRounds){
		this.id = id;
		this.moderatorName = moderator;
		this.eventQuestion = question;
		this.numChoices = numChoices;
		this.numRounds = numRounds;
		this.isComplete = false;
		this.acceptingUsers = true;
		this.numEdges = 0;
		this.currentMaster = 0;
		this.forcedComplete = false ;
	}

	/**
	 * Getter for ID
	 * @return the event id
	 */
	public String getID(){
		return id;
	}
    /**
     * Getter for the event question
     * @return the event question
     */
	public String getEventQuestion(){
		return eventQuestion;
	}
	/**
	 * Getter for the date created
	 * @return the date created
	 */
	public Date getDateCreated(){
		return dateCreated;
	}
	/**
	 * Getter for the is open boolean
	 * @return is open boolean
	 */
	public boolean getIsOpen(){
		return isOpen;
	}
	/**
	 * Getter for the number of choices
	 * @return the number of choices
	 */
	public int getNumChoices(){
		return numChoices;
	}
	/**
	 * Getter for the number of rounds
	 * @return the number of rounds
	 */
	public int getNumRounds(){
		return numRounds;
	}
	/**
	 * Getter for the number of edges
	 * @return number of edges
	 */
	public int getNumEdges(){
		return numEdges;
	}
	/**
	 * Getter for the list of users
	 * @return the user list
	 */
	public ArrayList<User> getUserList(){
		return userList;
	}
	/**
	 * Getter for the list of edges
	 * @return the edge list
	 */
	public ArrayList<Edge> getEdgeList(){
		return edgeList;
	}
	/**
	 * Getter for the list of choices
	 * @return choice list
	 */
	public ArrayList<DLChoice> getDLChoice(){
		return choiceList;
	}
	/**
	 * Getter for the moderator
	 * @return moderators name
	 */
	public String getModerator(){
		return moderatorName;
	}
	/**
	 * Getter for the completed boolean
	 * @return completed boolean
	 */
	public boolean getComplete(){
		return this.isComplete;
	}

	/**
	 * setter for the dtae created
	 * @param date given created date
	 * @return the date created
	 */
	public Date setDateCreated(Date date){
		return dateCreated = date;
	}
	/**
	 * setter for the is open boolean
	 * @param isOpen given boolean
	 * @return the is open boolean
	 */
	public boolean setIsOpen(boolean isOpen){
		return this.isOpen = isOpen;
	}

	/**
	 * setter for the number of edges
	 * @param numEdge given number of edges
	 * @return the new number of edges
	 */
	public int setNumEdges(int numEdge){
		return numEdges = numEdge;
	}
	
	/**
	 * Increments the number oof edges by 1. If it is the last edge, is complete boolean is set to true
	 */
	public void incrementEdges(){
		numEdges++;
		if(numEdges == numRounds * numChoices)
			this.isComplete = true;
	}
	
	/**
	 * Add given user to the user list
	 * @param user given user
	 */
	public void addUser(User user){
		if (userList == null){
			userList = new ArrayList<User>();			
		}
		userList.add(user);
	}
	
	/**
	 * Adds the given edge to the edge list
	 * @param edge given edge
	 */
	public void addEdge(Edge edge){
		if (edgeList == null){
			edgeList = new ArrayList<Edge>();
		}
		edgeList.add(edge);
	}
	/**
	 * Adds the choice to the choice list
	 * @param choice given choice
	 */
	public void addDLChoice(DLChoice choice){
		if (choiceList == null){
			choiceList = new ArrayList<DLChoice>();
		}
		choiceList.add(choice);
	}
	/**
	 * Sets is complete to true
	 */
	public void setComplete(){
		this.isComplete = true;
	}
	/**
	 * Getter for the is complete boolean
	 * @return the is complete boolean
	 */
	public boolean isComplete(){
		return isComplete ;
	}
	
	/**
	 * Sign in function
	 * @param name the name trying to sign in
	 * @param pswd the password trying to sign in
	 * @return true if they are signed in, false otherwise
	 */
	public boolean signIn(String name, String pswd){
		if (pswd == null){
			pswd = new String("");
		}
		for (int i = 0; i < userList.size(); i++){
			User check = userList.get(i);
			if(check.name.equals(name) && check.password.equals(pswd)){
				return true;
			}
		}
		return false;
	}
	/**
	 * Gets the next position in the user list
	 * @param name the user's name
	 * @return either that users index (if they exist) or the next index location
	 */
	//tested
	public int getNextPosition(String name){
		for (int i = 0; i < userList.size(); i++){
			User check = userList.get(i);
			if(check.name.equals(name)){
				return check.userIndex;
			}
		}
		return this.userList.size(); 
		
	}
	/**
	 * set the event so it is no longer accepting users
	 */
	public void notAcceptingUsers(){
		this.acceptingUsers = false;
	}
	/**
	 * Returns if the event is accepting users
	 * @return the accepting users boolean
	 */
	public boolean isAccepting(){
		return this.acceptingUsers;
	}
	/** These here are only for test cases 
	 * added by: nbosowski				
	 */
	/**
	 * sets accepting users boolean to given boolean
	 * @param b given boolean
	 */
	public void setAcceptingUsers(boolean b){
		this.acceptingUsers = b;
	}
	/**
	 * sets is complete function to given boolean
	 * @param b given boolean
	 */
	public void setIsComplete(boolean b){
		isComplete = b;
	}

	/**
	 * Getter for the current master (user whos turn it is)
	 * @return the current master
	 */
	public int getCurrentMaster() {
		return currentMaster;
	}

	/**
	 * Increments the current master to the next in the client state list
	 */
	public void incrementCurrentMaster() {
		if(currentMaster < stateList.size() -1){
			currentMaster++;
		}
		else
			currentMaster = 0;
	}
	
	/**
	 * Adds the given client state to the state list
	 * @param st given client state
	 */
	public void addClientState(ClientState st){
		stateList.add(st);
	}
	
	/**
	 * getter for the current master
	 * @return the current master
	 */
	public ClientState getClientState(){
		return stateList.get(currentMaster);
	}
	
	/**
	 * getter the client state list
	 * @return the client state list
	 */
	public ArrayList<ClientState> getStates(){
		return stateList;
	}
	
	/**
	 * setter for the number of choices
	 * @param i given number of choices
	 */
	public void setNumChoices(int i){
		numChoices = i ;
	}
	/**
	 * Sets forced complete boolean to true
	 */
	public void forceComplete(){
		forcedComplete = true ;
	}
	
	/**
	 * getter for the forced complete boolean
	 * @return the forced complete boolean
	 */
	public boolean getForced(){
		return forcedComplete ;
	}

}
