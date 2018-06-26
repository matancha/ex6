package oop.ex6.main;

public class InvalidVariableNameException extends ParsingException {
	@Override
	public String getMessage(){
		return "Variable name is illegal";
	}
}
