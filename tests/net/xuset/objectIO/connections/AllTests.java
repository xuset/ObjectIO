package net.xuset.objectIO.connections;

import net.xuset.objectIO.connections.sockets.p2pServer.GroupNetServerClientTest;
import net.xuset.objectIO.connections.sockets.tcp.TcpServerClientTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	FileConTest.class,
	StreamConTest.class,
	GroupNetServerClientTest.class,
	TcpServerClientTest.class
	})
public class AllTests {

}
