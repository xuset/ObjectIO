package net.xuset.objectIO.connections;

import net.xuset.objectIO.connections.sockets.p2pServer.P2PServerClientTest;
import net.xuset.objectIO.connections.sockets.tcp.TcpServerClientTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	FileConTest.class,
	StreamConTest.class,
	P2PServerClientTest.class,
	TcpServerClientTest.class
	})
public class AllTests {

}
