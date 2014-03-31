package net.xuset.objectIO.util.broadcast;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Listens for multicast datagram packets on the specified address and port. When a
 * message is received it is added to the queue. If the queue already contains the
 * message, the message is not added.
 * 
 * <p>This class is generally used with a BroadcastServer on another host. The
 * BoardcastSrever broadcasts a String message that is picked up the BroadcastClient.</p>
 * 
 * @author xuset
 * @see BroadcastServer
 * @since 1.0
 *
 */
public class BroadcastClient {
	private static final int socketTimeout = 2000;
	
	private final Queue<String> msgQueue = new ConcurrentLinkedQueue<String>();
	private final InetAddress group;
	private final MulticastSocket socket;
	private final Thread thread;
	private final int packetSize;
	
	private boolean keepGoing = true;
	
	
	/**
	 * Constructs a new BroadcastClient
	 * 
	 * @param port the port to listen on
	 * @param addr the address to listen on
	 * @param packetSize the max packet size
	 * @throws IOException if an I/O error occurs
	 */
	public BroadcastClient(int port, String addr, int packetSize)
			throws IOException {
		
		this.packetSize = packetSize;
		group = InetAddress.getByName(addr);
		socket = new MulticastSocket(port);
		socket.setSoTimeout(socketTimeout);
		socket.joinGroup(group);
		thread = new Thread(new Worker(), "Broadcast client");
		thread.start();
	}
	
	
	/**
	 * Returns the next message in the queue
	 * 
	 * @return the next message
	 */
	public String pollMsgQueue() { return msgQueue.poll(); }
	
	/**
	 * Indicates if the message is available to be polled
	 * 
	 * @return {@code true} if the queue is not empty
	 */
	public boolean isMsgQueueEmpty() { return msgQueue.isEmpty(); }
	
	
	/**
	 * Sets the stop flag
	 */
	public void stop() { keepGoing = false; }
	
	
	/**
	 * Indicates if the message receiver has stopped
	 * 
	 * @return {@code true} if the receiver has stopped
	 */
	public boolean isStopped() { return !thread.isAlive(); }
	
	private class Worker implements Runnable {
		@Override
		public void run() {
			try {
				while (keepGoing) {

				    byte[] buf = new byte[packetSize];
				    DatagramPacket packet = new DatagramPacket(buf, buf.length);
				    
				    try {
				    	
					    socket.receive(packet);
					    String received = new String(packet.getData());
					    if (!msgQueue.contains(received))
					    	msgQueue.offer(received);
					    
				    } catch (SocketTimeoutException ex) {
				    	
				    }
				}
			} catch (IOException ex) {
				System.err.println("Error recieving the datagram packet");
				ex.printStackTrace();
			} finally {
				try {
					socket.leaveGroup(group);
					socket.close();
				} catch (IOException e) {
					System.err.println("Error leaving the multicast group");
					e.printStackTrace();
				}
			}
			
		}
	}
}
