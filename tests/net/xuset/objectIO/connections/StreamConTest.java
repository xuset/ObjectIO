package net.xuset.objectIO.connections;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

public class StreamConTest {
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	private File tempFile;
	private List<MarkupMsg> testMsgs;
	private StreamCon streamCon;
	private MsgParser parser;

	@Before
	public void setUp() throws Exception {
		testMsgs = createTestMsgs();
		tempFile = tempFolder.newFile();
		
		streamCon = new StreamCon(
				new FileInputStream(tempFile),
				new FileOutputStream(tempFile, true),
				ConnectionIdGenerator.createNext());
		parser = streamCon.getParser();
	}

	@After
	public void tearDown() throws Exception {
		if (!streamCon.isClosed())
			streamCon.close();
		tempFile.delete();
	}

	@Test
	public void testSendMsg() throws IOException, InterruptedException {
		for (MarkupMsg msg : testMsgs)
			assertTrue(streamCon.sendMsg(msg));
		streamCon.flush();
		
		Thread.sleep(100);
		
		BufferedReader fileIn = new BufferedReader(new FileReader(tempFile));
		for (MarkupMsg msg : testMsgs) {
			String line = fileIn.readLine();
			assertEquals(parser.toRawString(msg), line);
		}
		fileIn.close();
	}
	
	@Test
	public void testPollMsg() throws IOException, InterruptedException {
		FileWriter writer = new FileWriter(tempFile, true);
		for (MarkupMsg msg : testMsgs) {
			writer.write(parser.toRawString(msg));
			writer.write(streamCon.getMessageDelimeter());
		}
		writer.flush();
		writer.close();
		
		Thread.sleep(100);
		
		for (MarkupMsg msg : testMsgs) {
			assertTrue(streamCon.isMsgAvailable());
			assertEquals(parser.toRawString(msg),
					parser.toRawString(streamCon.pollNextMsg()));
		}
		assertFalse(streamCon.isMsgAvailable());
	}
	
	@Test
	public void testClose() {
		assertFalse(streamCon.isClosed());
		streamCon.close();
		assertTrue(streamCon.isClosed());
	}
	
	private List<MarkupMsg> createTestMsgs() {
		String[] msgContent = { "uno", "dos", "tres" };
		ArrayList<MarkupMsg> msg = new ArrayList<MarkupMsg>();
		for (String s : msgContent) {
			MarkupMsg m = new MarkupMsg();
			m.setContent(s);
			msg.add(m);
		}
		return msg;
		
	}
}
