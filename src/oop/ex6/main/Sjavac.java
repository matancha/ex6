package oop.ex6.main;
import java.io.*;

/**
 * Class running the main flow of the programs
 */
public class Sjavac {
	/* Code Path index in arguments array */
	private static final int FILE_PATH_INDEX = 0;
	/* Valid code exit code */
	private static final String VALID_CODE_EXIT_CODE = "0";
	/* Invalid code exit code */
	private static final String INVALID_CODE_EXIT_CODE = "1";
	/* Program error exit code */
	private static final String PROGRAM_ERROR_EXIT_CODE = "2";
	/* Number of arguments programs is supposed to get */
	private static final int NUM_ARGUMENTS = 1;
	/* Error message if program is given invalid number of arguments */
	private static final String INVALID_NUM_ARGUMENTS_MESSAGE =
			"Sjavac should be supplied with file_path";

	/**
	 * Main program function
	 * @param args file_path - path to code to validate
	 */
	public static void main(String[] args) {
		if (args.length != NUM_ARGUMENTS) {
			System.out.println(PROGRAM_ERROR_EXIT_CODE);
			System.err.println(INVALID_NUM_ARGUMENTS_MESSAGE);
			return;
		}

		String codePath = args[FILE_PATH_INDEX];
		Parser parseObj = new Parser();
		try {
			parseObj.parse(codePath);
			System.out.println(VALID_CODE_EXIT_CODE);
		} catch (ParsingException e) {
			System.out.println(INVALID_CODE_EXIT_CODE);
			System.err.println(e.getMessage());
		} catch (IOError|FileNotFoundException e) {
			System.out.println(PROGRAM_ERROR_EXIT_CODE);
			System.err.println(e.getMessage());
		}
	}
}
