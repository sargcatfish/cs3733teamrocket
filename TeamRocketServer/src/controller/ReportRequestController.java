package controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.TeamRocketServerModel;
import model.Admin;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;

import server.ClientState;
import xml.Message;

/**
 * Controller for the administrator to generate reports
 * @author 
 *
 */
public class ReportRequestController {

	/** Client state: used to get the client id and other information identifying the individual client	 */
	ClientState state;

	/**
	 * Constructor for ReportRequestController
	 * @param st ClientState to be set
	 */
	public ReportRequestController(ClientState st) {
		this.state = st;
	}

	/**
	 * Processing function to parse the request and generate the response 
	 * @param request The request from the client to respond to
	 * @return The generated response
	 */
	public Message process(Message request) {
		Node signInR = request.contents.getFirstChild();

		NamedNodeMap adminAtts = signInR.getAttributes();
		String key = adminAtts.getNamedItem("key").getNodeValue();
		String eventType = adminAtts.getNamedItem("type").getNodeValue();

		Admin a = TeamRocketServerModel.getInstance().getAdmin();
		String xmlString = "";
		Message response;
		ResultSet m;

		if (!a.verify(key)) {
			xmlString = Message.responseHeader(request.id(), "Invalid key")
					+ "<reportResponse></reportResponse></response>";
			response = new Message(xmlString);
		} else {
			if (eventType.equals("open")) {
				m = Manager.retrieveEvent(true);
				try {
					xmlString = Message.responseHeader(request.id())
							+ "<reportResponse>";
					if (!(m == null)) {
						while (m.next()) {
							xmlString = xmlString + "<entry " + "id=\""
									+ m.getString("id") + "\" "
									+ "type = \"open\" " + "question = \""
									+ m.getString("eventQuestion") + "\" "
									+ "numChoices = \""
									+ m.getInt("numChoices") + "\" "
									+ "numRounds = \"" + m.getInt("numRounds")
									+ "\" " + "created = \""
									+ m.getDate("dateCreated") + "\" "
									+ "completed = \""
									+ m.getBoolean("isComplete") + "\" " + 
									"/>";

						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				xmlString = xmlString + "</reportResponse></response>";
				response = new Message(xmlString);
			}

			else {
				m = Manager.retrieveEvent(false);
				try {
					xmlString = Message.responseHeader(request.id())
							+ "<reportResponse>";
					if (!(m == null)) {
						while (m.next()) {
							xmlString = xmlString + "<entry " + "id=\""
									+ m.getString("id") + "\" "
									+ "type = \"closed\" " + "question = \""
									+ m.getString("eventQuestion") + "\" "
									+ "numChoices = \""
									+ m.getInt("numChoices") + "\" "
									+ "numRounds = \"" + m.getInt("numRounds")
									+ "\" " + "created = \""
									+ m.getDate("dateCreated") + "\" "
									+ "completed = \""
									+ m.getBoolean("isComplete") + "\" " + 
									"/>";

						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				xmlString = xmlString + "</reportResponse></response>";
				response = new Message(xmlString);
			}
		}
		return response;
	}
}
