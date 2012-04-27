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
	
public	DLChoice(int cIndex, String cName){
		choiceIndex = cIndex;
		choiceName = cName;
	}

public String getName() {
	return this.choiceName;
}

public int getIndex(){
	return this.choiceIndex;
}
}
