package objectIO.connections.sockets.p2pServer;

import objectIO.markupMsg.MsgAttribute;

public class CmdCrafter {
	public static P2PMsg craftNewCon(long id) {
		P2PMsg m = new P2PMsg(P2PMsg.Types.Connection);
		MsgAttribute a = new MsgAttribute("add");
		a.set(id);
		m.addAttribute(a);
		return m;
	}
	
	public static boolean isNewCon(P2PMsg msg) {
		return msg.getAttribute("add") != null;
	}
	
	public static long getNewConId(P2PMsg msg) {
		return msg.getAttribute("add").getLong();
	}
	
	public static P2PMsg craftDisconnect(long id) {
		P2PMsg m = new P2PMsg(P2PMsg.Types.Connection);
		MsgAttribute a = new MsgAttribute("rm");
		a.set(id);
		m.addAttribute(a);
		return m;
	}
	
	public static boolean isDisconnect(P2PMsg msg) {
		return msg.getAttribute("rm") != null;
	}
	
	public static long getDisconnectId(P2PMsg msg) {
		return msg.getAttribute("rm").getLong();
	}
	
}
