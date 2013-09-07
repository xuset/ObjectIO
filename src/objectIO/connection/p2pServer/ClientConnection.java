package objectIO.connection.p2pServer;


import java.net.Socket;

import objectIO.connection.AbstractConnection;
import objectIO.markupMsg.MarkupMsg;

public class ClientConnection extends AbstractConnection implements P2PConnection{
	private ClientHub hub;
	
	public Socket getSocket() { return hub.socket; }
	
	ClientConnection(long endPointId, ClientHub hub) {
		super(hub.getId());
		this.endPointId = endPointId;
		this.hub = hub;
		hub.getAllConnections().add(this);
		if (hub.connectionEvent != null)
			hub.connectionEvent.onNewConnection(hub, this);
	}
	
	public ClientConnection(ClientHub hub) {
		this(hub.getId(), hub);
	}

	public void flush() {
		hub.flushAll();
	}

	public boolean areStreamsOpen() {
		return true;
	}
	
	public void closeStreams() {
		disconnect();
	}

	public boolean isConnected() {
		return (hub != null && hub.isConnected());
	}
	
	public void disconnect() {
		hub.getAllConnections().remove(this);
		if (hub.connectionEvent != null)
			hub.connectionEvent.onDisconnection(hub, this);
		hub = null;
	}
	public boolean sendMessage(MarkupMsg dOrg) {
		hub.sendMessage(dOrg, endPointId);
		return false;
	}
}
