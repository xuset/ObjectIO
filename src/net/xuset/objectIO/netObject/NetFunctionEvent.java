package net.xuset.objectIO.netObject;

import net.xuset.objectIO.connections.ConnectionI;
import net.xuset.objectIO.markupMsg.MarkupMsg;

public interface NetFunctionEvent {
	public MarkupMsg calledFunc(MarkupMsg args, ConnectionI c);
	public void returnedFunc(MarkupMsg args, ConnectionI c);
}
