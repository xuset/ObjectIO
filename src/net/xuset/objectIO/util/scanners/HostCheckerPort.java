package net.xuset.objectIO.util.scanners;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import net.xuset.objectIO.util.NetworkPing;

import net.xuset.objectIO.util.scanners.HostChecker;


/**
 * Attempts to connect to a TCP port for a connection then immediately closes the
 * socket upon connection. This is used to determine if the port is open.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class HostCheckerPort implements HostChecker {
	private static final int defaultTimeout = 500;
	
	private final int port, timeout;
	
	
	/**
	 * Instantiates a new object with the given port and timeout
	 * 
	 * @param port TCP port to check
	 * @param timeout max amount of time to wait for a connection in milliseconds
	 */
	public HostCheckerPort(int port, int timeout) {
		this.port = port;
		this.timeout = timeout;
	}
	
	
	/**
	 * Instantiates a new object with the given port and with a default timeout of
	 * {@value #defaultTimeout} milliseconds.
	 * 
	 * @param port TCP port to check
	 */
	public HostCheckerPort(int port) {
		this(port, defaultTimeout);
	}

	/**
	 * Checks if the port is open.
	 * 
	 * @return {@code true} if the port is open. {@code false} if the port is closed or
	 * 			timeout was reached.
	 */
	@Override
	public boolean isUp(InetAddress addr) {
		if (NetworkPing.isHostUp(addr, timeout)) {
			Socket socket = null;
			try {
	            socket = new Socket();
	            socket.connect(new InetSocketAddress(addr, port), timeout);
	            socket.close();
	            return true;
	        } catch (IOException ex) {
	        	//Do nothing
	        } finally {
	        	tryClosingSocket(socket);
	        }
		}
		return false;
	}
	
	private static void tryClosingSocket(Socket s) {
    	if (s != null && !s.isClosed()) {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
	}

}
