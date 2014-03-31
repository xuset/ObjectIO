package net.xuset.objectIO.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.xuset.objectIO.connections.Connection;


/**
 * Creates random long primitives that can used to create id's for connections.
 * This class uses {@link java.util.Random#nextLong()} to generate random numbers.
 * 
 * @author xuset
 *
 */
public class ConnectionIdGenerator {
	private static List<Long> forbidden = Arrays.asList(Connection.BROADCAST_CONNECTION);
	private static ConnectionIdGenerator instance = new ConnectionIdGenerator();
	
	
	/**
	 * Creates a new id.
	 * 
	 * @return the new id
	 */
	public static long createNext() { return instance.createId(); }
	
	private final Random random = new Random();
	
	
	/**
	 * Creates a new id. It is best to use the static method {@link #createNext()}
	 * instead because each new instance of ConnectionIdGenerator creates a new instance
	 * of java.util.Random and this can cause collisions between different instances of
	 * ConnectionIdGenerator.
	 * 
	 * @return the new id
	 */
	public long createId() {
		long id = random.nextLong();
		if (forbidden.contains(id))
			return createId();
		else
			return id;
	}
}
