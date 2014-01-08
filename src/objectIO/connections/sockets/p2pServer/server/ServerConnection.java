package objectIO.connections.sockets.p2pServer.server;

import java.io.IOException;
import java.net.Socket;

import objectIO.connections.sockets.TCPCon;
import objectIO.connections.sockets.p2pServer.P2PMsg;

public class ServerConnection extends TCPCon{
	private P2PServer server;
	
	public ServerConnection(Socket socket, P2PServer server) throws IOException {
		super(socket);
		this.server = server;
		socket.setKeepAlive(true);
		socket.setTcpNoDelay(true);
		myId = server.id;
		if (sendMeetAndGreet(3000) == false) {
			close();
			throw new IOException("Communication could not be established!");
		}
		super.parser = this.parser;
		startListening();
	}
	
	private InputParser parser = new InputParser() {
		@Override
		public void parseInput(String input) {
			if (input != null) {
				P2PMsg msg = new P2PMsg(input, 0);
				if (msg.parsedProperly())
					server.forwardMsg(msg);
			} else {
				disconnect();
			}
		}
	};
	
	public void disconnect() {
		close();
		server.disconnect(this);
	}	
	
	@Override
	public void close() {
		super.close();
		server.disconnect(this);
	}
}
