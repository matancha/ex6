package oop.ex6.main;

import java.io.IOError;
import java.util.*;
import java.util.regex.*;

public class Parser {
	private static final Pattern VARIABLE_DECLARATION = Pattern.compile(" *(\\S+) *");
	private static final Pattern VARIABLE_ASSIGNMENT = Pattern.compile(" *([^=\\s]+) *= *(\\S*) *");
	private static final Pattern COMMENT_LINE = Pattern.compile("^[\\/][\\/]");
	private static final Pattern WHITESPACE_LINE = Pattern.compile("\\s*");
	private static final Pattern VARIABLE_DECLARATION_LINE =
			Pattern.compile("( *final )? *(int|char|double|String|boolean) +([^;]*);$");
	private static final Pattern VARIABLE_ASSIGNMENT_LINE = Pattern.compile("^ *([^=\\s]+) *= *(\\S*) *;$");

	private int lineNumber;
	private Scanner scanObj;
	private Scope globalScope;

	public Parser(Scanner scanObj){
		this.lineNumber = 0;
		this.scanObj = scanObj;
		this.globalScope = new Scope();
	}

	private String getNextLine() {
		lineNumber += 1;
		return scanObj.nextLine();
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void parse() throws ParsingException, IOError {
		while (scanObj.hasNext()){
			String line = getNextLine();
			if (! isWhitespaceOnly(line) && ! isComment(line)) {
				Matcher variableDeclarationMatcher = VARIABLE_DECLARATION_LINE.matcher(line);
				Matcher variableAssignmentMatcher = VARIABLE_ASSIGNMENT_LINE.matcher(line);
				if (variableDeclarationMatcher.matches()) {
					parseVariableDeclarationLine(variableDeclarationMatcher);
				} else if (variableAssignmentMatcher.matches()) {
					parseVariableAssignmentLine(variableAssignmentMatcher);
				} else {
					throw new IllegalLineException();
				}
			}
		}
	}

	private void parseVariableAssignmentLine(Matcher variableAssignmentMatcher) throws ParsingException {
		String variableName = variableAssignmentMatcher.group(1);
		Variable assignedVariable = globalScope.getVariable(variableName);
		assignValue(variableAssignmentMatcher, assignedVariable);
	}

	private void parseVariableDeclarationLine(Matcher matcher) throws ParsingException {
		boolean isFinal = isFinalVariable(matcher.group(1));
		String variableType = matcher.group(2);
		for (String section: matcher.group(3).split(",")) {
			Matcher declarationOnlyMatcher = VARIABLE_DECLARATION.matcher(section);
			Matcher assignmentMatcher = VARIABLE_ASSIGNMENT.matcher(section);
			if (declarationOnlyMatcher.matches()) {
				String variableName = declarationOnlyMatcher.group(1);
				globalScope.addVariable(variableName, new Variable(variableType, variableName, isFinal));
			} else if (assignmentMatcher.matches()) {
				String variableName = assignmentMatcher.group(1);
				Variable variable = new Variable(variableType, variableName, isFinal);
				globalScope.addVariable(variableName, variable);
				assignValue(assignmentMatcher, variable);
			}
		}
	}

	private boolean isFinalVariable(String answer) {
		return (answer != null);
	}

	private void assignValue(Matcher assignmentMatcher, Variable variable) throws ParsingException {
		if (Variable.isNameValid(assignmentMatcher.group(2))) {
			Variable assignedVariable = globalScope.getVariable(assignmentMatcher.group(2));
			variable.copyVariableValue(assignedVariable);
		} else {
			String variableValue = assignmentMatcher.group(2);
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
