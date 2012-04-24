package db;

public class db_testing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String id = Manager.generateEventID();
		System.out.print(Manager.connect() + "\n");
		//Manager.insertDLEvent(id, 4, 6, "Does Wesley have AIDS?", java.sql.Date.valueOf("2012-4-24"), false, "Nick");
	}

}
