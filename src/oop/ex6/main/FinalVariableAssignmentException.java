package oop.ex6.main;

public class FinalVariableAssignmentException extends ParsingException {
	@Override
	public String getMsg() {
		return "final variable can be assigned only once";
	}
}
