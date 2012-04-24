package model;

/**
 * Contains information relating to a user.
 * 
 * @author Wesley
 *
 */
public class User {
	String name;
	String password = null;
	boolean isModerator = false;
	int userIndex;
	
	public User(String n, String pwd, boolean isMod, int uIndex){
		name = n;
		password = pwd;
		isModerator = isMod;
		userIndex = uIndex;
	}

}
