package net.xuset.objectIO.connections.sockets;

import net.xuset.objectIO.connections.StreamConI;


/**
 * Interface that is useful for internet connections.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public interface InetCon extends StreamConI{
	
	/**
	 * The local id should be the same for all connections on one machine. This is not
	 * required and is left up to the implementation.
	 * 
	 * @return the local id of the connection.
	 */
	long getLocalId();
	
	
	/**
	 * Indicates if the connection is still alive.
	 * @return {@code true} if the connection is still connected
	 */
	boolean isConnected();
	
	
	/**
	 * The event listener will be notified when an event happens.
	 * 
	 * @param e the event listener to add
	 * @return {@code true} if successfully added
	 */
	boolean watchEvents(InetEventListener e);
	
	
	/**
	 * The event listener will no longer be notified
	 * 
	 * @param e event listener to remove
	 * @return {@code true} if the event listener was found and removed successfully
	 */
	boolean unwatchEvents(InetEventListener e);
	
	
	/**
	 * Indicates if this connection is broadcast connection. What the connection does and
	 * how it broadcasts is left up to the implementation.
	 * 
	 * @return {@code true} if the connection broadcasts
	 */
	boolean isBroadcast();
	
	
	/**
	 * Indicates if this connection is a loop back connection. In a loop back connection
	 * sent messages will be redirected to it's received messages.
	 * 
	 * @return {@code true} if the connection loops back
	 */
	boolean isLoopback();

}
