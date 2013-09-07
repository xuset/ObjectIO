package objectIO.connection.p2pServer;

import objectIO.connection.Connection;

interface ServerProtocol {
	public void sendNewConnection(Connection c);
	public void sendRemoveConnection(Connection c);
}
