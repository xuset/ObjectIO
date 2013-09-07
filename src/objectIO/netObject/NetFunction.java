package objectIO.netObject;

import objectIO.connection.Connection;
import objectIO.markupMsg.MarkupMsg;


public class NetFunction extends NetObject {
	private static final String callIdentifier = "call";
	private static final String returnIdentifier = "ret";
	
	public NetFunctionEvent function;

	public NetFunction(NetObjectControllerInterface controller, String name, NetFunctionEvent function) {
		this(controller, name);
		this.function = function;
	}
	public NetFunction(NetObjectControllerInterface controller, String name) {
		super(controller, name);
	}
	
	public NetFunction(String name) {
		super(null, name);
	}
	
	public void sendCall(MarkupMsg dOrg, Connection c) {
		sendCall(dOrg, c.getEndPointId());
	}
	
	public void sendCall(MarkupMsg dOrg, long connectionId) {
		dOrg.setAttribute("type", callIdentifier);
		controller.sendUpdate(dOrg, this, connectionId);
	}

	public void parseUpdate(MarkupMsg msg, Connection c) {
		String type = msg.getAttribute("type").value;
		if (type.equals(callIdentifier)) {
			MarkupMsg ret = function.calledFunc(msg, c);
			if  (ret != null) {
				ret.setAttribute("type", returnIdentifier);
				controller.sendUpdate(ret, this, c.getEndPointId());
			}
		} else if (type.equals(returnIdentifier)) {
			function.returnedFunc(msg, c);
		}
		return;
	}
}
