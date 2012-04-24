package model;

import java.util.ArrayList;
import java.sql.Date;

/** Class that stores references to information for a decisionLine event
 * @author Wesley, gdmcconnell
 * */
public class DLEvent {

	String id;
	String moderatorName; //The string "name" was send in the xml assuming it's the moderator name?
	String eventQuestion;
	Date dateCreated;
	boolean isOpen;		//True for open event
	int numChoices;
	int numRounds;
	int numEdges;

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
	DLEvent(String id, String name, String question, int numChoices, int numRounds){
		this.id = id;
		this.moderatorName = name;
		this.eventQuestion = question;
		this.numChoices = numChoices;
		this.numRounds = numRounds;
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

	public int getIsOpen(){
		if(isOpen){
			return 1;
		}
		return 0;
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

	public void addUserList(User user){
		if (userList == null){
			userList = new ArrayList<User>();			
		}
		userList.add(user);
	}

	public void addEdgeList(Edge edge){
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


}
