package objectIO.netObject;

import java.util.LinkedHashMap;

import objectIO.connection.Connection;
import objectIO.markupMsg.MarkupMsg;

public class NetClass extends NetObject implements NetObjectControllerInterface{
	private LinkedHashMap<String, NetObject> objects;
	private MarkupMsg buffer = new MarkupMsg();
	private long currentConnection = -1;
	
	public NetClass(NetObjectController controller, String name, int size) {
		super(controller, name);
		objects = new LinkedHashMap<String, NetObject>(size);
	}

	public void parseUpdate(MarkupMsg msg,  Connection c) {
		for (MarkupMsg child : msg.child) {
			NetObject obj = objects.get(child.name);
			obj.parseUpdate(child, c);
		}
	}

	 public boolean syncObject(NetObject obj) {
		boolean ret = (objects.put(obj.id, obj) == null);
		return ret;
	}

	public boolean unsyncObject(NetObject obj) {
		return (objects.remove(obj.id) != null);
	}

	public void sendUpdate(MarkupMsg msg, NetObject obj, long connectionId) {
		if (currentConnection != connectionId) {
			flushBuffer();
			currentConnection = connectionId;
		}
		msg.name = obj.id;
		buffer.child.add(msg);
	}
	
	public void update() {
		if (buffer.child.isEmpty() == false)
			flushBuffer();
	}
	
	private void flushBuffer() {
		controller.sendUpdate(buffer, this, currentConnection);
		buffer = new MarkupMsg();
	}
}
