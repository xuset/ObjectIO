package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;


/**
 * NetObject implementation that stores a given variable. The state of the object
 * can be changed by setting the variable stored to a different value via the the
 * {@code set(T)} method. When the state changes, {@code hasUpdates()} returns
 * {@code true}. To serialize the variable stored, {@code toString()} is called. To
 * deserialize a received String the abstract method {@link #createValFromStr(String)} is
 * called.
 * 
 * @author xuset
 * @since 1.0
 * @param <T> The type of variable that will be stored.
 */
public abstract class NetVar<T> extends AbstractNetObject {
	private T value, oldValue;
	private NetVarListener<T> listener = null;
	
	
	/**
	 * Should create the new value that is represented by the given String. The variable
	 * that this object stores will be set to the value returned by this method.
	 * 
	 * @param strVal the String representation of the object
	 * @return the object represented by the String.
	 */
	protected abstract T createValFromStr(String strVal);
	
	
	/**
	 * Constructs a new NetVar instance with the given String the objects id. The given
	 * value will be the initial value for this object.
	 * 
	 * @param id the id that will be returned by {@code getId()}
	 * @param value the initial value
	 */
	public NetVar(String id, T value) {
		super(id);
		this.value = value;
		this.oldValue = value;
	}
	
	
	/**
	 * Gets the listener that is called when the state of this object is changed
	 * via deserialization.
	 * 
	 * @return the listener used by this object
	 */
	public NetVarListener<T> getListener() { return listener; }
	
	
	/**
	 * The listener given will be called when deserializing a message changes the value
	 * of the stored variable. {@code set(T)} will not send a call to the listener.
	 * 
	 * @param listener that listener that will used
	 */
	public void setListener(NetVarListener<T> listener) { this.listener = listener; }
	
	
	/**
	 * Returns the variable that is being stored
	 * 
	 * @return variable being stored
	 */
	public T get() { return value; }
	
	
	/**
	 * Sets the variable to a new value. If the new value is different from the last
	 * value, {@code hasUpdates()} returns {@code true} and the object's updates
	 * can be serialized into a MarkupMsg. 
	 * 
	 * @param newValue the new value to set the stored variable to
	 */
	public void set(T newValue) {
		value = newValue;
	}

	@Override
	public MarkupMsg serializeToMsg() {
		MarkupMsg msg = new MarkupMsg();
		msg.setContent(value.toString());
		return msg;
	}

	@Override
	public void deserializeMsg(MarkupMsg msg) {
		T temp = createValFromStr(msg.getContent());
		boolean callListener = !temp.equals(value);
		
		value = temp;
		oldValue = temp;
		
		if (callListener && listener != null)
			listener.onVarChange(temp);
	}

	@Override
	public boolean hasUpdates() {
		if (value == oldValue)
			return false;
		
		return !value.equals(oldValue);
	}

	@Override
	public MarkupMsg serializeUpdates() {
		oldValue = value;
		return serializeToMsg();
	}
	
	//Integer
	
	/**
	 * Stores the value of an Integer.
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	public static class nInt extends NetVar<Integer> {
		
		/**
		 * Creates a new instance for storing Integer objects
		 * 
		 * @param id the id that will be returned by {@code getId()}
		 * @param initial the initial value of the variable stored
		 */
		public nInt(String id, Integer initial) {
			super(id, initial);
		}
		
		@Override
		protected Integer createValFromStr(String strVal) {
			return Integer.parseInt(strVal);
		}
	}
	
	//Boolean
	
	/**
	 * Stores the value of an Boolean.
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	public static class nBool extends NetVar<Boolean> {
		
		/**
		 * Creates a new instance for storing Boolean objects
		 * 
		 * @param id the id that will be returned by {@code getId()}
		 * @param initial the initial value of the variable stored
		 */
		public nBool(String id, Boolean initial) {
			super(id, initial);
		}
		
		@Override
		protected Boolean createValFromStr(String strVal) {
			return Boolean.parseBoolean(strVal);
		}
	}
	
	//Double
	
	/**
	 * Stores the value of an Double.
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	public static class nDouble extends NetVar<Double> {
		
		/**
		 * Creates a new instance for storing Double objects
		 * 
		 * @param id the id that will be returned by {@code getId()}
		 * @param initial the initial value of the variable stored
		 */
		public nDouble(String id, Double initial) {
			super(id, initial);
		}
		
		@Override
		protected Double createValFromStr(String strVal) {
			return Double.parseDouble(strVal);
		}
	}
	
	//Long

	
	/**
	 * Stores the value of an Long.
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	public static class nLong extends NetVar<Long> {
		
		/**
		 * Creates a new instance for storing Long objects
		 * 
		 * @param id the id that will be returned by {@code getId()}
		 * @param initial the initial value of the variable stored
		 */
		public nLong(String id, Long initial) {
			super(id, initial);
		}
		
		@Override
		protected Long createValFromStr(String strVal) {
			return Long.parseLong(strVal);
		}
	}
	
	//Float

	
	/**
	 * Stores the value of an Float.
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	public static class nFloat extends NetVar<Float> {
		
		/**
		 * Creates a new instance for storing Float objects
		 * 
		 * @param id the id that will be returned by {@code getId()}
		 * @param initial the initial value of the variable stored
		 */
		public nFloat(String id, Float initial) {
			super(id, initial);
		}
		
		@Override
		protected Float createValFromStr(String strVal) {
			return Float.parseFloat(strVal);
		}
	}
	
	//String

	
	/**
	 * Stores the value of an String.
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	public static class nString extends NetVar<String> {
		
		/**
		 * Creates a new instance for storing String objects
		 * 
		 * @param id the id that will be returned by {@code getId()}
		 * @param initial the initial value of the variable stored
		 */
		public nString(String id, String initial) {
			super(id, initial);
		}

		@Override
		protected String createValFromStr(String sValue) {
			return sValue;
		}
	}

}
