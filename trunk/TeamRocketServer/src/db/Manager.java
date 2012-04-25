package db;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;
import java.sql.Date;

import model.DLChoice;
import model.DLEvent;
import model.Edge;
import model.User;

public class Manager {
	/**
	 * Interface to the MySQL database.
	 * 
	 * updates to meetings/participants/availability
	 * 
	 * @author gdmcconnell, nbosowski
	 */

	/** Hard-coded database access information */
	private static final String SERVER = "mysql.wpi.edu";
	private static final String USER = "meowth";
	private static final String PASSWORD = "xuguHN";
	private static final String DATABASE = "teamrocket";

	// as long as you're using mysql, leave this alone.
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
		// already connected.
		if (con != null) {
			return true;
		}

		// Register the JDBC driver for MySQL. Simply accessing the class
		// will properly initialize everything.
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
		/* TODO: For some reason this does not like the config file so i changed it to use the constants and it now works */
		// Define URL for database server
		// NOTE: must fill in DATABASE NAME
		//		String url = "jdbc:" + DATABASE_TYPE + "://"
		//				+ dbConfig.getProperty(SERVER) + "/"
		//				+ dbConfig.getProperty(DATABASE);

		String url = "jdbc:" + DATABASE_TYPE + "://" + SERVER +"/" + DATABASE;
		try {
			// Get a connection to the database for a
			// user with the given user name and password.
			//	con = DriverManager.getConnection(url, dbConfig.getProperty(USER),
			//			dbConfig.getProperty(PASSWORD));
			con = DriverManager.getConnection(url, USER, PASSWORD);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/** Generate unique meeting id. */
	public static String generateEventID() {
		String id = UUID.randomUUID().toString();

		// abcdefgh-abcd
		return id.substring(0, 13);
	}

	/** Insert DLEvent into database. */
	public static boolean insertDLEvent(String id, int numChoices, int numRounds, String eventQuestion, 
			Date dateCreated, boolean isOpen, boolean acceptingUsers, String moderator) {
		try {
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"INSERT into DLEvents(id, numChoices, numRounds, eventQuestion, dateCreated, isOpen, " +
							"acceptingUsers, moderator, isComplete) VALUES(?,?,?,?,?,?,?,?,?);");
			pstmt.setString(1, id);
			pstmt.setInt(2, numChoices);
			pstmt.setInt(3, numRounds);
			pstmt.setString(4, trimString(eventQuestion, 32)); 	// no more than 32 characters.
			pstmt.setDate(5, dateCreated);
			pstmt.setBoolean(6, isOpen);								// no more than 4 characters (OPEN or CLOSE)
			pstmt.setBoolean(7,acceptingUsers);
			pstmt.setString(8,moderator);
			pstmt.setBoolean(9, false);							
			// Execute the SQL statement and update database accordingly.
			pstmt.executeUpdate();
			int numInserted = pstmt.getUpdateCount();
			if (numInserted == 0) {
				throw new IllegalArgumentException("Unable to insert event "
						+ id + ".");
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		return true;
	}

	/**
	 * Register user with meeting AND check password if already exists. If
	 * PASSWORD is null then treat as ""
	 */
	public static boolean signin(String eventID, String user, String password, boolean isModerator, int userIndex) {

		// normalize no password.
		if (password == null) {
			password = "";
		}

		try {
			// check that participant not already in meeting with different
			// pasword.
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT password FROM users WHERE id = ? and name=?;");
			pstmt.setString(1, eventID);
			pstmt.setString(2, user);

			// Execute the SQL statement and store result into the ResultSet
			ResultSet result = pstmt.executeQuery();

			// If there is a participant
			if (result.next()) {
				// check the password.
				String existingPassword = result.getString("password");
				if (!existingPassword.equals(password)) {
					return false;
				}

				// YES we match
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


			// Execute the SQL statement and update database accordingly.
			pstmt.executeUpdate();

			int numInserted = pstmt.getUpdateCount();
			if (numInserted == 0) {
				throw new IllegalArgumentException(
						"Unable to insert participant for " + eventID);
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		// able to sign in.
		return true;
	}

	/**
	 * Retrieve meeting from database for given id, returning null if invalid
	 * id.
	 * 
	 *  TODO: Fix this!
	 */
	public static DLEvent retrieveEvent(String id) {
		try {
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT id, numChoices, numRounds, eventQuestion, dateCreated, isOpen, moderator FROM DLEvents WHERE id = ?;");
			pstmt.setString(1, id);

			// Execute the SQL statement and store result into the ResultSet
			ResultSet result = pstmt.executeQuery();

			// no meeting? Return null
			if (!result.next()) {
				return null;
			}

			// pull out the values from the DATABASE
			int numChoices = result.getInt("numChoices");
			int numRounds = result.getInt("numRounds");
			String eventQuestion = result.getString("eventQuestion");
			Date dateCreated = result.getDate("dateCreated");
			boolean isOpen = result.getBoolean("isOpen");
			String moderator = result.getString("moderator");

			// construct meeting and return it
			DLEvent d = new DLEvent(id, moderator, eventQuestion, numChoices,
					numRounds);
			d.setDateCreated(dateCreated);
			d.setIsOpen(isOpen);

			pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT id, name, password, isModerator, userIndex FROM users WHERE id = ?;");
			pstmt.setString(1, id);

			// Execute the SQL statement and store result into the ResultSet
			ResultSet userResult = pstmt.executeQuery();

			// no meeting? Return null
			while (userResult.next()) {

				// pull out the values from the DATABASE
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

			// Execute the SQL statement and store result into the ResultSet
			ResultSet choiceResult = pstmt.executeQuery();

			// no meeting? Return null
			while (choiceResult.next()) {

				int choiceIndex = choiceResult.getInt("choiceIndex");
				String choiceName = choiceResult.getString("choiceName");
				// pull out the values from the DATABASE

				DLChoice c = new DLChoice(choiceIndex, choiceName);
				d.addDLChoice(c);
			}

			pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT id, leftChoice, rightChoice, height FROM edges WHERE id = ?;");
			pstmt.setString(1, id);

			// Execute the SQL statement and store result into the ResultSet
			ResultSet edgeResult = pstmt.executeQuery();

			// no meeting? Return null
			while (edgeResult.next()) {

				int leftChoice = edgeResult.getInt("leftChoice");
				int rightChoice = edgeResult.getInt("rightChoice");
				int height = edgeResult.getInt("height");
				// pull out the values from the DATABASE

				Edge e = new Edge(leftChoice, rightChoice, height);
				d.addEdge(e);
			}
			return d;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// some problem...
		return null;
	}
	/**Change the completion status of the event with given id*/
	// This now works
	public static boolean setCompletion(String id) {

		PreparedStatement pstmt;
		try {
			pstmt = Manager
					.getConnection()
					.prepareStatement(
							"UPDATE DLEvents SET isComplete = true WHERE id = ?;");
			pstmt.setString(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

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

	/**
	 * Remove event from the database along with any corresponding users, choices, and edges.
	 * 
	 * TODO: Not sure this is the best way to delete users, choices, and edges.
	 * 
	 * @param eventID
	 * @return true if the deletions were successful; false otherwise
	 */
	public static boolean deleteEvent(String meetingID) {
		try {
			PreparedStatement pstmt = Manager.getConnection().prepareStatement(
					"DELETE from DLEvents WHERE id = ?;");
			pstmt.setString(1, meetingID);

			// Execute the SQL statement and update database accordingly.
			pstmt.executeUpdate();

			int numDeleted = pstmt.getUpdateCount();
			if (numDeleted == 0) {
				return false;
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		return deleteUsers(meetingID) && deleteChoices(meetingID) && deleteEdges(meetingID);
	}

	public static boolean deleteUsers(String meetingID) {
		try {
			PreparedStatement pstmt = Manager.getConnection().prepareStatement(
					"DELETE from users WHERE id = ?;");
			pstmt.setString(1, meetingID);

			// Execute the SQL statement and update database accordingly.
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

	public static boolean deleteChoices(String meetingID) {
		try {
			PreparedStatement pstmt = Manager.getConnection().prepareStatement(
					"DELETE from choices WHERE id = ?;");
			pstmt.setString(1, meetingID);

			// Execute the SQL statement and update database accordingly.
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

	public static boolean deleteEdges(String meetingID) {
		try {
			PreparedStatement pstmt = Manager.getConnection().prepareStatement(
					"DELETE from edges WHERE id = ?;");
			pstmt.setString(1, meetingID);

			// Execute the SQL statement and update database accordingly.
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

	/** Make sure string is no longer than the given length. */
	public static String trimString(String s, int len) {
		if (s.length() <= len) {
			return s;
		}

		return s.substring(0, len);
	}

}