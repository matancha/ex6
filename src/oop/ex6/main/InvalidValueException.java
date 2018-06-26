package oop.ex6.main;

/**
 * Exception thrown if a value supplied is invalid
 */
public class InvalidValueException extends ParsingException {
	@Override
	public String getMessage() {
		return "Invalid value assigned to variable";
	}
}
