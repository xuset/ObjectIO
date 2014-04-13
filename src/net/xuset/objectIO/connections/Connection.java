package net.xuset.objectIO.connections;

import net.xuset.objectIO.markupMsg.MarkupMsg;

/**
 * Interface that outlines operations for sending and receiving messages across various
 * mediums. A {@code MarkupMsg} object is used for sending and reading
 * received messages.
 * 
 * <p>Implementations are able to choose how the {@code MarkupMsg} is sent and
 * received. The message could be sent across a TCP stream, file stream, or some other
 * medium.</p>
 * 
 * <p>Most implementations will be setup so that sending a message from one
 * {@code Connection} will be received by another {@code Connection}.
 * Sending can be done by calling {@code sendMsg(MarkupMsg)} and getting a received message
 * can be done by calling {@code pollNextMsg()}</p>
 * 
 * @author xuset
 * @see MarkupMsg
 * @see net.xuset.objectIO.connections.sockets.tcp.TcpCon TcpCon
 * @see FileCon
 * @since 1.0
 */

public interface Connection {
	/**
	 * This is primarily used as the {@code connectionId} when calling
	 * {@link Hub#sendMsg(MarkupMsg, long)}. If the hub supports
	 * broadcasting messages then usually the supplied
	 * message is passed to {@link Hub#broadcastMsg(MarkupMsg)}
	 * 
	 * @see Hub
	 */
	public static final long BROADCAST_CONNECTION = 0l;
	
	/**
	 * Sends a MarkupMsg. Depending on the implementing
	 * class, the message could be set across a TCP stream, file stream, or some other
	 * medium.
	 * 
	 * <p>Implementations are free to determine how to send messages. The protocol for
	 * sending messages is also defined by the implementation.</p>
	 * 
	 * @param msg message to send across the connection
	 * @return true if the sending the message was successful, false otherwise.
	 * 			It does not guarantee that the message was received.
	 */
	boolean sendMsg(MarkupMsg msg);
	
	
	/**
	 * Each connection has a unique id that allows others to distinguish between
	 * connections
	 * 
	 * @return the unique id associated with this connection
	 */
	long getId();
	
	
	/**
	 * Removes and returns a received message.
	 * Messages are returned in FIFO order. So messages received earlier will be returned
	 * before messages received latter. Be aware that received messages are not guaranteed
	 * to be in the order they were sent.
	 * 
	 * <p>this method should only return null if {@code isMsgAvailable()} returns
	 * false</p>
	 * 
	 * <p>In most situations, one {@code Connection} will send a message to another
	 * {@code Connection}. The received message can be read by calling
	 * {@code pollNextMsg()}.</p>
	 * 
	 * @return the next available {@code MarkupMsg} or {@code null} if there
	 * 			are no messages to poll
	 */
	MarkupMsg pollNextMsg();
	
	
	/**
	 * Returns whether message has been received and can be returned by calling
	 * {@code pollNextMsg()}
	 * 
	 * @return true if {@code pollNextMsg()} will not return null
	 */
	boolean isMsgAvailable();
}
