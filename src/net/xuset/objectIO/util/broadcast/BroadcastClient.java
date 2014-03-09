package net.xuset.objectIO.util.broadcast;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class BroadcastClient {
	private static final int socketTimeout = 2000;
	
	private final Queue<String> msgQueue = new ConcurrentLinkedQueue<String>();
	private final InetAddress group;
	private final MulticastSocket socket;
	private final Thread thread;
	private final int packetSize;
	
	private boolean keepGoing = true;
	
	public BroadcastClient(int port, String addr) throws IOException {
		this(port, addr, 256);
	}
	
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
	
	public String pollMsgQueue() { return msgQueue.poll(); }
	public boolean isMsgQueueEmpty() { return msgQueue.isEmpty(); }
	
	public void stop() { keepGoing = false; }
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
					    //System.out.println("broadcast recieved: " + received);
					    if (!msgQueue.contains(received))
					    	msgQueue.offer(received);
					    
				    } catch (SocketTimeoutException ex) {
				    	
				    }
				}
			} catch (IOException ex) {
				System.err.println("-Error recieving the datagram packet");
				ex.printStackTrace();
			} finally {
				try {
					socket.leaveGroup(group);
					socket.close();
				} catch (IOException e) {
					System.err.println("-Error leaving the multicast group");
					e.printStackTrace();
				}
			}
			
		}
	}
}
