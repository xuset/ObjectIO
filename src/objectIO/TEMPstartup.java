package objectIO;

import objectIO.markupMsg.MarkupMsg;

public class TEMPstartup {
	public static void main(String[] args) {
		MarkupMsg head = new MarkupMsg();
		MarkupMsg parent = new MarkupMsg();
		
		head.name = "";
		parent.name = "223234523453245";
		
		head.child.add(parent);
		
		for (int i = 0; i < 3; i++) {
			MarkupMsg child = new MarkupMsg();
			child.name = "child" + i;
			child.content = "content" + i;
			parent.child.add(child);
		}
		
		MarkupMsg newHead = new MarkupMsg(head.toString());
		System.out.println(head.toString());
		System.out.println(newHead.toString());
	}

}
