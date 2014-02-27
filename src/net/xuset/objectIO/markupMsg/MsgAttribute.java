package net.xuset.objectIO.markupMsg;

import net.xuset.objectIO.netObject.NetVar;

public class MsgAttribute {
	public String name, value;
	
	public MsgAttribute() { }
	public MsgAttribute(String name) {
		this.name = name;
	}
	public MsgAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}
	public MsgAttribute(NetVar<?> v) {
		this.name = v.getId();
		this.value = v.toString();
	}
	
	public static MsgAttribute cre(String name) {
		MsgAttribute n = new MsgAttribute();
		n.name = name;
		return n;
	}
	
	public String getName() { return name; }
	
	public String getString() { return value; }
	public int getInt() { return Integer.parseInt(value); }
	public double getDouble() { return Double.parseDouble(value); }
	public float getFloat() { return Float.parseFloat(value); }
	public long getLong() { return Long.parseLong(value); }
	public boolean getBool() { return Boolean.parseBoolean(value); }
	
	public MsgAttribute set(int value) 		{ return set(String.valueOf(value)); }
	public MsgAttribute set(double value) 	{ return set(String.valueOf(value)); }
	public MsgAttribute set(float value) 	{ return set(String.valueOf(value)); }
	public MsgAttribute set(long value) 	{ return set(String.valueOf(value)); }
	public MsgAttribute set(boolean value) 	{ return set(String.valueOf(value)); }
	public MsgAttribute set(char value) 	{ return set(String.valueOf(value)); }
	public MsgAttribute set(char[] value) 	{ return set(String.valueOf(value)); }
	public MsgAttribute set(Object value)	{ return set(String.valueOf(value)); }
	public MsgAttribute set(String value) 	{
		this.value = value;
		return this;
	}
}
