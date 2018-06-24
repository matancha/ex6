package oop.ex6.main;
import java.io.*;

public class Sjavac {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("2");
		}

		String codePath = args[0];
		Parser parseObj = new Parser();
		try {
			parseObj.parse(codePath);
			System.out.println("0");
		} catch (ParsingException e) {
			System.out.println("1");
		} catch (IOError|FileNotFoundException e) {
			System.out.println("2");
		}
	}
}
