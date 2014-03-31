package net.xuset.objectIO.netObject;

import net.xuset.objectIO.connections.ConnectionI;
import net.xuset.objectIO.markupMsg.MarkupMsg;

public class OfflineObjController implements NetObjUpdater{

	@Override
	public boolean registerNetObj(NetObject obj) {
		return true;
	}

	@Override
	public boolean unregisterNetObj(NetObject obj) {
		return true;
	}

	@Override
	public void sendUpdate(MarkupMsg msg, NetObject obj, long connectionId) {
				
	}

	@Override
	public void distributeAllUpdates() {
		
	}

	@Override
	public boolean distributeUpdate(MarkupMsg msg, ConnectionI con) {
		return true;
	}

}
