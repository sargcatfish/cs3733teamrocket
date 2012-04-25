package db;

public class db_testing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//String id = Manager.generateEventID();
			String id = "aaaaaaaaaaaaa";
	/* INSERT THE RANDOM TEST GARBAGE! */
			
		Manager.insertDLEvent(id, 4, 6, "Does Wesley have AIDS?", java.sql.Date.valueOf("2012-4-24"), false,true, "Nick");
		Manager.insertEdge(id, 20, 25, 16);
		Manager.insertChoice(id, 2, "Yes, Wesley has AIDS!");
		Manager.signin(id, "Nick", "Chicken is delicious", true, 1);
//		Manager.setCompletion(id);
	/* DELETE THE RANDOM TEST GARBAGE! */		
//		Manager.deleteEvent(id);
//		Manager.deleteChoices(id);
//		Manager.deleteUsers(id);
//		Manager.deleteEdges(id);
	}

}
