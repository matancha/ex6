package oop.ex6.main;

public class IllegalLineException extends ParsingException {
	@Override
	public String getMsg() {
		return "Line does not match legal s-java format";
	}
}
