package net.xuset.objectIO.markupMsg;

/**
 * Exception thrown when parsing a MarkupMsg fails
 * 
 * @author xuset
 * @since 1.0
 *
 */
public class InvalidFormatException extends Exception{
	private static final long serialVersionUID = -5275131998982306219L;
	
	/**
	 * Instantiates a new exception with the given throwable and the message 'Invalid
	 * format'
	 * 
	 * @param throwable the cause of the exception
	 */
	public InvalidFormatException(Throwable throwable) {
		super("Invalid format", throwable);
	}
	
	/**
	 * Instantiates a new exception with the given message 'Invalid format'.
	 */
	public InvalidFormatException() {
		super("Invalid format");
	}
	
	/**
	 * Instantiates a new exception with the message 'Invalid format: ' appended by
	 * the {@code msg} argument.
	 * 
	 * @param msg String to be append to the Exception's message
	 */
	public InvalidFormatException(String msg) {
		super("Invalid format: " + msg);
	}
}
