package net.xuset.objectIO.connections.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xuset.objectIO.markupMsg.AsciiMsgParser;
import net.xuset.objectIO.markupMsg.InvalidFormatException;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgAttribute;
import net.xuset.objectIO.markupMsg.MsgParser;


/**
 * This class is used to determine the id of the remote connection. This is mainly
 * useful for classes that implement the
 * {@link net.xuset.objectIO.connections.sockets.InetCon InetCon} interface. If both
 * the local and remote connection create a StreamGreeter upon connection, they can
 * exchange their local id's with the other. 
 * 
 * @author xuset
 * @since 1.0
 */
public class StreamGreeter {
	private static final Logger log = Logger.getLogger(StreamGreeter.class.getName());
	
	private static final int newLine = '\n';
	private static final String attributeName = "new connection";
	private static final int bufferSize = 1024;
	
	private final MsgParser msgParser = new AsciiMsgParser();
	private final OutputStream out;
	private final InputStream in;
	private final long localId;
	private long endId = -1L;
	
	
	/**
	 * gets local id of the remote connection
	 * @return id sent by the remote connection
	 */
	public long getEndPointId() { return endId; }
	
	
	/**
	 * Creates a StreamGreeter and attempts to determine the local id of the remote
	 * connection.
	 * 
	 * @param out output stream to write to
	 * @param in input stream to read from
	 * @param localId the local id that will be sent to the remote connection
	 * @param timeout the max amount of time to wait for data to be received
	 * @throws IOException if an I/O error occurs
	 * @throws HandshakeFailedException if the attempt fails
	 */
	public StreamGreeter(OutputStream out, InputStream in, long localId, long timeout)
			throws IOException, HandshakeFailedException{
		
		this.out = out;
		this.in = in;
		this.localId = localId;
		
		log.log(Level.INFO, "Attempting handshake with localId=" + localId);
		
		sendMeetAndGreet(timeout);
	}
	
	private void sendMeetAndGreet(long delay) throws IOException{
		char[] buffer = new char[bufferSize];
		
		sendGreetings();
		readData(buffer, delay);
		if (!determineEndId(buffer)) {
			log.log(Level.SEVERE, "Handshake failed. localId=" + localId);
			throw new HandshakeFailedException();
		}
		log.log(Level.INFO, "Handshake successful. localId=" + localId +
				" endId=" + endId);
	}
	
	private void sendGreetings() throws IOException {
		final MarkupMsg m = new MarkupMsg();
		m.addAttribute(attributeName, localId);
		
		out.write(msgParser.toRawByteArray(m));
		out.write(newLine);
		out.flush();
	}
	
	private void readData(char[] buffer, long delay) throws IOException{
		final long startTime = System.currentTimeMillis();
		
		int offset = 0;
		while (startTime + delay > System.currentTimeMillis() &&
				offset < buffer.length) {
			
			if (in.available() == 0) {
				try { Thread.sleep(1); } catch (InterruptedException ex) { }
				continue;
			}
			
			
			int read = in.read();
			if (read != -1) {
				buffer[offset] = (char) read;
				offset++;
				if (read == newLine)
					break;
			} else {
				break;
			}
			
		}
	}
	
	private boolean determineEndId(char[] buffer) {
		try {
			MarkupMsg recievedMsg = msgParser.parseFrom(buffer);
			MsgAttribute na = recievedMsg.getAttribute(attributeName);
			if (na != null) {
				endId = na.getLong();
				return true;
			}
		} catch (InvalidFormatException ex) {
			//Do nothing. just return false
		}
		return false;
	}
}
