package net.xuset.objectIO.connections.sockets.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xuset.objectIO.connections.sockets.InetCon;
import net.xuset.objectIO.connections.sockets.InetHub;


/**
 * Class that monitors a {@link java.net.ServerSocket ServerSocket} object for new
 * TCP connections and adds them to the specified hub by calling
 * {@code addConnection} on the hub.
 * 
 * @author xuset
 * @since 1.0
 * @param <T> they type of InetCon that will be added
 */
public abstract class TcpAcceptor<T extends InetCon> {
	private static final Logger log = Logger.getLogger(TcpAcceptor.class.getName());
	
	private final ServerSocket serverSocket;
	private final Thread thread;
	private final InetHub<T> hub;
	private boolean isStopped = false;
	
	
	/**
	 * Creates the appropriate connection (that extends InetCon) from a socket object.
	 * 
	 * @param s socket that will be used to create the connection
	 * @return the new connection
	 * @throws IOException if a socket error occurs
	 */
	protected abstract T createConnection(Socket s) throws IOException;
	
	
	/**
	 * The returned port number is what remote sockets should try to connect to.
	 * 
	 * @return the port the SocketServer is listening on.
	 */
	public int getLocalPort() { return serverSocket.getLocalPort(); }
	
	
	/**
	 * Sole constructor for TcpAcceptor.
	 * 
	 * @param hub hub to add the connections to
	 * @param port port to listen for new connections on
	 * @throws IOException if an exception occurs while creating the ServerSocket object
	 */
	public TcpAcceptor(InetHub<T> hub, int port) throws IOException {
		this.hub = hub;
		serverSocket = new ServerSocket(port);
		thread = new Thread(new Worker(), "Tcp server acceptor");
		thread.start();
		log.log(Level.INFO, "listening on port: " + port);
	}
	
	
	/**
	 * Once a new socket has been created the address is passed here, and if the method
	 * returns true, the connection is created and added to the hub. If it returns false,
	 * the socket is closed. This method can be overridden to implement a white-list or
	 * black-list.
	 * 
	 * @param addr address in question
	 * @return {@code true} if the connection should be created and added
	 */
	protected boolean isAcceptable(InetAddress addr) {
		return (addr != null);
	}
	
	
	/**
	 * Stops listening for new connections and closes the ServerSocket object.
	 */
	public void stop() {
		log.log(Level.INFO, "stop() called");
		
		isStopped = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
			log.log(Level.WARNING, e.getMessage(), e);
		}
		
		try {
			thread.join(500);
		} catch (InterruptedException e) {
			log.log(Level.WARNING, e.getMessage(), e);
		}
		
		//TODO this may be overkill.. 
		
		if (thread.isAlive())
			thread.interrupt();
	}
	
	
	/**
	 * Worker class that continually accepts new connections on the ServerSocket.
	 * 
	 * @author xuset
	 * @since 1.0
	 *
	 */
	private class Worker implements Runnable {

		@Override
		public void run() {
			try {
				while (true) {
					Socket s = serverSocket.accept();
					log.log(Level.INFO, "New socket connected.");
					handleNewSocket(s);
				}
				
			} catch (IOException ex) {
				if (!isStopped) {
					log.log(Level.SEVERE, ex.getMessage(), ex);
					stop();
				}
			}
		}
		
		private void handleNewSocket(Socket s) {
			try {
				if (isAcceptable(s.getInetAddress())) {
					T con = createConnection(s);
					hub.addConnection(con);
				} else {
					closeSocket(s);
				}
			} catch (IOException ex) {
				log.log(Level.SEVERE, ex.getMessage(), ex);
				closeSocket(s);
			}
		}
		
		private void closeSocket(Socket s) {
			try {
				if (!s.isClosed())
					s.close();
			}
			catch (IOException ex) {
				log.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
	}
}
