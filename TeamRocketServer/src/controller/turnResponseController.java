package controller;

import model.DLChoice;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import db.Manager;
import server.ClientState;
import xml.Message;
import model.TeamRocketServerModel;

public class turnResponseController {
	int nextPosition;
	TeamRocketServerModel model = TeamRocketServerModel.getInstance(); // retrieve the singleton


public void process(String id){
	int master = model.getTable().get(id).getCurrentMaster();
	/* This isnt done but i need to go now need to figure out how to retrieve the proper client state, keep a list of them in the event,
	 * or do that in the protocol handler?
	 */
	}
}