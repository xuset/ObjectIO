package objectIO.objectCreate;

import objectIO.connection.Connection;
import objectIO.markupMsg.MarkupMsg;
import objectIO.netObject.NetFunction;
import objectIO.netObject.NetFunctionEvent;
import objectIO.netObject.NetObjectController;

public class CreatorRoot {
	public static final String netFuncId = "create";
	
	private NetFunction function;
	
	public CreatorRoot(NetObjectController controller) {
		function = new NetFunction(controller, netFuncId);
		function.function = new NetFunctionEvent() {
			@Override
			public MarkupMsg calledFunc(MarkupMsg args, Connection c) {
				
				return null;
			}

			@Override
			public void returnedFunc(MarkupMsg args, Connection c) {
				
			}
		};
	}

}
