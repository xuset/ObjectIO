package net.xuset.objectIO.netObject;

import net.xuset.objectIO.connections.ConnectionI;
import net.xuset.objectIO.markupMsg.MarkupMsg;

public interface NetObjUpdater {
	boolean registerNetObj(NetObject obj);
	boolean unregisterNetObj(NetObject obj);
	
	void sendUpdate(MarkupMsg msg, NetObject obj, long connectionId);
	
	void distributeAllUpdates();
	boolean distributeUpdate(MarkupMsg msg, ConnectionI con);
}
