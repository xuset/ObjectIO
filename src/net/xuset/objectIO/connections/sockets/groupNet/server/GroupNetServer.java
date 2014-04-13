package net.xuset.objectIO.connections.sockets.groupNet.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xuset.objectIO.connections.sockets.InetEventListener;
import net.xuset.objectIO.connections.sockets.InetHub;
import net.xuset.objectIO.connections.sockets.ServerEventListener;
import net.xuset.objectIO.connections.sockets.groupNet.GroupCmdCrafter;
import net.xuset.objectIO.connections.sockets.groupNet.GroupNetMsg;
import net.xuset.objectIO.connections.sockets.tcp.TcpAcceptor;
import net.xuset.objectIO.markupMsg.MarkupMsg;


/**
 * Server that GroupClientHub objects connect to through TCP. The connected hub's send
 * messages to this server that are intended for other connections so the message is
 * forwarded.
 * 
 * @author xuset
 * @see GroupServerCon
 * @see net.xuset.objectIO.connections.sockets.groupNet.client.GroupClientHub GroupClientHub
 * @since 1.0
 *
 */
public class GroupNetServer implements InetHub<GroupServerCon>{
	private static final Logger log = Logger.getLogger(GroupNetServer.class.getName());
	
	private final long localId;
	private final List<ServerEventListener<GroupServerCon>> eventListeners;
	private final Queue<GroupServerCon> connections;
	private final GroupNetAcceptor acceptor;
	private boolean isShutdown = false;
	
	/**
	 * Returns the port that the server accepts new connections on.
	 * 
	 * @return the port used to accept new connections.
	 */
	public int getPort() { return acceptor.getLocalPort(); }
	
	
	/**
	 * Returns the object used to accept and add new connections to the server.
	 * 
	 * @return the TcpAcceptor used by the server.
	 */
	public TcpAcceptor<?> getAcceptor() { return acceptor; }
	
	public GroupNetServer(long localId, int port) throws IOException {
		this.localId = localId;
		connections = new ConcurrentLinkedQueue<GroupServerCon>();
		eventListeners = new ArrayList<ServerEventListener<GroupServerCon>>();
		acceptor = new GroupNetAcceptor(this, port);
		log.log(Level.INFO, "new instance created with id=" + localId);
	}
	
	/**
	 * Checks the to field of the message and forwards it to the correct connection.
	 * 
	 * @param msg message to forward
	 */
	void forwardMsg(GroupNetMsg msg, byte[] rawMsg) {
		if (msg.isBroadcast())
			broadcastMsg(msg, rawMsg);
		else {
			GroupServerCon c = getConnectionById(msg.to());
			if (c != null) {
				c.sendRawAndFlush(rawMsg);
			} else {
				log.log(Level.WARNING, "Can't forward message." +
						"Connection(" + msg.to() + ") does not exist");
			}
		}
	}
	
	private void broadcastMsg(GroupNetMsg msg, byte[] rawMsg) {
		for (GroupServerCon c : connections) {
			if (msg.from() != c.getId()) {
				c.sendRawAndFlush(rawMsg);
			}
		}
	}
	
	private boolean broadcastMsg(GroupNetMsg broadcast) {
		boolean allTrue = true;
		for (int i = 0; i < getConnectionCount(); i++) {
			GroupServerCon c = getConnectionByIndex(i);
			broadcast.to(c.getId());
			if (!c.sendMsgAndFlush(broadcast))
				allTrue = false;
			
		}
		return allTrue;
	}
	
	
	/** Closes and removes all connections. This does not shutdown the server. */
	public void disconnectAll() {
		while (!connections.isEmpty()) {
			GroupServerCon c = getConnectionByIndex(0);
			c.close();
		}
	}

	@Override
	public boolean isShutdown() {
		return isShutdown;
	}
	
	@Override
	public void shutdown() {
		log.log(Level.INFO, "Shutdown() called");
		isShutdown = true;
		acceptor.stop();
		disconnectAll();
		notifyLastRemove();
		notifyShutdown();
	}
	
