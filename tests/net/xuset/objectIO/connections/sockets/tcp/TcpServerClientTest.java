package net.xuset.objectIO.connections.sockets.tcp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.xuset.objectIO.connections.sockets.InetCon;
import net.xuset.objectIO.connections.sockets.InetHub;
import net.xuset.objectIO.connections.sockets.tcp.TcpCon;
import net.xuset.objectIO.connections.sockets.tcp.TcpServer;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.util.ConnectionIdGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TcpServerClientTest {

	private static final int connectionCount = 4;
	private static final String serverAddr = "127.0.0.1";
	private static final int serverPort = 3000;
	
	private InetHub<?> server;
	private List<InetCon> connections;
	
	@Before
	public void before() throws Exception {
		server = new TcpServer(5L, serverPort);
		connections = createConnections(connectionCount);
		
		Thread.sleep(100);
		assertEquals(server.getConnectionCount(), connections.size());
	}
	
	@After
	public void after() throws Exception {
		server.shutdown();
	}
	
	@Test
	public void testSendMsgFromClient() throws Exception {
		for (int i = 0; i < connections.size(); i++) {
			InetCon con = connections.get(i);
			assertTrue(sendMsg(con, "" + i));
			con.flush();
		}

		Thread.sleep(100);
		
		for (int i = 0; i < server.getConnectionCount(); i++) {
			InetCon con = server.getConnectionByIndex(i);
			checkNextMsg(con, "" + i);
		}
	}
	
	@Test
	public void testSendMsgFromServer() throws Exception {
		
		//send message to every connection in the server
		for (int i = 0; i < server.getConnectionCount(); i++) {
			InetCon con = server.getConnectionByIndex(i);
			assertTrue(sendMsg(con, "" + i));
			con.flush();
		}

		Thread.sleep(100);
		
		//check if the message was received
		for (int i = 0; i < connections.size(); i++) {
			InetCon con = connections.get(i);
			checkNextMsg(con, "" + i);
		}
	}
	
	@Test
	public void testBroadcastFromServer() throws Exception{
		MarkupMsg broadcast = new MarkupMsg();
		broadcast.setContent("broadcast");
		assertTrue(server.broadcastMsg(broadcast)); //broadcast the message to all
		for (int i = 0; i < server.getConnectionCount(); i++)
			server.getConnectionByIndex(i).flush(); //flush all

		Thread.sleep(100);
		
		for (int i = 0; i < connections.size(); i++) {
			InetCon con = connections.get(i);
			checkNextMsg(con, broadcast.getContent()); //check for received message
		}
	}
	
	@Test
	public void testServerShutdown() throws Exception {
		server.shutdown();
		
		Thread.sleep(100);
		
		for (InetCon con : connections) {
			assertTrue(con.isClosed());
		}
	}
	
	@Test
	public void testCloseConectionFromServer() throws Exception {
		while (server.getConnectionCount() > 0) {
			server.getConnectionByIndex(0).close();
		}
		
		Thread.sleep(100);
		
		for (InetCon con : connections) {
			assertTrue(con.isClosed());
		}
	}

	@Test
	public void testCloseConnectionFromClient() throws Exception{

		assertEquals(connectionCount, server.getConnectionCount());
		int count = connectionCount;
		for (InetCon con : connections) {
			con.close();
			count--;
			try { Thread.sleep(100); } catch (InterruptedException ex) { }
			assertEquals(count, server.getConnectionCount());
		}
	}
	
	private static void checkNextMsg(InetCon con, String expected) {
		assertTrue(con.isMsgAvailable());
		MarkupMsg msg = con.pollNextMsg();
		assertTrue(msg.getContent().equals(expected));
	}
	
	private static boolean sendMsg(InetCon con, String message) {
		MarkupMsg msg = new MarkupMsg();
		msg.setContent(message);
		boolean success = con.sendMsg(msg);
		con.flush();
		return success;
	}
	
	private static List<InetCon> createConnections(int amount) throws IOException {
		List<InetCon> connections = new ArrayList<InetCon>();
		for (int i = 0; i < amount; i++) {
			long newId = ConnectionIdGenerator.createNext();
			Socket s = new Socket(serverAddr, serverPort);
			InetCon con = new TcpCon(s, newId);
			connections.add(con);
		}
		return connections;
	}

}
