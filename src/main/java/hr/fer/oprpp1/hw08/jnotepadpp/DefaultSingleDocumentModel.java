package hr.fer.oprpp1.hw08.jnotepadpp;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import hr.fer.oprpp1.hw08.jnotepadpp.models.SingleDocumentListener;
import hr.fer.oprpp1.hw08.jnotepadpp.models.SingleDocumentModel;
/**
 * The class responsible for implementing a simple SingleDocumentModel.
 * @author Josip Vucić
 *
 */
public class DefaultSingleDocumentModel implements SingleDocumentModel {
	/**
	 * The file path of the document.
	 */
	Path filepath;
	/**
	 * The text area of the document.
	 */
	JTextArea editor = new JTextArea();
	/**
	 * The modification status of the document.
	 */
	boolean modified;
	/**
	 * List of listeneres attached to the document.
	 */
	List<SingleDocumentListener> listeners;
	/**
	 * Creates a new DefaultDocumentModel with the given arguments.
	 */
	public DefaultSingleDocumentModel(Path filepath, String textContent) {
		this.filepath = filepath == null ? null : filepath.toAbsolutePath();
		this.editor.setText(textContent);
		this.modified = false;
		this.listeners = new ArrayList<>();
		
		editor.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				setModified(true);
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				setModified(true);
				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				setModified(true);
				
			}
		});
	}
	/**
	 * Returns the text area of the document.
	 */
	@Override
	public JTextArea getTextComponent() {
		return editor;
	}
	/**
	 * Returns the file path of the document.
	 */
	@Override
	public Path getFilePath() {
		return filepath;
	}
	/**
	 * Sets the file path of the document if it is not null, throws an exception otherwise. Alerts listeners.
	 */
	@Override
	public void setFilePath(Path path) {
		if(path == null) throw new NullPointerException("Path must not be null");
		
		this.filepath = path.toAbsolutePath();
		alertListenersPathModified();

	}
	/**
	 * Returns the modification status of the document.
	 */
	@Override
	public boolean isModified() {
		return modified;
	}
	/**
	 * Sets the modification status of the document. Alerts listeners.
	 */
	@Override
	public void setModified(boolean modified) {
		this.modified = modified;
		alertListenersContentModified();
	}
	/**
	 * Adds the provided listener to the document. Throws a NullPointerException if the listener is null.
	 */
	@Override
	public void addSingleDocumentListener(SingleDocumentListener l) {
		if(l == null) throw new NullPointerException();
		
		listeners.add(l);

	}
	/**
	 * Removes the provided listener from the document.
	 */
	@Override
	public void removeSingleDocumentListener(SingleDocumentListener l) {
		if(l == null) return;
		
		listeners.remove(l);

	}
	/**
	 * Alerts listeners that the content was modified.
	 */
	private void alertListenersContentModified() {
		listeners.forEach((l) -> {l.documentModifyStatusUpdated(this);});
	}
	/**
	 * Alerts listeners that the path was modified.
	 */
	private void alertListenersPathModified() {
		listeners.forEach((l) -> {l.documentFilePathUpdated(this);});
	}
	/**
	 * Returns true if two documents are of the same path or if the paths are null and the text areas are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DefaultSingleDocumentModel))
			return false;
		DefaultSingleDocumentModel other = (DefaultSingleDocumentModel) obj;
		if (filepath == null) {
			if (other.filepath != null)
				return false;
			if(!this.editor.equals(other.editor))
				return false;
		} else if (!filepath.equals(other.filepath))
			return false;
		return true;
	}
	
	

}
