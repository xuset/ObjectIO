package net.xuset.objectIO.connections.sockets.p2pServer.client;

import java.io.IOException;

import net.xuset.objectIO.connections.Connection;
import net.xuset.objectIO.connections.ConnectionHub;
import net.xuset.objectIO.connections.sockets.p2pServer.P2PMsg;
import net.xuset.objectIO.connections.sockets.p2pServer.client.Commands.CmdChain;
import net.xuset.objectIO.markupMsg.MarkupMsg;



public class ClientHub extends ConnectionHub<ClientConnection> {
	private CmdChain cmdChain;

	ClientComm comm;
	
	public ConnectionEvent conEvent = null;
	
	public boolean isConnectedToServer() { return comm.isConnected(); }
	
	public ClientHub(String ip, int port, long id) throws IOException {
		super(id);
		cmdChain = new Commands.CmdConnect(this);
		cmdChain.append(
				new Commands.CmdDisconnect(this)).append(
				new Commands.CmdMsg(this));
		comm = new ClientComm(ip, port, this);
		addConnection(new BroadcastConnection(this));
	}
	
	@Override
	public ClientConnection getConnection(long id) {
		synchronized(connections) {
			return super.getConnection(id);
		}
	}
	
	void parseInput(String input) {
		if (input != null) {
			if (input == "") {
				System.err.println("Client recieved empty string!");
			} else {
				P2PMsg msg = new P2PMsg(input);
				if (msg.parsedProperly())
					cmdChain.handOff(msg);
			}
		} else
			shutdown();
	}

	@Override
	public boolean sendMsg(MarkupMsg msg, long endId) {
		Connection c = getConnection(endId);
		if (c == null)
			return false;
		return c.sendMsg(msg);
	}
	
	/*public boolean broadcastMsg(MarkupMsg msg) {
		P2PMsg parent = new P2PMsg();
		parent.child.add(msg);
		parent.to(Connection.BROADCAST_CONNECTION);
		parent.from(getId());
		parent.setBroadcast(true);
		return comm.sendMsg(parent);
	}*/

	@Override
	public boolean addConnection(ClientConnection con) {
		if (conEvent != null)
			conEvent.onConnection(this, con);
		synchronized(connections) {
			return super.addConnection(con);
		}
	}

	public void shutdown() {
		comm.close();
		synchronized(connections) {
			connections.clear();
		}
		if (conEvent != null)
			conEvent.onServerDisconnect(this);
	}
	
	public void flush() {
		boolean dataSent = false;
		for (ClientConnection c : connections) {
			boolean ret = c.flushOutputBuffer();
			if (ret)
				dataSent = true;
		}
		if (dataSent)
			comm.flush();
	}

}
