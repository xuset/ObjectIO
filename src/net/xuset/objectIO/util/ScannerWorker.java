package net.xuset.objectIO.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

class ScannerWorker {
	private final Thread thread;
	private final List<InetAddress> hosts = new ArrayList<InetAddress>();
	private final int hostsToScan;
	private final byte[] startAddr;
	private final HostChecker hostChecker;
	
	boolean isWorking() { return thread.isAlive(); }
	
	List<InetAddress> getHosts() {
		if (isWorking())
			return new ArrayList<InetAddress>(0);
		else
			return hosts;
	}
	
	ScannerWorker(byte[] startAddr, int hostsToScan, HostChecker hostChecker) {
		if (startAddr.length != 4)
			throw new IllegalArgumentException("Must use ipv4 address");
		this.hostChecker = hostChecker;
		this.hostsToScan = hostsToScan;
		this.startAddr = startAddr.clone();
		thread = new Thread(new Worker());
		thread.setName("Scanner worker");
		thread.start();
	}
	
	private class Worker implements Runnable {
		@Override
		public void run() {
			for (byte i = 0; i < hostsToScan; i++) {
				startAddr[3]++;
				InetAddress testAddr = constructAddress(startAddr);
				if (testAddr == null)
					continue;
				System.out.println("Testing " + testAddr.getHostAddress());
				if (hostChecker.isUp(testAddr)) {
		            System.out.println("Added " + testAddr.getHostAddress());
		            hosts.add(testAddr);
				}
				
			}
			System.out.println("Scanner finished");
		}
	}
		
	private static InetAddress constructAddress(byte[] addr) {
		try {
			return InetAddress.getByAddress(addr);
		} catch (UnknownHostException ex) {
			//Do nothing
		}
		return null;
	}
}