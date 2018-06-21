package oop.ex6.main;

import java.util.*;

public class Scope {
	private HashMap<String, Variable> nameToVariable;

	public Scope() {
		this.nameToVariable = new HashMap<String, Variable>();
	}

	public void addVariable(String variableName, Variable variableObj) throws DoubleDeclarationException {
		if (isInScope(variableName)) {
			throw new DoubleDeclarationException();
		}
		nameToVariable.put(variableName, variableObj);
	}

	public boolean isInScope(String variableName) {
		return nameToVariable.containsKey(variableName);
	}

	public Variable getVariable(String variableName) throws UndeclaredVariableException {
		if (! isInScope(variableName)) {
			throw new UndeclaredVariableException();
		}
		return nameToVariable.get(variableName);
	}
}
