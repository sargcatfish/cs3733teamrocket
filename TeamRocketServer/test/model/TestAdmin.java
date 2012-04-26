package model;

import junit.framework.TestCase;
/**
 * 
 * @author Timothy Kolek
 *
 */
public class TestAdmin extends TestCase {
	public void testAdmin(){
		Admin a = new Admin();
		assertFalse(a.signIn("rob", "key"));
		assertTrue(a.signIn("admin", "password"));
		a.setKey("key");
		assertFalse(a.verify("notkey"));
		assertTrue(a.verify("key"));
	}

}
