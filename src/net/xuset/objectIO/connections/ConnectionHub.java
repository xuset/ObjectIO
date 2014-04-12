package net.xuset.objectIO.connections;

import java.util.ArrayList;

import net.xuset.objectIO.markupMsg.MarkupMsg;

/**
 * A basic implementation of <code>HubI</code>.
 * 
 * @author xuset
 * @param <T> the type of <code>Connection</code> that will be stored
 * @see Connection
 * @since 1.0
 */

public class ConnectionHub<T extends Connection> implements Hub<T> {
	
	private final ArrayList<T> connections = new ArrayList<T>();
	
	@Override
	public boolean addConnection(T connection) {
		return connections.add(connection);
	}

	@Override
	public boolean removeConnection(T connection) {
		return connections.remove(connection);
	}
	
	@Override
	public T getConnectionById(long id) {
		for (T c : connections) {
			if (c.getId() == id)
				return c;
		}
		return null;
	}
	
	
	/**
	 * Sends a message to a stored connection whose id equals <code>connectionId</code>
	 * If there are multiple connections with the same id, only the connection that was
	 * added first gets sent the message. If the <code>connectionId</code> equals
	 * {@link Connection#BROADCAST_CONNECTION} then {@link #broadcastMsg(MarkupMsg)} is
	 * called and returned.
	 */
	@Override
	public boolean sendMsg(MarkupMsg message, long connectionId) {
		if (connectionId == Connection.BROADCAST_CONNECTION)
			return broadcastMsg(message);
		Connection c = getConnectionById(connectionId);
		if (c != null)
			return c.sendMsg(message);
		return false;
	}
	
	/**
	 * Sends a message to every stored connection.
	 * 
	 * @return true only if each message was sent successfully, false otherwise
	 */
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

	@Override
	public T getConnectionByIndex(int index) {
		return connections.get(index);
	}

	@Override
	public int getConnectionCount() {
		return connections.size();
	}
}
