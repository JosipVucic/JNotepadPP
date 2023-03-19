package hr.fer.oprpp1.hw08.jnotepadpp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import hr.fer.oprpp1.hw08.jnotepadpp.exceptions.FileAlreadyOpenException;
import hr.fer.oprpp1.hw08.jnotepadpp.local.ILocalizationListener;
import hr.fer.oprpp1.hw08.jnotepadpp.local.ILocalizationProvider;
import hr.fer.oprpp1.hw08.jnotepadpp.models.MultipleDocumentListener;
import hr.fer.oprpp1.hw08.jnotepadpp.models.MultipleDocumentModel;
import hr.fer.oprpp1.hw08.jnotepadpp.models.SingleDocumentListener;
import hr.fer.oprpp1.hw08.jnotepadpp.models.SingleDocumentModel;

/**
 * The class responsible for implementing a simple multiple document model as a
 * JTabbedPane.
 * 
 * @author Josip Vucić
 *
 */
public class DefaultMultipleDocumentModel extends JTabbedPane implements MultipleDocumentModel {

	private static final long serialVersionUID = 1L;
	/**
	 * List of open documents.
	 */
	List<SingleDocumentModel> documents = new ArrayList<>();
	/**
	 * The currently open document that is in focus.
	 */
	SingleDocumentModel currentDocument = null;
	/**
	 * The listeners on the model.
	 */
	List<MultipleDocumentListener> listeners = new ArrayList<>();
	/**
	 * The factory responsible for creating tab components.
	 */
	IDocumentTabFactory tabFactory;
	
	/**
	 * The localisation provider.
	 */
	private ILocalizationProvider provider;
	/**
	 * The listener for our localisation provider.
	 */
	private ILocalizationListener listener = () -> {
		for(SingleDocumentModel model : documents) {
			if(model.getFilePath()==null) {
				if (tabFactory != null)
				((IDocumentTab) DefaultMultipleDocumentModel.this.getTabComponentAt(documents.indexOf(model)))
				.setTitle(provider.getString("UnnamedTab"));
				
				DefaultMultipleDocumentModel.this.setTitleAt(documents.indexOf(model),
						provider.getString("UnnamedTab"));
				DefaultMultipleDocumentModel.this.setToolTipTextAt(documents.indexOf(model),
						provider.getString("UnnamedTab"));
			}
		}
	};

