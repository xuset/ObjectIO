package objectIO.connection.p2pServer;

import objectIO.connection.Hub;

public interface P2PHub <T extends P2PConnection>extends Hub<T>{
	public void shutdown();
	public void flushAll();
	public void setConnectionEvent(ConnectionEvent event);
}
