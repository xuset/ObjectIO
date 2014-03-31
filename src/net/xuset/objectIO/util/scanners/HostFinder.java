package net.xuset.objectIO.util.scanners;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import net.xuset.objectIO.util.scanners.HostChecker;
import net.xuset.objectIO.util.scanners.ScannerWorker;


/**
 * Scans hosts on the ipv4 /24 block of a given address. An instance of HostFinder
 * is used to determine if the host is acceptable. If the host is acceptable it is
 * added to a list. The acceptable hosts can be found by calling
 * {@link #createHostsList()}.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class HostFinder {
	private final int max = 254;
	private final ArrayList<ScannerWorker> workers = new ArrayList<ScannerWorker>();
	
	/**
	 * Starts a new scan with the given HostChecker object. The scan is started on the
	 * local subnet.
	 * 
	 * @param checker the checker used to determine if an address is acceptable
	 * @param threads the amount of threads to use
	 * @throws UnknownHostException if the local address could not be determined
	 */
	public HostFinder(HostChecker checker, int threads) throws UnknownHostException {
		this(checker, threads, InetAddress.getLocalHost());
	}
	
	/**
	 * Starts a new scan with the given HostChecker object, and the given address.
	 * 
	 * @param checker the checker used to determine if an address is acceptable
	 * @param threads the amount of threads to use
	 * @param addr the address to start the scan on. The scan uses the last 8 bits of the
	 * 			given address to determine what hosts to scan.
	 */
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
	
	
	/**
	 * Cancels the scan and waits for the threads to stop.
	 */
	public void cancelScan() {
		for (ScannerWorker w : workers) {
			w.setCancelScanFlag();
		}
		
		for (ScannerWorker w : workers) {
			w.joinThread();
		}
	}
	
	
	/**
	 * Indicates if the scan is still running.
	 * 
	 * @return {@code true} if the scan is still running
	 */
	public boolean isScanning() {
		for (int i = 0; i < workers.size(); i++) {
			if (workers.get(i).isWorking())
				return true;
		}
		return false;
	}
	
	
	/**
	 * Creates the lists of accepted hosts. It is best to call this after the scan has
	 * completed to get the full results
	 * 
	 * @return List containing all the accepted hosts
	 */
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
	
	
	/**
	 * Blocks until the scan completes or the timeout is reached
	 * 
	 * @param timeout the max amount of time to wait for the scan to complete in
	 * 			milliseconds
	 */
	public void waitForCompletion(long timeout) {
		long startTime = System.currentTimeMillis();
		while (startTime + timeout > System.currentTimeMillis() && isScanning()) {
			try { Thread.sleep(10); } catch (Exception ex) { ex.printStackTrace(); }
		}
	}
}
