package oop.ex6.main;

public class InvalidMethodNameException extends ParsingException {
	@Override
	public String getMessage() {
		return "Invalid method name";
	}
}
