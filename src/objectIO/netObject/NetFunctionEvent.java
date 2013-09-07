package objectIO.netObject;

import objectIO.connection.Connection;
import objectIO.markupMsg.MarkupMsg;

public interface NetFunctionEvent {
	public MarkupMsg calledFunc(MarkupMsg args, Connection c);
	public void returnedFunc(MarkupMsg args, Connection c);
}
