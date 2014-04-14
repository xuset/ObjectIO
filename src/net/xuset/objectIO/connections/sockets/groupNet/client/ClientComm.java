package net.xuset.objectIO.connections.sockets.groupNet.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xuset.objectIO.connections.sockets.groupNet.GroupNetMsg;
import net.xuset.objectIO.connections.sockets.tcp.TcpHandshakeCon;
import net.xuset.objectIO.markupMsg.InvalidFormatException;


/**
 * Connection used to communicate directly with the server (GroupNetServer).
 * 
 * @author xuset
 * @since 1.0
 */
class ClientComm extends TcpHandshakeCon {
	private static final Logger log = Logger.getLogger(ClientComm.class.getName());
	
	private final GroupClientHub hub;
	
	/**
	 * Attempts to connect to the GroupNetServer and create a ClientComm instance based
	 * on the TCP connection.
	 * 
	 * @param ip ip of the server
	 * @param port port of the server
	 * @param hub the hub that the ClientComm instance should use
	 * @return the new ClientComm object
	 * @throws IOException if there was an error connecting to the server or an I/O error
	 */
	static ClientComm connectToGroupNetServer(String ip, int port, GroupClientHub hub)
			throws IOException, UnknownHostException {
		
		Socket s = new Socket(ip, port);
		log.log(Level.INFO, "Socket connection to server established");
		try {
			s.setKeepAlive(true);
			s.setTcpNoDelay(true);
			return new ClientComm(s, hub);
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Connection instance could not be constructed", ex);
			s.close();
			throw new IOException(ex);
		}
	}
	
	private ClientComm(Socket s, GroupClientHub hub) throws IOException {
		super(s, hub.getLocalId());
		this.hub = hub;
	}
	
	
	/**
	 * Attempts to parse the received input using the hub's message parser.
	 * If the message object was successfully created it passed to
	 * {@link GroupClientHub#handleNewMsg(GroupNetMsg)}.
	 */
	@Override
	protected void handleRawInput(String input) {
		if (input == null) {
			log.log(Level.INFO, "Received null as input");
			hub.handleNewMsg(null);
			return;
		}
		
		if (input.equals("")) {
			log.log(Level.WARNING, "Client received an empty string");
			return;
		}
		
		try {
			GroupNetMsg msg = new GroupNetMsg(hub.getParser().parseFrom(input));
			hub.handleNewMsg(msg);
			return;
		} catch(InvalidFormatException ex) {
			log.log(Level.WARNING, "Recieved bad string as input", ex);
		}
		
	}

}
