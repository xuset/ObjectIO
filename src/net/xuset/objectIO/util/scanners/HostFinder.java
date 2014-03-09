package net.xuset.objectIO.util.scanners;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import net.xuset.objectIO.util.scanners.HostChecker;
import net.xuset.objectIO.util.scanners.ScannerWorker;

public class HostFinder {
	private final int max = 254;
	private final ArrayList<ScannerWorker> workers = new ArrayList<ScannerWorker>();
	
	public HostFinder(HostChecker checker) throws UnknownHostException {
		this(checker, 10);
	}
	
	public HostFinder(HostChecker checker, int threads) throws UnknownHostException {
		this(checker, threads, InetAddress.getLocalHost());
	}
	
	public HostFinder(HostChecker checker, int threads, InetAddress addr) {
		int hostsPer = max / threads;
		
		int hostsLeft = max;
		for (int i = 0; i < threads || hostsLeft > 0; i++) {
			int range = hostsPer;
			byte[] byteAddr = addr.getAddress();
			byteAddr[3] = (byte) (hostsPer * i);
			
			if (i == threads)
				range = hostsLeft;
			
			workers.add(new ScannerWorker(byteAddr, range, checker));
			hostsLeft-= range;
		}
	}
	
	public void cancelScan() {
		for (ScannerWorker w : workers) {
			w.setCancelScanFlag();
		}
		
		for (ScannerWorker w : workers) {
			w.joinThread();
		}
	}
	
	public boolean isScanning() {
		for (int i = 0; i < workers.size(); i++) {
			if (workers.get(i).isWorking())
				return true;
		}
		return false;
	}
	
	public List<InetAddress> createHostsList() {
		ArrayList<InetAddress> hosts = new ArrayList<InetAddress>();
		for (int i = 0; i < workers.size(); i++) {
			ScannerWorker sw = workers.get(i);
			if (!sw.isWorking()) {
				List<InetAddress> tempHosts = sw.getHosts();
				hosts.ensureCapacity(hosts.size() + tempHosts.size());
				for (int j = 0; j < tempHosts.size(); j++)
					hosts.add(tempHosts.get(j));
			}
		}
		return hosts;
	}
	
	public void waitForCompletion(long timeout) {
		long startTime = System.currentTimeMillis();
		while (startTime + timeout > System.currentTimeMillis() && isScanning()) {
			try { Thread.sleep(10); } catch (Exception ex) { ex.printStackTrace(); }
		}
	}
}
