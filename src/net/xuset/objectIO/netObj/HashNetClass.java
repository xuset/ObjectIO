package net.xuset.objectIO.netObj;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


/**
 * NetClass implementation that uses a HashMap as a back-end. This implementation is good
 * for containing a large amount of NetObject instances.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class HashNetClass extends NetClass {
	private final HashMap<String, NetObject> objects =
			new LinkedHashMap<String, NetObject>();

	
	/**
	 * Creates a new instance with the given id
	 * @param id the id that will be returned by {@code getId()}
	 */
	public HashNetClass(String id) {
		super(id);
	}

	@Override
	public void addObj(NetObject obj) {
		objects.put(obj.getId(), obj);
	}

	@Override
	public NetObject getObj(String id) {
		return objects.get(id);
	}

	@Override
	public boolean removeObj(NetObject obj) {
		return objects.remove(obj.getId()) != null;
	}

	@Override
	protected Iterator<NetObject> iterator() {
		return new IteratorAdaptor(objects.entrySet().iterator());
	}
	
	//Used as an adaptor for iterators of the type Iterator<Entry<String, NetObject>>
	private static class IteratorAdaptor implements Iterator<NetObject> {
		private final Iterator<Entry<String, NetObject>> it;
		
		public IteratorAdaptor(Iterator<Entry<String, NetObject>> it) {
			this.it = it;
		}

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public NetObject next() {
			return it.next().getValue();
		}

		@Override
		public void remove() {
			it.remove();
		}
		
	}

}
