package hr.fer.oprpp1.hw08.jnotepadpp.exceptions;
/**
 * The exception to throw if a file already exists.
 * @author Josip Vucić
 *
 */
public class FileAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	/**
	 * Creates a new exception with the provided message string.
	 * @param msg The message string.
	 */
	public FileAlreadyExistsException(String msg) {
		super(msg);
	}
}
