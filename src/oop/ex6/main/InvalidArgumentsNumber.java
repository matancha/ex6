package oop.ex6.main;

/**
 * Exception thrown if the number of arguments to Method is invalid
 */
public class InvalidArgumentsNumber extends ParsingException {
	@Override
	public String getMessage() {
		return "Invalid number of arguments supplied to method";
	}
}
