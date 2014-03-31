package net.xuset.objectIO.connections.sockets;


/**
 * Interface whose methods are called by the connection it belongs to when an event
 * occurs.
 * 
 * @author xuset
 * @see InetCon
 * @since 1.0
 *
 */
public interface InetEventListener {
	
	
	/**
	 * Called when the connection has been closed.
	 */
	void onClose();
}
