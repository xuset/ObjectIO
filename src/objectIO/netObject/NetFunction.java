package objectIO.netObject;

import objectIO.connections.Connection;
import objectIO.markupMsg.MarkupMsg;


public class NetFunction extends NetObject {
	private static final String callIdentifier = "call";
	private static final String returnIdentifier = "ret";
	
	public NetFunctionEvent function;

	public NetFunction(ObjControllerI controller, String name, NetFunctionEvent function) {
		this(controller, name);
		this.function = function;
	}
	public NetFunction(ObjControllerI controller, String name) {
		super(controller, name);
	}
	
	public NetFunction(String name) {
		super(null, name);
	}
	
	public void sendCall(MarkupMsg msg, Connection c) {
		sendCall(msg, c.getEndId());
	}
	
	public void sendCall(MarkupMsg msg, long connectionId) {
		sendMsg(callIdentifier, msg, connectionId);
	}

	@Override
	protected void parseUpdate(MarkupMsg msg, Connection c) {
		String type = msg.getAttribute("type").value;
		MarkupMsg child = msg.child.get(0);
		if (type.equals(callIdentifier)) {
			MarkupMsg ret = function.calledFunc(child, c);
			if  (ret != null)
				sendMsg(returnIdentifier, ret, c.getEndId());
		} else if (type.equals(returnIdentifier)) {
			function.returnedFunc(child, c);
		}
		return;
	}
	
	private void sendMsg(String type, MarkupMsg msg, long connectionId) {
		MarkupMsg parent = new MarkupMsg();
		parent.setAttribute("type", type);
		parent.child.add(msg);
		sendUpdate(parent, connectionId);
		
	}
}
