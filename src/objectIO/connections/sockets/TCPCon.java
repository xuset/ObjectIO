package objectIO.connections.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import objectIO.connections.Hub;
import objectIO.connections.StreamCon;

public class TCPCon extends StreamCon {
	protected final Socket socket;
	
	public String getConnectedAddr() { return socket.getInetAddress().getHostAddress(); }
	
	public TCPCon(String ip, int port, Hub<?> hub) throws UnknownHostException, IOException {
		this(new Socket(InetAddress.getByName(ip), port), hub);
	}
	
	public TCPCon(String ip, int port) throws UnknownHostException, IOException {
		this(ip, port, null);
	}
	
	public TCPCon(Socket s) throws IOException {
		this(s, null);
	}
	
	public TCPCon(Socket s, Hub<?> hub) throws IOException {
		this(s, s.getInputStream(), s.getOutputStream(), hub);
	}

	private TCPCon(Socket s, InputStream in, OutputStream out, Hub<?> hub) {
		super(in, out, hub);
		socket = s;
	}
	
	public static TCPCon CREATE(Socket s) {
		return CREATE(s, null);
	}
	
	public static TCPCon CREATE(Socket s, Hub<?> hub) {
		try {
			InputStream in = s.getInputStream();
			OutputStream out = s.getOutputStream();
			return new TCPCon(s, in, out, hub);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void close() {
		super.close();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isClosed() { return super.isClosed() && socket.isClosed(); }
	
	public boolean isConnected() { return isClosed() && socket.isConnected(); }
	

}
