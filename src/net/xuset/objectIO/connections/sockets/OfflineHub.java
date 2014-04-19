package net.xuset.objectIO.connections.sockets;

import net.xuset.objectIO.markupMsg.MarkupMsg;


/**
 * This is a dummy class that does absolutely nothing. 
 * 
 * <p>This class can be useful if you
 * have network code and wish do disable the networking functionality. The hub can be
 * replaced with a OfflineHub object. For example, suppose you have a TcpServer and don't
 * want the networking fucntionallity that TcpServer provides. With little changes to the
 * code, the hub object could be replaced by OfflineHub since they both implement
 * InetHub</p>
 * 
 * @author xuset
 * @see net.xuset.objectIO.connections.sockets.tcp.TcpServer TcpServer
 * @since 1.0
 *
 */
public class OfflineHub implements InetHub<InetCon> {
	private final long localId;
	
	
	/**
	 * Sole constructor for OfflineHub.
	 * 
	 * @param localId the local id that will be returned when calling {@code getLocalId()}
	 */
	public OfflineHub(long localId) {
		this.localId = localId;
	}
	
	
	/**
	 * Will always return null
	 * 
	 * @return always null
	 */
	@Override
	public InetCon getConnectionById(long connectionId) {
		return null;
	}

	
	/**
	 * The connection will not be stored and cannot be retrieved by calling
	 * {@code getConnectionByIndex(int)} or {@code getConnection(long)}.
	 * 
	 * @return always true
	 */
	@Override
	public boolean addConnection(InetCon connection) {
		return true;
	}

	
	/**
	 * No message is actually sent due to connections not being stored.
	 * 
	 * @return always true
	 */
	@Override
	public boolean sendMsg(MarkupMsg message, long connectionId) {
		return true;
	}

	
	/**
	 * No message is broadcasted due to connections not being stored.
	 * 
	 * @return always true
	 */
	@Override
	public boolean broadcastMsg(MarkupMsg message) {
		return true;
	}

	
	/**
	 * Will always throw an IndexOutOfBoundsException because no connections are stored.
	 * 
	 * @return never returns due to IndexOutOfBoundsException always being thrown
	 */
	@Override
	public InetCon getConnectionByIndex(int index) {
		throw new IndexOutOfBoundsException();
	}

	
	/**
	 * Since no connections are stored, the connection count is always 0
	 * 
	 * @return always 0
	 */
	@Override
	public int getConnectionCount() {
		return 0;
	}
	

	/**
	 * Connections are not stored so calling this method does nothing.
	 * 
	 * @return always true
	 */
	@Override
	public boolean removeConnection(InetCon connection) {
		return true;
	}

	
	/**
	 * Does absolutely nothing
	 */
	@Override
	public void shutdown() {
		
	}

	/** Does nothing.
	 * 
	 * @return always {@code true}
	 */
	@Override
	public boolean isShutdown() {
		return false;
	}

	
	@Override
	public long getLocalId() {
		return localId;
	}

	
	/**
	 * Any ServerEvent objects added will not be stored. 
	 * 
	 * @return always true
	 */
	@Override
	public boolean watchEvents(ServerEventListener e) {
		return true;
	}
	
	
	/**
	 * Since ServerEvent objects are not stored, calling {@code unwatchEvents} does
	 * nothing.
	 * 
	 * @return always true
	 */
	@Override
	public boolean unwatchEvents(ServerEventListener e) {
		return true;
	}
}
