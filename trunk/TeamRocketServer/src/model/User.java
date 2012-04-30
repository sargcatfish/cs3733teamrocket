package model;

/**
 * User class Contains information relating to a user.
 * 
 * @author Wesley
 *
 */
public class User {
	/** String of the name of the user*/
	String name;
	/** String of th password of the user*/
	String password = null;
	/** boolean for if the user is a moderator*/
	boolean isModerator = false;
	/** index of the user*/
	int userIndex;
	
	/**
	 * Constructor for a User
	 * @param n string for the name
	 * @param pwd string for the password
	 * @param isMod boolean for if the user is a moderator
	 * @param uIndex integer for the user index
	 */
	public User(String n, String pwd, boolean isMod, int uIndex){
		name = n;
		password = pwd;
		isModerator = isMod;
		userIndex = uIndex;
	}

	/**
	 * Getter for the index of the user
	 * @return the index of the user
	 */
	public int getIndex(){
		return userIndex;
	}
}
