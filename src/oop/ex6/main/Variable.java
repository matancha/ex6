package oop.ex6.main;
import java.util.regex.*;

public class Variable {
	private String name;
	private String type;

	public Variable(String type, String name) throws ParsingException {
		if (! isNameValid(name)) {
			throw new InvalidVariableNameException();
		}
		this.type = type;
		this.name = name;
	}

	public boolean isNameValid(String name) {
		String variableNamePattern = "([a-zA-Z]|[_][\\w])[\\w]*";
		Pattern pattern = Pattern.compile(variableNamePattern);
		Matcher matcher = pattern.matcher(name);

		return matcher.matches();
	}
}
