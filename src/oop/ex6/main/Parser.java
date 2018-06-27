package oop.ex6.main;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Class handling the main code parsing
 */
public class Parser {
	/** Lines **/
	/* Comment line regex */
	private static final Pattern COMMENT_LINE = Pattern.compile("^[\\/][\\/]");
	/* Only whitespace line  regex */
	private static final Pattern WHITESPACE_LINE = Pattern.compile("\\s*");
	/* Variable declaration line regex */
	private static final Pattern VARIABLE_DECLARATION_LINE =
			Pattern.compile("\\s*(" + Variable.getVariableTypesRegex() + ")\\s+([^;]*);\\s*");
	/* Final variable declaration line regex */
	private static final Pattern FINAL_VARIABLE_DECLARATION_LINE =
			Pattern.compile("\\s*final\\s+(" + Variable.getVariableTypesRegex() + ")\\s+([^;]*);\\s*");
	/* Variable assignment line regex */
	private static final Pattern VARIABLE_ASSIGNMENT_LINE =
			Pattern.compile("^\\s*([^=\\s]+)\\s*=\\s*(\\S*)\\s*;\\s*");
	/* Method declaration line regex */
	private static final Pattern METHOD_DECLARATION_LINE =
			Pattern.compile("\\s*void\\s+(\\S+)\\s*[(](.*)?[)]\\s*[{]\\s*");
	/* Return line regex */
	private static final Pattern RETURN_LINE = Pattern.compile("\\s*return\\s*;\\s*");
	/* Method call line regex */
	private static final Pattern METHOD_CALL_LINE = Pattern.compile("\\s*(\\S*)\\s*[(](.*)?[)];\\s*");
	/* Block suffix regex */
	private static final Pattern BLOCK_SUFFIX_LINE = Pattern.compile("\\s*[}]\\s*");
	/* If/While line regex */
	private static final Pattern IF_WHILE_LINE =
			Pattern.compile("\\s*(if|while)\\s*[(](\\s*\\S+\\s*((\\|\\||&&)\\s*\\S+\\s*)*)[)]\\s*[{]\\s*");
	/* Single value regex */
	private static final Pattern SINGLE_VALUE = Pattern.compile("\\s*(\\S+)\\s*");
	/* Variable assignment regex */
	private static final Pattern VARIABLE_ASSIGNMENT =
			Pattern.compile("\\s*([^=\\s]+)\\s*=\\s*(\'.*\'|\".*\"|\\S*)\\s*");
	/* Method parameter regex */
	private static final Pattern METHOD_PARAMETER =
			Pattern.compile("\\s*(final\\s+)?(" + Variable.getVariableTypesRegex() + ")\\s+(\\S+)\\s*");

	/* Variables delimiter */
	private static final String VARIABLES_DELIMITER = ",";
	/* Condition operators */
	private static final String CONDITION_OPERATORS = "\\|\\||&&";

	/* Line number in file */
	private int lineNumber;
	/* Global scope */
	private Scope globalScope;
	/* Local scope */
	private Scope localScope;
	/* Scope stack */
	private Stack<Scope> scopes;

	/**
	 * Constructor
	 */
	public Parser() {
		this.lineNumber = 0;
		this.globalScope = new Scope();
		this.localScope = globalScope;
		this.scopes = new Stack<Scope>();
	}

	/**
	 * Get current line number in file
	 * @return line number
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Gets the next line in file
	 * @param scanObj Scanner object
	 * @return line string
	 */
	private String getNextLine(Scanner scanObj) {
		lineNumber += 1;
		return scanObj.nextLine();
	}

	/**
	 * Main parsing function
	 * @param filePath path to file
	 * @throws ParsingException thrown if illegal action is conducted
	 * @throws IOError thrown if an IO Error is discovered during the program run
	 * @throws FileNotFoundException thrown if the path supplied does not exist
	 */
	public void parse(String filePath) throws ParsingException, IOError, FileNotFoundException {
		methodsParse(new Scanner(new File(filePath)));
		mainParse(new Scanner(new File(filePath)), true);
		mainParse(new Scanner(new File(filePath)), false);
	}

