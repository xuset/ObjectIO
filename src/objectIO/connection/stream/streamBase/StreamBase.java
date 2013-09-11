package objectIO.connection.stream.streamBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class StreamBase implements Stream{
	private BufferedReader input;
	private OutputStream output;
	private InputParser parser;
	private InputListener listener;
	private boolean hasShutdown = false;
	
	public BufferedReader getInput() { return input; }
	public OutputStream getOutput() { return output; }
	
	public StreamBase(BufferedReader input, OutputStream output, InputParser parser) {
		this.input = input;
		this.output = output;
		this.parser = parser;
		listener = new InputListener();
		listener.start();
	}
	
	public boolean areStreamsOpen() {
		return (input != null && output != null && hasShutdown == false);
	}
	
	public void shutdown() {
		hasShutdown = true;
		try {
			input.close();
			output.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		listener.interrupt();
	}

	public void closeStreams() {
		shutdown();
	}
	
	public boolean sendMessage(String message) {
		try {
			output.write(message.getBytes(), 0, message.length());
			output.write(13);
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public void flush() {
		try {
			output.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private class InputListener extends Thread {
		public InputListener() {
			setName("Stream Input Listener");
		}
		
		public void run() {
			while (hasShutdown == false) {
				try {
					parser.parse(input.readLine());
				} catch (IOException e) {
					//System.err.println(e.getMessage());
					parser.parse(null);
					e.printStackTrace();
				}
			}
		}
	}
}
