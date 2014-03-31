package net.xuset.objectIO.connections.sockets.groupNet.server;

import java.io.IOException;
import java.net.Socket;

import net.xuset.objectIO.connections.sockets.tcp.TcpAcceptor;

/**
 * Class used to create new GroupServerCon objects once they have been connected and
 * accepted.
 * 
 * @author xuset
 * @since 1.0
 *
 */
class GroupNetAcceptor extends TcpAcceptor<GroupServerCon> {
	private final GroupNetServer server;

	/**
	 * Constructs a new GroupNetAcceptor instance
	 * 
	 * @param server server instance the connections should use
	 * @param port port to listen for new connections on
	 * @throws IOException if an I/O error occurs
	 */
	public GroupNetAcceptor(GroupNetServer server, int port)
			throws IOException {
		
		super(server, port);
		this.server = server;
	}

	@Override
	protected GroupServerCon createConnection(Socket s) throws IOException {
		return new GroupServerCon(s, server);
	}

}
