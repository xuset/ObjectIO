package objectIO.connection.p2pServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import objectIO.connection.AbstractConnection;
import objectIO.connection.ConnectionHub;
import objectIO.connection.p2pServer.messages.ConnectMsg;
import objectIO.connection.p2pServer.messages.Message;
import objectIO.connection.stream.streamBase.InputParser;
import objectIO.connection.stream.streamBase.StreamBase;
import objectIO.markupMsg.MarkupMsg;

public class ClientHub extends ConnectionHub<ClientConnection> implements P2PHub<ClientConnection>{
	private boolean sentTextSinceFlush = false;
	
	protected Socket socket;
	protected StreamBase streamBase;
	protected ClientConnection server = null;
	
	public ConnectionEvent connectionEvent;
	
	public Socket getSocket() { return socket; }
	public ClientConnection getServerConnection() { return server; }
	
	public void setConnectionEvent(ConnectionEvent event) { connectionEvent = event; }
	
	private InputParser parser = new InputParser() {
		public void parse(String message) {
			parseInput(message);
		}
	};
	
	public ClientHub(long id, String host, int port) throws UnknownHostException{ //Id cannot be -1l or -2l; -1l is broadcast id and -2l is new connection Id;
		super(id);
		try {
			socket = new Socket(InetAddress.getByName(host), port);
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socket.setKeepAlive(true);
			socket.setTcpNoDelay(true);
			long endPointId = sendMeetAndGreet(input, socket.getOutputStream());
			if (endPointId != -1l) {
				streamBase = new StreamBase(input, socket.getOutputStream(), parser);
				server = new ClientConnection(endPointId, this);
				if (connectionEvent != null)
					connectionEvent.onNewConnection(this, server);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private long sendMeetAndGreet(BufferedReader input, OutputStream output) {
		try {
			String message = "hello my name is:" + getId();
			output.write(message.getBytes(), 0, message.length());
			output.write(13);
			String[] split = input.readLine().split(":", 2);
			return Long.parseLong(split[1]);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}
	
	public synchronized boolean sendMessage(MarkupMsg dOrg, long connectionId) {
		Message msg = new Message();
		msg.child.add(dOrg);
		msg.to(connectionId);
		msg.from(getId());
		return sendRawMessage(msg);
	}
	
	synchronized boolean sendRawMessage(Message message) {
		System.out.println("Client sent: " + message);
		if (sentTextSinceFlush == false)
			sentTextSinceFlush = true;
		return streamBase.sendMessage(message.toString());
	}
	
	boolean isConnected() {
		return socket.isConnected() && socket.isClosed() == false;
	}
	
	public void flushAll() {
		if (sentTextSinceFlush)
			streamBase.flush();
	}
	
	private void shutdownIO() {
		if (socket != null) {
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public synchronized void shutdown() {
		shutdownIO();
		streamBase.shutdown();
		if (socket != null) {
			try { socket.close(); } catch (IOException ex) { ex.printStackTrace(); }
		}
		socket = null;
		server = null;
		disconnectAll();
	}
	
	private void disconnectAll() {
		for (ClientConnection c : connections){
			c.disconnect();
		}
	}
	
	protected void parseInput(String input) {
		System.out.println("client " + getId() + " recieved: " + input);
		if (input != null) {
			Message message = new Message(input);
			handleNewMessage(message);
		} else {
			shutdown();
		}
	}
	
	private void handleNewMessage(Message message) {
		if (ConnectMsg.isConnectionMessage(message)) {
			addNewConnection(message);
		} else {
			addMessageToQueue(message);
		}
	}
	
	private void addMessageToQueue(Message msg) {
		AbstractConnection c = getConnection(msg.from());
		if (c != null)
			c.getMessageQueue().add(msg.child.get(0));
	}
	
	private void addNewConnection(Message msg) {
		long newCon = ConnectMsg.getnewConnections(msg);
		if (getConnection(newCon) == null)
			new ClientConnection(newCon, this);
	}
}