	/**
	 * Parses methods and adds them to the global scope
	 * @param scanObj Scanner object
	 * @throws ParsingException thrown if an illegal action is conducted
	 * @throws IOError thrown if an IO Error is discovered during the program run
	 */
	private void methodsParse(Scanner scanObj) throws ParsingException, IOError {
		int lastReturn = 0;
		while (scanObj.hasNext()) {
			String line = getNextLine(scanObj);
			Matcher variableDeclarationMatcher = VARIABLE_DECLARATION_LINE.matcher(line);
			Matcher variableAssignmentMatcher = VARIABLE_ASSIGNMENT_LINE.matcher(line);
			Matcher finalVariableDeclarationMatcher = FINAL_VARIABLE_DECLARATION_LINE.matcher(line);
			Matcher methodDeclarationMatcher = METHOD_DECLARATION_LINE.matcher(line);
			Matcher returnLineMatcher = RETURN_LINE.matcher(line);
			Matcher blockSuffixMatcher = BLOCK_SUFFIX_LINE.matcher(line);
			Matcher methodCallMatcher = METHOD_CALL_LINE.matcher(line);
			Matcher ifWhileMatcher = IF_WHILE_LINE.matcher(line);

			if (isWhitespaceOnly(line) || isComment(line) || variableDeclarationMatcher.matches() ||
					finalVariableDeclarationMatcher.matches() ||
					variableAssignmentMatcher.matches() || methodCallMatcher.matches()) {
			} else if (methodDeclarationMatcher.matches() && localScope == globalScope) {
				parseMethodDeclarationLine(methodDeclarationMatcher);
				createInnerScope();
			} else if (ifWhileMatcher.matches()) {
				createInnerScope();
			} else if (blockSuffixMatcher.matches() && localScope != globalScope) {
				returnOuterScope();
				if (localScope == globalScope) {
					if (lastReturn != lineNumber - 1) {
						throw new MissingReturnException();
					}
				}
			} else if (returnLineMatcher.matches()) {
				lastReturn = lineNumber;
			} else {
				throw new IllegalLineException();
			}
		}
		cleanUp();
	}

	/**
	 * Main Parser
	 * @param scanObj Scanner object
	 * @param globals flag indicating whether to parse global scope. true - if yes, no - if local only
	 * @throws ParsingException thrown if an illegal action is conducted
	 * @throws IOError thrown if an IO Error is discovered during the program run
	 */
	public void mainParse(Scanner scanObj, boolean globals) throws ParsingException, IOError {
		while (scanObj.hasNext()){
			String line = getNextLine(scanObj);
			Matcher variableDeclarationMatcher = VARIABLE_DECLARATION_LINE.matcher(line);
			Matcher variableAssignmentMatcher = VARIABLE_ASSIGNMENT_LINE.matcher(line);
			Matcher finalVariableDeclarationMatcher = FINAL_VARIABLE_DECLARATION_LINE.matcher(line);
			Matcher methodDeclarationMatcher = METHOD_DECLARATION_LINE.matcher(line);
			Matcher returnLineMatcher = RETURN_LINE.matcher(line);
			Matcher blockSuffixMatcher = BLOCK_SUFFIX_LINE.matcher(line);
			Matcher methodCallMatcher = METHOD_CALL_LINE.matcher(line);
			Matcher ifWhileMatcher = IF_WHILE_LINE.matcher(line);

			if (! isWhitespaceOnly(line) && ! isComment(line)) {
				if (globals && localScope == globalScope || ! globals && localScope != globalScope) {
					if (variableDeclarationMatcher.matches()) {
						parseVariableDeclarationLine(variableDeclarationMatcher);
					} else if (finalVariableDeclarationMatcher.matches()) {
						parseFinalVariableDeclarationLine(finalVariableDeclarationMatcher);
					} else if (variableAssignmentMatcher.matches()) {
						parseVariableAssignmentLine(variableAssignmentMatcher);
					} else if (methodCallMatcher.matches() && localScope != globalScope) {
						parseMethodCall(methodCallMatcher);
					} else if (ifWhileMatcher.matches() && localScope != globalScope) {
						parseIfWhile(ifWhileMatcher);
					}
				}
				if (methodDeclarationMatcher.matches()) {
					createInnerScope();
					String methodName = methodDeclarationMatcher.group(1);
					for (Variable variable: globalScope.getMethod(methodName).getArguments()) {
						localScope.addVariable(variable.getName(), variable);
					}
				} else if (ifWhileMatcher.matches()) {
					createInnerScope();
				} else if (blockSuffixMatcher.matches()) {
					returnOuterScope();
				} else if (returnLineMatcher.matches() && localScope != globalScope ||
						variableDeclarationMatcher.matches() || finalVariableDeclarationMatcher.matches() ||
						variableAssignmentMatcher.matches() ||
						methodCallMatcher.matches() && localScope != globalScope ||
						ifWhileMatcher.matches() && localScope != globalScope) {
				} else {
					throw new IllegalLineException();
				}
			}
		}
		cleanUp();
	}

