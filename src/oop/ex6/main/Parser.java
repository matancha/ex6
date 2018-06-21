package oop.ex6.main;

import java.io.IOError;
import java.util.*;
import java.util.regex.*;

public class Parser {
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
				Matcher varDec = getVariableDeclarationPattern().matcher(line);
				Matcher varAssign = getVariableAssignmentPattern().matcher(line);
				if (varDec.matches()) {
					Pattern declarationOnly = Pattern.compile("^ *(\\S+) *$");
					Pattern assignment = Pattern.compile(" *([^=\\s]+) *= *(\\S*) *$");
					String variableType = varDec.group(1);
					for (String section: varDec.group(2).split(",")) {
						Matcher decMatch = declarationOnly.matcher(section);
						Matcher assignMatch = assignment.matcher(section);
						if (decMatch.matches()) {
							String variableName = decMatch.group(1);
							Variable varObj = new Variable(variableType, variableName);
							globalScope.addVariable(variableName, varObj);
						} else if (assignMatch.matches()) {
							String variableName = assignMatch.group(1);
							Variable varObj = new Variable(variableType, variableName);
							globalScope.addVariable(variableName, varObj);
							if (Variable.isNameValid(assignMatch.group(2))) {
								Variable assignedVariable = globalScope.getVariable(assignMatch.group(2));
								varObj.copyVariableValue(assignedVariable);
							} else {
								String variableValue = assignMatch.group(2);
								varObj.setValue(variableValue);
							}
						}
					}
				} else if (varAssign.matches()) {
					String variableName = varAssign.group(1);
					Variable assignedVariable = globalScope.getVariable(variableName);
					if (Variable.isNameValid(varAssign.group(2))) {
						assignedVariable.copyVariableValue(globalScope.getVariable(varAssign.group(2)));
					} else {
						assignedVariable.setValue(varAssign.group(2));
					}
				} else {
					throw new IllegalLineException();
				}
			}
		}
	}

	private static Pattern getVariableAssignmentPattern() {
		String variableAssignment = "^ *([^=\\s]+) *= *(\\S*) *;$";
		return Pattern.compile(variableAssignment);
	}

	private static Pattern getVariableDeclarationPattern() {
		String variableDeclaration = " *(int|char|double|String|boolean) +([^;]*)[;]$";
		return Pattern.compile(variableDeclaration);
	}

	private static boolean isComment(String line) {
		String commentString = "^[\\/][\\/]";
		Pattern pattern = Pattern.compile(commentString);
		Matcher matcher = pattern.matcher(line);

		return matcher.find();
	}

	private static boolean isWhitespaceOnly(String line) {
		String whitespaceOnlyString = "\\s*";
		Pattern pattern = Pattern.compile(whitespaceOnlyString);
		Matcher matcher = pattern.matcher(line);

		return matcher.matches();
	}
}
