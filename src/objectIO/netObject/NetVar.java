package objectIO.netObject;

import objectIO.connections.Connection;
import objectIO.markupMsg.MarkupMsg;

public class NetVar extends NetObject {
	private String value;
	private boolean hasChanged = false;

	public NetVarChange onChangeEvent;
	public boolean autoUpdate = false;

	public NetVar(NetObjectControllerInterface controller, String name) {
		super(controller, name);
	}
	public NetVar(NetObjectControllerInterface controller, String name, String initialValue) {
		super(controller, name);
		value = initialValue;
	}
	public NetVar(NetObjectControllerInterface controller, String name, String initialValue, boolean autoUpdate) {
		this(controller, name, initialValue);
		this.autoUpdate = autoUpdate;
	}
	
	public NetVar(NetObjectControllerInterface controller, String name, String initialValue, boolean autoUpdate, NetVarChange event) {
		this(controller, name, initialValue, autoUpdate);
		onChangeEvent = event;
	}
	
	public String getToString() { return value; }
	public int getToInt() { return Integer.parseInt(value); }
	public double getToDouble() { return Double.parseDouble(value); }
	public float getToFloat() { return Float.parseFloat(value); }
	public long getToLong() { return Long.parseLong(value); }
	public boolean getToBool() { return Boolean.parseBoolean(value); }
	
	public void set(int value) 		{ set(String.valueOf(value)); }
	public void set(double value) 	{ set(String.valueOf(value)); }
	public void set(float value) 	{ set(String.valueOf(value)); }
	public void set(long value) 	{ set(String.valueOf(value)); }
	public void set(boolean value) 	{ set(String.valueOf(value)); }
	public void set(char value) 	{ set(String.valueOf(value)); }
	public void set(char[] value) 	{ set(String.valueOf(value)); }
	public void set(String value) 	{
		hasChanged = true;
		this.value = value;
		if (autoUpdate)
			sendVar();
	}
	
	public void update() {
		sendVar();
	}

	public void parseUpdate(MarkupMsg msg, Connection c) {
		value = msg.content;
		onChangeEvent.onChange(this, c);
	}
	
	private void sendVar() {
		if (hasChanged) {
			MarkupMsg msg = new MarkupMsg();
			msg.content = value;
			controller.sendUpdate(msg, this, Connection.BROADCAST_CONNECTION);
			hasChanged = false;
		}
	}
}
