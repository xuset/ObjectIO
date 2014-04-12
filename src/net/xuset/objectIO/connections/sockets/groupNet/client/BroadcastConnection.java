package net.xuset.objectIO.connections.sockets.groupNet.client;

import net.xuset.objectIO.connections.Connection;
import net.xuset.objectIO.connections.sockets.groupNet.GroupNetMsg;


/**
 * The broadcast connection used by GroupClientHub. This connection has an id
 * equal {@link Connection#BROADCAST_CONNECTION} and sets the broadcast flag to true
 * on all messages sent through this connection.
 * 
 * @author xuset
 * @since 1.0
 *
 */
class BroadcastConnection extends GroupClientCon {

	/**
	 * Creates a new BroadcastConnection.
	 * 
	 * @param hub GroupClientHub object that this connection should use
	 */
	BroadcastConnection(GroupClientHub hub) {
		super(hub, Connection.BROADCAST_CONNECTION, true);
	}

	@Override
	protected boolean sendRawMsg(GroupNetMsg msg) {
		msg.setBroadcast(true);
		return super.sendRawMsg(msg);
	}
	
	

}
