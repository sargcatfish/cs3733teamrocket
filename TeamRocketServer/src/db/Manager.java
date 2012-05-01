package db;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import java.sql.Date;

import model.DLChoice;
import model.DLEvent;
import model.Edge;
import model.TeamRocketServerModel;
import model.User;

//I do not think all of these try catches are needed. We should look at this a little bit to determine if thats is true.

public class Manager {
	/**
	 * Interface to the MySQL database.
	 * 
	 * updates to meetings/participants/availability
	 * 
	 * @author gdmcconnell, nbosowski
	 */

	/** Hard-coded database access information */
	
	/** string = server url */
	//private static final String SERVER = "mysql.wpi.edu";
	/** string = user */
	//private static final String USER = "meowth";
	/** string = password */
	//private static final String PASSWORD = "xuguHN";
	/** string = database */
	//private static final String DATABASE = "teamrocket";

	/** string = database type = as long as you're using mysql, leave this alone. */
	//private static final String DATABASE_TYPE = "mysql";
	
	/** string = server url */
	private static final String SERVER = "localhost";
	/** string = user */
	private static final String USER = "whoarethey";
	/** string = password */
	private static final String PASSWORD = "cs3733";
	/** string = database */
	private static final String DATABASE = "whoarethey";
	/** string = database type = as long as you're using mysql, leave this alone. */
	private static final String DATABASE_TYPE = "mysql";
	
	/* ------------- SQL Variables ------------- */

	/** The SQL connection to the database */
	static Connection con;
	/**
	 * Gets a connection to for the manager and makes a good effort to make sure
	 * it is open
	 * 
	 * @return either an open connection or null
	 */
	static synchronized Connection getConnection() {
		try {
			if (con != null && con.isClosed()) {
				con = null;
			}
		} catch (SQLException e) {
			con = null;
		}
		connect();
		return con;
	}

	/** Closes the database connection */
	public static void disconnect() {
		if (con == null) {
			return;
		}

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		con = null;
	}

