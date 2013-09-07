package objectIO.connection.stream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import objectIO.connection.AbstractConnection;
import objectIO.connection.stream.streamBase.InputParser;
import objectIO.connection.stream.streamBase.StreamBase;
import objectIO.markupMsg.MarkupMsg;

public class StreamIO extends AbstractConnection implements StreamConnection {
	protected StreamBase streamBase;
	
	public StreamIO(long myId) {
		super(myId);
	}
	
	public StreamIO(long myId, Socket s) {
		super(myId);
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			setStreams(in, s.getOutputStream());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public StreamIO(long myId, BufferedReader input, OutputStream output) {
		super(myId);
		setStreams(input, output);
	}
	
	private InputParser parser = new InputParser() {
		public void parse(String message) {
			parseInput(message);
		}
	};
	
	public void setStreams(BufferedReader input, OutputStream output) {
		sendMeetAndGreet(input, output);
		streamBase = new StreamBase(input, output, parser);
	}
	
	public void sendMeetAndGreet(BufferedReader input, OutputStream output) {
		try {
			String message = "hello my name is:" + myId;
			output.write(message.getBytes(), 0, message.length());
			output.write(13);
			String[] split = input.readLine().split(":", 2);
			endPointId = Long.parseLong(split[1]);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public final void shutdown() { streamBase.shutdown(); }

	public boolean areStreamsOpen() { return streamBase.areStreamsOpen(); }

	public void flush() { streamBase.flush(); }

	public boolean sendMessage(MarkupMsg message) { streamBase.sendMessage(message.toString()); return true; }
	
	protected void parseInput(String s) { messageQueue.add(new MarkupMsg(s)); }
	
	public void closeStreams() { streamBase.shutdown(); }
}
