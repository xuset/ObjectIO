package net.xuset.objectIO.connections.sockets.groupNet.client;

import net.xuset.objectIO.connections.sockets.groupNet.GroupCmdCrafter;
import net.xuset.objectIO.connections.sockets.groupNet.GroupNetMsg;
import net.xuset.objectIO.markupMsg.MarkupMsg;

/**
 * Class that is used to interpret GroupNetMsg objects that have been received from
 * the server. 
 * 
 * @author xuset
 *
 */

class Commands {
	
	/**
	 * Creates a predetermine list of commands and links them together
	 * 
	 * @param hub the hub the commands should be used on
	 * @return the command with the rest of the commands linked to it
	 */
	static CmdChain constructChain(GroupClientHub hub) {

		CmdChain cmdChain = new Commands.CmdConnect(hub);
		cmdChain.append(new Commands.CmdDisconnect(hub))
				.append( new Commands.CmdMsg(hub));
		
		return cmdChain;
	}
	
	
	/**
	 * Abstract base class for all commands
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	static abstract class CmdChain {
		private CmdChain next = null;
		
		protected abstract boolean myCommand(GroupNetMsg msg);
		protected abstract void doCommand(GroupNetMsg msg);
		
		/**
		 * Checks if this command can handle the message. If it can handle the message,
		 * {@code doCommand(GroupNetMsg)} is called, else it calls
		 * {@code handOff(GroupNetMsg)} on the next linked command.
		 * 
		 * @param msg message to handle or pass off to the next command
		 */
		public void handOff(GroupNetMsg msg) {
			if (myCommand(msg))
				doCommand(msg);
			else if (next != null)
				next.handOff(msg);
		}
		
		/**
		 * Links commands together.
		 * 
		 * @param cmd the command to link
		 * @return the argument {@code cmd}
		 */
		public CmdChain append(CmdChain cmd) {
			next = cmd;
			return cmd;
		}
	}
	
	
	/**
	 * Command used to add new 'virtual' connections to GroupClientHub
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	static class CmdConnect extends CmdChain{
		private GroupClientHub hub;
		
		public CmdConnect(GroupClientHub hub) {
			this.hub = hub;
		}

		@Override
		protected boolean myCommand(GroupNetMsg msg) {
			return GroupCmdCrafter.isNewCon(msg);
		}

		@Override
		protected void doCommand(GroupNetMsg msg) {
			long id = GroupCmdCrafter.getNewConId(msg);
			GroupClientCon c = new GroupClientCon(hub, id);
			hub.addConnection(c);
		}
	}
	
	
	/**
	 * Command used to remove 'virtual' connections from GroupClientHub
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	static class CmdDisconnect extends CmdChain{
		private GroupClientHub hub;
		
		public CmdDisconnect(GroupClientHub hub) {
			this.hub = hub;
		}

		@Override
		protected boolean myCommand(GroupNetMsg msg) {
			return GroupCmdCrafter.isDisconnect(msg);
		}

		@Override
		protected void doCommand(GroupNetMsg msg) {
			long id = GroupCmdCrafter.getDisconnectId(msg);
			GroupClientCon c = hub.getConnectionById(id);
			if (c != null)
				c.close();
		}
	}
	
	
	/**
	 * Command used to add the message to the appropriate connection's received message
	 * queue.
	 * 
	 * @author xuset
	 * @since 1.0
	 */
	static class CmdMsg extends CmdChain{
		private GroupClientHub hub;
		
		public CmdMsg(GroupClientHub hub) {
			this.hub = hub;
		}

		@Override
		protected boolean myCommand(GroupNetMsg msg) {
			return true;
		}

		@Override
		protected void doCommand(GroupNetMsg msg) {
			GroupClientCon c = hub.getConnectionById(msg.from());
			if (c != null) {
				for (MarkupMsg m : msg.getNestedMsgs())
					c.addMsgToQueue(m);
			} else {
				System.err.println("Connection (" + msg.from() +
						") is not found. message, " + msg + " recieved.");
			}
		}
	}
}
