package net.xuset.objectIO.connections;

import java.util.LinkedList;
import java.util.Queue;

import net.xuset.objectIO.markupMsg.MarkupMsg;

/**
 * Basic, abstract implementation of <code>ConnectionI</code>. This is just a helper
 * class that leaves sending and receiving of messages up to the implementing
 * class. <code>Connection</code> is backed by a <code>Queue</code> for storing received
 * messages. The implementing class can call the protected method
 * <code>addMsgToQueue</code> to add a received message to the queue. Calling
 * <code>pollNextMsg</code> polls from the queue.
 * 
 * @author xuset
 * @see ConnectionI
 * @since 1.0
 */

public abstract class Connection implements ConnectionI{
	/**
	 * This is primarily used as the <code>connectionId</code> when calling
	 * {@link HubI#sendMsg(MarkupMsg, long)}. If the hub supports
	 * broadcasting messages then usually the supplied
	 * message is passed to {@link HubI#broadcastMsg(MarkupMsg)}
	 * 
	 * @see HubI
	 * @see HubI#sendMsg(MarkupMsg, long)
	 * @see HubI#broadcastMsg(MarkupMsg)
	 * @since 1.0
	 */
	public static final long BROADCAST_CONNECTION = 0l;

	private final Queue<MarkupMsg> messageQueue = new LinkedList<MarkupMsg>();
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
	 * Sole constructor for <code>Connection</code>.
	 * 
	 * @param id value returned by <code>getId()</code>
	 * @see #getId()
	 */
	public Connection(long id) {
		this.id = id;
	}
}
