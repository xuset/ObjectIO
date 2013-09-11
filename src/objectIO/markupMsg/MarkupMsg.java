package objectIO.markupMsg;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MarkupMsg{
	
	protected boolean goodFormat = true;

	public List<MsgAttribute> attribute;
	public List<MarkupMsg> child;
	public String content = "";
	public String name;
	
	public boolean hasChild() { return !child.isEmpty(); }
	public boolean parsedProperly() { return goodFormat; }
	
	public MarkupMsg() {
		attribute = new LinkedList<MsgAttribute>();
		child = new LinkedList<MarkupMsg>();
	}
	
	public MarkupMsg(String toParse) {
		parse(toParse);
	}
	
	protected void parse(String input) {
		try {
			DataParser parser = new DataParser(input);
			name = parser.header.name;
			attribute = parser.header.msgAttributes;
			content = parser.body.content;
			child = parser.body.child;
		} catch (InvalidFormatException ex) {
			goodFormat = false;
			ex.printStackTrace();
		}
	}
	
	public MarkupMsg getChild(String name) {
		for (MarkupMsg d : child) {
			if (d.name.equals(name))
				return d;
		}
		return null;
	}
	
	/*public String getAttribute(String name) {
		for (MsgAttribute a : attribute) {
			if (a.name.equals(name))
				return a.value;
		}
		return null;
	}*/
	
	public MsgAttribute getAttribute(String name) {
		for (MsgAttribute a : attribute) {
			if (a.name.equals(name))
				return a;
		}
		return null;
	}
	
	
	public MsgAttribute setAttribute(String name, String value) {
		MsgAttribute a = getAttribute(name);
		if (a == null) {
			MsgAttribute aa = new MsgAttribute(name, value);
			attribute.add(aa);
			return aa;
		}
		a.value = value;
		return a;
	}
	/*public MsgAttribute addAttribute(String name, String value) {
		MsgAttribute att = new MsgAttribute(name, value);
		return attribute.add();
	}*/
	
	public boolean addAttribute(MsgAttribute attr) {
		return attribute.add(attr);
	}
	
	public boolean addAttribute(String name, String value) {
		return attribute.add(new MsgAttribute(name, value));
	}
	
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("<").append(name).append(":");
		Iterator<MsgAttribute> aIterator = attribute.iterator();
		while (aIterator.hasNext()) {
			MsgAttribute a = aIterator.next();
			builder.append(a.name).append("=\"").append(a.value).append("\"");
			if (aIterator.hasNext())
				builder.append(":");
		}
		builder.append(">").append(content);
		for (MarkupMsg d : child) {
			builder.append(d.toString());
		}
		builder.append("/>");
		return builder.toString();
	}
	
	public MarkupMsg clone() {
		MarkupMsg d = new MarkupMsg(this.toString());
		return d;
	}
	
	public void clear() {
		attribute.clear();
		child.clear();
		name = "";
		content = "";
		goodFormat = true;
	}
}
