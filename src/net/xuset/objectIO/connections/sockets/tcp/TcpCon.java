package net.xuset.objectIO.connections.sockets.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xuset.objectIO.connections.StreamConReader;
import net.xuset.objectIO.connections.sockets.InetCon;
import net.xuset.objectIO.connections.sockets.InetEventListener;


/**
 * A connection backed by TCP sockets.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class TcpCon extends StreamConReader implements InetCon{
	private static final Logger log = Logger.getLogger(TcpCon.class.getName());
	
	private final List<InetEventListener> eventListeners =
			new ArrayList<InetEventListener>();
	private final long localId;
	private final Socket socket;
	
	
	/**
	 * Constructs a TcpCon object backed by the given socket.
	 * 
	 * @param s socket used to create the connection
	 * @param endId the id of the connection
	 * @throws IOException if an I/O error occurs
	 */
	public TcpCon(Socket s, long endId) throws IOException {
		this(s, endId, endId);
	}
	
	
	/**
	 * Constructs a TcpCon object backed by the given socket.
	 * 
	 * @param s socket used to create the connection
	 * @param endId the remote id of the connection
	 * @param localId the local id of the connection
	 * @throws IOException if an I/O error occurs
	 */
	public TcpCon(Socket s, long endId, long localId) throws IOException {
		super(
				new BufferedInputStream(s.getInputStream()),
				new BufferedOutputStream(s.getOutputStream()),
				endId);
		this.localId = localId;
		socket = s;
	}
	
	
	/**
	 * Gets the remote address of the connection.
	 * 
	 * @return the address of the remote connection
	 */
	public InetAddress getConnectedAddr() {
		return socket.getInetAddress();
	}
	
	
	/**
	 * Sets the TCP No Delay option
	 * 
	 * @param set value to set to
	 * @see java.net.Socket#setTcpNoDelay(boolean)
	 * @throws SocketException if there is a TCP error
	 */
	public void setTcpNoDelay(boolean set) throws SocketException {
		socket.setTcpNoDelay(set);
	}
	
	
	/**
	 * Sets the TCP Keep Alive option
	 * 
	 * @param set value to set to
	 * @see java.net.Socket#setKeepAlive(boolean)
	 * @throws SocketException if there is a TCP error
	 */
	public void setTcpKeepAlive(boolean set) throws SocketException {
		socket.setKeepAlive(set);
	}
	
	@Override
	protected void handleRawInput(String input) {
		if (input == null) {
			log.log(Level.INFO, "connection(" + getId() + ") received null as input");
			close();
		} else
			super.handleRawInput(input);
	}

	@Override
	public boolean isClosed() {
		if (socket.isClosed() && !super.isClosed()) {
			log.log(Level.INFO, "connection(" + getId() + ")'s socket is closed but the" +
					"the connection object is not. calling close()");
			close();
		}
		return super.isClosed();
	}
	
	@Override
	public boolean isConnected() { return !isClosed() && socket.isConnected(); }

	@Override
	public long getLocalId() {
		return localId;
	}

	@Override
	public boolean watchEvents(InetEventListener e) {
		return eventListeners.add(e);
	}

	@Override
	public boolean unwatchEvents(InetEventListener e) {
		return eventListeners.remove(e);
	}
	
	@Override
	public synchronized void close() {
		super.close();
		
		if (!socket.isClosed()) {
			try { socket.close(); }
			catch (IOException e) {
				log.log(Level.WARNING, "(" + getId() + ") " + e.getMessage(), e);
			}
		}
		
		
		for (InetEventListener e : eventListeners)
			e.onClose();
		eventListeners.clear();
	}

	@Override
	public boolean isBroadcast() {
		return false;
	}

	@Override
	public boolean isLoopback() {
		return socket.getLocalAddress().isLoopbackAddress();
	}
	

}
