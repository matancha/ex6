package oop.ex6.main;

public class InvalidValueException extends ParsingException {
	@Override
	public String getMsg() {
		return "Invalid value assigned to variable";
	}
}