	@Override
	public GroupServerCon getConnectionById(long id) {
		for (GroupServerCon c : connections) {
			if (c.getId() == id)
				return c;
		}
		return null;
	}

	@Override
	public GroupServerCon getConnectionByIndex(int index) {
		int i = 0;
		for (GroupServerCon c : connections) {
			if (i == index)
				return c;
			i++;
		}
		return null;
	}

	@Override
	public int getConnectionCount() {
		return connections.size();
	}

	@Override
	public boolean addConnection(GroupServerCon connection) {
		log.log(Level.INFO, "Connection(" + connection.getId() + ") added");
		broadcastAddConnection(connection);
		boolean success = connections.add(connection);
		connection.watchEvents(new ConnectionEvent(connection));
		notifyAdd(connection);
		return success;
	}

	@Override
	public boolean removeConnection(GroupServerCon con) {
		log.log(Level.INFO, "Connection(" + con.getId() + ") removed");
		boolean success = connections.remove(con);
		broadcastMsg(GroupCmdCrafter.craftDisconnect(con.getId()));
		notifyRemove(con);
		if (connections.isEmpty())
			notifyLastRemove();
		
		return success;
	}

	@Override
	public boolean sendMsg(MarkupMsg message, long connectionId) {
		GroupServerCon c = getConnectionById(connectionId);
		if (c != null) {
			GroupNetMsg parent = craftGroupNetMsg(message, connectionId, getLocalId());
			return c.sendMsgAndFlush(parent);
		} else {
			log.log(Level.WARNING, "Can't send message to connection. " +
					"Connection(" + connectionId + ") does not exist.");
		}
		return false;
	}

	@Override
	public boolean broadcastMsg(MarkupMsg message) {
		GroupNetMsg broadcast = craftGroupNetMsg(message, 0L, getLocalId());
		return broadcastMsg(broadcast);
	}

	@Override
	public long getLocalId() {
		return localId;
	}

	@Override
	public boolean watchEvents(ServerEventListener<GroupServerCon> e) {
		return eventListeners.add(e);
	}

	@Override
	public boolean unwatchEvents(ServerEventListener<GroupServerCon> e) {
		return eventListeners.remove(e);
	}
	
	//Creates a GroupNetMessage with the given to and from fields
	private GroupNetMsg craftGroupNetMsg(MarkupMsg message, long to, long from) {
		GroupNetMsg parent = new GroupNetMsg();
		parent.addNested(message);
		parent.to(to);
		parent.from(from);
		return parent;
	}
	
	//Notifies listeners of a connection being removed
	private void notifyRemove(GroupServerCon c) {
		for (ServerEventListener<GroupServerCon> e : eventListeners)
			e.onRemove(c);
	}
	
	//Notifies listeners of the last connection being removed
	private void notifyLastRemove() {
		for (ServerEventListener<GroupServerCon> e : eventListeners)
			e.onLastRemove();
	}
	
	//Notifies listeners of a newly added connection
	private void notifyAdd(GroupServerCon c) {
		for (ServerEventListener<GroupServerCon> e : eventListeners)
			e.onAdd(c);
	}
	
	//Notifies listeners on server shutdown
	private void notifyShutdown() {
		for (ServerEventListener<GroupServerCon> e : eventListeners)
			e.onShutdown();
	}
	
	//Sends the 'add connection' command to all the connected clients
	private void broadcastAddConnection(GroupServerCon newCon) {
		broadcastMsg(GroupCmdCrafter.craftNewCon(newCon.getId()));
		for (int i = 0; i < getConnectionCount(); i++) {
			GroupServerCon c = getConnectionByIndex(i);
			newCon.sendMsgAndFlush(GroupCmdCrafter.craftNewCon(c.getId()));
		}
	}
	
	//Event used to remove connections when the connection gets closed
	private class ConnectionEvent implements InetEventListener {
		private final GroupServerCon con;
		
		ConnectionEvent(GroupServerCon con) { this.con = con; }
		
		@Override
		public void onClose() {
			removeConnection(con);
		}
	}
}
