package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;


/**
 * This class is what is used to simulate the function calls of {@link NetFunc}.
 * When a NetFunc is 'called', {@code funcCalled(MarkupMsg)} is called. When a
 * NetFunc 'returns', {@code funcReturned(MarkupMsg)} is called.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public interface NetFuncListener {
	
	/**
	 * Called when a NetFunc object deserializes a 'function call'.
	 * 
	 * @param msg the 'arguments' for the 'function call'
	 * @return the 'return value' of the 'function call'. If {@code null} is returned,
	 * nothing happens. If an object is returned, {@code funcReturned(MarkupMsg)} will
	 * be called on the sender.
	 * 
	 */
	MarkupMsg funcCalled(MarkupMsg msg);
	
	
	/**
	 * When a 'call' returns a value, the value can be read by implementing this method.
	 * @param msg the 'return value' of the 'function call'
	 */
	void funcReturned(MarkupMsg msg);
}
