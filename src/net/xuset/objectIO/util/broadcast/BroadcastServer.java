package net.xuset.objectIO.util.broadcast;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 * This class continually sends message in a multicast datagram packet on the given
 * port and address group.
 * 
 * <p>This class is generally used with BroadcastClient objects on other hosts. This
 * class sends String messages that are picked up the BroadcastClient objects.</p>
 * 
 * 
 * @author xuset
 * @see BroadcastClient
 * @since 1.0
 */
public class BroadcastServer {
	private static final int defaultDelay = 500;
	
	private final DatagramSocket socket;
	private final InetAddress group;
	private final int port;
	private final int delay;
	private Thread thread;
	private byte[] msg;
	private boolean keepGoing = true;
	
	
	/**
	 * Construct a new BroadcastServer with the given port and address group. To start
	 * the message send call {@code start(String)}.
	 * 
	 * @param port the port to broadcast on
	 * @param group the multicast address to use
	 * @throws SocketException if the socket could not bind to the port or could not be
	 * 			opened
	 */
	public BroadcastServer(int port, InetAddress group) throws SocketException {
		this(port, group, defaultDelay);
	}
	
	
	/**
	 * Construct a new BroadcastServer with the given port and address group. To start
	 * the message send call {@code start(String)}.
	 * 
	 * @param port the port to broadcast on
	 * @param group the multicast address to use
	 * @param delay the delay between sent messages in milliseconds
	 * @throws SocketException if the socket could not bind to the port or could not be
	 * 			opened
	 */
	public BroadcastServer(int port, InetAddress group, int delay)
			throws SocketException {
		
		this.delay = delay;
		this.port = port;
		this.group = group;
		socket = new DatagramSocket();
	}
	
	
	/**
	 * Starts the sending of the given message.
	 * 
	 * @param message the message to send
	 */
	public void start(String message) {
		if (thread != null && thread.isAlive())
			forceStop();
		msg = message.getBytes();
		thread = new Thread(new Worker(), "Multicast server");
		thread.start();
	}
	
	
	/**
	 * Sets the stop flag.
	 */
	public void stop() { keepGoing = false; }
	
	
	/**
	 * Indicates if the broadcast has stopped.
	 * 
	 * @return {@code true} if the broadcast is stopped
	 */
	public boolean isStopped() { return !thread.isAlive(); }
	
	private void forceStop() {
		stop();
		try {
			thread.join(100);
		} catch (InterruptedException e) {
			if (thread.isAlive())
				thread.interrupt();
			e.printStackTrace();
		}
	}
	
	
	/** Worker class that sends the messages. */
	private class Worker implements Runnable {
		@Override
		public void run() {
			try {
				while (keepGoing) {
					
		            DatagramPacket packet;
		            packet = new DatagramPacket(msg.clone(), msg.length, group, port);
		            socket.send(packet);
		            
		            try { Thread.sleep(delay); } catch(InterruptedException ex) { }
					
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (!socket.isClosed()) {
					socket.close();
				}
			}
		}
	}

}
