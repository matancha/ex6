package oop.ex6.main;

/**
 * Exception thrown when trying to perform a function on an undeclared variable
 */
public class UndeclaredVariableException extends ParsingException {
	@Override
	public String getMessage() {
		return "Variable is not declared";
	}
}
