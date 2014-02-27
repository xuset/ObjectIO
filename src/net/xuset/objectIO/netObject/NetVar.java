package net.xuset.objectIO.netObject;

import net.xuset.objectIO.connections.Connection;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.netObject.NetObject;
import net.xuset.objectIO.netObject.ObjControllerI;



public abstract class NetVar <T> extends NetObject {
	protected T value;
	protected T oldValue;
	
	public static interface OnChange<T> {
		public void onChange(NetVar<T> var, Connection c);
	};
	
	public boolean autoUpdate = true;
	private boolean fireEventOnLocalChange = false;
	private OnChange<T> event = null;
	
	protected abstract T parse(String sValue);

	public NetVar(T initial, String id, ObjControllerI controller) {
		super(controller, id);
		value = initial;
		oldValue = value;
	}
	
	public void setEvent(boolean fireOnLocalChange, OnChange<T> event) {
		this.fireEventOnLocalChange = fireOnLocalChange;
		this.event = event;
	}
	
	public void set(T newVal) {
		value = newVal;
		if (autoUpdate)
			update();
	}
	
	public T get() {
		return value;
	}
	
	public void update() {
		if (needsUpdating()) {
			sendUpdate(toString());
			oldValue = value;
			if (fireEventOnLocalChange && event != null)
				event.onChange(this, null);
		}
	}
	
	@Override
	public MarkupMsg getValue() {
		MarkupMsg msg = new MarkupMsg();
		msg.content = toString();
		return msg;
	}

	@Override
	protected void parseUpdate(MarkupMsg msg, Connection c) {
		value = parse(msg.content);
		oldValue = value;
		if (event != null)
			event.onChange(this, c);
	}
	
	protected final void sendUpdate(String sValue) {
		MarkupMsg msg = new MarkupMsg();
		msg.content = sValue;
		sendUpdate(msg, Connection.BROADCAST_CONNECTION);
	}
	
	protected boolean needsUpdating() {
		if (value == oldValue || value.equals(oldValue))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	//subclass definitions//
	
	//Integer
	
	public static class nInt extends NetVar<Integer> {
		public nInt(Integer initial, String id, ObjControllerI controller) {
			super(initial, id, controller);
		}
		
		@Override
		protected Integer parse(String newVal) {
			return Integer.valueOf(Integer.parseInt(newVal));
		}
	}
	
	//Boolean
	
	public static class nBool extends NetVar<Boolean> {
		public nBool(Boolean initial, String id, ObjControllerI controller) {
			super(initial, id, controller);
		}

		@Override
		protected Boolean parse(String sValue) {
			return Boolean.parseBoolean(sValue);
		}
	}
	
	//Double
	
	public static class nDouble extends NetVar<Double> {
		public nDouble(Double initial, String id, ObjControllerI controller) {
			super(initial, id, controller);
		}

		@Override
		protected Double parse(String sValue) {
			return Double.parseDouble(sValue);
		}
	}
	
	//Long
	
	public static class nLong extends NetVar<Long> {
		public nLong(Long initial, String id, ObjControllerI controller) {
			super(initial, id, controller);
		}

		@Override
		protected Long parse(String sValue) {
			return Long.parseLong(sValue);
		}
	}
	
	//Float
	
	public static class nFloat extends NetVar<Float> {
		public nFloat(Float initial, String id, ObjControllerI controller) {
			super(initial, id, controller);
		}

		@Override
		protected Float parse(String sValue) {
			return Float.parseFloat(sValue);
		}
	}
	
	//String
	
	public static class nString extends NetVar<String> {
		public nString(String initial, String id, ObjControllerI controller) {
			super(initial, id, controller);
		}

		@Override
		protected String parse(String sValue) {
			return sValue;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
}
