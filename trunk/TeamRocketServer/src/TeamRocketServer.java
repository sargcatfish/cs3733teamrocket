import java.io.IOException;

import server.Server;
import xml.Message;


public class TeamRocketServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (!Message.configure("decisionlines.xsd")) {
			System.exit(0);
		}
		
		// Start server and have ProtocolHandler be responsible for all XML messages.
		Server server = new Server(new DecisionLineProtocolHandler(), 9371);
	
		try {
			server.bind();
		} catch (IOException ioe) {
			System.err.println("Unable to launch server:" + ioe.getMessage());
			System.exit(-1);
		}

		// process all requests and exit.
		System.out.println("Server awaiting client connections");
		try {
			server.process();
			System.out.println("Server shutting down.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}    
	}

}
