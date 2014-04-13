package net.xuset.objectIO.connections.sockets.p2pServer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.xuset.objectIO.connections.Connection;
import net.xuset.objectIO.connections.sockets.InetCon;
import net.xuset.objectIO.connections.sockets.InetHub;
import net.xuset.objectIO.connections.sockets.groupNet.client.GroupClientHub;
import net.xuset.objectIO.connections.sockets.groupNet.server.GroupNetServer;
import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgParserTest;
import net.xuset.objectIO.util.ConnectionIdGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GroupNetServerClientTest {
	private static final int maxClients = 4; //minimum of 2
	private static final String serverIp = "127.0.0.1";
	private static final int serverPort = 3000;
	
	private InetHub<?> server;
	private List<InetHub<?>> clients = new ArrayList<InetHub<?>>();
	
	@Before
	public void beforeTest() throws Exception {
		assertTrue(maxClients > 1);
		server = new GroupNetServer(3L, 3000);
		for (int i = 0; i < maxClients; i++)
			clients.add(new GroupClientHub(serverIp, serverPort,
					ConnectionIdGenerator.createNext()));
		
		
		Thread.sleep(100);
		assertConnectionCountEquals(maxClients);
	}
	
	@After
	public void afterTest() throws Exception{
		assertEmptyMsgQueue();
		
		

		server.shutdown();
		Thread.sleep(100);
		assertEquals(server.getConnectionCount(), 0);
	}
	
	@Test
	public void testCloseConnectionFromClient() throws Exception {
		assertConnectionCountEquals(maxClients);
		
		while (!clients.isEmpty()) {
			InetHub<?> client = clients.remove(0);
			client.shutdown();
			
			Thread.sleep(200);
			assertConnectionCountEquals(clients.size());
		}
	}
	
	@Test
	public void testClientToClient() {
		InetHub<?> sender = clients.get(0);
		InetHub<?> reciever = clients.get(1);
		sender.sendMsg(createMsgWithContent(0), reciever.getLocalId());
		sender.getConnectionById(reciever.getLocalId()).flush();
		
		final long maxWait = 100L;
		long timeStarted = System.currentTimeMillis();
		MarkupMsg received = null;
		InetCon receiverCon = reciever.getConnectionById(sender.getLocalId());
		while (received == null && System.currentTimeMillis() - timeStarted < maxWait) {
			if (receiverCon.isMsgAvailable())
				received = receiverCon.pollNextMsg();
		}
		
		assertNotNull(received);
		assertMsgContentEquals(received, 0);
		assertEmptyMsgQueue();
	}
	
	@Test
	public void testLoad() {
		final int testMessages = 100;
		final long maxTimeToWait = 5 * 1000L; //in milliseconds
		final MarkupMsg testMsg = MsgParserTest.createMsgRandom();
		MsgParserTest.addNestedMsgs(testMsg, 100);
		InetHub<?> sender = clients.get(0);
		InetHub<?> reciever = clients.get(1);
		
		long time = System.currentTimeMillis();
		for (int i = 0; i < testMessages; i++)
			sender.sendMsg(testMsg, reciever.getLocalId());
		sender.getConnectionById(reciever.getLocalId()).flush();
		
		time = System.currentTimeMillis() - time;
		System.out.println("---Sent " + testMessages + " in " + (time / 1000.0) + "s");
		time = System.currentTimeMillis();
		
		InetCon recCon = reciever.getConnectionById(sender.getLocalId());
		int receivedCount = 0;
		while(receivedCount < testMessages &&
				System.currentTimeMillis() - time <= maxTimeToWait) {
			
			if (recCon.isMsgAvailable()) {
				recCon.pollNextMsg();
				receivedCount++;
			} else {
				Thread.yield();
			}
		}
		assertEquals(testMessages, receivedCount);
		
		time = System.currentTimeMillis() - time;
		System.out.println("---Received " + testMessages + " in " + (time / 1000.0) + "s");
	
		
		assertEmptyMsgQueue();
	}
	
	@Test
	public void testClientToBroadcast() {
		InetHub<?> sender = clients.get(0);
		sender.broadcastMsg(createMsgWithContent(0));
		sender.sendMsg(createMsgWithContent(1), Connection.BROADCAST_CONNECTION);
		sender.getConnectionById(Connection.BROADCAST_CONNECTION).flush();
		
		try { Thread.sleep(100); } catch (InterruptedException ex) { }
		
		for (InetHub<?> c : clients) {
			if (c == sender)
				continue;
			
			InetCon con = c.getConnectionById(sender.getLocalId());
			assertTrue(con.isMsgAvailable());
			assertMsgContentEquals(con.pollNextMsg(), 0);
			assertTrue(con.isMsgAvailable());
			assertMsgContentEquals(con.pollNextMsg(), 1);
		}
	}
	
	private void assertConnectionCountEquals(int count) {
		assertEquals(count, server.getConnectionCount());
		for (InetHub<?> c : clients)
			assertEquals(count, c.getConnectionCount());
	}
	
	private static void assertMsgContentEquals(MarkupMsg msg, int i) {
		assertTrue(msg.getContent().equals("" + i));
	}
	
	private void assertEmptyMsgQueue() {
		
		for (int i = 0; i < server.getConnectionCount(); i++) {
			InetCon con = server.getConnectionByIndex(i);
			assertFalse(con.isMsgAvailable());
			
		}
		
		for (InetHub<?> hub : clients) {
			for (int i = 0; i < hub.getConnectionCount(); i++) {
				InetCon con = hub.getConnectionByIndex(i);
				assertFalse(con.isMsgAvailable());
			}
		}
	}
	
	private static MarkupMsg createMsgWithContent(int i) {
		MarkupMsg msg = new MarkupMsg();
		msg.setContent(""  + i);
		return msg;
	}

}
