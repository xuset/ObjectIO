package objectIO.connections.sockets.p2pServer.client;

import java.util.ArrayList;

import objectIO.connections.Connection;
import objectIO.connections.sockets.p2pServer.P2PMsg;
import objectIO.markupMsg.MarkupMsg;

public class ClientConnection extends Connection {
	private final ArrayList<MarkupMsg> outputBuffer = new ArrayList<MarkupMsg>();
	private P2PMsg outputMsg;
	ClientHub hub;

	ClientConnection(ClientHub hub, long endId) {
		super(hub);
		this.hub = hub;
		this.endId = endId;
		resetOutputBuffer();
	}

	public void disconnect() {
		synchronized(hub.getAllConnections()) {
			hub.getAllConnections().remove(this);
		}
		if (hub.conEvent != null)
			hub.conEvent.onDisconnection(hub, this);
	}

	public void flush() {
		flushOutputBuffer();
		hub.flush();
	}

	@Override
	public boolean sendMsg(MarkupMsg msg) {
		return outputMsg.child.add(msg);
	}

	protected boolean sendRawMsg(P2PMsg msg) {
		return hub.comm.sendMsg(msg);
	}
	
	boolean flushOutputBuffer() {
		if (!outputMsg.child.isEmpty()) {
			sendRawMsg(outputMsg);
			resetOutputBuffer();
			return true;
		}
		return false;
	}
	
	void resetOutputBuffer() {
		outputBuffer.clear();
		outputMsg = new P2PMsg();
		outputMsg.to(endId);
		outputMsg.from(getId());
		outputMsg.child = outputBuffer;
	}

}
