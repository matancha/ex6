package oop.ex6.main;

/**
 * Exception thrown if a variable is declared twice
 */
public class DoubleDeclarationException extends ParsingException {
	@Override
	public String getMessage() {
		return "A variable/method cannot be declared twice";
	}
}
