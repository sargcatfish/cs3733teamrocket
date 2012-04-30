package model;
/**
 * Administrator class model
 * @author Wesley, Ian
 *
 */
public class Admin {
	/** string for the name of the administrator*/
	static String name;
	/** string for the password of the administrator*/
	static String pwd;
	/** string for the key of the administrator*/
	static String key;

	/**
	 * Constructor for Admin
	 */
	public Admin(){
		name = "admin";
		pwd = "password";
		key = null;
	}

	/**
	 * Sets the key field
	 * @param k string of the key to be set
	 */
	public void setKey(String k){
		key = k;
	}

	/**
	 * Sign In function for the administrator
	 * @param n string of the name
	 * @param p string of the password
	 * @return true if signs in, false otherwise
	 */
	public boolean signIn(String n, String p){
		if(name.equals(n) && pwd.equals(p)){
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Function to verify the key
	 * @param k string of given key to check against
	 * @return true if the key is the same, false otherwise
	 */
	public boolean verify(String k){
		if(k.equals(key)){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Getter for the key
	 * @return string of the key
	 */
	public String getKey(){
		return key;
	}

}
