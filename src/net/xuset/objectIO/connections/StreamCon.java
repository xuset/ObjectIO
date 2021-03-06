package net.xuset.objectIO.connections;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xuset.objectIO.markupMsg.AsciiMsgParser;
import net.xuset.objectIO.markupMsg.InvalidFormatException;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgParser;


/**
 * 
 * StreamCon is implementation of Connection that is backed by streams. Messages are
 * sent and received through InputStream and OutputStream objects.
 * 
 * <p>Messages are sent over the InputStream when {@code sendMsg(MarkupMsg)} is called.
 * When {@code pollNextMsg()} is called, it polls from the message queue. If the
 * message queue is empty, the OutputStream is checked for any new data.</p>
 * 
 * @author xuset
 * @since 1.0
 * @see java.io.InputStream
 * @see java.io.OutputStream
 */
public class StreamCon implements StreamConI{
	private static final Logger log = Logger.getLogger(StreamCon.class.getName());

	private static final String unsupportedSend =
			"Sending messages is not supported";
	private static final String unsupportedReceive =
			"Receiving messages is not supported";
	
	
	private final Queue<MarkupMsg> msgQueue = new LinkedList<MarkupMsg>();
	private final StringBuilder lineBuffer = new StringBuilder(8192);
	private final long id;
	private final InputStream in;
	private final OutputStream out;
	
	/** only true after {@code close()} is called */
	private boolean isClosed = false;
	
	private boolean reachedEndOfInput = false;
	private MsgParser msgParser = new AsciiMsgParser();

	
	/**
	 * The value is used to distinguish between the start and end of different messages.
	 * Sending a message that contains this value will result in incorrectly reading
	 * the message.
	 */
	private char messageDelimeter = '\n';
	
	
	/** 
	 * When {@code true} and when the message queue is empty, calling {@code pollNextMsg}
	 * or {@code isMsgAvailable} will block until a new message is received on the
	 * InputStream. 
	 */
	protected boolean shouldBlockForInput = false;
	
	
	/**
	 * Constructs a connection that is backed by IO streams. Both sending
	 * and receiving messages is enabled when both an InputStream and OutputStream are
	 * given.
	 * 
	 * @param in Stream that messages will be sent on. Must not be null.
	 * @param out Stream that messages are received from. Must not be null
	 * @param id value that will be returned when {@code getId()} is called
	 * @throws IllegalArgumentException if {@code in} or {@code out} is null.
	 * 
	 */
	public StreamCon(InputStream in, OutputStream out, long id) {
		if (in == null || out == null)
			throw new IllegalArgumentException("Neither in or out can be null");
		this.id = id;
		this.in = in;
		this.out = out;
	}
	
	
	/**
	 * Constructs a new StreamCon connection with sending messages disabled.
	 * Only receiving messages is enabled.
	 * 
	 * @param in stream to receive messages on. Must not be null.
	 * @param id value that will be returned by {@code getId()}.
	 * @throws IllegalArgumentException if {@code in} is null
	 * 
	 */
	public StreamCon(InputStream in, long id) {
		if (in == null)
			throw new IllegalArgumentException("in cannot be null");
		this.id = id;
		this.in = in;
		out = null;
	}
	
	
	/**
	 * Constructs a new StreamCon connection with receiving messages disabled.
	 * Only sending messages is enabled.
	 * 
	 * @param out stream to send messages on. Must not be null.
	 * @param id value that will be returned by {@code getId()}.
	 * @throws IllegalArgumentException if {@code out} is null
	 * 
	 */
	public StreamCon(OutputStream out, long id) {
		if (out == null)
			throw new IllegalArgumentException("out cannot be null");
		this.id = id;
		this.in = null;
		this.out = out;
	}
	
	
	/**
	 * Gets the character that distinguishes the start and end of different messages.
	 * If a message contains this character, the message will not be properly parsed.
	 * This is usually a new line character, but can be any character.
	 *  
	 * @return character that separates different messages
	 */
	public char getMessageDelimeter() {
		return messageDelimeter;
	}
	
	
	/**
	 * Sets the character that distinguishes the start and end of different messages.
	 * If a message contains this character, the message will not be properly parsed.
	 * This is usually a new line character, but can be any character.
	 * 
	 * @param delimeter the new character that will be used to seperate messages
	 */
	public void setMessageDelimeter(char delimeter) {
		messageDelimeter = delimeter;
	}
	
	
	/**
	 * Indicates if the end of the input stream was reached. 
	 * 
	 * @return true if the end of stream was reached.
	 */
	public boolean reachedEndOfInput() { return reachedEndOfInput; }
	
	
	/**
	 * Indicates if receiving messages is supported. Receiving messages is supported
	 * if an InputStream was passed into the constructor.
	 * 
	 * @return true if messages can be received
	 */
	public boolean isReceivingSupported() {
		return in != null;
	}
	
	
	/**
	 * Indicates if sending messages is supported. Sending messages is supported if an
	 * OutputStream was passed into the constructor.
	 * 
	 * @return true if messages can be sent
	 */
	public boolean isSendingSupported() {
		return out != null;
	}

	@Override
	public long getId() {
		return id;
	}
	
