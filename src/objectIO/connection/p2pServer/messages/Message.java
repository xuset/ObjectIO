package objectIO.connection.p2pServer.messages;

import objectIO.connection.AbstractConnection;
import objectIO.markupMsg.MarkupMsg;
import objectIO.markupMsg.MsgAttribute;

public class Message extends MarkupMsg {
	public enum Options {
		To, From, Broadcast, NewConnection, RemoveConnection, Type;
	}
	
	public enum Types {
		Standard, Connection;
	}
	
	Message(Types type) {
		super();
		setAttribute(Options.Type, String.valueOf(type.ordinal()));
	}
	
	public Message() {
		this(Types.Standard);
	}
	
	public Message(String input) {
		super(input);
	}
	
	public boolean isBroadcast() {
		if (to() == AbstractConnection.BROADCAST_CONNECTION)
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
	public void from(long id) 	{ MsgAttribute a = getAttribute(Options.From); if (a != null) a.value = String.valueOf(id); }
	
	public long to() 		{ MsgAttribute a = getAttribute(Options.To); if (a != null) return Long.parseLong(a.value); return -1; }
	public void to(long id) { MsgAttribute a = getAttribute(Options.To); if (a != null) a.value = String.valueOf(id); }
	
	public MsgAttribute getAttribute(Options o) {
		return getAttribute(String.valueOf(o.ordinal()));
	}
	
	public MsgAttribute setAttribute(Options o, String value) {
		return setAttribute(String.valueOf(o.ordinal()), value);
	}
}
