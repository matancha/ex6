package oop.ex6.main;

public class UninitializedVariableException extends ParsingException {
	@Override
	public String getMsg() {
		return "variable is uninitialized";
	}
}
