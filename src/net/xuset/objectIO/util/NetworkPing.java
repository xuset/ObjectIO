package net.xuset.objectIO.util;

import java.io.IOException;
import java.net.InetAddress;

import net.xuset.objectIO.util.scanners.HostChecker;


/**
 * This class is used to ping remote hosts.
 * It can be used to see if a host is up or to find the round-trip time of the ping.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class NetworkPing implements HostChecker{
	private static final int defaultTimeout = 500;
	
	private final int timeout;
	
	/**
	 * Creates a new NetworkPing object with the given timeout.
	 * 
	 * @param timeout timeout in milliseconds
	 */
	public NetworkPing(int timeout) {
		this.timeout = timeout;
	}

	
	/**
	 * Creates a new NetworkPing object with {@value #defaultTimeout} milliseconds as a
	 * timeout.
	 */
	public NetworkPing() {
		this(defaultTimeout);
	}
	
	
	/**
	 * Records the round-trip time of a packet sent to the given address.
	 * 
	 * @param addr the address of the host to test
	 * @param timeout the max amount of time to wait in milliseconds
	 * @return the round-trip time in milliseconds or -1 if the host could not be reached
	 * 			within the timeout
	 */
	public static int getPingTime(InetAddress addr, int timeout) {
		long start = System.currentTimeMillis();
		if (isHostUp(addr, timeout))
			return (int) (System.currentTimeMillis() - start);
		else
			return -1;
	}
	
	
	/**
	 * Tests if the host is up by pinging the given address. The default timeout
	 * for this method is {@value #defaultTimeout} milliseconds.
	 * 
	 * @param addr the address to ping
	 * @return {@code true} if the host is up
	 */
	public static boolean isHostUp(InetAddress addr) {
		return isHostUp(addr, defaultTimeout);
	}
	
	
	/**
	 * Test if the host is up by pinging the given address.
	 * 
	 * @param addr the address to ping
	 * @param timeout the max amount of time for this method to wait for a response in
	 * 			milliseconds
	 * 
	 * @return {@code true} if the host is up.
	 */
	public static boolean isHostUp(InetAddress addr, int timeout) {
		try {
			return addr.isReachable(timeout);
		} catch (IOException ex) {
			//Do nothing
		}
		return false;
	}

	
	/**
	 * Tests if the host is up by pinging it. The timeout for this method was set during
	 * the construction of the object.
	 */
	@Override
	public boolean isUp(InetAddress addr) {
		return isHostUp(addr, timeout);
	}
}
