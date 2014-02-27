package net.xuset.objectIO.connections;

public class ConnectionFailedException extends Exception{
	private static final long serialVersionUID = -7529463245152705040L;

	public ConnectionFailedException() {
		super("Connection failed!");
	}
}
