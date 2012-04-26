package model;

import junit.framework.TestCase;
/**
 * 
 * @author Timothy Kolek
 *
 */
public class TestEdge extends TestCase {
	
	public void testEdge() {
		Edge e = new Edge(1, 2, 150);
		assertEquals(e.getLeftChoice(), 1);
		assertEquals(e.getRightChoice(), 2);
		assertEquals(e.getHeight(), 150);
	}
	
	

}
