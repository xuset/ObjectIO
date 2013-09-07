package objectIO.netObject;

import java.util.LinkedHashMap;
import java.util.Queue;

import objectIO.connection.Connection;
import objectIO.connection.Hub;
import objectIO.markupMsg.MarkupMsg;


public class NetObjectController implements NetObjectControllerInterface{
	private LinkedHashMap<String, NetObject> objects = new LinkedHashMap<String, NetObject>();
	private Hub<?> hub;
	
	public NetObjectController(Hub<?> talkhub) {
		this.hub = talkhub;
	}
	
	public NetObject getById(String id) {
		return objects.get(id);
	}
	
	public boolean syncObject(NetObject obj) {
		return (objects.put(obj.id, obj) == null);
	}

	public boolean unsyncObject(NetObject obj) {
		return (objects.remove(obj.id) != null);
	}
	
	public void sendUpdate(MarkupMsg msg, NetObject object, long connectionId) {
		msg.name = object.getId();
		hub.sendMessage(msg, connectionId);
	}
	
	public void distributeRecievedUpdates() {
		for (Connection c : hub.getAllConnections()) {
			distrubute(c);
		}
	}
	
	private void distrubute(Connection c) {
		Queue<MarkupMsg> queue = c.getMessageQueue();
		MarkupMsg msg = null;
		while ((msg = queue.poll()) != null) {
			NetObject obj = objects.get(msg.name);
			if (obj != null)
				obj.parseUpdate(msg, c);
			else
				System.out.println("Missing: " + msg.name + " content:" + msg.toString());
		}
	}
}
