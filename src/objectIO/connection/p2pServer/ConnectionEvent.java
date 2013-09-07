package objectIO.connection.p2pServer;

public interface ConnectionEvent {
	public void onNewConnection(P2PHub<?> hub, P2PConnection connection);
	public void onDisconnection(P2PHub<?> hub, P2PConnection connection);
}