	/**
	 * Return to initial state between parse runs on file
	 */
	private void cleanUp() {
		lineNumber = 0;
		this.localScope = globalScope;
		this.scopes = new Stack<Scope>();
	}

	/**
	 * Does the line match a comment line
	 * @param line file line
	 * @return boolean
	 */
	private static boolean isComment(String line) {
		Matcher matcher = COMMENT_LINE.matcher(line);

		return matcher.find();
	}

	/**
	 * Does the line match a whitespace only line
	 * @param line file line
	 * @return boolean
	 */
	private static boolean isWhitespaceOnly(String line) {
		Matcher matcher = WHITESPACE_LINE.matcher(line);

		return matcher.matches();
	}

	/**
	 * Initiates actions when a new scope starts
	 */
	private void createInnerScope() {
		scopes.push(localScope);
		localScope = new Scope();
	}

	/**
	 * Initiates actions when a scope ends
	 */
	private void returnOuterScope() {
		try {
			localScope = scopes.pop();
		} catch (EmptyStackException e) {
			localScope = globalScope;
		}
	}

	/**
	 * Parse variable declaration line
	 * @param matcher matcher
	 * @throws ParsingException thrown if an illegal action is conducted
	 */
	private void parseVariableDeclarationLine(Matcher matcher) throws ParsingException {
		String variableType = matcher.group(1);
		if (matcher.group(2).startsWith(VARIABLES_DELIMITER) || matcher.group(2).endsWith(VARIABLES_DELIMITER)) {
			throw new IllegalLineException();
		}
		for (String section: matcher.group(2).split(VARIABLES_DELIMITER)) {
			Matcher declarationOnlyMatcher = SINGLE_VALUE.matcher(section);
			Matcher assignmentMatcher = VARIABLE_ASSIGNMENT.matcher(section);
			if (assignmentMatcher.matches()) {
				String variableName = assignmentMatcher.group(1);
				Variable variable = new Variable(variableType, variableName, false);
				localScope.addVariable(variableName, variable);
				assignValue(assignmentMatcher, variable);
			} else if (declarationOnlyMatcher.matches()) {
				String variableName = declarationOnlyMatcher.group(1);
				localScope.addVariable(variableName, new Variable(variableType, variableName, false));
			} else {
				throw new IllegalLineException();
			}
		}
	}

	/**
	 * Parse final variable declaration
	 * @param matcher matcher
	 * @throws ParsingException thrown if an illegal action is conducted
	 */
	private void parseFinalVariableDeclarationLine(Matcher matcher) throws ParsingException {
		String variableType = matcher.group(1);
		for (String section: matcher.group(2).split(VARIABLES_DELIMITER)) {
			Matcher assignmentMatcher = VARIABLE_ASSIGNMENT.matcher(section);
			if (assignmentMatcher.matches()) {
				String variableName = assignmentMatcher.group(1);
				Variable variable = new Variable(variableType, variableName, true);
				localScope.addVariable(variableName, variable);
				assignValue(assignmentMatcher, variable);
			} else {
				throw new IllegalLineException();
			}
		}
	}

	/**
	 * Parse variable assignment line
	 * @param matcher matcher
	 * @throws ParsingException thrown if an illegal action is conducted
	 */
	private void parseVariableAssignmentLine(Matcher matcher) throws ParsingException {
		String variableName = matcher.group(1);
		Variable assignedVariable = getMostSpecificVariable(variableName, getClonedStack());
		assignValue(matcher, assignedVariable);
	}

	/**
	 * Clone the Stack object (without warnings)
	 * @return copy
	 */
	private Stack<Scope> getClonedStack() {
		Stack<Scope> scopeStackCopy = new Stack<Scope>();
		scopeStackCopy.addAll(scopes);
		return scopeStackCopy;
	}

