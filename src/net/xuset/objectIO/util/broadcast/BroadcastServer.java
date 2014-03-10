package net.xuset.objectIO.util.broadcast;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;



public class BroadcastServer {
	private static final int defaultDelay = 500;
	
	private final DatagramSocket socket;
	private final InetAddress group;
	private final int port;
	private final int delay;
	private Thread thread;
	private byte[] msg;
	private boolean keepGoing = true;
	
	public BroadcastServer(int port, InetAddress group, String broadcastMsg)
			throws SocketException {
		this(port, group, broadcastMsg, defaultDelay);
	}
	
	public BroadcastServer(int port, InetAddress group, String broadcastMsg, int delay)
			throws SocketException {
		
		this(port, group, delay);
		start(broadcastMsg);
	}
	
	public BroadcastServer(int port, InetAddress group) throws SocketException {
		this(port, group, defaultDelay);
	}
	
	public BroadcastServer(int port, InetAddress group, int delay) throws SocketException {
		this.delay = delay;
		this.port = port;
		this.group = group;
		socket = new DatagramSocket();
	}
	
	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}
	
	public void start(String message) {
		if (thread != null && thread.isAlive())
			forceStop();
		msg = message.getBytes();
		thread = new Thread(new Worker(), "Multicast server");
		thread.start();
	}
	
	public void stop() { keepGoing = false; }
	public boolean isStopped() { return !thread.isAlive(); }
	
	private void forceStop() {
		stop();
		try {
			thread.join(100);
			if (thread.isAlive())
				thread.interrupt();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private class Worker implements Runnable {
		@Override
		public void run() {
			try {
				while (keepGoing) {
					
		            DatagramPacket packet;
		            packet = new DatagramPacket(msg.clone(), msg.length, group, port);
		            socket.send(packet);
		            
		            //System.out.println("broadcasted sent: " + new String(msg.clone()));
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
