package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;


/**
 * NetFunc allows functionality to be serialized and deserialized. A NetFunc object
 * could be thought of as a 'function' that can be 'called' through the serialization.
 * 
 * <p>
 * To 'call the NetFunc', use the method {@code sendCall(MarkupMsg)}; the given MarkupMsg
 * object are the 'arguments' for the 'function call'. After that, the NetFunc needs to
 * serialize it's updates. When the updates are deserialized, the 'function call' is
 * executed by calling {@link NetFuncListener#funcCalled(MarkupMsg)} with the 'function
 * argument' as the argument.</p>
 * 
 * <p>If the 'function call' returns a MarkupMsg object. That message is thought of
 * as the 'return value' for the 'function call'. This 'return value' is included
 * the NetFunc's updates. When the updates are deserialized,
 * {@link NetFuncListener#funcReturned(MarkupMsg)} is called with the 'return value' as
 * the argument.</p>
 * 
 * @author xuset
 * @since 1.0
 * @see NetFuncListener
 * @see net.xuset.objectIO.markupMsg.MarkupMsg
 *
 */
public class NetFunc extends AbstractNetObject {
	private static final String optionAttribute = "op";
	private static final String callOption = "cl";
	private static final String returnOption = "rt";
	
	private final NetFuncListener listener;
	private MarkupMsg bufferMsg = new MarkupMsg();

	
	/**
	 * Creates the NetFunc instance with the given id and listener. The given listener
	 * is used when a 'function call' or 'function return' is deserialized.
	 * 
	 * @param id the id that will be returned by {@code getId()}
	 * @param listener the listener used for 'calls' and 'returns'
	 */
	public NetFunc(String id, NetFuncListener listener) {
		super(id);
		this.listener = listener;
	}
	
	
	/**
	 * Simulates 'calling a function' through the serialization and deserialization of
	 * MarkupMsg objects.
	 * @param msg
	 */
	public void sendCall(MarkupMsg msg) {
		sendCraftedMsg(callOption, msg);
	}

	
	/**
	 * This method only returns an empty MarkupMsg object.
	 * 
	 * @return an empty MarkupMsg object
	 */
	@Override
	public MarkupMsg serializeToMsg() {
		MarkupMsg msg = new MarkupMsg();
		msg.setName(getId());
		return msg;
	}

	@Override
	public void deserializeMsg(MarkupMsg msg) {
		for (MarkupMsg nested : msg.getNestedMsgs())
			parseNested(nested);
	}

	@Override
	public boolean hasUpdates() {
		return !bufferMsg.getNestedMsgs().isEmpty();
	}

	@Override
	public MarkupMsg serializeUpdates() {
		MarkupMsg updates = bufferMsg;
		updates.setName(getId());
		bufferMsg = new MarkupMsg();
		return updates;
	}
	
	//parses one 'function call' or 'function return'
	private void parseNested(MarkupMsg nested) {
		String option = nested.getAttribute(optionAttribute).getValue();
		MarkupMsg funcArgs = nested.getNestedMsgs().get(0);
		
		if (option.equals(callOption)) {
			listener.funcCalled(funcArgs);
		} else if (option.equals(returnOption)) {
			listener.funcReturned(funcArgs);
		}
	}
	
	//stores the message in a buffer
	private void sendCraftedMsg(String optionValue, MarkupMsg msg) {
		MarkupMsg parentMsg = new MarkupMsg();
		parentMsg.addNested(msg);
		parentMsg.setAttribute(optionAttribute, optionValue);
		
		bufferMsg.addNested(parentMsg);
	}

}
