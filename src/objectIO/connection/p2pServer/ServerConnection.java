package objectIO.connection.p2pServer;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import objectIO.connection.p2pServer.messages.Message;
import objectIO.connection.stream.StreamIO;
import objectIO.markupMsg.MarkupMsg;

public class ServerConnection extends StreamIO implements P2PConnection{
	protected ServerHub hub;
	protected Socket socket;
	
	public ConnectionEvent connectionEvent;
	
	public Socket getSocket() { return socket; }
	
	public ServerConnection(Socket s, ServerHub hub) {
		super(hub.getId(), s);
		try {
			s.setKeepAlive(true);
			s.setTcpNoDelay(true);
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		this.hub = hub;
		socket = s;
	}

	protected void parseInput(String s) {
		System.out.println("Server recieved: " + s);
		if (s != null) {
			Message dataO = new Message(s);
			if (dataO.isBroadcast()) {
				hub.broadcastMessage(dataO);
				messageQueue.add(dataO.child.get(0));
			} else {
				long sendTo = dataO.to();
				if (sendTo == myId) {
					messageQueue.add(dataO.child.get(0));
				} else {
					hub.sendRawMessage(dataO);
				}
			}
		} else {
			disconnect();
		}
	}
	
	public boolean sendMessage(MarkupMsg message) {
		Message dataO = new Message();
		dataO.child.add(message);
		dataO.from(myId);
		dataO.to(endPointId);
		return sendRawMessage(dataO);
	}
	
	boolean sendRawMessage(Message message) {
		System.out.println("Server sent: " + message);
		return super.sendMessage(message);
	}
	
	public boolean isConnected() {
		return true;
	}
	
	private void shutdownIO() {
		if (socket != null) {
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public synchronized void disconnect() {
		hub.disconnectCon(this);
		if (hub.connectionEvent != null)
			hub.connectionEvent.onDisconnection(hub, this);
		shutdownIO();
		closeStreams();
		if (socket != null) {
			try { socket.close(); } catch (IOException ex) { ex.printStackTrace(); }
		}
		socket = null;
		if (hub.connectionEvent != null)
			hub.connectionEvent.onDisconnection(hub, this);
		hub = null;
	}

}
