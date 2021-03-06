package oop.ex6.main;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import java.io.*;

public class ParserTest {
    @Test
    public void emptyLinesFile() throws FileNotFoundException {
        testPassing("resources/empty_lines.txt");
    }

    @Test
    public void commentsFile() throws FileNotFoundException {
        testPassing("resources/comments_only.txt");
    }

    @Test
    public void simpleInitialization() throws FileNotFoundException {
        testPassing("resources/simple_initialization.txt");
    }

    @Test
    public void doubleInitialization() throws FileNotFoundException {
        testPassing("resources/double_initialization.txt");
    }

    @Test
    public void variableAssignment() throws FileNotFoundException {
        testPassing("resources/simple_assignment.txt");
    }

    @Test(expected = DoubleDeclarationException.class)
    public void initializingExistingVariable() throws Exception {
        testFailing("resources/initializing_initialized.txt");
    }

    @Test
    public void assigningVariableToAnother() throws FileNotFoundException {
        testPassing("resources/assigning_variable_to_another.txt");
    }

    @Test(expected = UndeclaredVariableException.class)
    public void assigningUndeclaredVariable() throws Exception {
        testFailing("resources/assigning_undeclared_variable.txt");
    }

    @Test(expected = FinalVariableAssignmentException.class)
    public void assigningFinalVariableAfterInitialized() throws Exception {
        testFailing("resources/assigning_final_variable.txt");
    }

    @Test(expected = IllegalLineException.class)
    public void declaringFinalVariableWithoutInitializing() throws Exception {
        testFailing("resources/declaring_final_without_initializing.txt");
    }

    @Test
    public void methodDeclaration() throws FileNotFoundException {
        testPassing("resources/method_declaration.txt");
    }

    @Test
    public void referGlobalVariable() throws FileNotFoundException {
        testPassing("resources/refer_global_variable.txt");
    }

    @Test(expected = UndeclaredVariableException.class)
    public void undeclaredLocalScope() throws Exception {
        testFailing("resources/undeclared_local_scope.txt");
    }

    @Test
    public void declaredOnlyInGlobal() throws FileNotFoundException {
        testPassing("resources/declared_only_in_global.txt");
    }

    @Test(expected = InvalidArgumentsNumber.class)
    public void notEnoughArguments() throws Exception {
        testFailing("resources/not_enough_arguments.txt");
    }

    @Test(expected = UndeclaredVariableException.class)
    public void undeclaredVariableSuppliedToMethod() throws Exception {
        testFailing("resources/undeclared_variable_method.txt");
    }

    @Test(expected = InvalidValueException.class)
    public void invalidValueSuppliedToMethod() throws Exception {
        testFailing("resources/invalid_type_method.txt");
    }

    @Test(expected = IllegalLineException.class)
    public void methodCallInGlobalScope() throws Exception {
        testFailing("resources/method_call_global_scope.txt");
    }

    @Test
    public void ifWhileBlock() throws FileNotFoundException {
        testPassing("resources/if_while_block.txt");
    }

    @Test(expected = InvalidValueException.class)
    public void whileBlockGivenIllegalArgument() throws Exception {
        testFailing("resources/illegal_value_block.txt");
    }

    @Test
    public void firstSchoolTest() throws FileNotFoundException {
    	testPassing("resources/ex6files/501.txt");
    }

	@Test(expected = IllegalLineException.class)
	public void secondSchoolTest() throws Exception {
		testFailing("resources/ex6files/502.txt");
	}

	@Test(expected = IllegalLineException.class)
	public void thirdSchoolTest() throws Exception {
    	testFailing("resources/ex6files/503.txt");
	}

	@Test(expected = InvalidValueException.class)
	public void fourthSchoolTest() throws Exception {
    	testFailing("resources/ex6files/504.txt");
	}

	@Test
	public void fifthSchoolTest() throws FileNotFoundException {
    	testPassing("resources/ex6files/505.txt");
	}

	@Test(expected = UninitializedVariableException.class)
    public void callingMethodWithUninitialized() throws Exception {
        testFailing("resources/calling_method_uninitialized.txt");
    }

	@Test
    public void variableInOuterScope() throws FileNotFoundException {
        testPassing("resources/variable_defined_outer.txt");
    }

    @Test
    public void innerAffectsOuter() throws FileNotFoundException {
        testPassing("resources/inner_affects_outer.txt");
    }

    @Test(expected = UninitializedVariableException.class)
    public void methodInnerNotAffectsGlobal() throws Exception {
        testFailing("resources/method_not_affects_global.txt");
    }

    @Test
    public void suffixWithoutIndentationDoesNotConcludeMethod() throws FileNotFoundException {
        testPassing("resources/suffix_no_indentation.txt");
    }

    @Test
    public void fifthTeenth() throws FileNotFoundException {
        testPassing("resources/SchoolSolution/test015.sjava");
    }

    @Test(expected = UninitializedVariableException.class)
    public void fourtySix() throws Exception {
        testFailing("resources/SchoolSolution/test046.sjava");
    }

    @Test(expected = UninitializedVariableException.class)
    public void fiftyFour() throws Exception {
        testFailing("resources/SchoolSolution/test054.sjava");
    }

    @Test
    public void fourHundredAndTwelve() throws FileNotFoundException {
        testPassing("resources/SchoolSolution/test412.sjava");
    }

    @Test
    public void fourHundredTwentySeven() throws FileNotFoundException {
        testPassing("resources/SchoolSolution/test427.sjava");
    }

    @Test
    public void ten() throws FileNotFoundException {
        testPassing("resources/SchoolSolution/10.sjava");
    }

    @Test
    public void FourHundredAndEighteen() throws FileNotFoundException {
        testPassing("resources/SchoolSolution/test418.sjava");
    }

    @Test
    public void FourHundredAndSeventySeven() throws FileNotFoundException {
        testPassing("resources/SchoolSolution/test477.sjava");
    }

    @Test
    public void barakNineteen() throws FileNotFoundException {
    	testPassing("resources/barak/19.sjava");
    }

    @Test
    public void barakThirty() throws FileNotFoundException {
	    testPassing("resources/barak/30.sjava");
    }

    @Test(expected = IllegalLineException.class)
    public void barakFiftySeven() throws Exception {
	    testFailing("resources/barak/57.sjava");
    }

    private void testPassing(String filePath) throws FileNotFoundException {
        Parser parseObj = new Parser();
        try {
            parseObj.parse(filePath);
        } catch (ParsingException e) {
            System.err.println(parseObj.getLineNumber() + ": " + e.getMessage());
            fail();
        }
    }

    private void testFailing(String filePath) throws FileNotFoundException, ParsingException {
        Parser parseObj = new Parser();
        parseObj.parse(filePath);
    }
}