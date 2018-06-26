package oop.ex6.main;

import java.util.*;

/**
 * Class representing a scope in s-java code
 */
public class Scope {
	/* Map between variableName and Variable object */
	private HashMap<String, Variable> nameToVariable;
	/* Map between methodName and Method object */
	private HashMap<String, Method> nameToMethod;

	/**
	 * Scope constructor
	 */
	public Scope() {
		this.nameToVariable = new HashMap<String, Variable>();
		this.nameToMethod = new HashMap<String, Method>();
	}

	/**
	 * Gets variable from scope
	 * @param variableName name of variable in scope
	 * @return Variable if found, null if not
	 */
	public Variable getVariable(String variableName) {
		if (isVariableInScope(variableName)) {
			return nameToVariable.get(variableName);
		}
		return null;
	}

	/**
	 * Adds variable to scope
	 * @param variableName name of variable
	 * @param variableObj variable object to add
	 * @throws DoubleDeclarationException thrown if trying to add an existing variable to scope
	 */
	public void addVariable(String variableName, Variable variableObj) throws DoubleDeclarationException {
		if (isVariableInScope(variableName)) {
			throw new DoubleDeclarationException();
		}
		nameToVariable.put(variableName, variableObj);
	}

	/**
	 * Gets method from scope
	 * @param methodName name of method in scope
	 * @return method object
	 * @throws UndeclaredMethodException thrown if trying to access undeclared method
	 */
	public Method getMethod(String methodName) throws UndeclaredMethodException {
		if (! isMethodInScope(methodName)) {
			throw new UndeclaredMethodException();
		}
		return nameToMethod.get(methodName);
	}

	/**
	 * Adds method to scope
	 * @param methodName name of method
	 * @param methodObj method object
	 * @throws DoubleDeclarationException thrown if trying to add an existing variable to scope
	 */
	public void addMethod(String methodName, Method methodObj) throws DoubleDeclarationException {
		if (isVariableInScope(methodName)) {
			throw new DoubleDeclarationException();
		}
		nameToMethod.put(methodName, methodObj);
	}

	/**
	 * Checks if variable is in scope
	 * @param variableName name of variable
	 * @return true - if found
	 */
	private boolean isVariableInScope(String variableName) {
		return nameToVariable.containsKey(variableName);
	}

	/**
	 * Checks if method is in scope
	 * @param methodName name of method
	 * @return true - if found
	 */
	private boolean isMethodInScope(String methodName) {
		return nameToMethod.containsKey(methodName);
	}
}