	/**
	 * Assign value to variable
	 * @param matcher matcher
	 * @param variable variable to assign value to
	 * @throws ParsingException thrown if an illegal action is conducted
	 */
	private void assignValue(Matcher matcher, Variable variable) throws ParsingException {
		if (Variable.isNameValid(matcher.group(2))) {
			String variableString = matcher.group(2);
			Variable assignedVariable = getMostSpecificVariable(variableString, getClonedStack());
			variable.copyVariableValue(assignedVariable);
		} else {
			String variableValue = matcher.group(2);
			variable.setValue(variableValue);
		}
	}

	/**
	 * Parse method declaration line
	 * @param matcher matcher
	 * @throws ParsingException thrown if an illegal action is conducted
	 */
	private void parseMethodDeclarationLine(Matcher matcher) throws ParsingException {
		List<Variable> methodParameters = new ArrayList<Variable>();
		String methodName = matcher.group(1);
		String parameters = matcher.group(2);
		if (! parameters.matches("\\s*")) {
			for (String parameter: parameters.split(VARIABLES_DELIMITER)) {
				Matcher parameterMatcher = METHOD_PARAMETER.matcher(parameter);
				if (parameterMatcher.matches()) {
					boolean isFinal = false;
					if (parameterMatcher.group(1) != null) {
						isFinal = true;
					}
					String variableType = parameterMatcher.group(2);
					String variableName = parameterMatcher.group(3);
					Variable methodVariable = new Variable(variableType, variableName, isFinal);
					methodVariable.setDefaultValue();
					methodParameters.add(methodVariable);
				} else {
					throw new IllegalLineException();
				}
			}
		}
		globalScope.addMethod(methodName, new Method(methodName, methodParameters));
	}

	/**
	 * Parse method call
	 * @param matcher matcher
	 * @throws ParsingException thrown if an illegal action is conducted
	 */
	private void parseMethodCall(Matcher matcher) throws ParsingException {
		List<String> methodArguments = new ArrayList<String>();
		String methodName = matcher.group(1);
		String arguments = matcher.group(2);
		if (! arguments.matches("\\s*")) {
			for (String argument: arguments.split(VARIABLES_DELIMITER)) {
				Matcher argumentMatcher = SINGLE_VALUE.matcher(argument);
				if (argumentMatcher.matches()) {
					String variableString = argumentMatcher.group(1);
					if (Variable.isNameValid(variableString)) {
						Variable variable = getMostSpecificVariable(variableString,
								getClonedStack());
						if (variable.getValue() != null) {
							methodArguments.add(variable.getValue());
						} else {
							throw new UninitializedVariableException();
						}
					} else {
						methodArguments.add(variableString);
					}
				} else {
					throw new IllegalLineException();
				}
			}
		}
		globalScope.getMethod(methodName).call(methodArguments);
	}

	/**
	 * Parse if/while statement
	 * @param matcher matcher
	 * @throws ParsingException thrown if an illegal action is conducted
	 */
	private void parseIfWhile(Matcher matcher) throws ParsingException {
		String conditionString = matcher.group(2);
		for (String condition: conditionString.split(CONDITION_OPERATORS)) {
			Matcher conditionMatcher = SINGLE_VALUE.matcher(condition);
			if (conditionMatcher.matches()) {
				String result = conditionMatcher.group(1);
				Variable boolVariable = new Variable("boolean", "test", false);
				if (Variable.isNameValid(result)) {
					Variable variable = getMostSpecificVariable(result, getClonedStack());
					if (variable.getValue() != null) {
						boolVariable.setValue(variable.getValue());
					} else {
						throw new UninitializedVariableException();
					}
				} else {
					boolVariable.setValue(result);
				}
			} else {
				throw new IllegalLineException();
			}
		}
	}

	/**
	 * Get variable from the most relevant scope
	 * @param variableName name of variable
	 * @param scopes stack of scopes
	 * @return most specific variable
	 * @throws ParsingException thrown if an illegal action is conducted
	 */
	private Variable getMostSpecificVariable(String variableName, Stack<Scope> scopes) throws ParsingException {
		if (localScope.getVariable(variableName) != null) {
			return localScope.getVariable(variableName);
		}

		int stackSize = scopes.size();
		for (int i=0; i<stackSize; i++) {
			Scope scope = scopes.pop();
			Variable variable = scope.getVariable(variableName);
			if (variable != null) {
				if (scope != globalScope) {
					return variable;
				} else {
					Variable copyVariable = new Variable(variable);
					localScope.addVariable(variable.getName(), copyVariable);
					return copyVariable;
				}
			}
		}
		throw new UndeclaredVariableException();
	}
}
