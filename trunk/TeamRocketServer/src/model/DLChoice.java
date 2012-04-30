package model;

/**
 * Contains information relating to the choice of a particular event.
 * 
 * @author Wesley
 *
 */
public class DLChoice {
	/** integer of the choice number*/
	int choiceIndex;
	/** string of the choice */
	String choiceName;
	
	/**
	 * Constructor for the DLChoice
	 * @param cIndex given index of the choice
	 * @param cName given name of the choice
	 */
	public	DLChoice(int cIndex, String cName){
		choiceIndex = cIndex;
		choiceName = cName;
	}

	/**
	 * Getter for the choice name
	 * @return string of the choice name
	 */
	public String getName() {
		return this.choiceName;
	}

	/**
	 * Getter for the index
	 * @return integer of the choice index
	 */
	public int getIndex(){
		return this.choiceIndex;
	}
}
