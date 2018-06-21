package oop.ex6.main;
import java.util.regex.*;

public class Variable {
	private String name;
	private String type;
	private String value;

	public Variable(String type, String name) throws ParsingException {
		if (! isNameValid(name)) {
			throw new InvalidVariableNameException();
		}
		this.type = type;
		this.name = name;
	}

	public static boolean isNameValid(String name) {
		String variableNamePattern = "([a-zA-Z]|[_][\\w])[\\w]*";
		Pattern pattern = Pattern.compile(variableNamePattern);
		Matcher matcher = pattern.matcher(name);

		return matcher.matches();
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) throws InvalidValueException {
		if (! isValueValid(value)) {
			System.out.println(value);
			throw new InvalidValueException();
		}
		this.value = value;
	}

	private boolean isValueValid(String value) {
		Pattern pattern = null;
		Matcher matcher;
		switch(type) {
			case "int":
				pattern = Pattern.compile("-?\\d+");
				break;
			case "double":
				pattern = Pattern.compile("-?(\\d+[.])?\\d+");
				break;
			case "String":
				pattern = Pattern.compile("\"\\S+\"");
				break;
			case "char":
				pattern = Pattern.compile("'\\S+'");
				break;
			case "boolean":
				pattern = Pattern.compile("(-?(\\d+[.])?\\d+|true|false)");
				break;
		}
		matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public void copyVariableValue(Variable copiedVariable) throws InvalidValueException {
		String copiedValue = copiedVariable.getValue();
		setValue(copiedValue);
	}
}
