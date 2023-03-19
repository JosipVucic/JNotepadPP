package hr.fer.oprpp1.hw08.jnotepadpp.models;
/**
 * The interface responsible for defining listeners for single document models.
 * @author Josip Vucić
 *
 */
public interface SingleDocumentListener {
	/**
	 * Should be executed if the modification status of the document was updated.
	 * @param model The affected document
	 */
	void documentModifyStatusUpdated(SingleDocumentModel model);
	/**
	 * Should be executed if the file path of the document was updated.
	 * @param model The affected document
	 */
	void documentFilePathUpdated(SingleDocumentModel model);
}
