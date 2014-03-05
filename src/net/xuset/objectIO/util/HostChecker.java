package net.xuset.objectIO.util;

import java.net.InetAddress;

public interface HostChecker extends Cloneable{
	boolean isUp(InetAddress addr);
}
