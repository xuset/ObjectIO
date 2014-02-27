package net.xuset.objectIO.connections;

import java.util.LinkedList;
import java.util.List;

import net.xuset.objectIO.markupMsg.MarkupMsg;



public class OfflineHub implements Hub<Connection> {
	private final LinkedList<Connection> list = new LinkedList<Connection>();
	private final long id;

	public OfflineHub(long id) {
		this.id = id;
	}
	
	@Override
	public List<Connection> getAllConnections() {
		return list;
	}

	@Override
	public Connection getConnection(long endPointid) {
		return null;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public boolean addConnection(Connection connection) {
		return true;
	}

	@Override
	public boolean sendMsg(MarkupMsg message, long endPointId) {
		return true;
	}

	@Override
	public boolean broadcastMsg(MarkupMsg message) {
		return true;
	}

}
