package net.xuset.objectIO.connections;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.xuset.objectIO.markupMsg.MarkupMsg;
import net.xuset.objectIO.markupMsg.MsgParser;
import net.xuset.objectIO.util.ConnectionIdGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileConTest {
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	private File file;
	private StreamCon fileCon;
	private MsgParser parser;

	@Before
	public void setUp() throws Exception {
		file = tempFolder.newFile();
		fileCon = new FileCon(file, ConnectionIdGenerator.createNext());
		parser = fileCon.getParser();
	}

	@After
	public void tearDown() throws Exception {
		fileCon.close();
	}

	@Test
	public void testSendAndPollMsgDifferentCon() throws FileNotFoundException, IOException {
		FileCon conReader = new FileCon(file, ConnectionIdGenerator.createNext());
		testFileConSendAndPoll(fileCon, conReader);
		conReader.close();
	}
	
	@Test
	public void testSendAndPollMsgSameCon() {
		testFileConSendAndPoll(fileCon, fileCon);
	}
	
	@Test
	public void testSendMsg() throws IOException {
		List<MarkupMsg> testMessages = createTestMessages();
		
		for (MarkupMsg msg : testMessages)
			assertTrue(fileCon.sendMsg(msg));
		fileCon.flush();
		
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		
		for (int i = 0; i < testMessages.size(); i++) {
			String line = reader.readLine();
			assertNotNull(line);
			String testMsgString = parser.toRawString(testMessages.get(i));
			assertEquals(testMsgString, line);
		}
		
		reader.close();
	}
	
	@Test
	public void testPollMsg() throws IOException {
		List<MarkupMsg> testMessages = createTestMessages();
		BufferedWriter writer = new BufferedWriter( new FileWriter(file));
		
		for (MarkupMsg m : testMessages)
			writer.write(parser.toRawString(m) + fileCon.getMessageDelimeter());
		writer.flush();
		writer.close();
		
		//fileCon.readAllLines();
		for (int i = 0; i < testMessages.size(); i++) {
			MarkupMsg testMsg = testMessages.get(i);
			assertTrue(fileCon.isMsgAvailable());
			assertMsgEquals(testMsg, fileCon.pollNextMsg());
		}
	}
	
	@Test
	public void testConClose() {
		assertFalse(fileCon.isClosed());
		fileCon.close();
		assertTrue(fileCon.isClosed());
	}
	
	private void testFileConSendAndPoll(StreamConI conSender,
			StreamConI conReceiver) {
		
		List<MarkupMsg> testMessages = createTestMessages();
		
		for (MarkupMsg msg : testMessages)
			assertTrue(conSender.sendMsg(msg));
		conSender.flush();
		
		//conReceiver.readAllLines();
		for (int i = 0; i < testMessages.size(); i++) {
			assertTrue(conReceiver.isMsgAvailable());
			MarkupMsg readMsg = conReceiver.pollNextMsg();
			assertMsgEquals(testMessages.get(i), readMsg);
		}
	}
	
	private static List<MarkupMsg> createTestMessages() {
		String[] msgContent = { "first1", "test message2", "last3" };
		ArrayList<MarkupMsg> messages = new ArrayList<MarkupMsg>();
		for (int i = 0; i < msgContent.length; i++) {
			MarkupMsg m = new MarkupMsg();
			m.setContent(msgContent[i]);
			messages.add(m);
		}
		return messages;
	}
	
	private void assertMsgEquals(MarkupMsg expected, MarkupMsg actual) {
		assertEquals(parser.toRawString(expected), parser.toRawString(actual));
	}

}
