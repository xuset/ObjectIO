package net.xuset.objectIO.netObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.xuset.objectIO.connections.ConnectionI;
import net.xuset.objectIO.markupMsg.MarkupMsg;



public class NetClass extends NetObject implements NetObjUpdater{
	private LinkedHashMap<String, NetObject> objects;
	private MarkupMsg buffer = new MarkupMsg();
	private long currentConnection = -1;
	
	public boolean autoUpdate = false;
	
	public NetClass(NetObjUpdater controller, String name, int size) {
		super(controller, name);
		objects = new LinkedHashMap<String, NetObject>(size);
	}

	@Override
	protected void parseUpdate(MarkupMsg msg,  ConnectionI c) {
		for (MarkupMsg child : msg.getNestedMsgs()) {
			NetObject obj = objects.get(child.getName());
			if (obj == null)
				System.out.println("Missing: " + msg.getName() + " content:" + msg.toString());
			else
				obj.parseUpdate(child, c);
		}
	}

	@Override
	public boolean registerNetObj(NetObject obj) {
		if (objects.containsKey(obj.getId()))
			throw new HashMapKeyCollision(obj.getId() + " has already been used");
		boolean ret = (objects.put(obj.getId(), obj) == null);
		return ret;
	}

		@Override
	public boolean unregisterNetObj(NetObject obj) {
		return (objects.remove(obj.getId()) != null);
	}

	@Override
	public void sendUpdate(MarkupMsg msg, NetObject obj, long connectionId) {
		if (currentConnection != connectionId) {
			flushBuffer();
			currentConnection = connectionId;
		}
		msg.setName(obj.getId());
		buffer.addNested(msg);
		if (autoUpdate)
			flushBuffer();
	}

	public void update() {
		if (buffer.getNestedMsgs().isEmpty() == false)
			flushBuffer();
	}
	
	@Override
	public MarkupMsg getValue() {
		MarkupMsg msg = new MarkupMsg();
		Iterator<Entry<String, NetObject>> it = objects.entrySet().iterator();
		while (it.hasNext()) {
			NetObject next = it.next().getValue();
			MarkupMsg value = next.getValue();
			value.setName(next.getId());
			if (value != null)
				msg.addNested(value);
		}
		return msg;
	}
	
	public void clearUpdateBuffer() {
		buffer = new MarkupMsg();
	}
	
	public void setValue(MarkupMsg msg) {
		if (msg == null)
			return;
		
		for (MarkupMsg c : msg.getNestedMsgs()) {
			String id = c.getName();
			objects.get(id).parseUpdate(c, null);
		}
	}
	
	private void flushBuffer() {
		if (buffer.getNestedMsgs().isEmpty())
			return;
		sendUpdate(buffer, currentConnection);
		buffer = new MarkupMsg();
	}

	@Override
	public void distributeAllUpdates() { }

	@Override
	public boolean distributeUpdate(MarkupMsg msg, ConnectionI con) {
		return false;
	}
}
