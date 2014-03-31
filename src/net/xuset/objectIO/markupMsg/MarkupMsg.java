package net.xuset.objectIO.markupMsg;

import java.util.LinkedList;
import java.util.List;


/**
 * A MarkupMsg object is used to transfer data in a protocol independent way
 * between connections. A MarkupMsg has a name and content field, it can have
 * attributes, and it can nest other MarkupMsg objects.
 * 
 * <p>A good example of how a MarkupMsg object is used is with the
 * {@link net.xuset.objectIO.connections.sockets.tcp.TcpCon TcpCon} connection. When
 * {@code sendMsg(MarkupMsg)} is called on the connection, the connection takes the
 * supplied message and converts it into a byte array using a {@link MsgParser}. The
 * byte array is sent through the sockets OutputStream and is converted back into
 * a MarkupMsg object on the other side.</p>
 * 
 * @author xuset
 * @see net.xuset.objectIO.connections.ConnectionI
 * @since 1.0
 *
 */
public class MarkupMsg{

	private final List<MsgAttribute> attributes;
	private final List<MarkupMsg> nestedMsgs;
	private String content = "";
	private String name = "";
	
	/**
	 * Creates an empty MarkupMsg object.
	 */
	public MarkupMsg() {
		this(new LinkedList<MsgAttribute>(), new LinkedList<MarkupMsg>());
	}
	
	
	/**
	 * Creates an empty MarkupMsg object with the supplied lists
	 * 
	 * @param attributes the attributes list. Can be returned by calling
	 * 			{@code getAttributes()}.
	 * @param nestedMsgs the nested messages list. Can be returned by calling
	 * 			{@code getNestedMsgs()}.
	 */
	protected MarkupMsg(List<MsgAttribute> attributes, List<MarkupMsg> nestedMsgs) {
		this.attributes = attributes;
		this.nestedMsgs = nestedMsgs;
	}
	
	
	/**
	 * Gets the list of attributes.
	 * 
	 * @return the {@literal List<MsgAttribute>} object used for storing attributes
	 */
	public List<MsgAttribute> getAttributes() { return attributes; }
	
	
	/**
	 * Gets the list of nested messages.
	 * 
	 * @return the {@literal List<MarkupMsg>} object used for storing nested messages.
	 */
	public List<MarkupMsg> getNestedMsgs() { return nestedMsgs; }
	
	
	/**
	 * Gets the name field of the message.
	 * 
	 * @return the name of the message
	 */
	public String getName() { return name; }
	
	
	/**
	 * Sets the name field of the message
	 * 
	 * @param name the new name of the message
	 */
	public void setName(String name) { this.name = name; }
	
	
	/**
	 * Gets the content field of the message
	 * 
	 * @return the content field
	 */
	public String getContent() { return content; }
	
	
	/**
	 * Sets the content field of the message
	 * 
	 * @param content the new content of the message.
	 */
	public void setContent(String content) { this.content = content; }
	
	
	/**
	 * Adds the message to the nested messages list
	 * 
	 * @param msg message to add
	 */
	public void addNested(MarkupMsg msg) {
		nestedMsgs.add(msg);
	}
	
	
	/**
	 * Returns the message with the supplied name. If multiple messages have the same
	 * name, the first one in the list is returned.
	 * 
	 * @param name name of the message to return
	 * @return the message whose name equals the supplied name, or null if no message
	 * 			has the supplied name
	 */
	public MarkupMsg getNested(String name) {
		for (MarkupMsg m : nestedMsgs) {
			if (m.name.equals(name))
				return m;
		}
		return null;
	}
	
	
	/**
	 * Returns the attribute with the supplied name. If multiple attributes have the same
	 * name, the first one in the list is returned.
	 * 
	 * @param name name of the attribute to return
	 * @return the attribute whose name equals the supplied name, or null if no attribute
	 * 			has the supplied name.
	 */
	public MsgAttribute getAttribute(String name) {
		for (MsgAttribute a : attributes) {
			if (a.getName().equals(name))
				return a;
		}
		return null;
	}
	
	
	/**
	 * Searches for an existing attribute with the supplied name and sets the attribute's
	 * value with supplied value. IF multiple attributes have the same name, the first
	 * one is used. If no attribute is found, a new attribute is created and added
	 * to the list with the supplied name and value.
	 * 
	 * @param name name of the attribute
	 * @param value new value of the attribute
	 * @return the message attribute found or created.
	 */
	public MsgAttribute setAttribute(String name, Object value) {
		MsgAttribute foundAttrib = getAttribute(name);
		if (foundAttrib == null) {
			MsgAttribute newAttrib = new MsgAttribute(name, value);
			attributes.add(newAttrib);
			return newAttrib;
		}
		foundAttrib.set(value);
		return foundAttrib;
	}
	
	
	/**
	 * Adds a new attribute to the list even if the list already contains an
	 * attribute the supplied name.
	 * 
	 * @param name name of the attribute to create
	 * @param value the value of the new attribute
	 */
	public void addAttribute(String name, Object value) {
		attributes.add(new MsgAttribute(name, value));
	}
}
