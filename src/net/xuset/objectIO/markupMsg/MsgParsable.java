package net.xuset.objectIO.markupMsg;


/**
 * Interface that should be used if something parses or converts a MarkupMsg to it's
 * raw form.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public interface MsgParsable {
	
	/**
	 * Sets the parser that should be used by the implementation to parse the raw input
	 * into a  MarkupMsg object and to convert a MarkupMsg object to it's raw form.
	 * 
	 * @param parser the parser to use
	 */
	void setParser(MsgParser parser);
	
	
	/**
	 * Returns the parser that is being used by the implementation
	 * 
	 * @return the parser that is being used to parse input into MarkupMsg objects.
	 */
	MsgParser getParser();
}
