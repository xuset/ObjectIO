package net.xuset.objectIO.netObject;

import net.xuset.objectIO.connections.Connection;
import net.xuset.objectIO.markupMsg.MarkupMsg;

public abstract class NetObject {
	private ObjControllerI controller;
	protected final String id;

	protected abstract void parseUpdate(MarkupMsg msg, Connection c);
	
	public String getId() { return id; }
	
	public NetObject(ObjControllerI controller, String id) {
		this.controller = controller;
		this.id = id;
		if (controller != null)
			controller.syncObject(this);
	}
	
	public void remove() {
		if (controller != null)
			controller.unsyncObject(this);
		controller = null;
	}
	
	public MarkupMsg getValue() {
		return null;
	}
	
	protected void sendUpdate(MarkupMsg data, long connectionId) {
		if (controller != null)
			controller.sendUpdate(data, this, connectionId);
	}
	
	public void setController(ObjControllerI controller) {
		remove();
		this.controller = controller;
		controller.syncObject(this);
	}
	
	public boolean isSynced() { return controller != null; }
	
}
