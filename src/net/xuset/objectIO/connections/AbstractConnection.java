package net.xuset.objectIO.connections;

import java.util.LinkedList;
import java.util.Queue;

import net.xuset.objectIO.markupMsg.MarkupMsg;

/**
 * Basic, abstract implementation of <code>Connection</code>. This is just a helper
 * class that leaves sending and receiving of messages up to the implementing
 * class. <code>Connection</code> is backed by a <code>Queue</code> for storing received
 * messages. The implementing class can call the protected method
 * <code>addMsgToQueue</code> to add a received message to the queue. Calling
 * <code>pollNextMsg</code> polls from the queue.
 * 
 * @author xuset
 * @see Connection
 * @since 1.0
 */

public abstract class AbstractConnection implements Connection{
	private final Queue<MarkupMsg> messageQueue;
	private final long id;
	
	@Override
	public abstract boolean sendMsg(MarkupMsg msg);

	@Override public long getId() { return id; }
	@Override public MarkupMsg pollNextMsg() { return messageQueue.poll(); }
	@Override public boolean isMsgAvailable() { return !messageQueue.isEmpty(); }
	
	
	/**
	 * <code>Connection</code> has a queue for storing received messages.
	 * Received messages can be added to this queue by calling this method.
	 * Calling <code>pollNextMsg()</code> polls items added the queue.
	 * 
	 * @param msg message to be added to the queue
	 * @see #pollNextMsg
	 */
	protected void addMsgToQueue(MarkupMsg msg) {
		messageQueue.add(msg);
	}
	
	/**
	 * Constructs a new instance with the given id.
	 * 
	 * @param id value returned by <code>getId()</code>
	 * @see #getId()
	 */
	public AbstractConnection(long id) {
		this(id, new LinkedList<MarkupMsg>());
	}
	
	
	/**
	 * Constructs a new instance with the given id. The given Queue object is used
	 * to store received messages. Messages are added to this queue by calling
	 * {@code addMsgToQueue(MarkupMsg)} and read by calling {@code pollNextMsg()}.
	 * 
	 * @param id the id of the connection
	 * @param receivedQueue the received messages queue
	 */
	public AbstractConnection(long id, Queue<MarkupMsg> receivedQueue) {
		messageQueue = receivedQueue;
		this.id = id;
	}
}
