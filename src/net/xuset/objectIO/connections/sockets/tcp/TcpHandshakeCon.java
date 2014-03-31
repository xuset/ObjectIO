package net.xuset.objectIO.connections.sockets.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.xuset.objectIO.connections.sockets.HandshakeFailedException;
import net.xuset.objectIO.connections.sockets.StreamGreeter;

/**
 * An internet connection with the local and remote id's synched with the remote
 * connection. Before a new TcpHandshakeCon object is fully constructed, it sends its
 * local id across the socket and waits to receive the remote connection's local id.
 * A StreamGreeter object is used to achieve the passing of local id's. After the
 * TcpHandshakeCon object is successfully constructed, calling {@code getId()} will
 * equal {@code getLocalId()} on the remote connection, and vice versa.
 * 
 * <p>The remote connection must also be TcpHandshakeCon or must use the StreamGreeter
 * when the socket is first created. </p>
 * 
 * @author xuset
 * @since 1.0
 * 
 */
public class TcpHandshakeCon extends TcpCon {
	
	
	/**
	 * Constructs a new TcpHandshakeCon object with the given socket.
	 * 
	 * @param s socket to use
	 * @param localId the local id of the connection
	 * @throws IOException if an I/O error occurs
	 * @throws HandshakeFailedException if the exchanging of id's fails
	 */
	public TcpHandshakeCon(Socket s, long localId)
			throws IOException, HandshakeFailedException{
		
		this(s, localId, 3000L);
	}
	
	
	/**
	 * Constructs a new TcpHandshakeCon object with the given socket.
	 * 
	 * @param s socket to use
	 * @param localId the local id of the connection
	 * @param handshakeTimeout the amount of time to wait for the remote connection to
	 * 		send it's local id
	 * @throws IOException if an I/O error occurs
	 * @throws HandshakeFailedException if the exchanging of id's fails
	 */
	public TcpHandshakeCon(Socket s, long localId, long handshakeTimeout)
			throws IOException, HandshakeFailedException {
		
		super(s, startHandshake(s, localId, handshakeTimeout), localId);
	}
	
	/**
	 * Send this connections local id and waits to receive the remote connections local id
	 * 
	 * @return the local id of the remote connection
	 * @throws IOException if an I/O error occurs
	 * @throws HandshakeFailedException if the exchanging of id's fails
	 */
	private static long startHandshake(Socket socket, long localId,
			long handshakeTimeout) throws IOException, HandshakeFailedException{
		
		//TODO maybe remove the keep alive
		socket.setKeepAlive(true);
		
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();
		StreamGreeter greeter = new StreamGreeter(out, in,
				localId, handshakeTimeout);
		
		return greeter.getEndPointId();
	}

}
