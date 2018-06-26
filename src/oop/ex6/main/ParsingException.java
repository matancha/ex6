package oop.ex6.main;

/**
 * Abstract Exception
 */
public abstract class ParsingException extends Exception {
	/**
	 * Returns a message detailing the reason for failure
	 * @return informative String for error cause
	 */
	public abstract String getMessage();
}
