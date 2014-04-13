

/**
 * GroupNet acts like a P2P infrastructure but is instead based on a TCP server-client
 * model. From here on, a server is referring to a
 * {@link net.xuset.objectIO.connections.sockets.groupNet.server.GroupNetServer
 * GroupNetServer} instance, and a client is referring to a
 * {@link net.xuset.objectIO.connections.sockets.groupNet.client.GroupClientHub
 * GroupClientHub} instance. Multiple clients can connect to single server. A client
 * has 'virtual' connections to all the other clients connected  to that server.
 * The clients are not connected to each other by any actual connection. When a client
 * sends another client a message across the 'virtual' connection. The message is sent
 * to the server who forwards it to the correct recipient.
 * 
 * <p>
 * When the server adds a new connection, a notice is sent to the clients notifying
 * them that a new connection has been added to the server. The new client can then
 * communicate with all the other clients and the other clients can communicate with the
 * new client. When a client disconnects from the server, a notice is sent to the rest
 * of the clients notifying them to remove their 'virtual' connection.
 * </p>
 */

package net.xuset.objectIO.connections.sockets.groupNet;