package oop.ex6.main;

public class UndeclaredMethodException extends ParsingException {
	@Override
	public String getMsg() {
		return "Method isn't declared";
	}
}
