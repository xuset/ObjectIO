package net.xuset.objectIO.connections;

import net.xuset.objectIO.markupMsg.MarkupMsg;

/**
 * A container interface for <code>ConnectionI</code> objects.
 * <code>HubI</code> outlines simple operations that are performed on connections.
 * 
 * <p>It is worth noting that a connection does not have to be stored in a hub.</p>
 * 
 * @author xuset
 * @param <T> type of {@link ConnectionI} to store
 * @since 1.0
 */

public interface HubI <T extends ConnectionI> {
	
	
	/**
	 * Searches stored connections for an id matching the supplied argument id.
	 * The id of stored connections is determined by {@link ConnectionI#getId()}.
	 * 
	 * <p>It is good practice when adding connections to the hub to not add multiple
	 * connections with same id. This can cause unexpected results.</p>
	 * 
	 * @param connectionId the id of the connection to return
	 * @return returns a connection with an id equal to <code>connectionId</code>. If no
	 * 			connection is found, <code>null</code> is returned.
	 * @see ConnectionI
	 * @since 1.0
	 */
	public T getConnectionById(long connectionId);
	
	
	/**
	 * Returns a stored connection.
	 * This can be used to traverse all the stored connections. Connections are not
	 * Guaranteed to be in specific order. If <code>index</index> is less than zero or
	 * greater than {@link #getConnectionCount()} then a
	 * <code>IndexOutOfBoundsException</code> is thrown.
	 * 
	 * @param index index of the element to return.
	 * @return the connection at the specified index
	 * @throws IndexOutOfBoundsException if index is out of bounds
	 * @see ConnectionI
	 * @since 1.0
	 */
	public T getConnectionByIndex(int index);
	
	
	/**
	 * Returns the number of connections that are stored.
	 * This can be used along with getConnectionByIndex to access all the stored
	 * connections.
	 * 
	 * @return the amount of connections stored.
	 * @since 1.0
	 */
	public int getConnectionCount();
	
	
	/**
	 * Stores a connection. Once added, the connection can be retrieved using
	 * <code>getConnectionById(long)</code> and <code>getConnectionByIndex(int)</code>
	 * 
	 * @param connection the connection to store
	 * @return true if the method was successful in adding the connection
	 * @see ConnectionI
	 * @since 1.0
	 */
	public boolean addConnection(T connection);
	
	
	/**
	 * Removes a previously stored connection.
	 * 
	 * @param connection the connection to remove
	 * @return true if the connection was found and removed successfully
	 * @see ConnectionI
	 * @since 1.0
	 */
	public boolean removeConnection(T connection);
	
	
	/**
	 * Finds a connection whose {@link ConnectionI#getId() getId()} equals the supplied
	 * argument <code>connectionId</code> then sends the message via the found connection.
	 * Usually the message is sent by calling {@link ConnectionI#sendMsg(MarkupMsg)} on
	 * the found connection.
	 * 
	 * @param message the message to send
	 * @param connectionId the id of the connection to use
	 * @return returns true if the message sent successfully or false if no connection was
	 * 			found or the sending the message failed
	 * @since 1.0
	 * @see ConnectionI
	 */
	public boolean sendMsg(MarkupMsg message, long connectionId);
	
	
	/**
	 * The supplied <code>MarkupMsg</code> is sent to all stored connections.
	 * Usually this done by calling {@linkplain ConnectionI#sendMsg(MarkupMsg)} on each
	 * stored connection.
	 * 
	 * @param message the message sent to all stored connections
	 * @return true if the broadcast was successful, false otherwise
	 */
	public boolean broadcastMsg(MarkupMsg message);
}
