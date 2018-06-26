package oop.ex6.main;

/**
 * Exception thrown if a line does not match any legal s-java code line
 */
public class IllegalLineException extends ParsingException {
	@Override
	public String getMessage() {
		return "Line does not match legal s-java format";
	}
}
