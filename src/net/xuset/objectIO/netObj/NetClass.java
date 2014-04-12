package net.xuset.objectIO.netObj;

import java.util.Iterator;

import net.xuset.objectIO.markupMsg.MarkupMsg;


/**
 * NetClass is used to store and organize NetObject instances. The updates of the stored
 * NetObject instances are combined into one MarkupMsg object when the serialization
 * occurs. Deserializing takes the single message and splits it up. The split-up messages
 * are then distributed to the correct NetObject instances.
 * 
 * <p>To determine which NetObject a message belongs to, the id of the NetObject is used.
 * When serializing a NetObject within a NetClass, the NetClass adds the NetObjects
 * id into the message. So when it comes time to deserialize, the NetClass finds the
 * NetObject with the same id as the one with message. If no NetObject is found, the
 * message is silently discarded.</p>
 * 
 * <p>NetClass is abstract so that the implementations can decide on how to store,
 * remove, and retrieve NetObjects.</p>
 * 
 * @author xuset
 * @since 1.0
 *
 */
public abstract class NetClass extends AbstractNetObject{
	
	/**
	 * Creates a new instance with the given id.
	 * @param id the id that will be returned by {@code getId()}
	 */
	public NetClass(String id) {
		super(id);
	}
	
	
	/**
	 * Adds an object to the NetClass. The given object can now be serialized and
	 * deserialized through the NetClass.
	 * 
	 * @param obj the object to store in the NetClass
	 */
	public abstract void addObj(NetObject obj);
	
	
	/**
	 * Removes the given object from the NetClass. Attempts to serialize and deserialize
	 * through the NetClass for the given object will fail.
	 * 
	 * @param obj the object to remove
	 * @return {@code true} if the object was found and removed
	 */
	public abstract boolean removeObj(NetObject obj);
	
	
	/**
	 * Protected method that gets an iterator that can be used to access every
	 * NetObject stored by this NetClass
	 * 
	 * @return the iterator for the NetClass
	 */
	protected abstract Iterator<NetObject> iterator();
	
	
	/**
	 * Searches the stored NetObjects for the one with the same id as the given id.
	 * 
	 * @param id the id of the NetObject to return
	 * @return the NetObject whose id equals the given id or null if no NetObject could
	 * 			be found
	 */
	protected NetObject getObj(String id) {
		Iterator<NetObject> iterator = iterator();
		while (iterator.hasNext()) {
			NetObject next = iterator.next();
			if (next.getId().equals(id))
				return next;
		}
		
		return null;
	}

	@Override
	public MarkupMsg serializeToMsg() {
		MarkupMsg parentMsg = new MarkupMsg();
		parentMsg.setName(getId());
		
		Iterator<NetObject> iterator = iterator();
		while (iterator.hasNext()) {
			NetObject obj = iterator.next();
			MarkupMsg serialized = obj.serializeToMsg();
			serialized.setName(obj.getId());
			parentMsg.addNested(serialized);
		}
		
		return parentMsg;
	}

	@Override
	public boolean hasUpdates() {
		Iterator<NetObject> iterator = iterator();
		while (iterator.hasNext()) {
			NetObject obj = iterator.next();
			if (obj.hasUpdates())
				return true;
		}
		
		return false;
	}

	@Override
	public MarkupMsg serializeUpdates(){
		MarkupMsg parentMsg = new MarkupMsg();
		parentMsg.setName(getId());

		Iterator<NetObject> iterator = iterator();
		while (iterator.hasNext()) {
			NetObject obj = iterator.next();
			if (!obj.hasUpdates())
				continue;
			
			MarkupMsg updates = obj.serializeUpdates();
			updates.setName(obj.getId());
			parentMsg.addNested(updates);
		}
		
		return parentMsg;
	}

	@Override
	public void deserializeMsg(MarkupMsg msg) {
		for (MarkupMsg nested : msg.getNestedMsgs()) {
			NetObject obj = getObj(nested.getName());
			if (obj != null)
				obj.deserializeMsg(nested);
		}
	}

}
