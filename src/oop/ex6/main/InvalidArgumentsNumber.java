package oop.ex6.main;

public class InvalidArgumentsNumber extends ParsingException {
	@Override
	public String getMsg() {
		return "Invalid number of arguments supplied to method";
	}
}
