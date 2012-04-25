package model;

public class Edge {
	int leftChoice;
	int rightChoice;
	int height;
	
	public Edge(int left, int right, int height){
		leftChoice = left;
		rightChoice = right;
		this.height = height;
	}

	public int getLeftChoice(){
		return leftChoice;
	}
	
	public int getRightChoice(){
		return rightChoice;
	}
	
	public int getHeight(){
		return height;
	}
	

}
