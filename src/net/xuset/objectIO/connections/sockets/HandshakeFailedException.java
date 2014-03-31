package net.xuset.objectIO.connections.sockets;

import java.io.IOException;


/**
 * This exception that should be thrown after a connection has been made (through sockets
 * or some other medium), and the remote  connection is not using the predetermined
 * protocol.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class HandshakeFailedException extends IOException {
	private static final long serialVersionUID = 4499580054716545171L;
	
	
	/** Constructs a HandshakeFailedException with the message 'Handshake failed'. */
	public HandshakeFailedException() {
		this("Handshake failed");
	}
	
	/** Constructs a new instance with the given message. */
	public HandshakeFailedException(String message) {
		super(message);
	}

}
