package hr.fer.oprpp1.hw08.jnotepadpp.models;
/**
 * The interface responsible for defining listeners for a multiple document model.
 * @author Josip Vucić
 *
 */
public interface MultipleDocumentListener {
	/**
	 * Should be executed if the current document has changed.
	 * @param previousModel The previously current model.
	 * @param currentModel The current current model.
	 */
	void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel);
	/**
	 * Should be executed if a document was added to the model.
	 * @param model The added model.
	 */
	void documentAdded(SingleDocumentModel model);
	/**
	 * Should be executed if a document was removed from the model.
	 * @param model The removed model.
	 */
	void documentRemoved(SingleDocumentModel model);
}
