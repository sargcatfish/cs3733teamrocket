package model;

import java.sql.Date;

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
		assertEquals(m.getNumEdges(), 0);
		assertFalse(m.getComplete());
		assertTrue(m.isAccepting());
		m.setComplete();
		assertTrue(m.getComplete());
		m.notAcceptingUsers();
		assertFalse(m.isAccepting());
		assertEquals(m.getNextPosition("rob"), 0);
		assertFalse(m.signIn("rob", null));
		DLChoice choice1 = new DLChoice(1, "cookies");
		assertEquals(choice1.getName(), "cookies");
		m.addDLChoice(choice1);
		Edge e = new Edge(1, 1, 1);
		m.addEdge(e);
		m.setNumEdges(m.getEdgeList().size());
		assertEquals(m.getNumEdges(), 1);
		int pos = m.getNextPosition("rob");
		User u = new User("rob", "pswd", false, pos);
		
		m.addUser(u);
		assertEquals(m.getNextPosition("rob"), 0);
		assertEquals(m.getUserList().size(), 1);
		assertEquals(m.getDLChoice().size(), 1);
		assertTrue(m.signIn("rob", "pswd"));
		m.setIsOpen(true);
		assertTrue(m.getIsOpen());
		m.setIsOpen(false);
		assertFalse(m.getIsOpen());
		m.setAcceptingUsers(true);
		assertTrue(m.isAccepting());
		m.setIsComplete(false);
		assertFalse(m.getComplete());
		Date dateCreated = new Date(System.currentTimeMillis());
		m.setDateCreated(dateCreated);
		assertEquals(m.getDateCreated(), dateCreated);
		
		
	}

}
