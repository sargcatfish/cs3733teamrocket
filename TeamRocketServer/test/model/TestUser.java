package model;

import junit.framework.TestCase;
/**
 * 
 * @author Timothy Kolek
 *
 */
public class TestUser extends TestCase {
	public void testUser(){
		User u = new User("name", "pswd", true, 0);
	}

}
