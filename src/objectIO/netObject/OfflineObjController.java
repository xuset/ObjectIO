package objectIO.netObject;

import objectIO.markupMsg.MarkupMsg;

public class OfflineObjController implements ObjControllerI{

	@Override
	public boolean syncObject(NetObject obj) {
		return true;
	}

	@Override
	public boolean unsyncObject(NetObject obj) {
		return true;
	}

	@Override
	public void sendUpdate(MarkupMsg msg, NetObject obj, long connectionId) {
				
	}

	@Override
	public void distributeRecievedUpdates() {
		
	}

}
