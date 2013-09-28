package objectIO.connections.p2pServer.client;

import objectIO.connections.Connection;
import objectIO.connections.p2pServer.P2PMsg;
import objectIO.markupMsg.MarkupMsg;

public class ClientConnection extends Connection {
	ClientHub hub;

	ClientConnection(ClientHub hub, long endId) {
		super(hub);
		this.hub = hub;
		this.endId = endId;
	}

	boolean sendRawMsg(P2PMsg msg) {
		return hub.comm.sendMsg(msg);
	}

	@Override
	public boolean sendMsg(MarkupMsg msg) {
		P2PMsg parent = new P2PMsg();
		parent.child.add(msg);
		parent.to(endId);
		parent.from(getId());
		return sendRawMsg(parent);
	}

	public void flush() {
		hub.flush();
	}

	public void disconnect() {
		synchronized(hub.getAllConnections()) {
			hub.getAllConnections().remove(this);
		}
		if (hub.conEvent != null)
			hub.conEvent.onDisconnection(hub, this);
	}

}
