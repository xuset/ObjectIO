package net.xuset.objectIO.connections.sockets.groupNet;

import java.util.LinkedList;
import java.util.List;

import net.xuset.objectIO.connections.Connection;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgAttribute;

public class GroupNetMsg extends MarkupMsg {
	public enum Options {
		To, From, Broadcast, NewConnection, RemoveConnection, Type;
	}
	
	public enum Types {
		Standard, Connection;
	}
	
	GroupNetMsg(Types type) {
		super();
		setAttribute(Options.Type, String.valueOf(type.ordinal()));
	}
	
	public GroupNetMsg(MarkupMsg msg) {
		super(msg.getAttributes(), msg.getNestedMsgs());
		setName(msg.getName());
		setContent(msg.getContent());
	}
	
	public GroupNetMsg(List<MarkupMsg> nestedMsgs) {
		super(new LinkedList<MsgAttribute>(), nestedMsgs);
	}
	
	public GroupNetMsg() {
		this(Types.Standard);
	}
	
	public boolean isBroadcast() {
		if (to() == Connection.BROADCAST_CONNECTION)
			return true;
		MsgAttribute a = getAttribute(Options.Broadcast);
		if (a != null && a.getValue() != null)
			return (a.getValue().equals("1")) ? true : false;
		return false;
	}
	
	public void setBroadcast(boolean isBroadcast) {
		setAttribute(Options.Broadcast, (isBroadcast) ? "1" : "0");
	}

	private static Types[] types = Types.values();
	public Types type() {
		MsgAttribute a = getAttribute(Options.Type);
		int index = Integer.parseInt(a.getValue());
		return types[index];
	}
	
	public long from() {
		MsgAttribute a = getAttribute(Options.From);
		if (a != null)
			return Long.parseLong(a.getValue());
		
		return -1;
	}
	
	public void from(long id) {
		setAttribute(Options.From, String.valueOf(id));
	}
	
	public long to() {
		MsgAttribute a = getAttribute(Options.To);
		if (a != null)
			return Long.parseLong(a.getValue());
		
		return -1;
	
	}
	public void to(long id) {
		setAttribute(Options.To, String.valueOf(id));
	}
	
	public MsgAttribute getAttribute(Options o) {
		return getAttribute(String.valueOf(o.ordinal()));
	}
	
	public MsgAttribute setAttribute(Options o, String value) {
		return setAttribute(String.valueOf(o.ordinal()), value);
	}
}
