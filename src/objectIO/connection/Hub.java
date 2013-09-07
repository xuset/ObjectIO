package objectIO.connection;

import java.util.LinkedList;

import objectIO.markupMsg.MarkupMsg;

public interface Hub <T extends Connection> {
	public LinkedList<T> getAllConnections();
	public T getConnection(long endPointid);
	public long getId();
	
	public boolean addConnection(T connection);
	public boolean sendMessage(MarkupMsg message, long endPointId);
}
