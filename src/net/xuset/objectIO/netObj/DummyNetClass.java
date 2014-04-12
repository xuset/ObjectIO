package net.xuset.objectIO.netObj;

import java.util.Iterator;


/**
 * A NetClass implementation that does not store anything. All the functions relating
 * to storing, removing, or retrieving NetObject instances do nothing. This is good
 * for disabling the functionality of a NetClass without having to change a lot of code.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class DummyNetClass extends NetClass {

	public DummyNetClass(String id) {
		super(id);
	}

	@Override
	public void addObj(NetObject obj) {

	}

	/**
	 * @return always false
	 */
	@Override
	public boolean removeObj(NetObject obj) {
		return false;
	}

	@Override
	protected Iterator<NetObject> iterator() {
		return new DummyIterator();
	}
	
	private static class DummyIterator implements Iterator<NetObject> {

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public NetObject next() {
			return null;
		}

		@Override
		public void remove() {
			//Do nothing
		}
		
	}

}
