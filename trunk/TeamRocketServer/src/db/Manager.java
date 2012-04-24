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

public class Manager {
	/**
	 * Interface to the MySQL database.
	 * 
	 * updates to meetings/participants/availability
	 * 
	 * @author gdmcconnell
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

		// Define URL for database server
		// NOTE: must fill in DATABASE NAME
		String url = "jdbc:" + DATABASE_TYPE + "://"
				+ dbConfig.getProperty(SERVER) + "/"
				+ dbConfig.getProperty(DATABASE);

		try {
			// Get a connection to the database for a
			// user with the given user name and password.
			con = DriverManager.getConnection(url, dbConfig.getProperty(USER),
					dbConfig.getProperty(PASSWORD));
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
	public static boolean insertDLEvent(String id, DLEvent d) {
		try {
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"INSERT into dlevents(id, numChoices, numRounds, eventQuestion, dateCreated, isOpen) VALUES(?,?,?,?,?,?);");
			pstmt.setString(1, id);
			pstmt.setInt(2, d.getNumChoices());
			pstmt.setInt(3, d.getNumRounds());
			pstmt.setString(4, trimString(d.getEventQuestion(), 32)); 	// no more than 32 characters.
			pstmt.setDate(5, d.getDateCreated());
			pstmt.setInt(6, d.getIsOpen());								// no more than 4 characters (OPEN or CLOSE)

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
							"SELECT password FROM users WHERE id = ? and user=?;");
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
							"INSERT into users(id,user,password,isModerator, userIndex) VALUES(?,?,?,?,?);");
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
							"SELECT id, numChoices, numRounds, eventQuestion, dateCreated, isOpen FROM DLEvents WHERE id = ?;");
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

			// construct meeting and return it
			DLEvent d = new DLEvent(id, String name, String question, int numChoices, int numRounds));

			// TODO: Get all availability information and participants and all
			// that...

			// For each participant in this meeting....
			// check that participant not already in meeting with different
			// pasword.
			PreparedStatement qstmt = Manager.getConnection().prepareStatement(
					"SELECT user,password FROM participants WHERE id = ?;");
			qstmt.setString(1, id);

			// Execute the SQL statement and store result into the ResultSet
			ResultSet qresult = qstmt.executeQuery();

			// If there is a participant
			while (qresult.next()) {
				// check the password.
				String existingUser = qresult.getString("user");
				String existingPassword = qresult.getString("password");

				// it will be ok that "" is returned for null passwords.
				m.signIn(existingUser, existingPassword);
			}

			for (Iterator<String> it = m.getParticipantIDs(); it.hasNext();) {
				String user = it.next();
				// check that participant not already in meeting with different
				// pasword.
				PreparedStatement rstmt = Manager
						.getConnection()
						.prepareStatement(
								"SELECT col,row FROM availability WHERE id = ? and user = ?;");
				rstmt.setString(1, id);
				rstmt.setString(2, user);

				// Execute the SQL statement and store result into the ResultSet
				ResultSet rresult = rstmt.executeQuery();

				// If there is an available slot, act on it
				while (rresult.next()) {
					// check the password.
					int col = rresult.getInt("col");
					int row = rresult.getInt("row");
					m.select(user, col, row);
				}
			}

			return m;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// some problem...
		return null;
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
		deleteUsers(meetingID);
		deleteChoices(meetingID);
		deleteEdges(meetingID);

		return true;
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