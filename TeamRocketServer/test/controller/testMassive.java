package controller;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.TestCase;
import model.DLEvent;
import model.TeamRocketServerModel;
import xml.Message;
import db.Manager;

public class testMassive extends TestCase {

	AddChoiceController addChoice = new AddChoiceController(null);
	CreateRequestController createRequest = new CreateRequestController(null);
	SignInRequestController signIn = new SignInRequestController(null);
	String question = "How about a pineapple kid?";
	int numChoices = 3;
	int numRounds = 4;
	String choices[] = {"No Escape!", "Dont Matter Kid", "Help me!", "Karl to the resue!"};
	int position = 0;
	
	
	
	public void testLots(){
	Message.configure("decisionlines.xsd");	
	String xmlSource = "<request version='1.0' id='test'><createRequest type='open' " +
				"question='" + question + "' numChoices='" + numChoices + "' numRounds='" + numRounds + "'>" +
				"<user name='Nick Bosowski'/></createRequest></request>";
		 
	Message request = new Message(xmlSource);
	Message response = createRequest.process(request);
	String key = response.contents.getFirstChild().getAttributes().getNamedItem("id").getNodeValue();
	
	String id = createRequest.testId;
	
	assertEquals(key,id);
	
	DLEvent dbEvent = Manager.retrieveEvent(id);
	DLEvent localEvent = TeamRocketServerModel.getInstance().getTable().get(id);
	
	assertEquals(numChoices, dbEvent.getNumChoices());
	assertEquals(numChoices, localEvent.getNumChoices());
	
	assertEquals(numRounds, dbEvent.getNumRounds());
	assertEquals(numRounds, localEvent.getNumRounds());
	
	assertEquals(0,dbEvent.getDLChoice().size());
	assertEquals(0,localEvent.getDLChoice().size());
	
	xmlSource = "<request version='1.0' id='test'><addChoiceRequest id='"+ id +"' number='"+ position + "' choice='" +
			choices[position++] + "'/></request>";
	 request = new Message(xmlSource);
	 response = addChoice.process(request);
	 
	 dbEvent = Manager.retrieveEvent(id);
	 localEvent = TeamRocketServerModel.getInstance().getTable().get(id);
	 
	 assertEquals(1,dbEvent.getDLChoice().size());
	 assertEquals(1,localEvent.getDLChoice().size());
	 
	 Node first = response.contents.getFirstChild();
	 NamedNodeMap map = first.getAttributes();
	 
	 assertEquals(position-1, Integer.parseInt(map.getNamedItem("number").getNodeValue()));
	 assertEquals(id, map.getNamedItem("id").getNodeValue());
	 assertEquals(choices[position-1], map.getNamedItem("choice").getNodeValue());
	 
	 
	 xmlSource = "<request version='1.0' id='test'>" +
				"<signInRequest id='" + id + "'>" +
				"<user name='Wesley'/>" + "</signInRequest></request>";
	 request = new Message(xmlSource);
	 response = signIn.process(request);
	
	 first = response.contents.getFirstChild();
	 map = first.getAttributes();
	 
	 assertEquals(id, map.getNamedItem("id").getNodeValue());
	 assertEquals("open", map.getNamedItem("type").getNodeValue());
	 assertEquals(question, map.getNamedItem("question").getNodeValue());
	 assertEquals(numChoices, Integer.parseInt(map.getNamedItem("numChoices").getNodeValue()));
	 assertEquals(numRounds, Integer.parseInt(map.getNamedItem("numRounds").getNodeValue()));
	 assertEquals(position, Integer.parseInt(map.getNamedItem("position").getNodeValue()));
	 
	 NodeList list = first.getChildNodes();
	 int length = list.getLength();
	 for(int i = 0; i<length;i++){
		 first = list.item(i);
		 map = first.getAttributes();
		 
		 assertEquals(choices[i], map.getNamedItem("value").getNodeValue());
		 assertEquals(i, Integer.parseInt(map.getNamedItem("index").getNodeValue()));
	 }
	 
	 
	 	xmlSource = "<request version='1.0' id='test'><addChoiceRequest id='"+ id +"' number='"+ position + "' choice='" +
				choices[position++] + "'/></request>";
		 request = new Message(xmlSource);
		 response = addChoice.process(request);
		 
		 
		 dbEvent = Manager.retrieveEvent(id);
		 localEvent = TeamRocketServerModel.getInstance().getTable().get(id);
			
		assertEquals(2,dbEvent.getDLChoice().size());
		assertEquals(2,localEvent.getDLChoice().size());
		 
		 
		 first = response.contents.getFirstChild();
		 map = first.getAttributes();
		 
		 assertEquals(position-1, Integer.parseInt(map.getNamedItem("number").getNodeValue()));
		 assertEquals(id, map.getNamedItem("id").getNodeValue());
		 assertEquals(choices[position-1], map.getNamedItem("choice").getNodeValue());
	
		 xmlSource = "<request version='1.0' id='test'>" +
					"<signInRequest id='" + id + "'>" +
					"<user name='Ian'/>" + "</signInRequest></request>";
		 request = new Message(xmlSource);
		 response = signIn.process(request);
		 first = response.contents.getFirstChild();
		 map = first.getAttributes();
		 
		 assertEquals(id, map.getNamedItem("id").getNodeValue());
		 assertEquals("open", map.getNamedItem("type").getNodeValue());
		 assertEquals(question, map.getNamedItem("question").getNodeValue());
		 assertEquals(numChoices, Integer.parseInt(map.getNamedItem("numChoices").getNodeValue()));
		 assertEquals(numRounds, Integer.parseInt(map.getNamedItem("numRounds").getNodeValue()));
		 assertEquals(position, Integer.parseInt(map.getNamedItem("position").getNodeValue()));
		 
		 list = first.getChildNodes();
		 length = list.getLength();
		 int temp = 0;
		 for(int i = 0; i<length;i++){
			 first = list.item(i);
			 map = first.getAttributes();
			 temp = Integer.parseInt(map.getNamedItem("index").getNodeValue());
			 assertEquals(choices[temp], map.getNamedItem("value").getNodeValue());
		 }	 
		 
		 
		 dbEvent = Manager.retrieveEvent(id);
		 localEvent = TeamRocketServerModel.getInstance().getTable().get(id);
			
		assertEquals(false, dbEvent.isAccepting());
		assertEquals(false, localEvent.isAccepting());
		assertEquals(2,dbEvent.getDLChoice().size());
		assertEquals(2,localEvent.getDLChoice().size());
		
		xmlSource = "<request version='1.0' id='test'><addChoiceRequest id='"+ id +"' number='"+ position + "' choice='" +
				choices[position++] + "'/></request>";
		 request = new Message(xmlSource);
		 response = addChoice.process(request);
		
		dbEvent = Manager.retrieveEvent(id);
		localEvent = TeamRocketServerModel.getInstance().getTable().get(id);
		 
		assertEquals(3,dbEvent.getDLChoice().size());
		assertEquals(3,localEvent.getDLChoice().size());
		 
		 Manager.deleteEvent(id);
		 TeamRocketServerModel.destroyEvent(id);
	}
}



