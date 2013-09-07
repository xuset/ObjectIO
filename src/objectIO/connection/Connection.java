package objectIO.connection;

import java.util.Queue;

import objectIO.markupMsg.MarkupMsg;

public interface Connection {
	public boolean sendMessage(MarkupMsg message);
	public Queue<MarkupMsg> getMessageQueue();
	public long getEndPointId();
	public long getLocalId();
	public MarkupMsg getNextMessage();
	public boolean messageAvailable();
}
