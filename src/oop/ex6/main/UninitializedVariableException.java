package oop.ex6.main;

/**
 * Exception thrown when trying to perform an illegal action with an uninitialized
 * variable
 */
public class UninitializedVariableException extends ParsingException {
	@Override
	public String getMessage() {
		return "variable is uninitialized";
	}
}
