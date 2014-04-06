package net.xuset.objectIO.connections;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xuset.objectIO.markupMsg.MarkupMsg;


/**
 * This class continually monitors the input stream for new data. Once new data is
 * available it is parsed and added to the message queue. This is different form
 * the StreamCon class in that StreamCon only reads from the InputStream when it is
 * needed.
 * 
 * <p>{@link #attemptToReadRawMessage()} is continually called from a separate thread
 * until {@code reachedEndOfInput()} is true. So if you extend this class and override
 * {@code attemptToReadRawMessage()} or {@code handleRawInput(String)}, make sure those
 * two methods are thread safe.</p>
 * <p>The method {@code #addMsgToQueue(MarkupMsg)} is thread safe.</p>
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class StreamConReader extends StreamCon {
	private static final Logger log = Logger.getLogger(StreamConReader.class.getName());
	
	private final Queue<MarkupMsg> msgQueue = new ConcurrentLinkedQueue<MarkupMsg>();
	

	/**
	 * Constructs a new StreamConReader object with the supplied InputStream and id.
	 * Using this constructor will disable sending messages because no OutputStream
	 * was supplied.
	 * 
	 * @param in InputStream to read data from
	 * @param id the id of the connection
	 */
	public StreamConReader(InputStream in, long id) {
		super(in, id);
		new InputReader();
		shouldBlockForInput = true;
	}

	
	/**
	 * Constructs a new StreamConReader object with the given I/O streams and id.
	 * Since both an InputStream and OutputStream is given, sending and receiving
	 * messages is enabled.
	 * 
	 * @param in InputStream to receive messages
	 * @param out OutputStream to send messages
	 * @param id the id of the connection
	 */
	public StreamConReader(InputStream in, OutputStream out, long id) {
		super(in, out, id);
		new InputReader();
		shouldBlockForInput = true;
	}

	
	/**
	 * Polls the next message in the received messages queue. The message is removed and
	 * returned.
	 */
	@Override
	public MarkupMsg pollNextMsg() {
		return msgQueue.poll();
	}

	
	/**
	 * Indicates if the received messages queue has messages stored. If this method
	 * returns true, the messages can be read by calling {@code pollNextMsg()}.
	 */
	@Override
	public boolean isMsgAvailable() {
		return !msgQueue.isEmpty();
	}

	@Override
	protected boolean addMsgToQueue(MarkupMsg msg) {
		return msgQueue.add(msg);
	}
	
	
	/** Worker thread that continually monitors the input stream for new data. */
	private class InputReader extends Thread {
		InputReader() {
			setName("Input stream reader");
			start();
		}
		
		@Override
		public void run() {
			log.log(Level.INFO, "connection(" + StreamConReader.this.getId() +
					") listening on input stream");
			while (!reachedEndOfInput()) {
				if (!attemptToReadRawMessage())
					Thread.yield();
			}
			log.log(Level.INFO, "connection(" + StreamConReader.this.getId() +
					") stopped listening on input stream");
			
		}
	}

}
