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
	public static String generateMeetingID() {
		String id = UUID.randomUUID().toString();

		// abcdefgh-abcd
		return id.substring(0, 13);
	}

	/** Insert meeting into database. */
	public static boolean insertMeeting(String id, Meeting m) {
		try {
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"INSERT into meetings(id,name,startH,numColumns,numRows) VALUES(?,?,?,?,?);");
			pstmt.setString(1, id);
			pstmt.setString(2, trimString(m.eventName, 32)); // no more than 32
																// characters.
			pstmt.setInt(3, m.startH);
			pstmt.setInt(4, m.numColumns);
			pstmt.setInt(5, m.numRows);

			// Execute the SQL statement and update database accordingly.
			pstmt.executeUpdate();

			int numInserted = pstmt.getUpdateCount();
			if (numInserted == 0) {
				throw new IllegalArgumentException("Unable to insert meeting "
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
	public static boolean signin(String meetingID, String user, String password) {

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
							"SELECT password FROM participants WHERE id = ? and user=?;");
			pstmt.setString(1, meetingID);
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
							"INSERT into participants(id,user,password) VALUES(?,?,?);");
			pstmt.setString(1, meetingID);
			pstmt.setString(2, user);
			pstmt.setString(3, password);

			// Execute the SQL statement and update database accordingly.
			pstmt.executeUpdate();

			int numInserted = pstmt.getUpdateCount();
			if (numInserted == 0) {
				throw new IllegalArgumentException(
						"Unable to insert participant for " + meetingID);
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
	 */
	public static Meeting retrieveMeeting(String id) {
		try {
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"SELECT id,name,startH,numColumns,numRows FROM meetings WHERE id = ?;");
			pstmt.setString(1, id);

			// Execute the SQL statement and store result into the ResultSet
			ResultSet result = pstmt.executeQuery();

			// no meeting? Return null
			if (!result.next()) {
				return null;
			}

			// pull out the values from the DATABASE
			String eventName = result.getString("name");
			int startH = result.getInt("startH");
			int numColumns = result.getInt("numColumns");
			int numRows = result.getInt("numRows");

			// construct meeting and return it
			Meeting m = new Meeting(numColumns, numRows, startH, eventName);

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

	/**
	 * if available is TRUE then we are adding tuples to the database. ==>
	 * INSERT if available is FALSE then we are removing tuples from the
	 * database. ==> DELETE
	 * 
	 */
	public static boolean updateAvailability(String id, String user,
			boolean available, int startCol, int startRow, int endCol,
			int endRow) {

		// delete tuples for entire range EVEN WHEN adding. This makes adding
		// immediate and obvious.
		try {
			PreparedStatement pstmt = Manager
					.getConnection()
					.prepareStatement(
							"DELETE from availability WHERE id = ? and user = ? and col >= ? and col <= ? and row >= ? and row <= ?;");
			pstmt.setString(1, id);
			pstmt.setString(2, user);
			pstmt.setInt(3, startCol);
			pstmt.setInt(4, endCol);
			pstmt.setInt(5, startRow);
			pstmt.setInt(6, endRow);

			// Execute the SQL statement and update database accordingly.
			pstmt.executeUpdate();

			int numDeleted = pstmt.getUpdateCount();
			if (numDeleted == 0) {
				if (!available) {
					throw new IllegalArgumentException(
							"Unable to delete availability: " + id);
				}
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		// HEY, I was trying to remove availability so leave now.
		if (!available) {
			return true;
		}

		// available
		try {
			for (int c = startCol; c <= endCol; c++) {
				StringBuilder sql = new StringBuilder(
						"INSERT into availability(id, user, col, row) VALUES");
				for (int r = startRow; r <= endRow; r++) {
					sql.append("(?,?," + c + "," + r + ")");
					if (r < endRow) {
						sql.append(",");
					} else {
						sql.append(";");
					} // be sure to terminate sql
				}

				// bulk-prepare all inserts by setting id/user
				PreparedStatement pstmt = Manager.getConnection()
						.prepareStatement(sql.toString());
				int idx = 1;
				for (int r = startRow; r <= endRow; r++) {
					pstmt.setString(idx, id);
					pstmt.setString(idx + 1, user);
					idx += 2;
				}

				// Execute the SQL statement and update database accordingly.
				pstmt.executeUpdate();

				int numInserted = pstmt.getUpdateCount();
				if (numInserted == 0) {
					throw new IllegalArgumentException(
							"Unable to insert availability: " + id);
				}
			}

		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		return true;
	}

	/**
	 * Remove meeting from the database.
	 * 
	 * TODO: Make sure you also eliminate from participants and availability
	 * 
	 * @param meetingID
	 * @return
	 */
	public static boolean deleteMeeting(String meetingID) {
		try {
			PreparedStatement pstmt = Manager.getConnection().prepareStatement(
					"DELETE from meetings WHERE id = ?;");
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