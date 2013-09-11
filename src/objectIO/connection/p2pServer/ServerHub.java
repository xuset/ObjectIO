package objectIO.connection.p2pServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import objectIO.connection.Connection;
import objectIO.connection.ConnectionHub;
import objectIO.connection.p2pServer.messages.ConnectMsg;
import objectIO.connection.p2pServer.messages.Message;

public class ServerHub extends ConnectionHub<ServerConnection> implements P2PHub<ServerConnection>{
	private ServerSocket server;
	private boolean serverRunning = false;
	
	public ConnectionEvent connectionEvent;
	public AcceptListener acceptListener;
	public int maxConnections = 4;
	
	public void setConnectionEvent(ConnectionEvent event) { connectionEvent = event; }
	
	public ServerHub(long id, int port) throws IOException {
		super(id);
		server = new ServerSocket(port);
	}
	
	boolean sendRawMessage(Message dataO) {
		ServerConnection c = getConnection(dataO.to());
		if (c != null) {
			return c.sendRawMessage(dataO);
		}
		return false;
	}
	
	public void flushAll() {
		for (P2PConnection c : connections) {
			c.flush();
		}
	}
	
	public void startServer() {
		serverRunning = true;
		acceptListener = new AcceptListener();
		acceptListener.setName("Server Accept Listener");
		acceptListener.start();
	}
	
	public void shutdown() {
		serverRunning = false;
		stopListening();
		for (P2PConnection c : connections) {
			c.disconnect();
		}
	}
	
	public void stopListening() {
		if (acceptListener != null) {
			acceptListener.shutdown();
			acceptListener = null;
		}
	}
	
	/*private StringBuilder joiner = new StringBuilder();
	protected synchronized void broadcastMessage(DataOrganizer message, long sendersId) {
		joiner.append(sendersId).append(":").append(message);
		String s = joiner.toString();
		for (ServerConnection c : connections) {
			if (c.getEndPointId() != sendersId)
				c.sendRawMessage(s);
		}
		joiner.delete(0, joiner.length());
	}*/
	
	protected boolean broadcastMessage(Message message) {
		for (ServerConnection c : connections) {
			if (message.from() != c.getEndPointId())
				c.sendRawMessage(message);
		}
		return true;
		//return super.broadcastMessage(message);
	}
	
	protected boolean acceptAddress(InetAddress address) {
		return true;
	}
	
	protected void disconnectCon(Connection c) {
		connections.remove(c);
		for (ServerConnection con : connections) {
			con.sendRawMessage(ConnectMsg.removeConnection(c));
		}
	}
	
	protected void addNewConnection(Socket s) {
		ServerConnection c = new ServerConnection(s, this);
		broadcastConnections(c);
		connections.add(c);
		System.out.println("connection added");
		if (connectionEvent != null)
			connectionEvent.onNewConnection(this, c);
	}
	
	private void broadcastConnections(ServerConnection newCon) {
		for (ServerConnection c : connections) {
			c.sendRawMessage(ConnectMsg.addConnection(newCon));
			c.flush();
			
			newCon.sendRawMessage(ConnectMsg.addConnection(c));
		}
		newCon.flush();
	}
	
	public class AcceptListener extends Thread {
		private boolean shutdown = false;
		
		public int maxConnections = 4;
		
		public void run() {
			serverAcceptLoop();
		}
		
		public void shutdown() {
			shutdown = true;
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.interrupt();
		}
		
		/*void broadCastMessage(DataOrganizer message, long sendersId) {
			for (P2PConnection c : connections) {
				c.sendMessage(sendersId + ":" + message);
			}
		}*/
		
		private void serverAcceptLoop() {
			while (shutdown == false && serverRunning) {
				if (connections.size() < maxConnections) {
					try {
						Socket s = server.accept();
						if (acceptAddress(s.getInetAddress())) {
							addNewConnection(s);
						} else {
							s.close();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						//System.err.println(ex.getMessage());
					}
				} else {
					try { Thread.sleep(1); } catch (Exception ex) { }
				}
				
			}
		}
	}
	
}
