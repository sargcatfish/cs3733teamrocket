package db;

import junit.framework.TestCase;
import db.*;
import java.sql.Date;

import model.*;
/**
 * 
 * @author Timothy Kolek
 *
 */

public class TestManager extends TestCase {


	public void testDataBase(){
		String id = "flustercucker";
		int numChoices = 3;
		int numRounds = 4;
		String eventQuestion = "What's the best type of bacon?";
		Date dateCreated =  new Date(System.currentTimeMillis());
		boolean isOpen = false;
		boolean acceptingUsers = true;
		String moderator = "Kim Jeoung Il";
		
		Manager.deleteEvent(id);
		
		Manager.insertDLEvent(id, numChoices, numRounds, eventQuestion, dateCreated, isOpen, acceptingUsers, moderator);
		DLEvent d = Manager.retrieveEvent(id);
		
		assertEquals(id,d.getID());
		assertEquals(numChoices,d.getNumChoices());
		assertEquals(numRounds, d.getNumRounds());
		assertEquals(eventQuestion, d.getEventQuestion());
		assertEquals(dateCreated,d.getDateCreated());
		assertFalse(d.getIsOpen());
		assertTrue(d.isAccepting());
		assertEquals(moderator, d.getModerator());
		
		Manager.deleteEvent(id);
		
	}
}
