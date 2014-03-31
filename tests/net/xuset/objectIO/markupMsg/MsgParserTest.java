package net.xuset.objectIO.markupMsg;

import static org.junit.Assert.*;

import org.junit.Test;

public class MsgParserTest {
	
	private MsgParser parser = new AsciiMsgParser();

	@Test
	public void testContent() throws Exception{
		MarkupMsg msg = new MarkupMsg();
		msg.setContent("test");
		MarkupMsg test = parser.parseFrom(parser.toRawString(msg));
		assertEquals("test", test.getContent());
	}
	
	@Test
	public void testAttributes() throws Exception {
		final int attribCount = 5;
		MarkupMsg msg = new MarkupMsg();
		for (int i = 0; i < attribCount; i++)
			msg.addAttribute("" + Math.random(), "" + Math.random());
		
		MarkupMsg test = parser.parseFrom(parser.toRawString(msg));
		for (int i = 0; i < attribCount; i++)
			assertEquals(msg.getAttributes().get(i).getValue(),
					test.getAttributes().get(i).getValue());
	}
	
	@Test
	public void testNesting() throws Exception {
		MarkupMsg msg = createMsgRandomNests();
		MarkupMsg test = parser.parseFrom(parser.toRawString(msg));
		assertEquals(parser.toRawString(msg), parser.toRawString(test));
		
	}
	
	private MarkupMsg createMsgRandomNests() {
		MarkupMsg base = createMsgRandom();
		
		MarkupMsg next = base;
		int layers = (int) (Math.random() * 3) + 2;
		for (int i = 0; i < layers; i++) {
			addNestedMsgs(next);
			next = next.getNestedMsgs().get(0);
		}
		return base;
	}
	
	private void addNestedMsgs(MarkupMsg msg, int count) {
		for (int i = 0; i < count; i++) {
			msg.getNestedMsgs().add(createMsgRandom());
		}
	}
	
	private void addNestedMsgs(MarkupMsg msg) {
		addNestedMsgs(msg, (int) (Math.random() * 2) + 2);
	}
	
	private MarkupMsg createMsgRandom() {
		MarkupMsg msg = new MarkupMsg();
		int attribCount = (int) (Math.random() * 5) + 2;
		for (int i = 0; i < attribCount; i++) {
			msg.addAttribute("" + Math.random(), "" + Math.random());
		}
		msg.setContent("" + Math.random());
		msg.setName("" + Math.random());
		return msg;
	}

}
