package oop.ex6.main;
import java.util.*;
import java.util.regex.*;

/**
 * Class representing variable in s-java code
 */
public class Variable {
	/* Variable types */
	private static final String[] VARIABLE_TYPES = new String[]{"int", "double", "String", "char", "boolean"};
	/* Protected keywords */
	private static final List<String> ILLEGAL_VARIABLE_NAMES = new ArrayList<String>(
			Arrays.asList("int", "double", "boolean", "char", "String", "void", "final", "if", "while", "true",
					"false", "return"));
	/* Variable name regex */
	private static final String VARIABLE_NAME_REGEX = "([a-zA-Z]|[_][\\w])[\\w]*";
	/* Variable name */
	private String name;
	/* Variable type */
	private String type;
	/* Variable value */
	private String value;
	/* Flag indicating whether a variable is final */
	private boolean isFinal;

	/**
	 * Constructor
	 * @param type variable type
	 * @param name variable name
	 * @param isFinal final variable flag
	 * @throws InvalidVariableNameException thrown if variable name is invalid
	 */
	public Variable(String type, String name, boolean isFinal) throws InvalidVariableNameException {
		if (! isNameValid(name)) {
			throw new InvalidVariableNameException();
		}
		this.type = type;
		this.name = name;
		this.isFinal = isFinal;
	}

	/**
	 * Copy constructor
	 * @param copiedVariable variable object to copy
	 */
	public Variable(Variable copiedVariable) {
		this.type = copiedVariable.getType();
		this.name = copiedVariable.getName();
		this.isFinal = copiedVariable.getIsFinal();
		this.value = copiedVariable.getValue();
	}

	/**
	 * Checks whether variable name is valid
	 * @param name variable name
	 * @return true - if answer is positive
	 */
	public static boolean isNameValid(String name) {
		if (ILLEGAL_VARIABLE_NAMES.contains(name)) {
			return false;
		}
		Pattern pattern = Pattern.compile(VARIABLE_NAME_REGEX);
		Matcher matcher = pattern.matcher(name);

		return matcher.matches();
	}

	/**
	 * Get variable types regex
	 * @return variable type regex
	 */
	public static String getVariableTypesRegex() {
		return String.join("|", VARIABLE_TYPES);
	}

	/**
	 * Gets variable name
	 * @return name
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * Gets variable type
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets is variable is final
	 * @return true - if final
	 */
	public boolean getIsFinal() {
		return isFinal;
	}

	/**
	 * Gets value of variable
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set variable value
	 * @param value new value of variable
	 * @throws ParsingException thrown if illegal action was conducted
	 */
	public void setValue(String value) throws ParsingException {
		if (! isValueValid(value)) {
			throw new InvalidValueException();
		} else if (isFinal && this.value != null) {
			throw new FinalVariableAssignmentException();
		}
		this.value = value;
	}

	/**
	 * Returns whether variable value is valid
	 * @param value value to check
	 * @return true - if valid
	 * @throws InvalidValueException thrown if value is invalid
	 */
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
				pattern = Pattern.compile("\"[\\s\\S]*\"");
				break;
			case "char":
				pattern = Pattern.compile("'\\S?'");
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

	/**
	 * Assigns default value to variable according to type
	 * @throws ParsingException thrown if illegal action was conducted
	 */
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

	/**
	 * Copy variable obj value
	 * @param copiedVariable variable to copy
	 * @throws ParsingException thrown if illegal action was conducted
	 */
	public void copyVariableValue(Variable copiedVariable) throws ParsingException {
		String copiedValue = copiedVariable.getValue();
		if (copiedValue == null) {
			throw new UninitializedVariableException();
		}
		setValue(copiedValue);
	}
}
