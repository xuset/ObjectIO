package net.xuset.objectIO.netObject;

import java.util.concurrent.ConcurrentHashMap;

import net.xuset.objectIO.connections.ConnectionI;
import net.xuset.objectIO.connections.HubI;
import net.xuset.objectIO.markupMsg.MarkupMsg;



public class StandardObjUpdater implements NetObjUpdater{
	private final ConcurrentHashMap<String, NetObject> objects =
			new ConcurrentHashMap<String, NetObject>();
	private final HubI<?> hub;
	private boolean run = true;
	
	public int threadSleep = 10;
	
	public StandardObjUpdater(HubI<?> hub) {
		this.hub = hub;
	}
	
	public void setRunning(boolean running) {
		run = running;
	}
	
	public NetObject getById(String id) {
		return objects.get(id);
	}

	@Override
	public boolean registerNetObj(NetObject obj) {
		if (objects.containsKey(obj.getId()))
			throw new HashMapKeyCollision(obj.getId() + " has already been used");
		return (objects.put(obj.getId(), obj) == null);
	}

	@Override
	public boolean unregisterNetObj(NetObject obj) {
		return (objects.remove(obj.getId()) != null);
	}

	@Override
	public void sendUpdate(MarkupMsg msg, NetObject object, long connectionId) {
		msg.setName(object.getId());
		hub.sendMsg(msg, connectionId);
	}

	@Override
	public void distributeAllUpdates() {
		for (int i = 0; i < hub.getConnectionCount(); i++) {
			ConnectionI c = hub.getConnectionByIndex(i);
			distrubute(c);
		}
	}
	
	private void distrubute(ConnectionI c) {
		MarkupMsg msg = null;
		while (run && c.isMsgAvailable() && (msg = c.pollNextMsg()) != null) {
			distributeUpdate(msg, c);
		}
	}

	@Override
	public boolean distributeUpdate(MarkupMsg msg, ConnectionI con) {
		NetObject obj = objects.get(msg.getName());
		if (obj != null) {
			obj.parseUpdate(msg, con);
			return true;
		}
		return false;
	}
}
