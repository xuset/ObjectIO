package objectIO.connection.stream.streamBase;

public interface Stream {
	public void flush();
	public boolean areStreamsOpen();
	public void closeStreams();
}
