package net.xuset.objectIO.connections;

import net.xuset.objectIO.markupMsg.MsgParsable;

/**
 * An extension of {@code ConnectionI}, this interface outlines methods that could
 * by useful for connections that are backed by streams.
 * 
 * @author xuset
 * @since 1.0
 * @see java.io.InputStream
 * @see java.io.OutputStream
 */

public interface StreamConI extends ConnectionI, MsgParsable{
	
	/**
	 * Returns whether the connection has been closed.
	 * 
	 * <p>If the connection is closed, the connection should no longer be used. Be aware,
	 * this method can return true even though {@code close()} was never called.</p>
	 * 
	 * @return true if the connection is closed, false otherwise.
	 * @since 1.0
	 */
	boolean isClosed();
	
	
	/**
	 * Closes the connection, releasing any system resources.
	 * Once a connection has been closed, it should no longer be used.
	 * 
	 * @since 1.0
	 */
	void close();
	
	
	/**
	 * Flushes the connection's output buffer. Not all connections buffer their outgoing
	 * messages so calling flush on some implementations will do nothing.
	 * 
	 * @since 1.0
	 */
	void flush();
}
