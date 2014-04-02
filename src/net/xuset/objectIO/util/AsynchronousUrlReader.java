package net.xuset.objectIO.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * This class reads the contents addressed by a given url. The reading happens in a
 * separate thread. The contents can be read by calling {@code getContents()}. This
 * method will return an empty string until the thread finishes reading. Before
 * trusting that the String returned by {@code getContents()} is accurate, make sure
 * {@code isFinished()} is true and {@code encounteredError()} is false.
 * 
 * <p>AsynchronousUrlReader is backed by a {@link java.net.URL URL}.</p>
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class AsynchronousUrlReader {
	private static final long defaultTimeout = 3000L;
	
	private final String url;
	private final int bufferSize;
	private final long timeout;
	private final Thread thread;
	
	private String contents = "";
	private boolean reachedEndOfStream = false;
	private IOException exception = null;
	
	
	/**
	 * Returns the contents that have been read.
	 * 
	 * @return the contents read from the source or an empty string if the reading
	 * 			has not finished
	 */
	public String getContents() { return contents; }
	
	
	/**
	 * Returns true if the end of stream was reached before running out of space
	 * allocated for the buffer.
	 * 
	 * @return {@code true} if the end of stream was reached
	 */
	public boolean reachedEndOfStream() { return reachedEndOfStream; }
	
	
	/**
	 * Returns if an error was encountered
	 * 
	 * @return {@code true} if an error was encountered
	 */
	public boolean encounteredError() { return exception != null; }
	
	
	/**
	 * Returns the exception that was encountered.
	 * 
	 * @return the encountered exception or null if no exception was encountered
	 */
	public IOException getException() { return exception; }
	
	
	/**
	 * Indicates if reading the contents has finished
	 * 
	 * @return {@code true} if the reading has finished
	 */
	public boolean isFinished() { return !thread.isAlive(); }
	
	
	/**
	 * Constructs a new AsynchronousUrlReader with the given url and buffer size. A
	 * default timeout of {@value #defaultTimeout} milliseconds is used.
	 * 
	 * @param url string url to read the contents from
	 * @param bufferSize the max buffer size
	 */
	public AsynchronousUrlReader(String url, int bufferSize) {
		this(url, bufferSize, defaultTimeout);
	}
	
	
	/**
	 * Constructs a new AsynchronousUrlReader with the given url, buffer size, and
	 * timeout.
	 * 
	 * @param url string url to read the contents from
	 * @param bufferSize the max buffer size
	 * @param timeout the max amount of milliseconds to finish reading from the url
	 */
	public AsynchronousUrlReader(String url, int bufferSize, long timeout) {
		if (url == null)
			throw new IllegalArgumentException("Url cannot be null");
		if (bufferSize <= 0)
			throw new IllegalArgumentException("BufferSize must be greater than 0");
		if (timeout <= 0)
			throw new IllegalArgumentException("Timeout must be greater than 0");
		
		this.url = url;
		this.bufferSize = bufferSize;
		this.timeout = timeout;
		thread = new Thread(new Worker(), this.getClass().getSimpleName());
		thread.start();
	}
	
	
	/**
	 * Worker thread that reads from the url
	 * 
	 * @author xuset
	 *
	 */
	private class Worker implements Runnable {

		@Override
		public void run() {
			InputStream stream = null;
			
			try {
				URL urlObject = new URL(url);
				stream = new BufferedInputStream(urlObject.openStream());
				
				int index = 0;
				char buffer[] = new char[bufferSize];
				long startTime = System.currentTimeMillis();
				while (index < buffer.length) {
					
					if (startTime + timeout <= System.currentTimeMillis())
						throw new IOException("Timeout reached");
					
					int read = stream.read();
					if (read == -1) {
						reachedEndOfStream = true;
						break;
					} else {
						buffer[index] = (char) read;
						index++;
					}
					
				}
				
				contents = new String(buffer);
			} catch (IOException ex) {
				exception = ex;
				ex.printStackTrace();
			} finally {
				if (stream != null) {
					try { stream.close(); }
					catch(IOException ex) { ex.printStackTrace(); }
				}
			}
		}
	}
}
