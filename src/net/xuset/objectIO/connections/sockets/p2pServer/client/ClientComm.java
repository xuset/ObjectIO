package net.xuset.objectIO.connections.sockets.p2pServer.client;

import java.io.IOException;

import net.xuset.objectIO.connections.sockets.TCPCon;



class ClientComm extends TCPCon {
	private ClientHub hub;
	
	public ClientComm(String ip, int port, ClientHub hub) throws IOException{
		super(ip, port, hub);
		this.hub = hub;
		socket.setKeepAlive(true);
		if (sendMeetAndGreet(3000) == false) {
			close();
			throw new IOException("Communication could not be established!");
		}
		parser = parseInput;
		startListening();
	}
	
	private InputParser parseInput = new InputParser() {
		@Override
		public void parseInput(String input) {
			hub.parseInput(input);
		}
	};

}
