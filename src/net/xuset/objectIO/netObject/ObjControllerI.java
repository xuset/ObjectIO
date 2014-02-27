package net.xuset.objectIO.netObject;

import net.xuset.objectIO.markupMsg.MarkupMsg;

public interface ObjControllerI {
	boolean syncObject(NetObject obj);
	boolean unsyncObject(NetObject obj);
	void sendUpdate(MarkupMsg msg, NetObject obj, long connectionId);
	void distributeRecievedUpdates();
}
