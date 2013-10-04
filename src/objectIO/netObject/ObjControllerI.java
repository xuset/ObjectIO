package objectIO.netObject;

import objectIO.markupMsg.MarkupMsg;

interface NetObjectControllerInterface {
	boolean syncObject(NetObject obj);
	boolean unsyncObject(NetObject obj);
	void sendUpdate(MarkupMsg msg, NetObject obj, long connectionId);
}
