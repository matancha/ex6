package oop.ex6.main;
import java.util.regex.*;

public class Variable {
	private String name;
	private String type;
	private String value;
	private boolean isFinal;

	public Variable(String type, String name, boolean isFinal) throws ParsingException {
		this.type = type;
		this.name = name;
		this.isFinal = isFinal;
		if (! isNameValid(this.name)) {
			throw new InvalidVariableNameException();
		}
	}

	public static boolean isNameValid(String name) {
		String variableNamePattern = "([a-zA-Z]|[_][\\w])[\\w]*";
		Pattern pattern = Pattern.compile(variableNamePattern);
		Matcher matcher = pattern.matcher(name);

		return matcher.matches();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) throws ParsingException {
		if (! isValueValid(value)) {
			throw new InvalidValueException();
		} else if (isFinal && value != null) {
			throw new FinalVariableAssignmentException();
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

	public void copyVariableValue(Variable copiedVariable) throws ParsingException {
		String copiedValue = copiedVariable.getValue();
		setValue(copiedValue);
	}
}
