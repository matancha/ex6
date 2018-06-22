package oop.ex6.main;
import java.util.*;
import java.util.regex.*;

public class Method {
	private String name;
	private List<Variable> arguments;

	public Method(String name, List<Variable> arguments) throws ParsingException {
		this.name = name;
		this.arguments = arguments;
		if (! isNameValid(this.name)) {
			throw new InvalidMethodNameException();
		}
	}

	public List<Variable> getArguments() {
		return this.arguments;
	}

	private static boolean isNameValid(String name) {
		String methodNamePattern = "[a-zA-Z][\\w]*";
		Pattern pattern = Pattern.compile(methodNamePattern);
		Matcher matcher = pattern.matcher(name);

		return matcher.matches();
	}
}
