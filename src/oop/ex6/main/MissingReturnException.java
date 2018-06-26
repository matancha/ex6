package oop.ex6.main;

/**
 * Exception thrown if a method does not have a return line in the appropriate place
 */
public class MissingReturnException extends ParsingException {
	@Override
	public String getMessage() {
		return "Method is missing return msg";
	}
}
