package oop.ex6.main;
import java.util.*;
import java.util.regex.*;

public class Variable {
	private static final String[] VARIABLE_TYPES = new String[]{"int", "double", "String", "char", "boolean"};
	private static final List<String> ILLEGAL_VARIABLE_NAMES = new ArrayList<String>(
			Arrays.asList("int", "double", "boolean", "char", "String", "void", "final", "if", "while", "true",
					"false", "return"));
	private String name;
	private String type;
	private String value;
	private boolean isFinal;

	public Variable(String type, String name, boolean isFinal) throws ParsingException {
		if (! isNameValid(name)) {
			throw new InvalidVariableNameException();
		}
		this.type = type;
		this.name = name;
		this.isFinal = isFinal;
	}

	public Variable(Variable copiedVariable) throws ParsingException {
		this.type = copiedVariable.getType();
		this.name = copiedVariable.getName();
		this.isFinal = copiedVariable.getIsFinal();
		this.value = copiedVariable.getValue();
	}

	public String getType() {
		return type;
	}

	public boolean getIsFinal() {
		return isFinal;
	}

	public static boolean isNameValid(String name) {
		if (ILLEGAL_VARIABLE_NAMES.contains(name)) {
			return false;
		}
		String variableNamePattern = "([a-zA-Z]|[_][\\w])[\\w]*";
		Pattern pattern = Pattern.compile(variableNamePattern);
		Matcher matcher = pattern.matcher(name);

		return matcher.matches();
	}

	public String getName(){
		return this.name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) throws ParsingException {
		if (! isValueValid(value)) {
			throw new InvalidValueException();
		} else if (isFinal && this.value != null) {
			throw new FinalVariableAssignmentException();
		}
		this.value = value;
	}

	private boolean isValueValid(String value) throws InvalidValueException {
		Pattern pattern;
		switch(type) {
			case "int":
				pattern = Pattern.compile("-?\\d+");
				break;
			case "double":
				pattern = Pattern.compile("-?(\\d+[.])?\\d+");
				break;
			case "String":
				pattern = Pattern.compile("\"(\\S)*\"");
				break;
			case "char":
				pattern = Pattern.compile("'(\\S)*'");
				break;
			case "boolean":
				pattern = Pattern.compile("(-?(\\d+[.])?\\d+|true|false)");
				break;
			default:
				throw new InvalidValueException();
		}
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public void copyVariableValue(Variable copiedVariable) throws ParsingException {
		String copiedValue = copiedVariable.getValue();
		setValue(copiedValue);
	}

	public static String getVariableTypesRegex() {
		return String.join("|", VARIABLE_TYPES);
	}

	public void setDefaultValue() throws ParsingException {
		switch(type) {
			case "int":
				setValue("0");
				break;
			case "double":
				setValue("0.0");
				break;
			case "String":
				setValue("\"\"");
				break;
			case "char":
				setValue("''");
				break;
			case "boolean":
				setValue("true");
				break;
			default:
				throw new InvalidValueException();
		}
	}
}
