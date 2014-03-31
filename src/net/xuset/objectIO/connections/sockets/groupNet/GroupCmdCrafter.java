package net.xuset.objectIO.connections.sockets.groupNet;


/**
 * Used creating and interpreting GroupNetMsg objects.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class GroupCmdCrafter {
	
	/**
	 * Creates the message that instructs the creation of a GroupClientCon.
	 * 
	 * @param id id of the new connection
	 * @return the message to send to clients
	 */
	public static GroupNetMsg craftNewCon(long id) {
		GroupNetMsg m = new GroupNetMsg(GroupNetMsg.Types.Connection);
		m.addAttribute("add", id);
		return m;
	}
	
	
	/**
	 * Indicates if the message is new connection instruction message.
	 * 
	 * @param msg the message in question
	 * @return {@code true} if the message is new connection message
	 */
	public static boolean isNewCon(GroupNetMsg msg) {
		return msg.getAttribute("add") != null;
	}
	
	
	/**
	 * Gets the id of the new GroupClientCon.
	 * 
	 * @param msg the message to get the id from
	 * @return the id of the new connection
	 */
	public static long getNewConId(GroupNetMsg msg) {
		return msg.getAttribute("add").getLong();
	}
	
	
	/**
	 * Creates a message that instructs the removal of a GroupClientCon.
	 * 
	 * @param id the id of the connection to remove
	 * @return the message to send to clients
	 */
	public static GroupNetMsg craftDisconnect(long id) {
		GroupNetMsg m = new GroupNetMsg(GroupNetMsg.Types.Connection);
		m.addAttribute("rm", id);
		return m;
	}
	
	/**
	 * Indicates if the message is a disconnection message.
	 * 
	 * @param msg the message in question
	 * @return {@code true} if the connection is a disconnection message
	 */
	public static boolean isDisconnect(GroupNetMsg msg) {
		return msg.getAttribute("rm") != null;
	}
	
	/**
	 * Gets the id of the message to disconnect
	 * 
	 * @param msg the message to get the id from
	 * @return the id of the message to disconnect
	 */
	public static long getDisconnectId(GroupNetMsg msg) {
		return msg.getAttribute("rm").getLong();
	}
	
}
