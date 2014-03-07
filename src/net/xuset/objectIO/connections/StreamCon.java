package net.xuset.objectIO.connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgAttribute;



public class StreamCon extends Connection{
	private static final byte[] newLine = new byte[] { 13 };
	
	private final BufferedReader in;
	private final OutputStream out;
	private InputListener listener;
	private boolean isClosed = false;
	
	public InputParser parser;
	
	public static interface InputParser { void parseInput(String input); }
	
	public StreamCon(InputStream in, OutputStream out, Hub<?> hub) {
		super(hub);
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = out;
		parser = parseInput;
	}
	
	public StreamCon(InputStream in, OutputStream out) {
		this(in, out, null);
	}
	
	public boolean isClosed() { return isClosed; }

	@Override
	public boolean sendMsg(MarkupMsg message) {
		try {
			//System.out.println(getId() + " sent: " + message.toString());
			String raw = message.toString();
			out.write(raw.getBytes(), 0, raw.length());
			out.write(newLine);
			return true;
		} catch (IOException ex) {
			close();
			ex.printStackTrace();
		}
		return false;
	}
	
	public void flush() {
		try {
			out.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void close() {
		isClosed = true;
		if (listener != null)
			listener.stopListening();
		try {
			in.close();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void startListening() {
		stopListening();
		listener = new InputListener();
		listener.start();
	}
	
	public void stopListening() {
		if (listener != null)
			listener.stopListening();
		listener = null;
	}

	@Override
	public boolean sendMeetAndGreet(long delay) {
		final String attributeName = "new connection";
		MarkupMsg m = new MarkupMsg();
		m.addAttribute(MsgAttribute.cre(attributeName).set(myId));
		try {
			out.write(m.toString().getBytes());
			out.write(newLine);
			out.flush();
			String rx = in.readLine();
			MarkupMsg rMsg = new MarkupMsg(rx);
			MsgAttribute na = rMsg.getAttribute(attributeName);
			if (na != null) {
				endId = na.getLong();
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private InputParser parseInput = new InputParser() {
		@Override
		public void parseInput(String input) {
			if (input == null)
				close();
			else {
				MarkupMsg m = new MarkupMsg(input);
				if (m.parsedProperly())
					messageQueue.add(m);
			}
		}
	};
	
	private class InputListener extends Thread {
		private boolean stopListening = false;
		public InputListener() {
			setName("Stream Input Listener");
		}
		
		public void stopListening() {
			stopListening = true;
			interrupt();
		}

		@Override
		public void run() {
			stopListening = false;
			while (!stopListening && !isClosed) {
				try {
					String raw = in.readLine();
					//System.out.println(myId + " recieved: " + raw);
					parser.parseInput(raw);
				} catch (IOException e) {
					if (stopListening == false) {
						System.err.println(e.getMessage() + " in stream connection");
						parser.parseInput(null);
					}
					return;
				}
			}
		}
	}
}