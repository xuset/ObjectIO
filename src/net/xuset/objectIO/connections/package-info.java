

/**
 * Connections is the package which houses the how a message is sent. Every connection
 * that sends and receives MarkupMsg objects inherits the
 * {@link net.xuset.objectIO.connections.Connection Connection} interface.
 * Something that stores multiple connections should inherit the
 * {@link net.xuset.objectIO.connections.Hub Hub} interface.
 * 
 * @see net.xuset.objectIO.connections.FileCon FileCon
 * @see net.xuset.objectIO.connections.StreamCon StreamCon
 * @see net.xuset.objectIO.connections.sockets.tcp.TcpCon TcpCon
 */

package net.xuset.objectIO.connections;