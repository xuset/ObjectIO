package net.xuset.objectIO.netObject;

import net.xuset.objectIO.connections.ConnectionI;
import net.xuset.objectIO.markupMsg.MarkupMsg;


public class NetFunction extends NetObject {
	private static final String callIdentifier = "call";
	private static final String returnIdentifier = "ret";
	
	public NetFunctionEvent function;

	public NetFunction(NetObjUpdater controller, String name, NetFunctionEvent function) {
		this(controller, name);
		this.function = function;
	}
	public NetFunction(NetObjUpdater controller, String name) {
		super(controller, name);
	}
	
	public NetFunction(String name) {
		super(null, name);
	}
	
	public void sendCall(MarkupMsg msg, ConnectionI c) {
		sendCall(msg, c.getId());
	}
	
	public void sendCall(MarkupMsg msg, long connectionId) {
		sendMsg(callIdentifier, msg, connectionId);
	}
	
	@Override
	public MarkupMsg getValue() {
		return null;
	}

	@Override
	protected void parseUpdate(MarkupMsg msg, ConnectionI c) {
		String type = msg.getAttribute("type").getValue();
		MarkupMsg child = msg.getNestedMsgs().get(0);
		if (type.equals(callIdentifier)) {
			MarkupMsg ret = function.calledFunc(child, c);
			if  (ret != null)
				sendMsg(returnIdentifier, ret, c.getId());
		} else if (type.equals(returnIdentifier)) {
			function.returnedFunc(child, c);
		}
		return;
	}
	
	private void sendMsg(String type, MarkupMsg msg, long connectionId) {
		MarkupMsg parent = new MarkupMsg();
		parent.setAttribute("type", type);
		parent.addNested(msg);
		sendUpdate(parent, connectionId);
		
	}
}
