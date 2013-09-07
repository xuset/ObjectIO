package objectIO.connection.p2pServer;

import java.net.Socket;

import objectIO.connection.stream.StreamConnection;

public interface P2PConnection extends StreamConnection{
	public boolean isConnected();
	public void disconnect();
	public Socket getSocket();
}
