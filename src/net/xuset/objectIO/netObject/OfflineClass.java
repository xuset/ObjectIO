package net.xuset.objectIO.netObject;

import net.xuset.objectIO.connections.ConnectionI;
import net.xuset.objectIO.markupMsg.MarkupMsg;

public class OfflineClass extends NetClass {

	public OfflineClass() {
		super(null, null, 0);
	}

	//@Override
	//public MarkupMsg getValue() { return new MarkupMsg(); }

	@Override
	public void clearUpdateBuffer() { }

	//@Override
	//public void setValue(MarkupMsg msg) { }

	@Override
	public void distributeAllUpdates() { }

	@Override
	protected void parseUpdate(MarkupMsg msg,  ConnectionI c) { }

	//@Override
	//public boolean syncObject(NetObject obj) { return true; }

	//@Override
	//public boolean unsyncObject(NetObject obj) { return true; }

	@Override
	public void sendUpdate(MarkupMsg msg, NetObject obj, long connectionId) { }

	@Override
	public void update() { }
	
	@Override
	public void removeUpdater() { }

	@Override
	protected void sendUpdate(MarkupMsg data, long connectionId) { }

	@Override
	public void setUpdater(NetObjUpdater controller) { }

}
