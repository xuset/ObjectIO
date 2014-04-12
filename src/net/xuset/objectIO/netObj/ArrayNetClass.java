package net.xuset.objectIO.netObj;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * NetClass implementation that uses an ArrayList as a back-end. This class is
 * better than {@link HashNetClass} for containing a small number of NetObject
 * instances.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class ArrayNetClass extends NetClass {
	private final ArrayList<NetObject> objects = new ArrayList<NetObject>();

	/**
	 * Creates a new instance with the given id
	 * @param id the id that will be returned by {@code getId()}
	 */
	public ArrayNetClass(String id) {
		super(id);
	}

	@Override
	public void addObj(NetObject obj) {
		objects.add(obj);
	}

	@Override
	public boolean removeObj(NetObject obj) {
		return objects.remove(obj);
	}

	@Override
	protected Iterator<NetObject> iterator() {
		return objects.iterator();
	}

}
