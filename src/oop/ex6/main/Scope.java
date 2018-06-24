package oop.ex6.main;

import java.util.*;

public class Scope {
	private HashMap<String, Variable> nameToVariable;
	private HashMap<String, Method> nameToMethod;

	public Scope() {
		this.nameToVariable = new HashMap<String, Variable>();
		this.nameToMethod = new HashMap<String, Method>();
	}

	public void addVariable(String variableName, Variable variableObj) throws DoubleDeclarationException {
		if (isVariableInScope(variableName)) {
			throw new DoubleDeclarationException();
		}
		nameToVariable.put(variableName, variableObj);
	}

	public void addMethod(String methodName, Method methodObj) throws DoubleDeclarationException {
		if (isVariableInScope(methodName)) {
			throw new DoubleDeclarationException();
		}
		nameToMethod.put(methodName, methodObj);
	}

	public boolean isVariableInScope(String variableName) {
		return nameToVariable.containsKey(variableName);
	}

	public boolean isMethodInScope(String methodName) {
		return nameToMethod.containsKey(methodName);
	}

	public Variable getVariable(String variableName) {
		if (isVariableInScope(variableName)) {
			return nameToVariable.get(variableName);
		}
		return null;
	}

	public Method getMethod(String methodName) throws UndeclaredMethodException {
		if (! isMethodInScope(methodName)) {
			throw new UndeclaredMethodException();
		}
		return nameToMethod.get(methodName);
	}
}
