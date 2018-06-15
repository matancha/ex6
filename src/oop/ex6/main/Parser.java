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

	public boolean parse() throws ParsingException, IOError {
		while (scanObj.hasNext()){
			String line = getNextLine();
			if (! isWhitespaceOnly(line) || isComment(line)) {
				System.out.println(line);
				System.out.println("hi");
				if (isVariableDeclaration(line)){
					String declarationLine = line.split(";")[0];
					String[] separatedBySpaces = declarationLine.split("\\s+");
					String variablesType = separatedBySpaces[0];
					String[] variablesWithoutType = Arrays.copyOfRange(separatedBySpaces,
							1, separatedBySpaces.length-1);
					String variablesString = String.join("", variablesWithoutType);
					String[] separatedByCommas = variablesString.split(",");
					for (String variable: separatedByCommas) {
						globalScope.addVariable(variable, new Variable(variablesType, variable));
					}
				} else {
					throw new IllegalLineException();
				}
			}
		}
		return true;
	}

	private boolean isVariableAssignment(String line) {
		String variableAssignment = "=";
		Pattern pattern = Pattern.compile(variableAssignment);
		Matcher matcher = pattern.matcher(line);

		return matcher.find();
	}

	private static boolean isVariableDeclaration(String line) {
		String variableDeclaration = "^(int|double|String|boolean|char)\\b";
		Pattern pattern = Pattern.compile(variableDeclaration);
		Matcher matcher = pattern.matcher(line);

		return matcher.find();
	}

	private static boolean isComment(String line) {
		String commentString = "^\\\\";
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
