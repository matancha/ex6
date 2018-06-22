package oop.ex6.main;

public class MissingReturnException extends ParsingException {
	@Override
	public String getMsg() {
		return "Method is missing return msg";
	}
}
