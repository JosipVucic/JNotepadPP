package hr.fer.oprpp1.hw08.jnotepadpp.models;

import java.nio.file.Path;
/**
 * The interface responsible for defining multiple document models.
 * @author Josip Vucić
 *
 */
public interface MultipleDocumentModel extends Iterable<SingleDocumentModel> {
	/**
	 * Creates a new document and adds it to the model. May also make it the currently selected document.
	 * @return The created document.
	 */
	SingleDocumentModel createNewDocument();
	/**
	 * 
	 * @return The currently selected document.
	 */
	SingleDocumentModel getCurrentDocument();
	/**
	 * Loads a new document from the given path and adds it to the model. May also make it the currently selected document.
	 * @param path The provided path.
	 * @return The loaded document.
	 */
	SingleDocumentModel loadDocument(Path path);
	/**
	 * Saves the provided document to the provided path. May save it to its default path if the provided path is null.
	 * @param model The provided document.
	 * @param newPath The save path.
	 */
	void saveDocument(SingleDocumentModel model, Path newPath);
	/**
	 * Closes the provided document removing it from the model.
	 * @param model The provided document.
	 */
	void closeDocument(SingleDocumentModel model);
	/**
	 * Adds the provided listener to this model.
	 * @param l
	 */
	void addMultipleDocumentListener(MultipleDocumentListener l);
	/**
	 * Removes the provided listener from this model.
	 * @param l
	 */
	void removeMultipleDocumentListener(MultipleDocumentListener l);
	/**
	 * 
	 * @return The number of currently open documents.
	 */
	int getNumberOfDocuments();
	/**
	 * 
	 * @param index The given index.
	 * @return The document at the given index.
	 */
	SingleDocumentModel getDocument(int index);
}
