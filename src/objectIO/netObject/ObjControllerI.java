package objectIO.netObject;

import objectIO.markupMsg.MarkupMsg;

public interface ObjControllerI {
	boolean syncObject(NetObject obj);
	boolean unsyncObject(NetObject obj);
	void sendUpdate(MarkupMsg msg, NetObject obj, long connectionId);
}
