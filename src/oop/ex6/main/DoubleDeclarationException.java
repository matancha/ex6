package oop.ex6.main;

public class DoubleDeclarationException extends ParsingException {
	@Override
	public String getMsg() {
		return "A variable cannot be declared twice";
	}
}
