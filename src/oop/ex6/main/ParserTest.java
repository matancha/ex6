package oop.ex6.main;

import org.junit.*;
import java.util.*;

public class ParserTest {
	@Test
	public void emptyLinesFile() {
		Scanner scanner = new Scanner("empty_lines.txt");
		Parser parseObj = new Parser(scanner);
		try {
			Assert.assertTrue(parseObj.parse());
		} catch (ParsingException e) {
			System.out.println(parseObj.getLineNumber() + ": " + e.getMsg());
		}
	}
}