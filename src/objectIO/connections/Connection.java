package objectIO.connections;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import objectIO.markupMsg.MarkupMsg;
import objectIO.markupMsg.MsgAttribute;

public abstract class Connection{
	public static final long BROADCAST_CONNECTION = 0l;

	protected Queue<MarkupMsg> messageQueue = new ConcurrentLinkedQueue<MarkupMsg>();
	protected long endId = -1l;
	protected long myId = -1l;
	
	public abstract boolean sendMsg(MarkupMsg msg);
	
	public Queue<MarkupMsg> msgQueue() { return messageQueue; }
	public long getEndId() { return endId; }
	public long getId() { return myId; }
	public MarkupMsg getNextMsg() { return messageQueue.poll(); }
	public boolean msgAvailable() { return (messageQueue.isEmpty() == false); }
	
	public Connection(Hub<?> hub) {
		this.myId = hub.getId();
	}
	
	public Connection() { }
	
	protected boolean sendMeetAndGreet(long timeout) {
		final String attributeName = "new connection";
		MarkupMsg m = new MarkupMsg();
		m.addAttribute(MsgAttribute.cre(attributeName).set(myId));
		if (sendMsg(m) == false)
			return false;
		long timeStarted = System.currentTimeMillis();
		while (timeStarted + timeout > System.currentTimeMillis()) {
			MarkupMsg next = null;
			while ((next = messageQueue.poll()) != null) {
				MsgAttribute na = next.getAttribute(attributeName);
				if (na != null) {
					endId = na.getLong();
					return true;
				}
			}
			try { Thread.sleep(2); } catch (Exception ex) { }
		}
		System.out.println(myId + " missed connection");
		return false;
	}
}
