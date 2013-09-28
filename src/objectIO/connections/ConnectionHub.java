package objectIO.connections;

import java.util.LinkedList;

import objectIO.markupMsg.MarkupMsg;

public class ConnectionHub<T extends Connection> implements Hub<T> {
	private long myId;
	
	protected LinkedList<T> connections = new LinkedList<T>();
	
	@Override public LinkedList<T> getAllConnections() { return connections; }
	@Override public long getId() { return myId; }

	public ConnectionHub(long id) {
		myId = id;
	}
	
	@Override
	public boolean addConnection(T connection) {
		return connections.add(connection);
	}
	
	@Override
	public T getConnection(long id) {
		for (T c : connections) {
			if (c.getEndId() == id)
				return c;
		}
		return null;
	}
	
	@Override
	public boolean sendMsg(MarkupMsg message, long connectionId) {
		if (connectionId == Connection.BROADCAST_CONNECTION)
			return broadcastMsg(message);
		Connection c = getConnection(connectionId);
		if (c != null)
			return c.sendMsg(message);
		return false;
	}
	
	@Override
	public boolean broadcastMsg(MarkupMsg message) {
		boolean allSuccessful = true;
		for (Connection c : connections) {
			boolean success = c.sendMsg(message);
			if (success == false && allSuccessful == true)
				allSuccessful = false;
		}
		return allSuccessful;
	}
}
