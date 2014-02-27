package net.xuset.objectIO.netObject;

import net.xuset.objectIO.connections.Connection;
import net.xuset.objectIO.markupMsg.MarkupMsg;

public interface NetFunctionEvent {
	public MarkupMsg calledFunc(MarkupMsg args, Connection c);
	public void returnedFunc(MarkupMsg args, Connection c);
}
