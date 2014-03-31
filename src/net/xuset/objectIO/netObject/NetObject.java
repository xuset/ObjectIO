package net.xuset.objectIO.netObject;

import net.xuset.objectIO.connections.ConnectionI;
import net.xuset.objectIO.markupMsg.MarkupMsg;

public abstract class NetObject {
	private NetObjUpdater updater;
	private final String id;

	protected abstract void parseUpdate(MarkupMsg msg, ConnectionI c);
	public abstract MarkupMsg getValue();
	
	public String getId() { return id; }
	
	public NetObject(NetObjUpdater updateer, String id) {
		this.updater = updateer;
		this.id = id;
		if (updateer != null)
			updateer.registerNetObj(this);
	}
	
	protected void sendUpdate(MarkupMsg data, long connectionId) {
		if (updater != null)
			updater.sendUpdate(data, this, connectionId);
	}
	
	public void setUpdater(NetObjUpdater updater) {
		removeUpdater();
		this.updater = updater;
		updater.registerNetObj(this);
	}
	
	public void removeUpdater() {
		if (updater != null)
			updater.unregisterNetObj(this);
		updater = null;
	}
	
	public boolean hasUpdater() { return updater != null; }
	
}
