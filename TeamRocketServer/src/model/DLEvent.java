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
	}

	//Getters
	public String getID(){
		return id;
	}

	public String getEventQuestion(){
		return eventQuestion;
	}

	public Date getDateCreated(){
		return dateCreated;
	}

	public boolean getIsOpen(){
		return isOpen;
	}

	public int getNumChoices(){
		return numChoices;
	}

	public int getNumRounds(){
		return numRounds;
	}

	public int getNumEdges(){
		return numEdges;
	}

	public ArrayList<User> getUserList(){
		return userList;
	}

	public ArrayList<Edge> getEdgeList(){
		return edgeList;
	}

	public ArrayList<DLChoice> getDLChoice(){
		return choiceList;
	}
	
	public String getModerator(){
		return moderatorName;
	}
	
	public boolean getComplete(){
		return this.isComplete;
	}

	//Setters

	public String setID(String id){
		return this.id = id;
	}

	public String setEventQuestion(String ques){
		return eventQuestion = ques;
	}

	public Date setDateCreated(Date date){
		return dateCreated = date;
	}

	public boolean setIsOpen(boolean isOpen){
		return this.isOpen = isOpen;
	}

	public int setNumChoices(int numChoice){
		return numChoices = numChoice;
	}

	public int setNumRounds(int numRound){
		return numRounds = numRound;
	}

	public int setNumEdges(int numEdge){
		return numEdges = numEdge;
	}

	public void addUser(User user){
		if (userList == null){
			userList = new ArrayList<User>();			
		}
		userList.add(user);
	}

	public void addEdge(Edge edge){
		if (edgeList == null){
			edgeList = new ArrayList<Edge>();
		}
		edgeList.add(edge);
	}

	public void addDLChoice(DLChoice choice){
		if (choiceList == null){
			choiceList = new ArrayList<DLChoice>();
		}
		choiceList.add(choice);
	}
	
	public boolean setComplete(){
		this.isComplete = true;
		return true;
	}
	//added by rhollinger
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
	//added by rhollinger
	public int getPosition(){
		return this.userList.size(); 
		
	}


}
