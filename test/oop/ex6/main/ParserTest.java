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

    private void testPassing(File testFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(testFile);
        Parser parseObj = new Parser(scanner);
        try {
            assertTrue(parseObj.parse());
        } catch (ParsingException e) {
            System.err.println(parseObj.getLineNumber() + ": " + e.getMsg());
            fail();
        }
    }
}