package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import db.Manager;
import model.*;

public class dfdsfskj {

		public static boolean insertChoice(String id, int choiceIndex, String choiceName, String name, DLEvent event){
			try {
				PreparedStatement pstmt = Manager.getConnection().prepareStatement(
						"INSERT into choices(id,choiceIndex,choiceName,name) VALUES(?,?,?,?);");
				pstmt.setString(1, id);
				pstmt.setInt(2, choiceIndex);
				pstmt.setString(3, choiceName);
				pstmt.setString(4,name);
				
				pstmt.executeUpdate();
				
				int numInserted = pstmt.getUpdateCount();
				if (numInserted == 0) {
					throw new IllegalArgumentException("Unable to insert choice "
							+ id + ".");
				}
			} catch (SQLException e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
			event.addDLChoice(new DLChoice(choiceIndex, choiceName, name));
			return true;
		}
}
