package oop.ex6.main;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Parser {
	private static final Pattern VARIABLE_DECLARATION = Pattern.compile("\\s*(\\S+)\\s*");
	private static final Pattern VARIABLE_ASSIGNMENT = Pattern.compile("\\s*([^=\\s]+)\\s*=\\s*(\\S*)\\s*");
	private static final Pattern COMMENT_LINE = Pattern.compile("^[\\/][\\/]");
	private static final Pattern WHITESPACE_LINE = Pattern.compile("\\s*");
	private static final Pattern VARIABLE_DECLARATION_LINE =
			Pattern.compile("\\s*(" + Variable.getVariableTypesRegex() + ")\\s+([^;]*);$");
	private static final Pattern FINAL_VARIABLE_DECLARATION_LINE =
			Pattern.compile("\\s*final\\s+(" + Variable.getVariableTypesRegex() + ")\\s+([^;]*);$");
	private static final Pattern VARIABLE_ASSIGNMENT_LINE = Pattern.compile("^\\s*([^=\\s]+)\\s*=\\s*(\\S*)\\s*;$");
	private static final Pattern METHOD_DECLARATION_LINE = Pattern.compile("void\\s+(\\S+)[(](.*)[)]\\s*\\{\\s*");
	private static final Pattern METHOD_PARAMETER =
			Pattern.compile("\\s*(final\\s+)?(" + Variable.getVariableTypesRegex() + ")\\s+(\\S+)");
	private static final Pattern METHOD_BLOCK = Pattern.compile(" *void\\s[\\s\\S]+?(return;\\s*\\n}|\\n})");
	private static final Pattern RETURN_LINE = Pattern.compile("\\s*return;\\s*");
	private static final Pattern BLOCK_SUFFIX = Pattern.compile("\\s*[}]\\s*");
	private static final String DELIMITER = ",";

	private int lineNumber;
	private Scope globalScope;
	private Scope localScope;
	private Stack<Scope> scopeStack;

	public Parser(){
		this.lineNumber = 0;
		this.globalScope = new Scope();
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
		methodsParse(new Scanner(new File(filePath)).useDelimiter("\\A"));
		mainParse(new Scanner(new File(filePath)));
	}

	private void methodsParse(Scanner scanObj) throws ParsingException, IOError {
		String fileChar = scanObj.next();
		Matcher methodBlockMatcher = METHOD_BLOCK.matcher(fileChar);

		while (methodBlockMatcher.find()) {
			if (methodBlockMatcher.group(1).startsWith("return")) {
				Matcher methodDeclarationMatcher =
						METHOD_DECLARATION_LINE.matcher(methodBlockMatcher.group().split("\\n")[0]);
				if (methodDeclarationMatcher.matches()) {
					parseMethodDeclarationLine(methodDeclarationMatcher);
				} else {
					throw new IllegalLineException();
				}
			} else {
				throw new MissingReturnException();
			}
		}
	}

	public void mainParse(Scanner scanObj) throws ParsingException, IOError {
		while (scanObj.hasNext()){
			String line = getNextLine(scanObj);
			Matcher variableDeclarationMatcher = VARIABLE_DECLARATION_LINE.matcher(line);
			Matcher variableAssignmentMatcher = VARIABLE_ASSIGNMENT_LINE.matcher(line);
			Matcher finalVariableDeclarationMatcher = FINAL_VARIABLE_DECLARATION_LINE.matcher(line);
			Matcher methodDeclarationMatcher = METHOD_DECLARATION_LINE.matcher(line);
			Matcher returnLineMatcher = RETURN_LINE.matcher(line);
			Matcher blockSuffixMatcher = BLOCK_SUFFIX.matcher(line);

			if (! isWhitespaceOnly(line) && ! isComment(line)) {
				if (variableDeclarationMatcher.matches()) {
					parseVariableDeclarationLine(variableDeclarationMatcher);
				} else if (finalVariableDeclarationMatcher.matches()) {
					parseFinalVariableDeclarationLine(finalVariableDeclarationMatcher);
				} else if (variableAssignmentMatcher.matches()) {
					parseVariableAssignmentLine(variableAssignmentMatcher);
				} else if (methodDeclarationMatcher.matches()) {
					scopeStack.push(localScope);
					localScope = new Scope();
					String methodName = methodDeclarationMatcher.group(1);
					for (Variable variable: globalScope.getMethod(methodName).getArguments()) {
						localScope.addVariable(variable.getName(), variable);
					}
				} else if (blockSuffixMatcher.matches()) {
					localScope = scopeStack.pop();
				} else if (returnLineMatcher.matches()) {
				} else {
					throw new IllegalLineException();
				}
			}
		}
	}

	private void parseMethodDeclarationLine(Matcher matcher) throws ParsingException {
		List<Variable> methodParameters = new ArrayList<Variable>();
		String methodName = matcher.group(1);
		String parameters = matcher.group(2);
		if (parameters != null) {
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

	private void parseFinalVariableDeclarationLine(Matcher matcher) throws ParsingException {
		String variableType = matcher.group(1);
		for (String section: matcher.group(2).split(DELIMITER)) {
			Matcher assignmentMatcher = VARIABLE_ASSIGNMENT.matcher(section);
			if (assignmentMatcher.matches()) {
				String variableName = assignmentMatcher.group(1);
				Variable variable = new Variable(variableType, variableName, true);
				globalScope.addVariable(variableName, variable);
				assignValue(assignmentMatcher, variable);
			} else {
				throw new IllegalLineException();
			}
		}
	}

	private void parseVariableAssignmentLine(Matcher matcher) throws ParsingException {
		String variableName = matcher.group(1);
		Variable assignedVariable = globalScope.getVariable(variableName);
		assignValue(matcher, assignedVariable);
	}

	private void parseVariableDeclarationLine(Matcher matcher) throws ParsingException {
		String variableType = matcher.group(1);
		for (String section: matcher.group(2).split(DELIMITER)) {
			Matcher declarationOnlyMatcher = VARIABLE_DECLARATION.matcher(section);
			Matcher assignmentMatcher = VARIABLE_ASSIGNMENT.matcher(section);
			if (declarationOnlyMatcher.matches()) {
				String variableName = declarationOnlyMatcher.group(1);
				globalScope.addVariable(variableName, new Variable(variableType, variableName, false));
			} else if (assignmentMatcher.matches()) {
				String variableName = assignmentMatcher.group(1);
				Variable variable = new Variable(variableType, variableName, false);
				globalScope.addVariable(variableName, variable);
				assignValue(assignmentMatcher, variable);
			} else {
				throw new IllegalLineException();
			}
		}
	}

	private void assignValue(Matcher matcher, Variable variable) throws ParsingException {
		if (Variable.isNameValid(matcher.group(2))) {
			Variable assignedVariable = globalScope.getVariable(matcher.group(2));
			variable.copyVariableValue(assignedVariable);
		} else {
			String variableValue = matcher.group(2);
			variable.setValue(variableValue);
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
}
