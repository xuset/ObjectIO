package objectIO.connections.p2pServer.client;

public interface ConnectionEvent {
	public void onConnection(ClientHub hub, ClientConnection connection);
	public void onDisconnection(ClientHub hub, ClientConnection connection);
	public void onServerDisconnect(ClientHub hub);
}
