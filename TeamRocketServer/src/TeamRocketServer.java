import java.io.IOException;

import server.Server;
import xml.Message;

/**
 * Main file for Team Rocket server
 * @author Team Rocket
 *
 */
public class TeamRocketServer {

	/**
	 * Main function
	 * @param args No arguments
	 */
	public static void main(String[] args) {
		
		if (!Message.configure("decisionlines.xsd")) {
			System.exit(0);
		}
		 
		/**
		 * Start server and have ProtocolHandler be responsible for all XML messages.
		 */
		Server server = new Server(new DecisionLineProtocolHandler(), 9371);
	
		try {
			server.bind();
		} catch (IOException ioe) {
			System.err.println("Unable to launch server:" + ioe.getMessage());
			System.exit(-1);
		}

		/**
		 * process all requests and exit.
		 */
		System.out.println("Server awaiting client connections");
		try {
			server.process();
			System.out.println("Server shutting down.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}    
	}

}
