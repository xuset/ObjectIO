package net.xuset.objectIO.connections.sockets.groupNet.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xuset.objectIO.connections.Connection;
import net.xuset.objectIO.connections.sockets.InetEventListener;
import net.xuset.objectIO.connections.sockets.InetHub;
import net.xuset.objectIO.connections.sockets.ServerEventListener;
import net.xuset.objectIO.connections.sockets.groupNet.GroupNetMsg;
import net.xuset.objectIO.connections.sockets.groupNet.client.Commands.CmdChain;
import net.xuset.objectIO.markupMsg.AsciiMsgParser;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgParsable;
import net.xuset.objectIO.markupMsg.MsgParser;


/**
 * This hub connects to GroupNetServer via a socket. The term server in this comment
 * is meant to reference GroupNetServer while the term client is meant to reference
 * GroupClientHub. All connections stored by this hub
 * are 'virtual' connections. Meaning that the hub is not connected to them via sockets or
 * other means. When a message is sent across one of these 'virtual' connections,
 * the message is forwarded to the server who has the real connection. The connections
 * of this hub are meant to mirror the connections of the server. When the server adds
 * a new client, it notifies the clients to add a 'virtual' connection that can be
 * used to communicate with the new client. When the server removes a client,
 * it notifies the clients to remove the 'virtual' connection that refernces the client.
 * 
 * <p>All id's for the server's connections and the client's 'virtual' connections
 * are synchronized. So if this clients's id is 9234 (just a random number), all other
 * clients who have a virtual connection to this client will know the id is 9234.</p>
 * 
 * <p>This hub contains one initial connection, which is an instance of
 * {@link BroadcastConnection}. This connection's {@code getId()} method equals
 * {@code Connection#BROADCAST_CONNECTION}. The purpose of this connection is to send
 * messages that should be broadcasted to all connections. Since all connections are
 * 'virtual', there is no point in sending each 'virtual' connection the message that
 * should be broadcasted. This would waste a lot of bandwidth because all the messages
 * go to the server and are then forwarded. So instead this special connection is used
 * to send the messages that should be broadcasted to the server, and the server
 * broadcasts the the message for you.</p>
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class GroupClientHub implements InetHub<GroupClientCon>, MsgParsable{
	private static final Logger log = Logger.getLogger(GroupClientHub.class.getName());
	
	private final List<ServerEventListener> eventListeners;
	private final List<GroupClientCon> connections = new ArrayList<GroupClientCon>();
	private final long localId;
	private final CmdChain cmdChain;
	private final ClientComm comm;
	private MsgParser msgParser = new AsciiMsgParser();
	private boolean isShutdown = false;
	private boolean flushNeeded = false;
	
	/**
	 * Indicates if this hub is connected to the server.
	 * 
	 * @return {@code true} if the hub is connected
	 */
	public boolean isConnectedToServer() { return comm.isConnected(); }
	
	
	/**
	 * Sole constructor used to to create the GroupClientHub object and connect
	 * to the GroupNetServer.
	 * 
	 * @param ip ip of the server
	 * @param port port to connect on the server
	 * @param localId the local id of this hub
	 * @throws IOException if a I/O error occurs while trying to connect to the server
	 */
	public GroupClientHub(String ip, int port, long localId) throws IOException {
		this.localId = localId;
		eventListeners = new ArrayList<ServerEventListener>();
		cmdChain = Commands.constructChain(this);
		
		comm = ClientComm.connectToGroupNetServer(ip, port, this);
		comm.watchEvents(new ClientCommEventListener());
		addConnection(new BroadcastConnection(this));
		log.log(Level.INFO, "new instance created with id=" + localId);
	}
	
	/**
	 * When the connection to the server receives a message, it passes the new message
	 * to this method.
	 * 
	 * @param msg the newly received message
	 */
	void handleNewMsg(GroupNetMsg msg) {
		if (msg == null)
			shutdown();
		else
			cmdChain.handOff(msg);
	}
	
	/**
	 * Sends the message directly to server
	 * 
	 * @param msg message to send
	 * @return if the message was sent successfully
	 */
	boolean sendRaw(GroupNetMsg msg) {
		flushNeeded = true;
		return comm.sendMsg(msg);
	}
	
	/**
	 * Flushes the connection to the server
	 */
	void flushComm() {
		if (flushNeeded)
			comm.flush();
	}
	
	@Override
	public GroupClientCon getConnectionById(long id) {
		synchronized(connections) {
			for (GroupClientCon c : connections) {
				if (c.getId() == id)
					return c;
			}
		}
		return null;
	}

	@Override
	public boolean sendMsg(MarkupMsg msg, long endId) {
		Connection c = getConnectionById(endId);
		if (c == null)
			return false;
		return c.sendMsg(msg);
	}

	@Override
	public boolean addConnection(GroupClientCon con) {
		log.log(Level.INFO, "Adding connection(" + con.getId() + ")");
		con.watchEvents(new ConnectionEvent(con));
		synchronized(connections) {
			connections.add(con);
		}
		for (ServerEventListener e : eventListeners)
			e.onAdd(con);
		return true;
	}

	@Override
	public boolean removeConnection(GroupClientCon connection) {
		log.log(Level.INFO, "removing connection(" + connection.getId() + ")");
		boolean found;
		synchronized(connections) {
			found = connections.remove(connection);
		}
		for (ServerEventListener e : eventListeners)
			e.onRemove(connection);
		return found;
	}

	@Override
	public boolean isShutdown() {
		return isShutdown;
	}

	@Override
	public void shutdown() {
		log.log(Level.INFO, "Shutdown called");
		if (isShutdown)
			return;
		
		isShutdown = true;
		if (!comm.isClosed())
			comm.close();
		synchronized(connections) {
			connections.clear();
		}
		for (ServerEventListener e : eventListeners)
			e.onLastRemove();
		for (ServerEventListener e : eventListeners)
			e.onShutdown();
	}

	@Override
	public long getLocalId() {
		return localId;
	}

	@Override
	public GroupClientCon getConnectionByIndex(int index) {
		synchronized (connections) {
			return connections.get(index);
		}
	}

	@Override
	public int getConnectionCount() {
		return connections.size();
	}

	@Override
	public boolean broadcastMsg(MarkupMsg message) {
		GroupClientCon broadcastCon = getConnectionById(Connection.BROADCAST_CONNECTION);
		if (broadcastCon != null) {
			return broadcastCon.sendMsg(message);
		} else {
			boolean allTrue = true;
			for (int i = 0; i < connections.size(); i++) {
				GroupClientCon c = getConnectionByIndex(i);
				if (!c.sendMsg(message))
					allTrue = false;
			}
			return allTrue;
		}
	}

	@Override
	public boolean watchEvents(ServerEventListener e) {
		return eventListeners.add(e);
	}

	@Override
	public boolean unwatchEvents(ServerEventListener e) {
		return eventListeners.remove(e);
	}

	@Override
	public void setParser(MsgParser parser) {
		msgParser = parser;
	}

	@Override
	public MsgParser getParser() {
		return msgParser;
	}
	
	private class ConnectionEvent implements InetEventListener {
		private final GroupClientCon con;
		
		ConnectionEvent(GroupClientCon con) {
			this.con = con;
		}

		@Override
		public void onClose() {
			removeConnection(con);
		}
		
	}
	
	private class ClientCommEventListener implements InetEventListener {

		@Override
		public void onClose() {
			//TODO this is uneeded
		}
		
	}

}
