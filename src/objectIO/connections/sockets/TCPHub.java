package objectIO.connections.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import objectIO.connections.ConnectionHub;
import objectIO.connections.StreamCon;

public class TCPHub extends ConnectionHub<TCPCon> {
	private boolean manuallyStopped = false;
	
	protected ServerSocket serverSocket;
	
	public final Acceptor acceptor;
	
	public TCPHub(long id) {
		super(id);
		acceptor = new Acceptor();
	}
	
	public void closeConnections() {
		acceptor.stop();
		for (StreamCon c : connections)
			c.close();
	}
	
	public class Acceptor implements Runnable{
		protected Thread thread = null;
		
		protected boolean acceptAddress(String adr) {
			return true;
		}
		
		public void start(int port) throws IOException {
			stop();
			manuallyStopped = false;
			serverSocket = new ServerSocket(port);
			thread = new Thread(this);
			thread.setName("TCP Connection Acceptor");
			thread.start();
		}
		
		public void stop() {
			manuallyStopped = true;
			if (serverSocket != null && !serverSocket.isClosed())
				try { serverSocket.close(); } catch (IOException e) { e.printStackTrace(); }
			if (thread != null && thread.isAlive())
				thread.interrupt();
			thread = null;
			serverSocket = null;
		}
		
		public void run() {
			while (true) {
				try {
					Socket s = serverSocket.accept();
					if (acceptAddress(s.getInetAddress().getHostAddress()))
						addConnection(new TCPCon(s, TCPHub.this));
					else
						s.close();
				} catch (IOException e) {
					if (!manuallyStopped)
						e.printStackTrace();
					break;
				}

				
			}
		}
	}

}
