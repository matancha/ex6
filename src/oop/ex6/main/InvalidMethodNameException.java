package oop.ex6.main;

public class InvalidMethodNameException extends ParsingException {
	@Override
	public String getMsg() {
		return "Invalid method name";
	}
}
