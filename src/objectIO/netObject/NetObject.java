package objectIO.netObject;

import objectIO.connections.Connection;
import objectIO.markupMsg.MarkupMsg;

public abstract class NetObject {
	protected ObjControllerI controller;
	protected String id;

	protected abstract void parseUpdate(MarkupMsg msg, Connection c);
	
	public String getId() { return id; }
	
	public NetObject(ObjControllerI controller, String id) {
		this.controller = controller;
		this.id = id;
		if (controller != null)
			controller.syncObject(this);
	}
	
	public void remove() {
		controller.unsyncObject(this);
	}
	
}
