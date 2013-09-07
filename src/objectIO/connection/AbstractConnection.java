package objectIO.connection;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import objectIO.markupMsg.MarkupMsg;

public abstract class AbstractConnection implements Connection{
	public static final long BROADCAST_CONNECTION = 0l;

	protected Queue<MarkupMsg> messageQueue = new ConcurrentLinkedQueue<MarkupMsg>();
	protected long endPointId = 0l;
	protected long myId = 0l;
	
	public Queue<MarkupMsg> getMessageQueue() { return messageQueue; }
	public long getEndPointId() { return endPointId; }
	public long getLocalId() { return myId; }
	public MarkupMsg getNextMessage() { return messageQueue.poll(); }
	public boolean messageAvailable() { return (messageQueue.isEmpty() == false); }
	
	public AbstractConnection(long myId) {
		this.myId = myId;
	}
	
	public AbstractConnection(ConnectionHub<?> hub) {
		this.myId = hub.getId();
	}
}
