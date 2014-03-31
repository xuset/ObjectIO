package net.xuset.objectIO.util.scanners;

import java.net.InetAddress;


/**
 * Interface used to determine if the address is acceptable.
 * 
 * @author xuset
 * @see HostFinder
 * @since 1.0
 *
 */
public interface HostChecker extends Cloneable{
	
	/**
	 * Checks if the given address is up and meets the criteria of the implementation
	 * 
	 * @param addr the address in question
	 * @return {@code true} if the address meets the criteria
	 */
	boolean isUp(InetAddress addr);
}