	@Override
	public boolean isClosed() { return isClosed; }
	
	
	/**
	 * Returns the next message in the queue. If the queue is empty, the input stream is
	 * checked for new data.
	 * 
	 * @throws UnsupportedOperationException if receiving messages is not supported
	 */
	@Override
	public MarkupMsg pollNextMsg() {
		if (!isReceivingSupported())
			throw new UnsupportedOperationException(unsupportedReceive);
		
		if (!isMsgAvailable())
			attemptToReadRawMessage();
		return msgQueue.poll();
	}
	
	
	/**
	 * Checks if the received messages queue is not empty. If it is empty, the input
	 * stream is checked for new data.
	 * 
	 * @return {@code true} if a message can be polled by {@code pollNextMsg()}
	 * @throws UnsupportedOperationException if receiving messages is not supported
	 */
	@Override
	public boolean isMsgAvailable() {
		if (!isReceivingSupported())
			throw new UnsupportedOperationException(unsupportedReceive);
		
		if (msgQueue.isEmpty())
			attemptToReadRawMessage();
		return !msgQueue.isEmpty();
	}

	
	/**
	 * @throws UnsupportedOperationException if sending messages is not supported
	 */
	@Override
	public boolean sendMsg(MarkupMsg message) {
		if (!isSendingSupported())
			throw new UnsupportedOperationException(unsupportedSend);
		
		try {
			byte[] bytes = msgParser.toRawByteArray(message);
			sendRawMsg(bytes);
			return true;
		} catch (IOException ex) {
			log.log(Level.SEVERE, ex.getMessage(), ex);
			close();
		}
		return false;
	}
	
	
	/**
	 * Writes the given byte array directly to the output stream. This bypasses any
	 * calls to the message parser. After the byte array is written,
	 * the message delimiter byte is written to the stream.
	 * 
	 * @param bytes the byte array to write to the stream
	 * @throws IOException if an error occurs while writing to the stream
	 * @throws UnsupportedOperationException if sending messages is not supported
	 * @see #getMessageDelimeter()
	 */
	protected synchronized void sendRawMsg(byte[] bytes) throws IOException {
		if (!isSendingSupported())
			throw new UnsupportedOperationException(unsupportedSend);
		out.write(bytes, 0, bytes.length);
		out.write(messageDelimeter);
	}

	
	/**
	 * @throws UnsupportedOperationException if sending messages is not supported
	 */
	@Override
	public void flush() {
		if (!isSendingSupported())
			throw new UnsupportedOperationException(unsupportedSend);
		try {
			out.flush();
		} catch (IOException ex) {
			log.log(Level.SEVERE, "connection(" + getId() + ") : " + ex.getMessage(), ex);
			close();
		}
	}

	@Override
	public void close() {
		log.log(Level.INFO, "close() called on connection(" + getId() + ")");
		
		reachedEndOfInput = true;
		isClosed = true;
		
		try { in.close(); }
		catch (IOException ex) { log.log(Level.WARNING, ex.getMessage(), ex); }
		
		try { out.close(); }
		catch (IOException ex) { log.log(Level.WARNING, ex.getMessage(), ex); }
	}
	
	
	/**
	 * Adds a message to the received messages queue. The added messages can be read by
	 * calling {@code pollNextMsg()}
	 * 
	 * @param msg message to add to the received message queue
	 * @return This method never fails at adding the message and will always return
	 * 		{@code true}.
	 */
	protected boolean addMsgToQueue(MarkupMsg msg) {
		return msgQueue.add(msg);
	}
	
	
	/**
	 * Called when a raw message has been read from the input stream. The method
	 * then parses the string into a MarkupMsg object and adds it the message queue. The
	 * new message can be read by calling {@code pollNextMsg()}.
	 * 
	 * @param input string containing the contents of the MarkupMsg. The {@code input}
	 * 			argument can be null if the end of stream was reached.
	 */
	protected void handleRawInput(String input) {
		if (input != null) {
			try {
				MarkupMsg m = msgParser.parseFrom(input);
				addMsgToQueue(m);
			} catch (InvalidFormatException e) {
				log.log(Level.WARNING, "connection(" + getId() + ") : " +
						e.getMessage() + "\n input=" + input, e);
			}
		}
	}
	
	
	/**
	 * Attempts to read an entire MarkupMsg object in it's raw form. If it does read
	 * the entire raw MarkupMsg object, it calls {@code handleRawInput(String)}. If
	 * not enough data is present in the InputStream to construct an entire message,
	 * false is returned. A entire message consists of all data between two message
	 * delimiters.
	 * 
	 * @return true if an entire raw MarkupMsg object was read
	 * @see #getMessageDelimeter()
	 */
	protected boolean attemptToReadRawMessage() {
		try {
			synchronized(in) {
				
				while (in.available() > 0 || shouldBlockForInput) {
					int read = in.read();
					reachedEndOfInput = read == -1;
					
					if (reachedEndOfInput) {
						log.log(Level.INFO, "connection(" + getId() +
								") reached the end of the input stream");
						handleRawInput(null);
						return false;
					}
					
					if (read == messageDelimeter) {
						handleRawInput(lineBuffer.toString());
						lineBuffer.delete(0, lineBuffer.length());
						return true;
					}
					
					if (lineBuffer.length() == lineBuffer.capacity())
						lineBuffer.ensureCapacity(lineBuffer.length() + in.available());
					
					lineBuffer.append((char) read);
				}
				
			}
		} catch (IOException ex) {
			if (!isClosed()) {
				log.log(Level.SEVERE, "connection(" + getId() + ") : " +
						ex.getMessage(), ex);
				reachedEndOfInput = true;
				handleRawInput(null);
			}
		}
		
		return false;
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