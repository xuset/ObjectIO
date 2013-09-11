package objectIO.connection.p2pServer.messages;

import objectIO.connection.Connection;
import objectIO.markupMsg.MarkupMsg;
import objectIO.markupMsg.MsgAttribute;

public class ConnectMsg {
	public static Message addConnection(Connection c) {
		Message m = new Message(Message.Types.Connection);
		MsgAttribute a = new MsgAttribute("add");
		a.set(c.getEndPointId());
		m.addAttribute(a);
		return m;
	}
	
	public static Message removeConnection(Connection c) {
		Message m = new Message(Message.Types.Connection);
		MsgAttribute a = new MsgAttribute("rm");
		a.set(c.getEndPointId());
		m.addAttribute(a);
		return m;
	}
	
	public static long getConChangeId(MarkupMsg m) {
		return m.getAttribute("conId").getLong();
	}
	
	public static boolean isAddConnection(MarkupMsg m) {
		return m.getAttribute("add") != null;
	}
	
	public static boolean isRemoveConnection(MarkupMsg m) {
		return m.getAttribute("rm") != null;
	}
}
