package net.xuset.objectIO.connections.sockets.groupNet.server;

import java.io.IOException;
import java.net.Socket;

import net.xuset.objectIO.connections.sockets.groupNet.GroupNetMsg;
import net.xuset.objectIO.connections.sockets.tcp.TcpHandshakeCon;
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
	}
	
	
	/**
	 * Passes received messages to the server so the server can forward the message
	 * to the right connection. If the input is null, the connection is gets closed.
	 */
	@Override
	protected void handleRawInput(String input) {
		if (input != null) {
			try {
				GroupNetMsg msg = new GroupNetMsg(getParser().parseFrom(input));
				server.forwardMsg(msg);
			} catch (InvalidFormatException ex) {
				ex.printStackTrace();
			}
				
		} else {
			close();
		}
	}
	
	
	/**
	 * Sends the messages and flushes the stream.
	 */
	@Override
	public boolean sendMsg(MarkupMsg msg) {
		boolean success = super.sendMsg(msg);
		flush();
		return success;
	}
}