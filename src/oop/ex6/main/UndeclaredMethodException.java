package oop.ex6.main;

/**
 * Exception thrown when trying to perform a function on an undeclared method
 */
public class UndeclaredMethodException extends ParsingException {
	@Override
	public String getMessage() {
		return "Method isn't declared";
	}
}
