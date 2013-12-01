package objectIO.markupMsg;

class DataParser {
	private int endOfHeader = -1;
	
	HeaderParser header;
	BodyParser body;
	
	DataParser(String input, int nestedLevels) throws InvalidFormatException{
		header = parseHeader(input);
		body = parseBody(input, nestedLevels);
	}
	
	private HeaderParser parseHeader(String input) throws InvalidFormatException{
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
		return new HeaderParser(s);
	}
	
	private BodyParser parseBody(String input, int nestedLevels) throws InvalidFormatException{
		if (endOfHeader == -1)
			return null;
		int lastIndex = input.lastIndexOf("/>");
		String s = input.substring(endOfHeader + 1, lastIndex);
		return new BodyParser(s, nestedLevels);
	}
}
