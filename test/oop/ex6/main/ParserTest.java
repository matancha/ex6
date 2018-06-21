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
        testPassing(new File("resources/empty_lines.txt"));
    }

    @Test
    public void commentsFile() throws FileNotFoundException {
        testPassing(new File("resources/comments_only.txt"));
    }

    @Test
    public void simpleInitialization() throws FileNotFoundException {
        testPassing(new File("resources/simple_initialization.txt"));
    }

    @Test
    public void doubleInitialization() throws FileNotFoundException {
        testPassing(new File("resources/double_initialization.txt"));
    }

    @Test
    public void variableAssignment() throws FileNotFoundException {
        testPassing(new File("resources/simple_assignment.txt"));
    }

    @Test(expected = DoubleDeclarationException.class)
    public void initializingExistingVariable() throws Exception {
        testFailing(new File("resources/initializing_initialized.txt"));
    }

    @Test
    public void assigningVariableToAnother() throws FileNotFoundException {
        testPassing(new File("resources/assigning_variable_to_another.txt"));
    }

    @Test(expected = UndeclaredVariableException.class)
    public void assigningUndeclaredVariable() throws Exception {
        testFailing(new File("resources/assigning_undeclared_variable.txt"));
    }

    @Test(expected = FinalVariableAssignmentException.class)
    public void assigningFinalVariableAfterInitialized() throws Exception {
        testFailing(new File("resources/assigning_final_variable.txt"));
    }

    private void testPassing(File testFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(testFile);
        Parser parseObj = new Parser(scanner);
        try {
            parseObj.parse();
        } catch (ParsingException e) {
            System.err.println(parseObj.getLineNumber() + ": " + e.getMsg());
            fail();
        }
    }

    private void testFailing(File testFile) throws FileNotFoundException, ParsingException {
        Scanner scanner = new Scanner(testFile);
        Parser parseObj = new Parser(scanner);
        parseObj.parse();
    }
}