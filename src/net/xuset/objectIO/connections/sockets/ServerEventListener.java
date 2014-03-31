package net.xuset.objectIO.connections.sockets;


/**
 * Interface whose methods are called when a server event occurs.
 * 
 * @author xuset
 * @since 1.0
 * @param <T> the type of InetCon that the server uses.
 */
public interface ServerEventListener<T extends InetCon> {
	
	
	/**
	 * Called when the connection has been removed from the server.
	 * 
	 * @param con the removed connection
	 */
	void onRemove(T con);
	
	
	/**
	 * Called when a new connection is added to the server.
	 * 
	 * @param con the added connection
	 */
	void onAdd(T con);
	
	
	/**
	 * Called when the last connection has been removed
	 */
	void onLastRemove();
	
	
	/**
	 * Called when the server shuts down.
	 */
	void onShutdown();
}
