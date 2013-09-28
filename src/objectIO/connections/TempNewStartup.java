package objectIO.connections;

import java.io.IOException;
import java.net.InetAddress;

import objectIO.connections.p2pServer.client.ClientHub;
import objectIO.connections.p2pServer.server.P2PServer;
import objectIO.markupMsg.MarkupMsg;

public class TempNewStartup {
	public static void main(String[] args) throws IOException {
		P2PServer server = new P2PServer(3000);
		server.accepter.start();
		String ip = InetAddress.getLocalHost().getHostAddress();
		ClientHub hub4 = new ClientHub(ip, 3000, 4l);
		ClientHub hub5 = new ClientHub(ip, 3000, 5l);
		ClientHub hub6 = new ClientHub(ip, 3000, 6l);
		
		while (hub5.connections.size() != 2) {
			try { Thread.sleep(100); } catch (Exception ex) { }
		}
		MarkupMsg msg = new MarkupMsg();
		msg.content = "it worked!";
		hub4.broadcastMsg(msg);
		//hub4.sendMsg(msg, 6l);
		//hub4.flush();
		while (hub6.getConnection(4l).msgAvailable() == false) {
			try { Thread.sleep(100); } catch (Exception ex) { }
		}
		hub4.shutdown();
		//hub6.shutdown();
		//while (hub5.getConnection(4l).msgAvailable() == false) {
		//	try { Thread.sleep(100); } catch (Exception ex) { }
		//}
		hub5.shutdown();
		server.shutdown();
	}

}
