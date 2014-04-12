package net.xuset.objectIO.netObj;


/**
 * Used in conjunction with NetVar to notify a variable change.
 * @author xuset
 * @since 1.0
 * @param <T> the type of variable the NetVar stores
 */
public interface NetVarListener<T> {
	
	/**
	 * Called by NetVar when the variable of the NetVar changes.
	 * @param newValue the new value of the variable
	 */
	void onVarChange(T newValue);
}
