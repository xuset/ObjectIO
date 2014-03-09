package net.xuset.objectIO.util;

import java.io.IOException;
import java.net.InetAddress;

import net.xuset.objectIO.util.scanners.HostChecker;

public class NetworkPing implements HostChecker{
	private static final int defaultTimeout = 500;
	
	private final int timeout;
	
	public NetworkPing(int timeout) {
		this.timeout = timeout;
	}
	
	public NetworkPing() {
		this(defaultTimeout);
	}
	
	public static int getPingTime(InetAddress addr, int timeout) {
		long start = System.currentTimeMillis();
		if (isHostUp(addr, timeout))
			return (int) (System.currentTimeMillis() - start);
		else
			return timeout;
	}
	
	public static boolean isHostUp(InetAddress addr) {
		return isHostUp(addr, defaultTimeout);
	}
	
	public static boolean isHostUp(InetAddress addr, int timeout) {
		try {
			return addr.isReachable(timeout);
		} catch (IOException ex) {
			//Do nothing
		}
		return false;
	}

	@Override
	public boolean isUp(InetAddress addr) {
		return isHostUp(addr, timeout);
	}
}
