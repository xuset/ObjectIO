package net.xuset.objectIO.connections.sockets;

import net.xuset.objectIO.connections.Hub;


/**
 * Outlines methods useful for hubs that are used for internet connections. Since some
 * hubs who deal with internet connections also act as servers, the {@code shutdown()}
 * method is added to shutdown the server. The method does close and remove all
 * connections, but is left up to the implementation if it needs to do more.
 * 
 * @author xuset
 * @see InetCon
 * @since 1.0
 * @param <T> the type of InetCon that will be stored
 */
public interface InetHub<T extends InetCon> extends Hub<T>{
	
	
	/**
	 * Shuts down the hub, closing and removing all connections.
	 */
	void shutdown();
	
	
	/**
	 * Indicates if the hub has been shutdown.
	 * @return {@code true} if the hub has been shutdown
	 */
	boolean isShutdown();
	
	
	/**
	 * The returned local id should be the same as the local id's of the connections.
	 * This is not required and is left up to the implementation.
	 * 
	 * @return the local id of the hub
	 */
	long getLocalId();
	
	
	/**
	 * The event listener will be notified when an event occurs.
	 * 
	 * @param e the listener that will be notified
	 * @return {@code true} if the listener was successfully added.
	 */
	boolean watchEvents(ServerEventListener e);
	
	
	/**
	 * The event listener will no long be notified of events.
	 * 
	 * @param e event listener to remove.
	 * @return {@code true} if the listener was found and removed successfully.
	 */
	boolean unwatchEvents(ServerEventListener e);
}
