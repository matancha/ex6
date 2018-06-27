package oop.ex6.main;

/**
 * Exception thrown if a variable name supplied is invalid
 */
public class InvalidVariableNameException extends ParsingException {
	@Override
	public String getMessage(){
		return "Variable name is illegal";
	}
}
