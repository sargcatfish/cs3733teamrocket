package model;
import db.Manager;
import server.ClientState;
import xml.Message;

public class MockClient implements ClientState {

	@Override
	public boolean sendMessage(Message m) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object setData(Object newData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String id() {
		// TODO Auto-generated method stub
		return  Manager.generateEventID();
	}

}
