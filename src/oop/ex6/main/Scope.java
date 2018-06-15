package oop.ex6.main;

import java.util.*;

public class Scope {
	private HashMap<String, Variable> nameToVariable;

	public Scope() {
		this.nameToVariable = new HashMap<String, Variable>();
	}

	public void addVariable(String variableName, Variable variableObj) {
		nameToVariable.put(variableName, variableObj);
	}
}
