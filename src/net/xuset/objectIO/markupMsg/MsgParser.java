package net.xuset.objectIO.markupMsg;


/**
 * Interface that outlines the operations necessary to convert a MarkupMsg object to
 * a raw form and back again. This is mainly used when a message needs to be read or
 * written to a I/O stream.
 * 
 * @author xuset
 * @since 1.0
 *
 */
public interface MsgParser {
	
	/**
	 * Takes a string and converts it into a MarkupMsg object.
	 * 
	 * @param rawInput the raw form of a MarkupMsg object
	 * @return the newly created MarkupMsg object
	 * @throws InvalidFormatException if the parser was unable to create the message.
	 */
	MarkupMsg parseFrom(String rawInput) throws InvalidFormatException;
	
	
	/**
	 * Takes a character array and converts it into a MarkupMsg object.
	 * 
	 * @param rawInput the raw form of a MarkupMsg object
	 * @return the newly created MarkupMsg object
	 * @throws InvalidFormatException if the parser was unable to create the message.
	 */
	MarkupMsg parseFrom(char[] rawInput) throws InvalidFormatException;
	
	
	/**
	 * Takes a byte array and converts it into a MarkupMsg object.
	 * 
	 * @param rawInput the raw form of a MarkupMsg object
	 * @return the newly created MarkupMsg object
	 * @throws InvalidFormatException if the parser was unable to create the message.
	 */
	MarkupMsg parseFrom(byte[] rawInput) throws InvalidFormatException;
	
	
	/**
	 * Converts a MarkupMsg object into a string that can be converted back by calling
	 * {@code parseFrom}.
	 * 
	 * @param msg the message to convert into a raw form
	 * @return the raw form of the message
	 */
	String toRawString(MarkupMsg msg);
	
	
	/**
	 * Converts a MarkupMsg object into a character array that can be converted back by 
	 * calling {@code parseFrom}.
	 * 
	 * @param msg the message to convert into a raw form
	 * @return the raw form of the message
	 */
	char[] toRawCharArray(MarkupMsg msg);
	
	
	/**
	 * Converts a MarkupMsg object into a byte array that can be converted back by calling
	 * {@code parseFrom}.
	 * 
	 * @param msg the message to convert into a raw form
	 * @return the raw form of the message
	 */
	byte[] toRawByteArray(MarkupMsg msg);
}
