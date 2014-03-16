package net.xuset.objectIO.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class AsynchronousUrlReader {
	private final String url;
	private final int bufferSize;
	private final long timeout;
	private final Thread thread;
	
	private String contents = "";
	private boolean reachedEndOfStream = false;
	private boolean didError = false;
	
	public String getContents() { return contents; }
	public boolean reachedEndOfStream() { return reachedEndOfStream; }
	public boolean encounteredError() { return didError; }
	public boolean isFinished() { return !thread.isAlive(); }
	public boolean wasSuccessfull() { return isFinished() && !encounteredError(); }
	
	public AsynchronousUrlReader(String url, int bufferSize) {
		this(url, bufferSize, 3000L);
	}
	
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
		thread = new Thread(new Worker(), "AsynchronousUrlReader");
		thread.start();
	}
	
	private class Worker implements Runnable {

		@Override
		public void run() {
			InputStream stream = null;
			
			try {
				URL urlObject = new URL(url);
				stream = urlObject.openStream();
				
				int index = 0;
				char buffer[] = new char[bufferSize];
				long startTime = System.currentTimeMillis();
				while (index < buffer.length &&
						startTime + timeout > System.currentTimeMillis()) {
					
					if (true) {
						int read = stream.read();
						if (read == -1) {
							reachedEndOfStream = true;
							break;
						} else {
							buffer[index] = (char) read;
							index++;
						}
					}
				}
				
				if (startTime + timeout <= System.currentTimeMillis())
					didError = true; //exceeded timeout
				
				contents = new String(buffer);
			} catch (IOException ex) {
				didError = true;
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
