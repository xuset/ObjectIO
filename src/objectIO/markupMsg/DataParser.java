package objectIO.markupMsg;

import java.util.List;

class DataParser {
	private int endOfHeader = -1;
	
	HeaderParser header;
	BodyParser body;
	
	DataParser(String input, int nestedLevels, List<MarkupMsg> child,
			List<MsgAttribute> msgAttributes) throws InvalidFormatException{
		header = parseHeader(input, msgAttributes);
		body = parseBody(input, nestedLevels, child);
	}
	
	private HeaderParser parseHeader(String input, List<MsgAttribute> msgAttributes) throws InvalidFormatException{
		int beginning = -1;
		int end = -1;
		
		for (int i = 0; i < input.length() && (beginning == -1 || end == -1); i++) {
			char c = input.charAt(i);
			
			if (beginning == -1 && c == '<')
				beginning = i;
			
			if (end == -1  && c == '>')
				end = i;
		}
		if (end == -1 || beginning == -1)
			throw new InvalidFormatException();
		endOfHeader = end;
		String s = input.substring(beginning + 1, end);
		return new HeaderParser(s, msgAttributes);
	}
	
	private BodyParser parseBody(String input, int nestedLevels, List<MarkupMsg> child) throws InvalidFormatException{
		if (endOfHeader == -1)
			return null;
		int lastIndex = input.lastIndexOf("/>");
		String s = input.substring(endOfHeader + 1, lastIndex);
		return new BodyParser(s, nestedLevels, child);
	}
}
