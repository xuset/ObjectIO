package objectIO.markupMsg;

public class InvalidFormatException extends Exception{
	private static final long serialVersionUID = -5275131998982306219L;
	
	InvalidFormatException() {
		super("Invalid Format");
	}
}