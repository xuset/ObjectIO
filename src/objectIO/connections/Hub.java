package objectIO.connections;

import java.util.List;

import objectIO.markupMsg.MarkupMsg;

public interface Hub <T extends Connection> {
	public List<T> getAllConnections();
	public T getConnection(long endPointid);
	public long getId();
	
	public boolean addConnection(T connection);
	public boolean sendMsg(MarkupMsg message, long endPointId);
	public boolean broadcastMsg(MarkupMsg message);
}
