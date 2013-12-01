package objectIO.connections.p2pServer.client;

import objectIO.connections.p2pServer.CmdCrafter;
import objectIO.connections.p2pServer.P2PMsg;
import objectIO.markupMsg.MarkupMsg;
class Commands {
	
	public static abstract class CmdChain {
		private CmdChain next = null;
		
		protected abstract boolean myCommand(P2PMsg msg);
		protected abstract void doCommand(P2PMsg msg);
		
		public void handOff(P2PMsg msg) {
			if (myCommand(msg))
				doCommand(msg);
			else if (next != null)
				next.handOff(msg);
		}
		
		public CmdChain append(CmdChain cmd) {
			next = cmd;
			return cmd;
		}
	}
	
	public static class CmdConnect extends CmdChain{
		private ClientHub hub;
		
		public CmdConnect(ClientHub hub) {
			this.hub = hub;
		}

		@Override
		protected boolean myCommand(P2PMsg msg) {
			return CmdCrafter.isNewCon(msg);
		}

		@Override
		protected void doCommand(P2PMsg msg) {
			long id = CmdCrafter.getNewConId(msg);
			ClientConnection c = new ClientConnection(hub, id);
			hub.addConnection(c);
		}
	}
	
	public static class CmdDisconnect extends CmdChain{
		private ClientHub hub;
		
		public CmdDisconnect(ClientHub hub) {
			this.hub = hub;
		}

		@Override
		protected boolean myCommand(P2PMsg msg) {
			return CmdCrafter.isDisconnect(msg);
		}

		@Override
		protected void doCommand(P2PMsg msg) {
			long id = CmdCrafter.getDisconnectId(msg);
			ClientConnection c = hub.getConnection(id);
			if (c != null)
				c.disconnect();
		}
	}
	
	public static class CmdMsg extends CmdChain{
		private ClientHub hub;
		
		public CmdMsg(ClientHub hub) {
			this.hub = hub;
		}

		@Override
		protected boolean myCommand(P2PMsg msg) {
			return true;
		}

		@Override
		protected void doCommand(P2PMsg msg) {
			ClientConnection c = hub.getConnection(msg.from());
			if (c != null) {
				for (MarkupMsg m : msg.child)
					c.msgQueue().add(m);
			} else {
				System.err.println("Connection (" + msg.from() + ") is not found. message, " + msg + " recieved.");
			}
		}
	}
}