	/**
	 * Creates a new DefaultMultipleDocumentModel with the given arguments.
	 * 
	 * @param tabFactory      The factory to create tab components with. If null tab
	 *                        components will have just the title.
	 * @param defaultTabTitle The default title to use for created tabs.
	 */
	public DefaultMultipleDocumentModel(IDocumentTabFactory tabFactory) {
		this.tabFactory = tabFactory;
		this.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				SingleDocumentModel tmp = currentDocument;
				try {
					currentDocument = documents.get(getSelectedIndex());
				} catch (IndexOutOfBoundsException e1) {
					currentDocument = null;
				}
				alertDocChanged(tmp, currentDocument);
			}
		});
	}

	/**
	 * Creates a new DefaultMultipleDocumentModel with the given arguments.
	 * 
	 * @param tabFactory The factory to create tab components with. If null tab
	 *                   components will have just the title.
	 */
	public DefaultMultipleDocumentModel(IDocumentTabFactory tabFactory, ILocalizationProvider provider) {
		this(tabFactory);
		
		if(provider == null) throw new NullPointerException();
		this.provider = provider;
		this.provider.addLocalizationListener(listener);
	}

	/**
	 * Creates a new DefaultMultipleDocumentModel.
	 */
	public DefaultMultipleDocumentModel() {
		this(null);
	}

	/**
	 * Returns an iterator over the open documents.
	 */
	@Override
	public Iterator<SingleDocumentModel> iterator() {
		return documents.iterator();
	}

	/**
	 * Creates a new document. Alerts listeners.
	 */
	@Override
	public SingleDocumentModel createNewDocument() {
		addDocumentTab(null, "");

		alertDocAdded(currentDocument);
		return currentDocument;
	}

	/**
	 * Returns the current document.
	 */
	@Override
	public SingleDocumentModel getCurrentDocument() {
		return currentDocument;
	}

	/**
	 * Loads the document at the provided path. Alerts listeners. If the path is
	 * null a NullPointerException is thrown. If there was an error reading the file
	 * an IllegalArgumentException is thrown.
	 */
	@Override
	public SingleDocumentModel loadDocument(Path path) {
		if (path == null)
			throw new NullPointerException("Path must not be null");

		SingleDocumentModel existing = getExisting(path);

		if (existing != null) {
			SingleDocumentModel tmp = currentDocument;
			currentDocument = existing;
			alertDocChanged(tmp, currentDocument);

			setSelectedIndex(documents.indexOf(currentDocument));
		} else {

			byte[] okteti;
			try {
				okteti = Files.readAllBytes(path);
			} catch (IOException ex) {
				throw new IllegalArgumentException("Error reading file", ex);
			}

			String text = new String(okteti, StandardCharsets.UTF_8);

			addDocumentTab(path, text);
			alertDocAdded(currentDocument);
		}

		return currentDocument;
	}

	/**
	 * Saves the specified document on the specified path. If the path is null the
	 * document is saved on it's old path. If there is no old path an
	 * IllegalStateException is thrown. If the file at the new path is already open
	 * an FileAlreadyOpenException is thrown. If there was an error writing the file
	 * an IllegalArgumentException is thrown.
	 */
	@Override
	public void saveDocument(SingleDocumentModel model, Path newPath) {
		if (newPath == null) {
			newPath = model.getFilePath();
			if (newPath == null) {
				throw new IllegalStateException("Cannot save file because path is null");
			}
		} else if (getExisting(newPath) != null) {
			throw new FileAlreadyOpenException("File at new path is already opened");
		}

		try {
			Files.writeString(newPath, model.getTextComponent().getText());
		} catch (IOException ex) {
			throw new IllegalArgumentException("Error saving file", ex);
		}

		model.setModified(false);
		model.setFilePath(newPath);

	}

	/**
	 * Closes the specified document. If the document is null a NullPointerException
	 * is thrown. Alerts listeners.
	 */
	@Override
	public void closeDocument(SingleDocumentModel model) {
		if (model == null)
			throw new NullPointerException();

		this.removeTabAt(documents.indexOf(model));
		alertDocRemoved(model);
		documents.remove(model);
		try {
			currentDocument = documents.get(getSelectedIndex());
		} catch (IndexOutOfBoundsException e) {
			currentDocument = null;
		}
		alertDocChanged(model, currentDocument);
	}

	/**
	 * Adds the provided listener to the model. If the listener is null a
	 * NullPointerException is thrown.
	 */
	@Override
	public void addMultipleDocumentListener(MultipleDocumentListener l) {
		if (l == null)
			throw new NullPointerException();

		listeners.add(l);

	}

	/**
	 * Removes the provided listener from the model.
	 */
	@Override
	public void removeMultipleDocumentListener(MultipleDocumentListener l) {
		if (l == null)
			return;

		listeners.remove(l);
	}

	/**
	 * Returns the number of open documents.
	 */
	@Override
	public int getNumberOfDocuments() {
		return documents.size();
	}

	/**
	 * Returns the document at the specified index.
	 */
	@Override
	public SingleDocumentModel getDocument(int index) {
		// alertDocChanged(currentDocument, documents.get(index));
		// currentDocument = documents.get(index);
		// return currentDocument;
		return documents.get(index);
	}

	/**
	 * Returns an existing open document if there is one. Null otherwise.
	 * 
	 * @param path The path of the document.
	 * @return The open document or null.
	 */
	private SingleDocumentModel getExisting(Path path) {
		Optional<SingleDocumentModel> existing = documents.stream().filter((doc) -> {
			return path.toAbsolutePath().equals(doc.getFilePath());
		}).findAny();

		if (existing.isEmpty()) {
			return null;
		}

		return existing.get();
	}

	/**
	 * Adds a default listener to the document that will change tab titles and
	 * tool-tips when alerted accordingly.
	 * 
	 * @param doc The document to add the listener to.
	 */
	private void addDefaultListener(SingleDocumentModel doc) {
		doc.addSingleDocumentListener(new SingleDocumentListener() {

			@Override
			public void documentModifyStatusUpdated(SingleDocumentModel model) {
				if (tabFactory != null) {
					if (model.isModified())
						((IDocumentTab) DefaultMultipleDocumentModel.this.getTabComponentAt(documents.indexOf(model)))
								.setIcon(ImageIcons.getIcon("unsaved"));
					else
						((IDocumentTab) DefaultMultipleDocumentModel.this.getTabComponentAt(documents.indexOf(model)))
								.setIcon(ImageIcons.getIcon("saved"));
				} else {
					if (model.isModified())
						setIconAt(documents.indexOf(model), ImageIcons.getIcon("unsaved"));
					else
						setIconAt(documents.indexOf(model), ImageIcons.getIcon("saved"));
				}
			}

			@Override
			public void documentFilePathUpdated(SingleDocumentModel model) {
				if (tabFactory != null) {
					((IDocumentTab) DefaultMultipleDocumentModel.this.getTabComponentAt(documents.indexOf(model)))
							.setTitle(model.getFilePath().getFileName().toString());
				}

				DefaultMultipleDocumentModel.this.setTitleAt(documents.indexOf(model),
						model.getFilePath().getFileName().toString());
				DefaultMultipleDocumentModel.this.setToolTipTextAt(documents.indexOf(model),
						model.getFilePath().toString());

			}
		});
	}

	/**
	 * Adds a new document tab to the pane using the provided path and the text of
	 * the document.
	 * 
	 * @param path The provided path, may be null.
	 * @param text The provided text, may be empty.
	 */
	private void addDocumentTab(Path path, String text) {
		String defaultTitle = provider == null ? "(unnamed)" : provider.getString("UnnamedTab");
		
		String title = path == null ? defaultTitle : path.getFileName().toString();
		String tooltip = path == null ? defaultTitle : path.toString();

		SingleDocumentModel tmp = currentDocument;
		currentDocument = new DefaultSingleDocumentModel(path, text);
		alertDocChanged(tmp, currentDocument);

		documents.add(currentDocument);
		JScrollPane scrollPane = new JScrollPane(currentDocument.getTextComponent());
		insertTab(title, ImageIcons.getIcon("saved"), scrollPane, tooltip, getTabCount());

		if (tabFactory != null)
			setTabComponentAt(getTabCount() - 1, tabFactory.createTab(title, currentDocument));

		setSelectedIndex(getTabCount() - 1);

		addDefaultListener(currentDocument);
	}

	/**
	 * Alerts listeners that a document was added.
	 * 
	 * @param model The added document.
	 */
	private void alertDocAdded(SingleDocumentModel model) {
		listeners.forEach((l) -> l.documentAdded(model));
	}

	/**
	 * Alerts listeners that a document was removed.
	 * 
	 * @param model The removed document.
	 */
	private void alertDocRemoved(SingleDocumentModel model) {
		listeners.forEach((l) -> l.documentRemoved(model));
	}

	/**
	 * Alerts listeners that the current document has changed.
	 * 
	 * @param prev The previously current document.
	 * @param next The current current document.
	 */
	private void alertDocChanged(SingleDocumentModel prev, SingleDocumentModel next) {
		listeners.forEach((l) -> l.currentDocumentChanged(prev, next));
	}

}
