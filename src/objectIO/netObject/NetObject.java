package objectIO.netObject;

import objectIO.connections.Connection;
import objectIO.markupMsg.MarkupMsg;

public abstract class NetObject {
	protected NetObjectControllerInterface controller;
	protected String id;

	public abstract void parseUpdate(MarkupMsg msg, Connection c);
	
	public String getId() { return id; }
	
	public NetObject(NetObjectControllerInterface controller, String id) {
		this.controller = controller;
		this.id = id;
		controller.syncObject(this);
	}
	
	public void remove() {
		controller.unsyncObject(this);
	}
	
}
