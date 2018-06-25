package oop.ex6.main;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Parser {
	private static final Pattern SINGLE_VALUE = Pattern.compile("\\s*(\\S+)\\s*");
	private static final Pattern VARIABLE_ASSIGNMENT =
			Pattern.compile("\\s*([^=\\s]+)\\s*=\\s*((\\'.*\\'|\\\".*\\\"|\\S*))\\s*");
	private static final Pattern COMMENT_LINE = Pattern.compile("^[\\/][\\/]");
	private static final Pattern WHITESPACE_LINE = Pattern.compile("\\s*");
	private static final Pattern VARIABLE_DECLARATION_LINE =
			Pattern.compile("\\s*(" + Variable.getVariableTypesRegex() + ")\\s+([^;]*);\\s*");
	private static final Pattern FINAL_VARIABLE_DECLARATION_LINE =
			Pattern.compile("\\s*final\\s+(" + Variable.getVariableTypesRegex() + ")\\s+([^;]*);\\s*");
	private static final Pattern VARIABLE_ASSIGNMENT_LINE = Pattern.compile("^\\s*([^=\\s]+)\\s*=\\s*(\\S*)\\s*;\\s*");
	private static final Pattern METHOD_DECLARATION_LINE =
			Pattern.compile("\\s*void\\s+(\\S+)\\s*[(](.*)?[)]\\s*[{]\\s*");
	private static final Pattern METHOD_PARAMETER =
			Pattern.compile("\\s*(final\\s+)?(" + Variable.getVariableTypesRegex() + ")\\s+(\\S+)\\s*");
	private static final Pattern RETURN_LINE = Pattern.compile("\\s*return\\s*;\\s*");
	private static final Pattern METHOD_CALL_LINE = Pattern.compile("\\s*(\\S*)\\s*[(](.*)?[)];\\s*");
	private static final Pattern BLOCK_SUFFIX_LINE = Pattern.compile("\\s*[}]\\s*");
	private static final Pattern IF_WHILE_LINE =
			Pattern.compile("\\s*(if|while)\\s*[(](\\s*\\S+\\s*((\\|\\||&&)\\s*\\S+\\s*)*)[)]\\s*[{]\\s*");
	private static final String DELIMITER = ",";

	private int lineNumber;
	private Scope globalScope;
	private Scope localScope;
	private Stack<Scope> scopeStack;

	public Parser(){
		this.lineNumber = 0;
		this.globalScope = new Scope();
		this.localScope = globalScope;
		this.scopeStack = new Stack<Scope>();
	}

	private String getNextLine(Scanner scanObj) {
		lineNumber += 1;
		return scanObj.nextLine();
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void parse(String filePath) throws ParsingException, IOError, FileNotFoundException {
		methodsParse(new Scanner(new File(filePath)));
		this.scopeStack = new Stack<Scope>();
		mainParse(new Scanner(new File(filePath)), true);
		mainParse(new Scanner(new File(filePath)), false);
	}

	private void methodsParse(Scanner scanObj) throws ParsingException, IOError {
		int lastReturn = 0;
		lineNumber = 0;
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
				createNewScope();
			} else if (ifWhileMatcher.matches()) {
				createNewScope();
			} else if (blockSuffixMatcher.matches() && localScope != globalScope) {
				localScope = scopeStack.pop();
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
	}

	public void mainParse(Scanner scanObj, boolean globals) throws ParsingException, IOError {
		lineNumber = 0;
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
					createNewScope();
					String methodName = methodDeclarationMatcher.group(1);
					for (Variable variable: globalScope.getMethod(methodName).getArguments()) {
						localScope.addVariable(variable.getName(), variable);
						variable.setDefaultValue();
					}
				} else if (ifWhileMatcher.matches()) {
					createNewScope();
				} else if (blockSuffixMatcher.matches()) {
					try {
						localScope = scopeStack.pop();
					} catch (EmptyStackException e) {
						localScope = globalScope;
					}
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
	}

	private static boolean isComment(String line) {
		Matcher matcher = COMMENT_LINE.matcher(line);

		return matcher.find();
	}

	private static boolean isWhitespaceOnly(String line) {
		Matcher matcher = WHITESPACE_LINE.matcher(line);

		return matcher.matches();
	}

	private void createNewScope() {
		scopeStack.push(localScope);
		localScope = new Scope();
	}

	private void parseVariableDeclarationLine(Matcher matcher) throws ParsingException {
		String variableType = matcher.group(1);
		if (matcher.group(2).startsWith(DELIMITER) || matcher.group(2).endsWith(DELIMITER)) {
			throw new IllegalLineException();
		}
		for (String section: matcher.group(2).split(DELIMITER)) {
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

	private void parseFinalVariableDeclarationLine(Matcher matcher) throws ParsingException {
		String variableType = matcher.group(1);
		for (String section: matcher.group(2).split(DELIMITER)) {
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

	private void parseVariableAssignmentLine(Matcher matcher) throws ParsingException {
		String variableName = matcher.group(1);
		Variable assignedVariable;
		assignedVariable = getMostSpecificVariable(variableName, (Stack<Scope>)scopeStack.clone());
		assignValue(matcher, assignedVariable);
	}

	private void assignValue(Matcher matcher, Variable variable) throws ParsingException {
		if (Variable.isNameValid(matcher.group(2))) {
			String variableString = matcher.group(2);
			Variable assignedVariable = getMostSpecificVariable(variableString, (Stack<Scope>)scopeStack.clone());
			variable.copyVariableValue(assignedVariable);
		} else {
			String variableValue = matcher.group(2);
			variable.setValue(variableValue);
		}
	}

	private void parseMethodDeclarationLine(Matcher matcher) throws ParsingException {
		List<Variable> methodParameters = new ArrayList<Variable>();
		String methodName = matcher.group(1);
		String parameters = matcher.group(2);
		if (! parameters.matches("\\s*")) {
			for (String parameter: parameters.split(DELIMITER)) {
				Matcher parameterMatcher = METHOD_PARAMETER.matcher(parameter);
				if (parameterMatcher.matches()) {
					boolean isFinal = false;
					if (parameterMatcher.group(1) != null) {
						isFinal = true;
					}
					String variableType = parameterMatcher.group(2);
					String variableName = parameterMatcher.group(3);
					methodParameters.add(new Variable(variableType, variableName, isFinal));
				} else {
					throw new IllegalLineException();
				}
			}
		}
		globalScope.addMethod(methodName, new Method(methodName, methodParameters));
	}

	private void parseMethodCall(Matcher matcher) throws ParsingException {
		List<String> methodArguments = new ArrayList<String>();
		String methodName = matcher.group(1);
		String arguments = matcher.group(2);
		if (! arguments.matches("\\s*")) {
			for (String argument: arguments.split(DELIMITER)) {
				Matcher argumentMatcher = SINGLE_VALUE.matcher(argument);
				if (argumentMatcher.matches()) {
					String variableString = argumentMatcher.group(1);
					if (Variable.isNameValid(variableString)) {
						Variable variable = getMostSpecificVariable(variableString,
								(Stack<Scope>)scopeStack.clone());
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

	private void parseIfWhile(Matcher matcher) throws ParsingException {
		String conditionString = matcher.group(2);
		for (String condition: conditionString.split("\\|\\||&&")) {
			Matcher conditionMatcher = SINGLE_VALUE.matcher(condition);
			if (conditionMatcher.matches()) {
				String result = conditionMatcher.group(1);
				Variable boolVariable = new Variable("boolean", "test", false);
				if (Variable.isNameValid(result)) {
					Variable variable = getMostSpecificVariable(result, (Stack<Scope>)scopeStack.clone());
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

	private Variable getMostSpecificVariable(String variableString, Stack<Scope> scopeStack) throws ParsingException {
		if (localScope.getVariable(variableString) != null) {
			return localScope.getVariable(variableString);
		}

		int stackSize = scopeStack.size();
		for (int i=0; i<stackSize; i++) {
			Scope scope = scopeStack.pop();
			Variable variable = scope.getVariable(variableString);
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
