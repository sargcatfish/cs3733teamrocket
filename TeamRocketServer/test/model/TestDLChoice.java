package model;

import junit.framework.TestCase;
/**
 * 
 * @author Timothy Kolek
 *
 */
public class TestDLChoice extends TestCase {
	
	public void testChoice() {
		DLChoice choice = new DLChoice(1, "rob");
		assertEquals(choice.getName(), "rob");
	}

}
