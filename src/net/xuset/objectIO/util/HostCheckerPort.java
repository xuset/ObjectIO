package net.xuset.objectIO.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class HostCheckerPort implements HostChecker {
	private final int port, timeout;
	
	public HostCheckerPort(int port, int timeout) {
		this.port = port;
		this.timeout = timeout;
	}
	
	public HostCheckerPort(int port) {
		this(port, 500);
	}

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