	/**
	 * Utility method to validate connection is valid.
	 * 
	 * @return true if DATABASE is available; false otherwise.
	 */
	public static boolean isConnected() {
		if (con == null) {
			return false;
		}
		try {
			return !con.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Connects to DATABASE
	 * @return true if connection is established; false otherwise.
	 */
	public static boolean connect() {
		/** already connected */
		if (con != null) {
			return true;
		} 
		/** Register the JDBC driver for MySQL. Simply accessing the class
		* will properly initialize everything. 
		*/
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException cnfe) {
			System.err.println("Unable to locate mySQL drivers");
			return false;
		}

		Properties dbConfig = new Properties();
		try {
			dbConfig.load(new FileReader(new File("db.config")));
		} catch (Exception e1) {
			System.err
			.println("Unable to locate db.config configuration file.");
			return false;
		}

		String url = "jdbc:" + DATABASE_TYPE + "://" + SERVER +"/" + DATABASE;
		try {
			/** Get a connection to the database for a
			* user with the given user name and password. 
			*/
			con = DriverManager.getConnection(url, USER, PASSWORD);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Generate unique meeting id.
	 * @return Generated ID
	 */
	public static String generateEventID() {
		String id = UUID.randomUUID().toString();

		/** format of the created string abcdefgh-abcd */
		return id.substring(0, 13);
	}

	/**
	 * Insert DLEvent into database
	 * @param id string of the event ID
	 * @param numChoices integer of the number of choices
	 * @param numRounds integer of the number of rounds
	 * @param eventQuestion string of the question the decision is being made on
	 * @param isOpen boolean is the event open
	 * @param acceptingUsers boolean is the event accepting users
	 * @param moderator string of the moderators name
	 * @return true if the event was put into the database, false otherwise
	 */
	public static boolean insertDLEvent(String id, int numChoices, int numRounds, String eventQuestion, 
			boolean isOpen, boolean acceptingUsers, String moderator) {
		try {
			/** NOW() function is used for date */
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"INSERT into DLEvents(id, numChoices, numRounds, eventQuestion, dateCreated, isOpen, " +
							"acceptingUsers, moderator, isComplete) VALUES(?,?,?,?,NOW(),?,?,?,?);"); 
			pstmt.setString(1, id);
			pstmt.setInt(2, numChoices);
			pstmt.setInt(3, numRounds);
			/** no more than 32 characters */
			pstmt.setString(4, trimString(eventQuestion, 32)); 
			/** // no more than 4 characters (OPEN or CLOSE) */
			pstmt.setBoolean(5, isOpen);
			pstmt.setBoolean(6,acceptingUsers);
			pstmt.setString(7,moderator);
			pstmt.setBoolean(8, false);							
			/** Execute the SQL statement and update database accordingly. */
			pstmt.executeUpdate();
			int numInserted = pstmt.getUpdateCount();
			if (numInserted == 0) {
				throw new IllegalArgumentException("Unable to insert event "
						+ id + ".");
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	TeamRocketServerModel.getInstance().getTable().put(id,	Manager.retrieveEvent(id));
		return true;
	}

	/**
	 * Register user with meeting AND check password if already exists. If
	 * PASSWORD is null then treat as ""
	 * @param eventID string of the event ID
	 * @param user string of the user's name 
	 * @param password string of the user's password
	 * @param isModerator boolean is the user the moderator
	 * @param userIndex integer of the number of the user in the order
	 * @return true if signed in successfully, false otherwise
	 */
	public static boolean signin(String eventID, String user, String password, boolean isModerator, int userIndex) {

		/** normalize no password. */
		if (password == null) {
			password = "";
		}

		try {
			/**
			 * check that participant not already in meeting with different
			 *password.
			 */
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT password FROM users WHERE id = ? and name=?;");
			pstmt.setString(1, eventID);
			pstmt.setString(2, user);

			
			/** Execute the SQL statement and store result into the ResultSet */
			ResultSet result = pstmt.executeQuery();

			/** If there is a participant */
			if (result.next()) {
				/** check the password. */
				String existingPassword = result.getString("password");
				if (!existingPassword.equals(password)) {
					return false;
				}

				return true;
			}

			pstmt = Manager
					.getConnection()
					.prepareStatement(
							"INSERT into users(id,name,password,isModerator, userIndex) VALUES(?,?,?,?,?);");
			pstmt.setString(1, eventID);
			pstmt.setString(2, user);
			pstmt.setString(3, password);
			pstmt.setBoolean(4, isModerator);
			pstmt.setInt(5, userIndex);

			/** Execute the SQL statement and update database accordingly. */
			pstmt.executeUpdate();

			int numInserted = pstmt.getUpdateCount();
			if (numInserted == 0) {
				throw new IllegalArgumentException(
						"Unable to insert participant for " + eventID);
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		return true;
	}

	/**
	 * Retrieve meeting from database for given id, returning null if invalid
	 * id.
	 * @param id string of the event id to be retrieved
	 * @return The DLEvent with the given id
	 */
	public static DLEvent retrieveEvent(String id) {
		try {
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT id, numChoices, numRounds, eventQuestion, dateCreated, isOpen," +
							"acceptingUsers,  moderator, isComplete FROM DLEvents WHERE id = ?;");
			pstmt.setString(1, id);

			/** Execute the SQL statement and store result into the ResultSet */
			ResultSet result = pstmt.executeQuery();

			/**
			 * no meeting? Return null
			 */
			if (!result.next()) {
				return null;
			}

			/** pull out the values from the DATABASE */
			int numChoices = result.getInt("numChoices");
			int numRounds = result.getInt("numRounds");
			String eventQuestion = result.getString("eventQuestion");
			Date dateCreated = result.getDate("dateCreated");
			boolean isOpen = result.getBoolean("isOpen");
			boolean acceptingUsers = result.getBoolean("acceptingUsers");
			String moderator = result.getString("moderator");
			boolean isComplete = result.getBoolean("isComplete");

			/** construct meeting */
			DLEvent d = new DLEvent(id, moderator, eventQuestion, numChoices,
					numRounds);
			d.setDateCreated(dateCreated);
			d.setIsOpen(isOpen);
			d.setAcceptingUsers(acceptingUsers);
			d.setIsComplete(isComplete);

			pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT id, name, password, isModerator, userIndex FROM users WHERE id = ?;");
			pstmt.setString(1, id);

			/** Execute the SQL statement and store result into the ResultSet */
			ResultSet userResult = pstmt.executeQuery();

			/** no meeting? Return null */
			while (userResult.next()) {

				/** pull out the values from the DATABASE */
				String name = userResult.getString("name");
				String password = userResult.getString("password");
				boolean isModerator = userResult.getBoolean("isModerator");
				int userIndex = userResult.getInt("userIndex");

				User u = new User(name, password, isModerator, userIndex);
				d.addUser(u);
			}

			pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT id, choiceIndex, choiceName FROM choices WHERE id = ?;");
			pstmt.setString(1, id);
 
			/** Execute the SQL statement and store result into the ResultSet */
			ResultSet choiceResult = pstmt.executeQuery();

			while (choiceResult.next()) {

				/** pull out the values from the DATABASE */
				int choiceIndex = choiceResult.getInt("choiceIndex");
				String choiceName = choiceResult.getString("choiceName");

				DLChoice c = new DLChoice(choiceIndex, choiceName);
				d.addDLChoice(c);
			}

			pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT id, leftChoice, rightChoice, height FROM edges WHERE id = ?;");
			pstmt.setString(1, id);

			/** Execute the SQL statement and store result into the ResultSet */
			ResultSet edgeResult = pstmt.executeQuery();

			while (edgeResult.next()) {

				/** pull out the values from the DATABASE */
				int leftChoice = edgeResult.getInt("leftChoice");
				int rightChoice = edgeResult.getInt("rightChoice");
				int height = edgeResult.getInt("height");

				Edge e = new Edge(leftChoice, rightChoice, height);
				d.addEdge(e);
			}
			return d;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		/** returns null if anything goes wrong */
		return null;
	}

	/**
	 * 
	 * 
	 * 
	 *  TODO: Fix this!
	 */
	/**
	 * Retrieve meeting from database for given event type, returning null if invalid
	 * @param type boolean  indicate the type of event whether it's open or closed
	 * @return the ResultSet event retrieved form the database
	 */
	public static ResultSet retrieveEvent(boolean type) {
		try {
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT id, numChoices, numRounds, eventQuestion, dateCreated, isOpen," +
							"acceptingUsers,  moderator, isComplete FROM DLEvents WHERE isOpen = ?;");
			pstmt.setBoolean(1, type);

			/** Execute the SQL statement and store result into the ResultSet */
			ResultSet result = pstmt.executeQuery();

			/** no meeting? Return null*//*
			if (!result.next()) {
				return null;
			}
			else {*/
				return result;
			//}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}




	/**
	 * Sets the completion of the event with the given id
	 * @param id string of the id of the event
	 * @return the completion (0 for not complete, 1 for complete)
	 */
	public static int setCompletion(String id) {
		int result = 0 ;
		
		PreparedStatement pstmt;
		try {
			pstmt = Manager
					.getConnection()
					.prepareStatement(
							"UPDATE DLEvents SET isComplete = true WHERE id = ?;");
			pstmt.setString(1, id);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result ;
	}
	
	/**
	 * Changes event to not accept users for closing the event  by the moderator
	 * @param id string of the id of the event
	 * @return integer of the number affected
	 */
	public static int setClosed(String id){
		int result = 0 ;
		
		PreparedStatement pstmt;
		try {
			pstmt = Manager
					.getConnection()
					.prepareStatement(
							"UPDATE DLEvents SET acceptingUsers = false WHERE id = ?;");
			pstmt.setString(1, id);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result ;
	}
	
	/**
	 * Sets the accepting users of the event with the given id
	 * @param id string of the id of the event
	 * @return integer that accepting users is set to
	 * @throws SQLException
	 */
	public static int setacceptingUsers(String id){
		int result = 0 ;
		
		PreparedStatement pstmt;
		try {
			pstmt = Manager
					.getConnection()
					.prepareStatement(
							"UPDATE DLEvents SET acceptingUsers = false WHERE id = ?;");
			pstmt.setString(1, id);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeamRocketServerModel.getInstance().getTable().get(id).setAcceptingUsers(false);
		return result ;
	}

	/**
	 * Inserts the choice into the event with the given id
	 * @param id string of the id of the event
	 * @param choiceIndex the index where the choice is going to be put in the event
	 * @param choiceName the name of the choice being added
	 * @return returns true is the choice was added, false otherwise 
	 * @throws SQLException
	 */
	public static boolean insertChoice(String id, int choiceIndex, String choiceName){
		try {
			PreparedStatement pstmt = Manager.getConnection().prepareStatement(
					"INSERT into choices(id,choiceIndex,choiceName) VALUES(?,?,?);");
			pstmt.setString(1, id);
			pstmt.setInt(2, choiceIndex);
			pstmt.setString(3, choiceName);

			pstmt.executeUpdate();

			int numInserted = pstmt.getUpdateCount();
			if (numInserted == 0) {
				throw new IllegalArgumentException("Unable to insert choice "
						+ id + ".");
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		return true;
	}

	/**
	 *  Inserts the edge into the event with the given id
	 * @param id string of the id of the event
	 * @param leftChoice integer of the left choice line the edge connects to
	 * @param rightChoice integer of the right choice line the edge connects to
	 * @param height integer of the height of the edge
	 * @return returns true is the choice was added, false otherwise 
	 * @throws SQLException
	 */
	public static boolean insertEdge(String id, int leftChoice, int rightChoice, int height){
		try {
			PreparedStatement pstmt = Manager.getConnection().prepareStatement(
					"INSERT into edges(id, leftChoice, rightChoice, height) VALUES(?,?,?,?);");
			pstmt.setString(1, id);
			pstmt.setInt(2, leftChoice);
			pstmt.setInt(3, rightChoice);
			pstmt.setInt(4, height);

			pstmt.executeUpdate();

			int numInserted = pstmt.getUpdateCount();
			if (numInserted == 0) {
				throw new IllegalArgumentException("Unable to insert edge "
						+ id + "... this is all Wesley's fault!");
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		return true;
	}

	/**
	 * Gets the ids of the events of the specified type that are greater than the specified number of days in age
	 * @param isComplete boolean whether the events retrieved are complete or not
	 * @param daysOld integer for how many days old to compare the events to
	 * @return ResultSet of all the events of the given type that are more than the given number of days old
	 * @throws SQLException
	 */
	public static ResultSet getEventsDays(boolean isComplete, int daysOld) {	
		ResultSet result ;

		try {
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT id FROM DLEvents WHERE isComplete = ? && TO_DAYS(NOW()) - TO_DAYS(dateCreated) > ?;");
			pstmt.setBoolean(1, isComplete) ;
			pstmt.setInt(2, daysOld);

			/** Execute the SQL statement and store result into the ResultSet */
			result = pstmt.executeQuery();
			return result;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

	}
	
	/**
	 * Gets the ids of all events that are greater than the specified number of days in age
	 * @param daysOld integer for how many days old to compare the events to
	 * @return ResultSet of all the events that are more than the given number of days old
	 * @throws SQLException
	 */
	public static ResultSet getEventsDays(int daysOld) {	
		ResultSet result ;

		try {
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT id FROM DLEvents WHERE TO_DAYS(NOW()) - TO_DAYS(dateCreated) > ?;");
			pstmt.setInt(1, daysOld);

			/** Execute the SQL statement and store result into the ResultSet */
			result = pstmt.executeQuery();
			return result;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	/**
	 * Deletes the events with the given ids from the database
	 * @param result WHAT IS THIS?!!
	 * @return integer of the number of affected events
	 */
	public static int deleteEvent(ResultSet result){
		int returnVal = 0 ;
		try {
			while(result.next()){
				deleteEvent(result.getString("id")) ;
				System.out.println(result.getString("id")) ;
				returnVal++ ;
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e) ;
		}
		return returnVal ;
	}

	
	/**
	 * Remove event from the database along with any corresponding users, choices, and edges.
	 * @param meetingID String of the event id to delete
	 * @return
	 */
	public static boolean deleteEvent(String meetingID) {
		try {
			PreparedStatement pstmt = Manager.getConnection().prepareStatement(
					"DELETE from DLEvents WHERE id = ?;");
			pstmt.setString(1, meetingID);

			/** Execute the SQL statement and update database accordingly.*/
			pstmt.executeUpdate();

			int numDeleted = pstmt.getUpdateCount();
			if (numDeleted == 0) {
				return false;
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		deleteUsers(meetingID);
		deleteChoices(meetingID);
		deleteEdges(meetingID);
		return  true;
	}

	/**
	 * Delete the users that are in the event with the given id
	 * @param meetingID string id of the meeting
	 * @return true if 1 or more users were deleted, false otherwise
	 */
	public static boolean deleteUsers(String meetingID) {
		try {
			PreparedStatement pstmt = Manager.getConnection().prepareStatement(
					"DELETE from users WHERE id = ?;");
			pstmt.setString(1, meetingID);

			/** Execute the SQL statement and update database accordingly.*/
			pstmt.executeUpdate();

			int numDeleted = pstmt.getUpdateCount();
			if (numDeleted == 0) {
				return false;
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		return true;
	}

	/**
	 * Delete the choices that are in the event with the given id
	 * @param meetingID string id of the meeting
	 * @return true if 1 or more choices were deleted, false otherwise
	 */
	public static boolean deleteChoices(String meetingID) {
		try {
			PreparedStatement pstmt = Manager.getConnection().prepareStatement(
					"DELETE from choices WHERE id = ?;");
			pstmt.setString(1, meetingID);

			/** Execute the SQL statement and update database accordingly.*/
			pstmt.executeUpdate();

			int numDeleted = pstmt.getUpdateCount();
			if (numDeleted == 0) {
				return false;
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		return true;
	}

	/**
	 * Delete the edges that are in the event with the given id
	 * @param meetingID string id of the meeting
	 * @return true if 1 or more edges were deleted, false otherwise
	 */
	public static boolean deleteEdges(String meetingID) {
		try {
			PreparedStatement pstmt = Manager.getConnection().prepareStatement(
					"DELETE from edges WHERE id = ?;");
			pstmt.setString(1, meetingID);

			/** Execute the SQL statement and update database accordingly.*/
			pstmt.executeUpdate();

			int numDeleted = pstmt.getUpdateCount();
			if (numDeleted == 0) {
				return false;
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		return true;
	}

	/**
	 * Trims the given string if it is larger than the given length
	 * @param s The given string
	 * @param len The length to trim the given string to
	 * @return The trimmed string if the given string was too large, otherwise returns the given string
	 */
	public static String trimString(String s, int len) {
		if (s.length() <= len) {
			return s;
		}

		return s.substring(0, len);
	}

	/**
	 * Changed the completion of events that are larger than the given number of days old
	 * @param daysOld integer for how many days old to compare the events to
	 * @return Integer of the number of events that were changed
	 */
	public static int setCompletion(int daysOld) {

		int result = 0 ;
		PreparedStatement pstmt;
		try {
			pstmt = Manager
					.getConnection()
					.prepareStatement(
							"UPDATE DLEvents SET isComplete = true WHERE !isComplete && TO_DAYS(NOW()) - TO_DAYS(dateCreated) > ?;");
			pstmt.setInt(1, daysOld) ;
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

}