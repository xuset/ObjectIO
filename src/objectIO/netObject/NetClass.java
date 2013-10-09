package objectIO.netObject;

import java.util.LinkedHashMap;
import java.util.Map;

import objectIO.connections.Connection;
import objectIO.markupMsg.MarkupMsg;

public class NetClass extends NetObject implements ObjControllerI{
	private LinkedHashMap<String, NetObject> objects;
	private MarkupMsg buffer = new MarkupMsg();
	private long currentConnection = -1;
	public boolean autoUpdate = true;
	
	public NetClass(ObjController controller, String name, int size) {
		super(controller, name);
		objects = new LinkedHashMap<String, NetObject>(size);
	}

	@Override
	protected void parseUpdate(MarkupMsg msg,  Connection c) {
		for (MarkupMsg child : msg.child) {
			NetObject obj = objects.get(child.name);
			obj.parseUpdate(child, c);
		}
	}

	@Override
	public boolean syncObject(NetObject obj) {
		boolean ret = (objects.put(obj.id, obj) == null);
		return ret;
	}

		@Override
	public boolean unsyncObject(NetObject obj) {
		return (objects.remove(obj.id) != null);
	}

	@Override
	public void sendUpdate(MarkupMsg msg, NetObject obj, long connectionId) {
		if (currentConnection != connectionId) {
			flushBuffer();
			currentConnection = connectionId;
		}
		msg.name = obj.id;
		buffer.child.add(msg);
		if (autoUpdate)
			flushBuffer();
	}

	public void update() {
		if (buffer.child.isEmpty() == false)
			flushBuffer();
	}
	
	private void flushBuffer() {
		if (buffer.child.isEmpty())
			return;
		controller.sendUpdate(buffer, this, currentConnection);
		buffer = new MarkupMsg();
	}
	
	@Override
	public void remove() {
		for (Map.Entry<String, NetObject> o: objects.entrySet()) {
			o.getValue().remove();
		}
	}
}
