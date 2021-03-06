package net.xuset.objectIO.connections.sockets.groupNet.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xuset.objectIO.connections.AbstractConnection;
import net.xuset.objectIO.connections.sockets.InetCon;
import net.xuset.objectIO.connections.sockets.InetEventListener;
import net.xuset.objectIO.connections.sockets.groupNet.GroupNetMsg;
import net.xuset.objectIO.markupMsg.AsciiMsgParser;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgParser;


/**
 * Virtual connection used by GroupClientHub. This connection is a 'virtual' connection
 * used to communicate with a different GroupClientCon on the internet without needing
 * a real connection like TCP. The messages sent through this connection are sent to the
 * server (GroupNetServer) and are forwarded across a real connection to the appropriate
 * client.
 * 
 * @author xuset
 * @since 1.0
 * @see net.xuset.objectIO.connections.sockets.groupNet.server.GroupNetServer GroupNetServer
 *
 */
public class GroupClientCon extends AbstractConnection implements InetCon{
	private static final Logger log = Logger.getLogger(GroupClientCon.class.getName());

	private final List<InetEventListener> eventListeners =
			new ArrayList<InetEventListener>();
	private final GroupClientHub hub;
	private final boolean isBroadcast;
	private MsgParser msgParser = new AsciiMsgParser();

	private boolean isClosed = false;


	/**
	 * Creates a non-broadcast instance of GroupClientCon
	 * 
	 * @param hub GroupClientHub that this connection should use
	 * @param endId the end id the this connection
	 */
	GroupClientCon(GroupClientHub hub, long endId) {
		this(hub, endId, false);
	}


	/**
	 * Creates an instance of GroupClientCon
	 * 
	 * @param hub GroupClientHub that this connection should use
	 * @param endId the end if the this connection
	 * @param isBroadcast will be returned by {@link #isBroadcast()}
	 */
	GroupClientCon(GroupClientHub hub, long endId, boolean isBroadcast) {
		super(endId, new ConcurrentLinkedQueue<MarkupMsg>());
		this.hub = hub;
		this.isBroadcast = isBroadcast;
	}

	@Override
	public boolean isLoopback() {
		return false;
	}

	@Override
	public boolean isBroadcast() {
		return isBroadcast;
	}

	/**
	 * Used to send a message directly to the server
	 * 
	 * @param msg message to send
	 * @return {@code true} if the message was sent successfully
	 */
	protected boolean sendRawMsg(GroupNetMsg msg) {
		return hub.sendRaw(msg);
	}

	@Override
	public void flush() {
		if (isClosed)
			throw new RuntimeException("Connection is closed");
		hub.flushComm();
	}

	@Override
	public boolean sendMsg(MarkupMsg msg) {
		if (isClosed)
			throw new RuntimeException("Connection is closed");

		GroupNetMsg parentMsg = new GroupNetMsg();
		parentMsg.to(getId());
		parentMsg.from(getLocalId());
		parentMsg.addNested(msg);

		sendRawMsg(parentMsg);
		return true;
	}

	@Override
	public boolean isClosed() {
		return isClosed;
	}

	@Override
	public void close() {
		log.log(Level.INFO, "close() called");
		isClosed = true;
		for (InetEventListener e : eventListeners)
			e.onClose();
		eventListeners.clear();
	}

	@Override
	public long getLocalId() {
		return hub.getLocalId();
	}

	@Override
	public boolean isConnected() {
		return !isClosed;
	}

	@Override
	public boolean watchEvents(InetEventListener e) {
		return eventListeners.add(e);
	}

	@Override
	public boolean unwatchEvents(InetEventListener e) {
		return eventListeners.remove(e);
	}

	@Override
	protected void addMsgToQueue(MarkupMsg msg) {
		super.addMsgToQueue(msg);
	}

	@Override
	public void setParser(MsgParser parser) {
		msgParser = parser;
	}

	@Override
	public MsgParser getParser() {
		return msgParser;
	}

}
