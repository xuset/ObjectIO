package net.xuset.objectIO.netObj;

import net.xuset.objectIO.markupMsg.MarkupMsg;


/**
 * This class outlines the functions necessary for synchronizing data across connections.
 * The purpose of NetObject along with it's implementations is to provide an easy to
 * to keep data synchronized between connections.
 * 
 * <p>The implementations of NetObject can serialize their states to MarkupMsg objects
 * which can then be sent to a ConnectionI. The receiver can then deserialize the
 * received message into a NetObject of the same class type.</p>
 * 
 * @author xuset
 * @since 1.0
 */
public interface NetObject {
	
	
	/**
	 * Gets the unique id associated with this NetObject. The id is used to determine
	 * which NetObject an update message or serialized message belongs to.
	 * 
	 * <p>No two NetObjects should have
	 * the same id and be in the same scope. Meaning it is okay to have two NetObjects
	 * with the same id as long as they are not stored together. For example, if you
	 * have two NetClass objects with id's "netClass1" and "netClass2" it is okay to have
	 * two different NetObjects with the same id as long as they are not stored within
	 * the same NetClass object.</p>
	 * 
	 * 
	 * @return the id of this object
	 */
	String getId();
	
	
	/**
	 * Converts the entire object into a MarkupMsg object. The entire object's state
	 * should be stored inside the returned MarkupMsg object. When the MarkupMsg object
	 * gets deserialized, the new state of the object should be identical to when it
	 * was serialized.
	 * 
	 * @return a MarkupMsg object with the entire state of the NetObject stored.
	 */
	MarkupMsg serializeToMsg();
	
	
	/**
	 * Deserialize the given MarkupMsg object. The entire state of the NetObject
	 * does not have to be present in the given MarkupMsg object for this method to
	 * work correctly. It can take a partial or complete state and update itself
	 * accordingly.
	 * 
	 * @param msg
	 */
	void deserializeMsg(MarkupMsg msg);
	
	
	/**
	 * Indicates if the objects state has changed since the last call to
	 * {@code serializeUpdates()}.
	 * 
	 * @return {@code true} if the object has changed and can be updated.
	 */
	boolean hasUpdates();
	
	
	/**
	 * This method only serializes the state that has changed since the last call to
	 * this method. {@code serializeToMsg()} converts the entire state into a MarkupMsg
	 * object but this method converts only what has changed since the last call. This
	 * method is significantly more bandwidth friendly than {@code serializeToMsg()}. If
	 * the object has not changed since the last call, an empty MarkupMsg object is
	 * returned.
	 * 
	 * @return the updated and partial state of the object as a MarkupMsg object
	 */
	MarkupMsg serializeUpdates();
}
