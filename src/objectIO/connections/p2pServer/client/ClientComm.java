package objectIO.connections.p2pServer.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import objectIO.connections.StreamCon;

class ClientComm extends StreamCon {
	private Socket socket;
	private ClientHub hub;
	
	private ClientComm(Socket socket, InputStream in, OutputStream out, ClientHub hub) throws IOException{
		super(in, out, hub);
		this.socket = socket;
		this.hub = hub;
		socket.setKeepAlive(true);
		if (sendMeetAndGreet(3000) == false) {
			close();
			throw new IOException("Communication could not be established!");
		}
		parser = parseInput;
		startListening();
	}
	
	public static ClientComm connect(String ip, int port, ClientHub hub) throws IOException {
		Socket soc = new Socket(InetAddress.getByName(ip), port);
		InputStream in = soc.getInputStream();
		OutputStream out = soc.getOutputStream();
		ClientComm comm = new  ClientComm(soc, in, out, hub);
		return comm;
	}
	
	private InputParser parseInput = new InputParser() {
		@Override
		public void parseInput(String input) {
			hub.parseInput(input);
		}
	};
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.close();
	}

}
