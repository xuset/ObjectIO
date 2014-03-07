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
	
	private boolean cancelScan = false;
	
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
	
	void setCancelScanFlag() {
		cancelScan = true;
	}
	
	void joinThread() {
		if (thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class Worker implements Runnable {
		@Override
		public void run() {
			for (byte i = 0; i < hostsToScan && !cancelScan; i++) {
				startAddr[3]++;
				InetAddress testAddr = constructAddress(startAddr);
				if (testAddr == null)
					continue;
				//System.out.println("Testing: " + testAddr.getHostAddress());
				if (hostChecker.isUp(testAddr)) {
					//System.out.println("    Adding: " + testAddr.getHostAddress());
					hosts.add(testAddr);
				}
			}
		}
	}
		
	private static InetAddress constructAddress(byte[] addr) {
		try {
			return InetAddress.getByAddress(addr);
		} catch (UnknownHostException ex) {
			//ex.printStackTrace();
			//Do nothing
		}
		return null;
	}
}