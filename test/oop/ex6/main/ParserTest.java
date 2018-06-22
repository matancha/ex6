package oop.ex6.main;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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

    private void testPassing(String filePath) throws FileNotFoundException {
        Parser parseObj = new Parser();
        try {
            parseObj.parse(filePath);
        } catch (ParsingException e) {
            System.err.println(parseObj.getLineNumber() + ": " + e.getMsg());
            fail();
        }
    }

    private void testFailing(String filePath) throws FileNotFoundException, ParsingException {
        Parser parseObj = new Parser();
        parseObj.parse(filePath);
    }
}