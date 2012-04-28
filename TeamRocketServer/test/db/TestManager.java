package db;

import junit.framework.TestCase;
import db.*;
import java.sql.Date;
import java.sql.Connection;
import java.sql.SQLException;

import model.*;
/**
 * 
 * @author Nick Bosowski
 *
 */

public class TestManager extends TestCase {


	public void testDataBase(){
		String id = Manager.generateEventID();
		int numChoices = 3;
		int numRounds = 4;
		String eventQuestion = "What's the best type of bacon?";
		Date dateCreated =  new Date(System.currentTimeMillis());
		boolean isOpen = false;
		boolean acceptingUsers = true;
		String moderator = "Kim Jeoung Il";
		
//		Manager.deleteEvent(id);
//		Manager.deleteChoices(id);
//		Manager.deleteUsers(id);
//		Manager.deleteEdges(id);
		
		Manager.insertDLEvent(id, numChoices, numRounds, eventQuestion, isOpen, acceptingUsers, moderator);
		DLEvent d = Manager.retrieveEvent(id);
		
		assertEquals(id,d.getID());
		assertEquals(numChoices,d.getNumChoices());
		assertEquals(numRounds, d.getNumRounds());
		assertEquals(eventQuestion, d.getEventQuestion());
//		assertEquals(dateCreated,d.getDateCreated());
		assertFalse(d.getIsOpen());
		assertTrue(d.isAccepting());
		assertEquals(moderator, d.getModerator());
		assertFalse(d.getComplete());
		
		int choiceIndex = 3;
		String choiceName = "Penguins";
		String user = "Nick";
		String password = "WARGARBLE";
		boolean isModerator = true;
		int userIndex = 1;
		
		int leftChoice = 2;
		int rightChoice = 3;
		int height = 200;
		
		Manager.setCompletion(id);
		assertTrue(Manager.signin(id, user, password, isModerator, userIndex)); // can we sign in
		assertTrue(Manager.signin(id, user, password, isModerator, userIndex)); // can we resign in
		assertFalse(Manager.signin(id, user, "failbuckets", isModerator, userIndex)); // wrong password
		Manager.insertChoice(id, choiceIndex, choiceName);
		Manager.insertEdge(id, leftChoice, rightChoice, height);
		
		
		
		d = Manager.retrieveEvent(id);
		DLChoice l = d.getDLChoice().get(0);
		Edge e = d.getEdgeList().get(0);
		
		assertEquals(1,d.getDLChoice().size());
		assertEquals(choiceName, l.getName());
		assertEquals(1,d.getEdgeList().size());
		
		assertEquals(leftChoice, e.getLeftChoice());
		assertEquals(rightChoice, e.getRightChoice());
		assertEquals(height, e.getHeight());
		
		assertTrue(d.getComplete());
		
		Manager.deleteEvent(id);
		
	}
	
	public void testDisconnect() throws SQLException{
		Manager.connect();
		Connection connect = Manager.getConnection();
		assertTrue(Manager.isConnected());
		assertFalse(connect == null);
		
		connect.close();
		assertFalse(Manager.isConnected());
		Manager.disconnect();
		assertFalse(Manager.isConnected());
		
		assertTrue(Manager.con == null);
	}
}
