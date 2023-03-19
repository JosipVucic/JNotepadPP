package hr.fer.oprpp1.hw08.jnotepadpp.exceptions;
/**
 * The exception to throw if a file is already open in the app.
 * @author Josip Vucić
 *
 */
public class FileAlreadyOpenException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	/**
	 * Creates a new exception with the provided message string.
	 * @param msg The message string.
	 */
	public FileAlreadyOpenException(String msg) {
		super(msg);
	}
}
