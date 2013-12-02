package objectIO.markupMsg;

import java.util.List;

import objectIO.markupMsg.MsgAttribute;

class HeaderParser {
	String name;
	
	HeaderParser(String input, List<MsgAttribute> msgAttributes) throws InvalidFormatException{
		String[] split = input.split(":");
		if (split.length == 0)
			name = "";
		else
			name = split[0].trim();
		
		for (int i = 1; i < split.length; i++) {
			String[] rawAttribute = split[i].split("=", 2);
			MsgAttribute at = new MsgAttribute();
			
			at.name = rawAttribute[0].trim();
			
			int firstQuote = rawAttribute[1].indexOf("\"");
			int lastQuote = rawAttribute[1].lastIndexOf("\"");
			
			at.value = rawAttribute[1].substring(firstQuote + 1, lastQuote).trim();
			
			msgAttributes.add(at);
		}
	}
}
