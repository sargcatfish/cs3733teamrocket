package model;

import junit.framework.TestCase;
/**
 * 
 * @author Timothy Kolek, rhollinger
 *
 */
public class TestDLEvent extends TestCase {
	
	public void testEvent(){
		DLEvent m = new DLEvent("thisistheeventid", "moderator", "What is the meaning of life?", 5, 3);
		assertEquals(m.getID(), "thisistheeventid");
		assertEquals(m.getEventQuestion(), "What is the meaning of life?");
		assertEquals(m.getModerator(), "moderator");
		assertEquals(m.getNumChoices(), 5);
		assertEquals(m.getNumRounds(), 3);
		assertFalse(m.getComplete());
		assertTrue(m.isAccepting());
		m.setComplete();
		assertTrue(m.getComplete());
		m.notAcceptingUsers();
		assertFalse(m.isAccepting());
		assertEquals(m.getNextPosition("rob"), 1);
		assertFalse(m.signIn("rob", "pswd"));
		DLChoice choice1 = new DLChoice(1, "cookies");
		m.addDLChoice(choice1);
		Edge e = new Edge(1, 1, 1);
		m.addEdge(e);
		m.setNumEdges(m.getEdgeList().size());
		int pos = m.getNextPosition("rob");
		User u = new User("rob", "pswd", false, pos);
		m.addUser(u);
		assertEquals(m.getUserList(), 1);
		assertEquals(m.getDLChoice(), 1);
	
		
	}

}
