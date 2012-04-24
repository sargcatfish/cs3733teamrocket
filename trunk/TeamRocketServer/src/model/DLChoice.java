package model;

/**
 * Contains information relating to the choice of a particular event.
 * 
 * @author Wesley
 *
 */
public class DLChoice {
	int choiceIndex;
	String choiceName;
	String name;
	
public	DLChoice(int cIndex, String cName, String name){
		choiceIndex = cIndex;
		choiceName = cName;
		this.name = name;
	}

}
