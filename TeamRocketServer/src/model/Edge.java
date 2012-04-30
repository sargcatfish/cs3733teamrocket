package model;

/**
 * Edge class
 * @author Timothy Kolek
 *
 */
public class Edge {
	/** Integer of the number of the left choice of the edge	 */
	int leftChoice;
	/** Integer of the number of the right choice of the edge	 */
	int rightChoice;
	/** Integer of the number of the height of the edge	 */
	int height;
	
	/**
	 * Constructor for an Edge
	 * @param left integer for the left choice
	 * @param right integer for the right choice
	 * @param height integer for the height
	 */
	public Edge(int left, int right, int height){
		leftChoice = left;
		rightChoice = right;
		this.height = height;
	}

	/**
	 * Getter for the left choice
	 * @return integer of the left choice
	 */
	public int getLeftChoice(){
		return leftChoice;
	}
	
	/**
	 * Getter for the right choice
	 * @return integer of the left choice
	 */
	public int getRightChoice(){
		return rightChoice;
	}
	
	/**
	 * Getter for the height
	 * @return integer of the left choice
	 */
	public int getHeight(){
		return height;
	}
	

}
