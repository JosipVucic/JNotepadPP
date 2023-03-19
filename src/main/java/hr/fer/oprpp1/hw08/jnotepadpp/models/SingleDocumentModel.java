package hr.fer.oprpp1.hw08.jnotepadpp.models;

import java.nio.file.Path;

import javax.swing.JTextArea;
/**
 * The interface responsible for defining a single document model.
 * @author Josip Vucić
 *
 */
public interface SingleDocumentModel {
	/**
	 * 
	 * @return The text area of the model.
	 */
	JTextArea getTextComponent();
	/**
	 * 
	 * @return The file path of the document.
	 */
	Path getFilePath();
	/**
	 * Sets the file path of the document to the given path.
	 * @param path
	 */
	void setFilePath(Path path);
	/**
	 * 
	 * @return The modification status of the model.
	 */
	boolean isModified();
	/**
	 * Sets the modification status of the model.
	 * @param modified The new modification status.
	 */
	void setModified(boolean modified);
	/**
	 * Adds the given listener to the model.
	 * @param l
	 */
	void addSingleDocumentListener(SingleDocumentListener l);
	/**
	 * Removes the given listener from the model.
	 * @param l
	 */
	void removeSingleDocumentListener(SingleDocumentListener l);

}
