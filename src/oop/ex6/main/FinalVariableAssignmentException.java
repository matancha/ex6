package oop.ex6.main;

/**
 * Exception thrown if a final variable is assigned a value when the action is not allowed
 */
public class FinalVariableAssignmentException extends ParsingException {
	@Override
	public String getMessage() {
		return "final variable can be assigned only once";
	}
}
