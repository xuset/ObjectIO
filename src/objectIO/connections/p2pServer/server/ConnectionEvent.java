package objectIO.connections.p2pServer.server;

public interface ConnectionEvent {
	void onConnect(P2PServer s, ServerConnection c);
	void onDisconnect(P2PServer s, ServerConnection c);
	void onLastDisconnect(P2PServer s);
}
