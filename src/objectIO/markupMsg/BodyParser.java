package objectIO.markupMsg;

import java.util.List;

class BodyParser {
	String content = "";
	
	BodyParser(String input, int nestedLevels, List<MarkupMsg> child) throws InvalidFormatException {
		int firstTag = input.indexOf("<");
		if (firstTag != -1 && nestedLevels > 0)
			content = input.substring(0, firstTag).trim();
		else 
			content = input;
		
		if (nestedLevels <= 0)
			return;
		
		int openTags = 0;
		int openIndex = -1;
		
		int closedTags = 0;
		int closedIndex = -1;
		
		int currentIndex = -1;
		boolean noMoreTags = false;
		boolean findingClosingTag = false;
		
		while (currentIndex < input.length() && noMoreTags == false) {
			NextTag tag = new NextTag(input, currentIndex + 1);
			
			if (tag.type == NextTag.tag.open) {
				openTags++;
				if (findingClosingTag == false) {
					findingClosingTag = true;
					openIndex = tag.index;
				}
			} else if (tag.type == NextTag.tag.closed) {
				closedTags++;
				closedIndex = tag.index;
			} else {
				noMoreTags = true;
			}

			currentIndex = tag.index;
			
			if (openTags != 0 && openTags == closedTags && noMoreTags == false) {
				String newChildString = input.substring(openIndex, closedIndex + 2).trim();
				child.add(new MarkupMsg(newChildString, nestedLevels - 1));
				findingClosingTag = false;
			}
		}
		if (openTags != closedTags) {
			throw new InvalidFormatException();
		}
		
		
	}
	
	private static class NextTag {
		enum tag {
			open, closed, none;
		}
		
		tag type;
		int index;
		
		NextTag(String input, int startingIndex) {
			if (startingIndex < input.length()) {
				int open = input.indexOf("<", startingIndex);
				int closed = input.indexOf("/>", startingIndex);
				if (open != -1 && open < closed) {
					type = tag.open;
					index = open;
				} else if (closed != -1) {
					type = tag.closed;
					index = closed;
				} else {
					type = tag.none;
					index = -1;
				}
			} else {
				type = tag.none;
				index = -1;
			}
		}
		
	}
}
