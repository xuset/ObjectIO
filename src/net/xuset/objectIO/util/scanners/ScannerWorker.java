package net.xuset.objectIO.util.scanners;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import net.xuset.objectIO.util.scanners.HostChecker;


/**
 * Class that scans a range of address on a seperate thread
 * 
 * @author xuset
 * @since 1.0
 */
class ScannerWorker {
	private final Thread thread;
	private final List<InetAddress> hosts = new ArrayList<InetAddress>();
	private final int hostsToScan;
	private final byte[] startAddr;
	private final HostChecker hostChecker;
	
	private boolean cancelScan = false;
	
	/**
	 * Indicates if the thread is still working
	 * 
	 * @return {@code true} if the thread is alive
	 */
	boolean isWorking() { return thread.isAlive(); }
	
	
	/**
	 * Returns the list of accepted addresses.
	 * 
	 * @return the list of address or an empty list if the worker is working
	 */
	List<InetAddress> getHosts() {
		if (isWorking())
			return new ArrayList<InetAddress>(0);
		else
			return hosts;
	}
	
	
	/**
	 * Starts a new scan for the given address range.
	 * 
	 * @param startAddr address to start scan on
	 * @param hostsToScan amount of hosts to scan
	 * @param hostChecker used to determine if the address should be added to the list
	 */
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
	
	
	/**
	 * Sets the cancel flag. The thread will try to exit shortly after.
	 */
	void setCancelScanFlag() {
		cancelScan = true;
	}
	
	/**
	 * Blocks until the thread dies.
	 */
	void joinThread() {
		if (thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Worker class that does the scanning
	 */
	private class Worker implements Runnable {
		@Override
		public void run() {
			for (byte i = 0; i < hostsToScan && !cancelScan; i++) {
				startAddr[3]++;
				InetAddress testAddr = constructAddress(startAddr);
				if (testAddr == null)
					continue;
				if (hostChecker.isUp(testAddr)) {
					hosts.add(testAddr);
				}
			}
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