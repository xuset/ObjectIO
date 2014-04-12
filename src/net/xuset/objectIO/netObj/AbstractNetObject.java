package net.xuset.objectIO.netObj;


/**
 * Helper class so implementations will not have to define a trivial {@code getId()}
 * method every time. The only method implemented in this class is {@code getId()}.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public abstract class AbstractNetObject implements NetObject{
	private final String id;
	
	
	/**
	 * Constructs a new instance with the given id.
	 * 
	 * @param id the id that will be returned by {@code getId()}
	 */
	public AbstractNetObject(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
}
