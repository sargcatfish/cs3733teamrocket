package db;

public class db_testing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String id = Manager.generateEventID();
		//		Manager.connect();
		//Manager.insertDLEvent(id, 4, 6, "Does Wesley have AIDS?", java.sql.Date.valueOf("2012-4-24"), false, "Nick");
			Manager.insertEdge(id, 20, 25, 16);
	}

}
