package objectIO.connection;

import java.util.Iterator;
import java.util.LinkedList;

import objectIO.markupMsg.MarkupMsg;

public class ConnectionHub<T extends Connection> implements Hub<T> {
	private long myId;
	
	protected LinkedList<T> connections = new LinkedList<T>();
	
	public LinkedList<T> getAllConnections() { return connections; }
	public long getId() { return myId; }

	public ConnectionHub(long id) {
		myId = id;
	}
	
	public boolean addConnection(T connection) {
		return connections.add(connection);
	}
	
	public T getConnection(long id) {
		for (T c : connections) {
			if (c.getEndPointId() == id)
				return c;
		}
		return null;
	}
	
	private Iterator<T> iterator = connections.iterator();
	public MarkupMsg getNextMessage() {
		if (iterator.hasNext()) {
			T c = iterator.next();
			if (c.messageAvailable())
				return c.getNextMessage();
		} else {
			iterator = connections.iterator();
		}
		return null;
	}
	
	public boolean sendMessage(MarkupMsg message, long connectionId) {
		if (connectionId == AbstractConnection.BROADCAST_CONNECTION)
			return broadcastMessage(message);
		Connection c = getConnection(connectionId);
		if (c != null)
			return c.sendMessage(message);
		return false;
	}
	
	protected boolean broadcastMessage(MarkupMsg message) {
		boolean allSuccessful = true;
		for (Connection c : connections) {
			boolean success = c.sendMessage(message);
			if (success == false && allSuccessful == true)
				allSuccessful = false;
		}
		return allSuccessful;
	}
}
