package objectIO.connections.sockets.p2pServer.client;

import objectIO.connections.Connection;
import objectIO.connections.sockets.p2pServer.P2PMsg;

public class BroadcastConnection extends ClientConnection {

	BroadcastConnection(ClientHub hub) {
		super(hub, Connection.BROADCAST_CONNECTION);
	}

	@Override
	protected boolean sendRawMsg(P2PMsg msg) {
		msg.setBroadcast(true);
		return super.sendRawMsg(msg);
	}
	
	

}
