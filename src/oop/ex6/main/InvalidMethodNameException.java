package oop.ex6.main;

/**
 * Exception thrown if a method name supplied is invalid
 */
public class InvalidMethodNameException extends ParsingException {
	@Override
	public String getMessage() {
		return "Invalid method name";
	}
}
