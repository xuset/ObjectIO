package net.xuset.objectIO.markupMsg;

public class AsciiMsgParser implements MsgParser {
	private static final String magicNumber = "345";
	private static final char unitSep = 31;
	private static final char recordSep = 30;
	private static final String unitSepStr = new String(new char[] {unitSep});
	
	private final boolean parseNested;
	
	/**
	 * Constructs an AsciiMsgParser.
	 * This parser parses the nested messages part of messages.
	 */
	public AsciiMsgParser() {
		this(true);
	}
	
	/**
	 * Creates an AsciiMsgParser.
	 * 
	 * @param parseNested if {@code false} the nested messages part of a message will not
	 * 			be parsed. If {@code true}, nested messages will be parsed.
	 */
	public AsciiMsgParser(boolean parseNested) {
		this.parseNested = parseNested;
	}

	@Override
	public MarkupMsg parseFrom(String rawInput) throws InvalidFormatException {
		return createFromStringSafe(rawInput, parseNested);
	}

	@Override
	public MarkupMsg parseFrom(char[] rawInput) throws InvalidFormatException {
		return parseFrom(new String(rawInput));
	}

	@Override
	public MarkupMsg parseFrom(byte[] rawInput) throws InvalidFormatException {
		return parseFrom(new String(rawInput));
	}

	@Override
	public String toRawString(MarkupMsg msg) {
		return createToString(msg);
	}

	@Override
	public char[] toRawCharArray(MarkupMsg msg) {
		return toRawString(msg).toCharArray();
	}

	@Override
	public byte[] toRawByteArray(MarkupMsg msg) {
		return toRawString(msg).getBytes();
	}
	
	private static String createToString(MarkupMsg msg) {
		return createToStringBuilder(msg).toString();
	}
	
	protected static StringBuilder createToStringBuilder(MarkupMsg msg) {
		StringBuilder builder = new StringBuilder();
		StringBuilder[] nestedBuilders = new StringBuilder[msg.getNestedMsgs().size()];
		
		for (int i = 0; i < nestedBuilders.length; i++)
			nestedBuilders[i] = createToStringBuilder(msg.getNestedMsgs().get(i));
		
		builder.append(magicNumber).append(unitSep);
		builder.append(msg.getAttributes().size()).append(unitSep);
		builder.append(msg.getNestedMsgs().size()).append(unitSep);
		for (StringBuilder sb : nestedBuilders)
			builder.append(sb.length()).append(unitSep);
		builder.append(msg.getName()).append(unitSep);
		builder.append(msg.getContent()).append(unitSep);
		for (MsgAttribute attrib : msg.getAttributes()) {
			builder.append(attrib.getName()).append(unitSep);
			builder.append(attrib.getValue()).append(unitSep);
		}
		for (StringBuilder sb : nestedBuilders)
			builder.append(sb);
		builder.append(recordSep);
		
		return builder;
	}
	
	private static void checkMagicNumber(String rawInput) throws InvalidFormatException{
		int index = rawInput.indexOf(unitSep);
		
		if (index == -1)
			throw new InvalidFormatException();
		
		if (index != magicNumber.length())
			throw new InvalidFormatException();
		
		String sub = rawInput.substring(0, index);
		if (!sub.equals(magicNumber))
			throw new InvalidFormatException();
	}
	
	private static int getNthIndexOf(char searchFor, int nthOccurence, String rawInput) {
		int occurences = 0;
		for (int i = 0; i < rawInput.length(); i++) {
			if (rawInput.charAt(i) == searchFor)
				occurences++;
			if (occurences == nthOccurence)
				return i;
		}
		return -1;
	}
	
	private static int getIntAtUnitSepOffset(String rawInput, int offset)
			throws InvalidFormatException {
		
		int firstIndex = getNthIndexOf(unitSep, offset, rawInput);
		int lastIndex = getNthIndexOf(unitSep, offset + 1, rawInput);
		
		if (firstIndex == -1 || lastIndex == -1)
			throw new InvalidFormatException();
		
		String sub = rawInput.substring(firstIndex + 1, lastIndex);
		return Integer.parseInt(sub);
	}
	
	private static void setAttributes(MarkupMsg msg, String[] parts, int attribCount,
			int attribIndex) {
		
		for (int i = 0; i < 2 * attribCount; i += 2) {
			MsgAttribute attrib = new MsgAttribute(parts[attribIndex + i]);
			attrib.set(parts[attribIndex + i + 1]);
			msg.getAttributes().add(attrib);
		}
	}
	
	private static void addNestedMsgs(MarkupMsg parent, String[] parts, String rawInput,
			int nestedIndex, int nestedCount, int maxParts)
			throws InvalidFormatException {
		
		int currentIndex = getNthIndexOf(unitSep, maxParts - 1, rawInput) + 1;
		for (int i = 0; i < nestedCount; i++) {
			int nestMsgSize = Integer.parseInt(parts[nestedIndex + i]);
			String subRaw = rawInput.substring(currentIndex, currentIndex + nestMsgSize);
			parent.addNested(createFromString(subRaw, true));
			currentIndex += nestMsgSize;
		}
	}
	
	private static MarkupMsg createFromString(String rawInput, boolean parseNested)
			throws InvalidFormatException {
		
		checkMagicNumber(rawInput);
		final int attribCount = getIntAtUnitSepOffset(rawInput, 1);
		final int nestedCount = getIntAtUnitSepOffset(rawInput, 2);
		final int nestedIndex = 3;
		final int nameIndex = nestedIndex + nestedCount;
		final int contentIndex = nameIndex + 1;
		final int attribIndex = contentIndex + 1;
		final int maxParts = attribIndex + 2 * attribCount + 1;
		
		MarkupMsg msg = new MarkupMsg();
		
		String[] parts = rawInput.split(unitSepStr, maxParts);
		
		msg.setName(parts[nameIndex]);
		msg.setContent(parts[contentIndex]);
		
		setAttributes(msg, parts, attribCount, attribIndex);
		
		if (parseNested)
			addNestedMsgs(msg, parts, rawInput, nestedIndex, nestedCount, maxParts);
		
		return msg;
	}
	
	private static MarkupMsg createFromStringSafe(String rawInput, boolean parseNested)
			throws InvalidFormatException {
		
		try {
			return createFromString(rawInput, parseNested);
		} catch (IndexOutOfBoundsException ex) {
			throw new InvalidFormatException(ex);
		} catch (NullPointerException ex) {
			throw new InvalidFormatException(ex);
		}
	}

}
