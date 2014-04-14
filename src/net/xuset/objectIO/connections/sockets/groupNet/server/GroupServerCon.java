package net.xuset.objectIO.connections.sockets.groupNet.server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xuset.objectIO.connections.sockets.groupNet.GroupNetMsg;
import net.xuset.objectIO.connections.sockets.tcp.TcpHandshakeCon;
import net.xuset.objectIO.markupMsg.AsciiMsgParser;
import net.xuset.objectIO.markupMsg.InvalidFormatException;
import net.xuset.objectIO.markupMsg.MarkupMsg;


/**
 * Connection used by GroupNetServer.
 * 
 * @author xuset
 * @see GroupNetServer
 * @since 1.0
 *
 */
public class GroupServerCon extends TcpHandshakeCon{
	private static final Logger log = Logger.getLogger(GroupServerCon.class.getName());
	
	private final GroupNetServer server;
	
	
	/**
	 * Creates a new GroupNetServerCon
	 * 
	 * @param socket socket this connection should communicate on
	 * @param server the server this connection should forward messages to
	 * @throws IOException if an I/O error occurs
	 */
	GroupServerCon(Socket socket, GroupNetServer server) throws IOException {
		super(socket, server.getLocalId());
		this.server = server;
		socket.setKeepAlive(true);
		socket.setTcpNoDelay(true);
		setParser(new AsciiMsgParser(false));
	}
	
	
	/**
	 * Passes received messages to the server so the server can forward the message
	 * to the right connection. If the input is null, the connection is gets closed.
	 */
	@Override
	protected void handleRawInput(String input) {
		if ("".equals(input))
			log.log(Level.WARNING, "connection(" + getId() + ") received empty string");
		
		if (input != null) {
			try {
				GroupNetMsg msg = new GroupNetMsg(getParser().parseFrom(input));
				server.forwardMsg(msg, input.getBytes());
			} catch (InvalidFormatException ex) {
				log.log(Level.WARNING, "connection(" + getId() +
						") received bad string as input", ex);
			}
				
		} else {
			log.log(Level.INFO, "connection(" + getId() +
					") received null. closing connection");
			close();
		}
	}
	
	
	/**
	 * Writes the byte array to the stream and then flushes the stream.
	 * @{code sendRawMsg(byte[])} is called then {@code flush()} is called.
	 * 
	 * @param bytes the byte array to write to the output stream
	 * @see #sendRawMsg(byte[])
	 * @see #flush()
	 */
	void sendRawAndFlush(byte[] bytes) {
		try {
			sendRawMsg(bytes);
			flush();
		} catch (IOException e) {
			log.log(Level.WARNING, "connection(" + getId() + ")" + e.getMessage(), e);
			close();
		}
	}
	
	
	/**
	 * Sends the message then flushes the stream.
	 * {@code sendMsg(MarkupMsg)} is called then {@code flush()} is called.
	 * 
	 * @param msg the message to send
	 * @return returns the value from {@code sendMsg(MarkupMsg)}
	 * @see #sendRawMsg(byte[])
	 * @see #flush()
	 */
	boolean sendMsgAndFlush(MarkupMsg msg) {
		boolean success = sendMsg(msg);
		flush();
		return success;
	}
}
