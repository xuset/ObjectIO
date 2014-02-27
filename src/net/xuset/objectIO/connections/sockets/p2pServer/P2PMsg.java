package net.xuset.objectIO.connections.sockets.p2pServer;

import net.xuset.objectIO.connections.Connection;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgAttribute;

public class P2PMsg extends MarkupMsg {
	public enum Options {
		To, From, Broadcast, NewConnection, RemoveConnection, Type;
	}
	
	public enum Types {
		Standard, Connection;
	}
	
	P2PMsg(Types type) {
		super();
		setAttribute(Options.Type, String.valueOf(type.ordinal()));
	}
	
	public P2PMsg() {
		this(Types.Standard);
	}
	
	public P2PMsg(String input) {
		super(input);
	}
	
	public P2PMsg(String input, int nestedLevels) {
		super(input, nestedLevels);
	}
	
	public boolean isBroadcast() {
		if (to() == Connection.BROADCAST_CONNECTION)
			return true;
		MsgAttribute a = getAttribute(Options.Broadcast);
		if (a != null && a.value != null)
			return (a.value.equals("1")) ? true : false;
		return false;
	}
	
	public void setBroadcast(boolean isBroadcast) {
		setAttribute(Options.Broadcast, (isBroadcast) ? "1" : "0");
	}

	private static Types[] types = Types.values();
	public Types type() {
		MsgAttribute a = getAttribute(Options.Type);
		int index = Integer.parseInt(a.value);
		return types[index];
	}
	
	public long from() 			{ MsgAttribute a = getAttribute(Options.From); if (a != null) return Long.parseLong(a.value); return -1; }
	public void from(long id) 	{ setAttribute(Options.From, String.valueOf(id)); }
	
	public long to() 		{ MsgAttribute a = getAttribute(Options.To); if (a != null) return Long.parseLong(a.value); return -1; }
	public void to(long id) { setAttribute(Options.To, String.valueOf(id)); }
	
	public MsgAttribute getAttribute(Options o) {
		return getAttribute(String.valueOf(o.ordinal()));
	}
	
	public MsgAttribute setAttribute(Options o, String value) {
		return setAttribute(String.valueOf(o.ordinal()), value);
	}
}
