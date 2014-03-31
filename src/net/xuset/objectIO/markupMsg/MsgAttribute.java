package net.xuset.objectIO.markupMsg;


/**
 * Class used by MarkupMsg for attributes.
 * A MsgAttribute has a name, and value field.
 * 
 * @author xuset
 *
 */
public class MsgAttribute {
	private final String name;
	private String value;
	
	
	/**
	 * Constructs a MsgAttribute with the given name and an empty value.
	 * 
	 * @param name name of the attribute
	 */
	public MsgAttribute(String name) {
		this(name, "");
	}
	
	
	/**
	 * Constructs a MsgAttribute with the given name and value
	 * 
	 * @param name name of the attribute
	 * @param value value of the attribute determine by calling {@link #set(Object)}.
	 * @throws IllegalArgumentException if {@code name} is null
	 */
	public MsgAttribute(String name, Object value) {
		if (name == null)
			throw new IllegalArgumentException("name cannot be null");
		this.name = name;
		set(value);
	}

	
	/**
	 * Returns the name of the attribute
	 * 
	 * @return the name
	 */
	public String getName() { return name; }
	
	
	/**
	 * Returns the value of the attribute as a string
	 * 
	 * @return the value as a string
	 */
	public String getValue() { return value; }
	
	
	/**
	 * Returns the value of the attribute as a string
	 * 
	 * @return the value as a string
	 */
	public String getString() { return value; }
	
	
	/**
	 * Returns the value of the attribute as a integer
	 * 
	 * @return the value as a integer
	 */
	public int getInt() { return Integer.parseInt(value); }
	
	
	/**
	 * Returns the value of the attribute as a double
	 * 
	 * @return the value as a double
	 */
	public double getDouble() { return Double.parseDouble(value); }
	
	
	/**
	 * Returns the value of the attribute as a float
	 * 
	 * @return the value as a float
	 */
	public float getFloat() { return Float.parseFloat(value); }
	
	
	/**
	 * Returns the value of the attribute as a long
	 * 
	 * @return the value as a long
	 */
	public long getLong() { return Long.parseLong(value); }
	
	
	/**
	 * Returns the value of the attribute as a boolean
	 * 
	 * @return the value as a boolean
	 */
	public boolean getBool() { return Boolean.parseBoolean(value); }
	
	
	/**
	 * Sets the value of the attribute. The value of the attribute is a String and is
	 * determine by calling {@link String#valueOf(Object)} with the supplied object.
	 * 
	 * @param value the new value of the attribute
	 */
	public void set(Object value) {
		this.value = String.valueOf(value);
	}
	
	/**
	 * Returns the attributes name and value.
	 */
	@Override
	public String toString() {
		return name + ": " + value;
	}
}
