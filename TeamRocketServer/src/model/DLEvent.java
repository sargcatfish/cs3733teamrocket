package model;

import java.util.ArrayList;
import java.sql.Date;

/** Class that stores references to information for a decisionLine event
 * @author Wesley, gdmcconnell, nbosowski
 * */
public class DLEvent {

	private String id;
	private String moderatorName; //The string "name" was send in the xml assuming it's the moderator name?
	private String eventQuestion;
	private Date dateCreated;
	private boolean isOpen;		//True for open event
	private int numChoices;
	private int numRounds;
	private int numEdges;
	private boolean isComplete;
	private boolean acceptingUsers;

	ArrayList<User> userList = new ArrayList<User>();
	ArrayList<Edge> edgeList = new ArrayList<Edge>();
	ArrayList<DLChoice> choiceList = new ArrayList<DLChoice>();

	/** The setters should be doing the work of initializing the attr 
	 * @param numRounds 
	 * @param numChoices
	 * @param question 
	 * @param name 
	 * @param id 
	 * 
	 *  
	 *  */
	public DLEvent(String id, String moderator, String question, int numChoices, int numRounds){
		this.id = id;
		this.moderatorName = moderator;
		this.eventQuestion = question;
		this.numChoices = numChoices;
		this.numRounds = numRounds;
		this.isComplete = false;
		this.acceptingUsers = true;
		this.numEdges = 0;
	}

	//Getters
	//tested
	public String getID(){
		return id;
	}
	//tested
	public String getEventQuestion(){
		return eventQuestion;
	}
	//tested
	public Date getDateCreated(){
		return dateCreated;
	}
	//tested
	public boolean getIsOpen(){
		return isOpen;
	}
	//tested
	public int getNumChoices(){
		return numChoices;
	}
	//tested
	public int getNumRounds(){
		return numRounds;
	}
	//tested
	public int getNumEdges(){
		return numEdges;
	}
	//tested
	public ArrayList<User> getUserList(){
		return userList;
	}
	//tested
	public ArrayList<Edge> getEdgeList(){
		return edgeList;
	}
	//tested
	public ArrayList<DLChoice> getDLChoice(){
		return choiceList;
	}
	//tested
	public String getModerator(){
		return moderatorName;
	}
	//tested
	public boolean getComplete(){
		return this.isComplete;
	}

	//Setters
/**
	public String setID(String id){
		return this.id = id;
	}

	public String setEventQuestion(String ques){
		return eventQuestion = ques;
	}
*/
	//tested
	public Date setDateCreated(Date date){
		return dateCreated = date;
	}
	//tested
	public boolean setIsOpen(boolean isOpen){
		return this.isOpen = isOpen;
	}
/**
	public int setNumChoices(int numChoice){
		return numChoices = numChoice;
	}

	public int setNumRounds(int numRound){
		return numRounds = numRound;
	}
*/
	//tested
	public int setNumEdges(int numEdge){
		return numEdges = numEdge;
	}
	//tested
	public void addUser(User user){
		if (userList == null){
			userList = new ArrayList<User>();			
		}
		userList.add(user);
	}
	//tested
	public void addEdge(Edge edge){
		if (edgeList == null){
			edgeList = new ArrayList<Edge>();
		}
		edgeList.add(edge);
	}
	//tested
	public void addDLChoice(DLChoice choice){
		if (choiceList == null){
			choiceList = new ArrayList<DLChoice>();
		}
		choiceList.add(choice);
	}
	//tested
	public void setComplete(){
		this.isComplete = true;
	}
	//added by rhollinger
	//tested
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
	/**added by rhollinger
	 * 
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
	 * added by rhollinger
	 * set the event so it is no longer accepting users
	 */
	//tested
	public void notAcceptingUsers(){
		this.acceptingUsers = false;
	}
	//tested
	public boolean isAccepting(){
		return this.acceptingUsers;
	}
	/* These here are only for test cases 
	 * added by: nbosowski				
	 */
	//tested
	public void setAcceptingUsers(boolean b){
		this.acceptingUsers = b;
	}
	//tested
	public void setIsComplete(boolean b){
		isComplete = b;
	}


}
