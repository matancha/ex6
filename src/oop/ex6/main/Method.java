package oop.ex6.main;
import java.util.*;
import java.util.regex.*;

/**
 * Class representing an s-java method
 */
public class Method {
	/* Regex for valid method name */
	private static final String VALID_NAME_REGEX = "[a-zA-Z][\\w]*";
	/* Name of method */
	private final String name;
	/* Arguments supplied to method */
	private final List<Variable> arguments;

	/**
	 * Method constructor
	 * @param name Name of method
	 * @param arguments Arguments supplied to method
	 * @throws InvalidMethodNameException Thrown if method name is invalid
	 */
	public Method(String name, List<Variable> arguments) throws InvalidMethodNameException {
		this.name = name;
		this.arguments = arguments;
		if (! isNameValid(this.name)) {
			throw new InvalidMethodNameException();
		}
	}

	/**
	 * Gets arguments supplied to method
	 * @return arguments
	 */
	public List<Variable> getArguments() {
		return this.arguments;
	}

	/**
	 * Checks if method name is valid
	 * @param name method name
	 * @return true or false
	 */
	private static boolean isNameValid(String name) {
		Pattern pattern = Pattern.compile(VALID_NAME_REGEX);
		Matcher matcher = pattern.matcher(name);

		return matcher.matches();
	}

	/**
	 * Invokes the method with the supplied arguments
	 * @param callArguments Arguments supplied while invoking
	 * @throws ParsingException Thrown if illegal action was conducted
	 */
	void call(List<String> callArguments) throws ParsingException {
		if (callArguments.size() != arguments.size()) {
			throw new InvalidArgumentsNumber();
		}
		for (int i=0;i<callArguments.size();i++) {
			arguments.get(i).setValue(callArguments.get(i));
		}
	}
}
