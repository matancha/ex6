package oop.ex6.main;

public class InvalidVariableNameException extends ParsingException {
	public String getMsg(){
		return "Variable name is illegal";
	}
}
