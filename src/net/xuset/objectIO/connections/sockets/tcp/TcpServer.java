package net.xuset.objectIO.connections.sockets.tcp;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xuset.objectIO.connections.sockets.InetCon;
import net.xuset.objectIO.connections.sockets.InetEventListener;
import net.xuset.objectIO.connections.sockets.InetHub;
import net.xuset.objectIO.connections.sockets.ServerEventListener;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.util.ConnectionIdGenerator;


/**
 * TcpServer listens on the given port for new socket connections. When a new socket
 * object is connected, it is used to create a TcpCon object. The new TcpCon object
 * is added to the hub. When the connection is closed, it is automatically removed from
 * the hub.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class TcpServer implements InetHub<TcpCon>{
	private static final Logger log = Logger.getLogger(TcpServer.class.getName());
	
	private final ArrayList<ServerEventListener<TcpCon>> eventListeners =
			new ArrayList<ServerEventListener<TcpCon>>();
	private final ArrayList<TcpCon> connections = new ArrayList<TcpCon>();
	private final long localId;
	private final TcpAcceptor<TcpCon> acceptor;

	private boolean isShutdown = false;
	
	/**
	 * Constructs a new TcpServer object with the given local id and acceptor object.
	 * 
	 * @param localId the local id of the hub
	 * @param acceptor the acceptor to use
	 */
	public TcpServer(long localId, TcpAcceptor<TcpCon> acceptor) {
		this.localId = localId;
		this.acceptor = acceptor;
		log.log(Level.INFO, "new tcp server created with id=" + localId);
	}
	
	
	/**
	 * Constructs a new TcpServer object with the given local id and port to listen on.
	 * A TcpAcceptor is constructed based on the given port.
	 * 
	 * @param localId the local id of the hub
	 * @param port the port acceptor will listen to
	 * @throws IOException if an I/O error occurs while creating the acceptor
	 */
	public TcpServer(long localId, int port) throws IOException {
		this.localId = localId;
		acceptor = new TcpConAcceptor(this, port);
		log.log(Level.INFO, "new tcp server created with id=" + localId);
	}
	
	
	/**
	 * Gets the TcpAcceptor object.
	 * 
	 * @return the TcpAcceptor used by the server
	 */
	public TcpAcceptor<TcpCon> getAcceptor() {
		return acceptor;
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
		while (getConnectionCount() > 0) {
			getConnectionByIndex(0).close();
		}
	}

	@Override
	public long getLocalId() {
		return localId;
	}
	
	@Override
	public TcpCon getConnectionById(long id) {
		synchronized(connections) {
			for (int i = 0; i < connections.size(); i++) {
				TcpCon con = connections.get(i);
				if (con.getId() == id)
					return con;
			}
		}
		return null;
	}

	@Override
	public TcpCon getConnectionByIndex(int index) {
		synchronized(connections) {
			return connections.get(index);
		}
	}

	@Override
	public int getConnectionCount() {
		return connections.size();
	}

	@Override
	public boolean removeConnection(TcpCon connection) {
		log.log(Level.INFO, "Removing connection(" + connection.getId() + ")");
		boolean found;
		synchronized(connections) {
			found = connections.remove(connection);
		}
		for (ServerEventListener<TcpCon> l : eventListeners)
			l.onRemove(connection);
		if (connections.isEmpty()) {
			for (ServerEventListener<TcpCon> l : eventListeners)
				l.onLastRemove();
		}
		return found;
	}

	@Override
	public boolean addConnection(TcpCon connection) {
		log.log(Level.INFO, "Adding connection(" + connection.getId() + ")");
		connection.watchEvents(new EventListener(connection));
		synchronized(connections) {
			connections.add(connection);
		}
		for (ServerEventListener<TcpCon> l : eventListeners)
			l.onAdd(connection);
		return true;
	}

	@Override
	public boolean sendMsg(MarkupMsg message, long connectionId) {
		InetCon con = getConnectionById(connectionId);
		if (con == null) {
			log.log(Level.WARNING, "Connection(" + connectionId + ") does not exist");
			return false;
		}
		return con.sendMsg(message);
	}

	@Override
	public boolean broadcastMsg(MarkupMsg message) {
		boolean allTrue = true;
		synchronized(connections) {
			for (int i = 0; i < connections.size(); i++) {
				InetCon con = connections.get(i);
				if (!con.sendMsg(message))
					allTrue = false;
			}
		}
		return allTrue;
	}

	@Override
	public boolean watchEvents(ServerEventListener<TcpCon> e) {
		return eventListeners.add(e);
	}

	@Override
	public boolean unwatchEvents(ServerEventListener<TcpCon> e) {
		return eventListeners.remove(e);
	}
	
	
	/**
	 * Event listener used to remove the connection when the connection is closed.
	 *
	 * @author xuset
	 * @since 1.0
	 *
	 */
	private class EventListener implements InetEventListener {
		private final InetCon con;
		public EventListener(InetCon con) { this.con = con; }
		
		@Override
		public void onClose() {
			synchronized(connections) {
				connections.remove(con);
			}
		}
	}

	
	/**
	 * The default TcpAcceptor used by the server.
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	private class TcpConAcceptor extends TcpAcceptor<TcpCon> {

		public TcpConAcceptor(InetHub<TcpCon> hub, int port) throws IOException {
			super(hub, port);
		}

		@Override
		protected TcpCon createConnection(Socket s) throws IOException {
			long newId = ConnectionIdGenerator.createNext();
			return new TcpCon(s, newId, getLocalId());
		}
		
	}

}
