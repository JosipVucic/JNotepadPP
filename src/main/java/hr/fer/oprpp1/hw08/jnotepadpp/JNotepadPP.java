package hr.fer.oprpp1.hw08.jnotepadpp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import hr.fer.oprpp1.hw08.jnotepadpp.exceptions.FileAlreadyOpenException;
import hr.fer.oprpp1.hw08.jnotepadpp.local.FormLocalizationProvider;
import hr.fer.oprpp1.hw08.jnotepadpp.local.LocalizableAction;
import hr.fer.oprpp1.hw08.jnotepadpp.local.LocalizationProvider;
import hr.fer.oprpp1.hw08.jnotepadpp.models.MultipleDocumentListener;
import hr.fer.oprpp1.hw08.jnotepadpp.models.MultipleDocumentModel;
import hr.fer.oprpp1.hw08.jnotepadpp.models.SingleDocumentListener;
import hr.fer.oprpp1.hw08.jnotepadpp.models.SingleDocumentModel;
/**
 * The class responsible for defining the frame and behaviour of our JNotepad++ app.
 * @author Josip Vucić
 *
 */
public class JNotepadPP extends JFrame {

	private static final long serialVersionUID = 1L;
	/**
	 * The model that contains our open documents.
	 */
	private MultipleDocumentModel docTabs;
	/**
	 * The status bar for displaying information.
	 */
	private StatusBar statusBar;
	/**
	 * The localization provider.
	 */
	private FormLocalizationProvider provider = new FormLocalizationProvider(LocalizationProvider.getInstance(), this);
	/**
	 * Creates a new JNotepad++ frame.
	 */
	public JNotepadPP() {

		// Iz nekog razloga ovo ne radi
		// provider = new FormLocalizationProvider(LocalizationProvider.getInstance(),
		// this);

		System.out.println(provider.getString("TestMessage"));

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(750, 600);
		setTitle("JNotepad++");

		initGUI();

		WindowListener wl = new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (!hasUnsavedChanges()) {
					dispose();
					return;
				}
				showUnsavedChangesExit(null, true);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				statusBar.stopClock();
			}
		};
		this.addWindowListener(wl);

		setLocationRelativeTo(null);
	}
	/**
	 * Initializes the GUI.
	 */
	private void initGUI() {

		this.getContentPane().setLayout(new BorderLayout());
		docTabs = new DefaultMultipleDocumentModel(new DocumentTabFactory(ImageIcons.getIcon("saved"), closeTabAction), provider);

		docTabs.addMultipleDocumentListener(new MultipleDocumentListener() {

			@Override
			public void documentRemoved(SingleDocumentModel model) {
			}

			@Override
			public void documentAdded(SingleDocumentModel model) {
				model.addSingleDocumentListener(new SingleDocumentListener() {

					@Override
					public void documentModifyStatusUpdated(SingleDocumentModel model) {
						if (model.equals(docTabs.getCurrentDocument())) {
							saveDocumentAction.setEnabled(model.isModified());
						}

					}

					@Override
					public void documentFilePathUpdated(SingleDocumentModel model) {
						if (model.equals(docTabs.getCurrentDocument())) {
							JNotepadPP.this.setTitle(model.getFilePath().toString() + " - JNotepad++");
						}

					}
				});
			}

			@Override
			public void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel) {
				if (currentModel == null) {
					toggleDocumentActionStates(false);
					toggleSelectionActions(false);
					saveDocumentAction.setEnabled(false);
					statusBar.setStatus(0, 1, 1, 0);
					JNotepadPP.this.setTitle("JNotepad++");
					return;
				} else if (currentModel.getFilePath() == null) {
					JNotepadPP.this.setTitle("(unnamed) - JNotepad++");
				} else {
					JNotepadPP.this.setTitle(currentModel.getFilePath().toString() + " - JNotepad++");
				}

				toggleDocumentActionStates(true);

				JTextComponent editor = currentModel.getTextComponent();

				int len = Math.abs(editor.getCaret().getDot() - editor.getCaret().getMark());
				toggleSelectionActions(len != 0);

				saveDocumentAction.setEnabled(currentModel.isModified());

				updateStatus(editor);

			}
		});

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add((JTabbedPane) docTabs, BorderLayout.CENTER);
		statusBar = new StatusBar(provider);
		this.getContentPane().add(statusBar, BorderLayout.SOUTH);

		createActions();
		createMenus();
		createToolbars(contentPane);

		toggleDocumentActionStates(false);
		toggleSelectionActions(false);
		saveDocumentAction.setEnabled(false);

		this.getContentPane().add(contentPane);

	}
	/**
	 * Toggles actions pertaining to open documents on or off according to the provided boolean state.
	 * @param state The desired state for the actions.
	 */
	private void toggleDocumentActionStates(boolean state) {
		// saveDocumentAction.setEnabled(state);
		saveAsDocumentAction.setEnabled(state);
		pasteSelectedPartAction.setEnabled(state);
		statsAction.setEnabled(state);
		closeDocumentAction.setEnabled(state);
	}
	/**
	 * The action responsible for creating documents.
	 */
	private Action createDocumentAction = new LocalizableAction("Create", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				addDefaultCaretListener(docTabs.createNewDocument());
			} catch (Exception ex) {
				showError(provider.getString("ErrorCreatingFileMessage"), provider.getString("ErrorCreatingFile"));

				return;
			}

			// openedFilePath = null;
		}
	};
	/**
	 * The action responsible for opening documents.
	 */
	private Action openDocumentAction = new LocalizableAction("Open", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Open file");
			if (fc.showOpenDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File fileName = fc.getSelectedFile();
			Path filePath = fileName.toPath();

			if (!Files.isReadable(filePath)) {
				showError(provider.getString("FileNotExistMessage") + " " + fileName.getAbsolutePath().toString(),
						provider.getString("ErrorReadingFile"));

				return;
			}

			try {
				addDefaultCaretListener(docTabs.loadDocument(filePath));
			} catch (Exception ex) {
				showError(provider.getString("ErrorReadingFileMessage") + " " + fileName.getAbsolutePath().toString(),
						provider.getString("ErrorReadingFile"));

				return;
			}

			// openedFilePath = filePath;
		}
	};
	/**
	 * The action responsible for saving documents.
	 */
	private Action saveDocumentAction = new LocalizableAction("Save", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			saveSingleAction(docTabs.getCurrentDocument(), docTabs.getCurrentDocument().getFilePath());
		}
	};
	/**
	 * The action responsible for saving documents in a different path or format.
	 */
	private Action saveAsDocumentAction = new LocalizableAction("SaveAs", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			saveSingleAction(docTabs.getCurrentDocument(), null);
		}
	};
	/**
	 * The action responsible for cutting text content.
	 */
	private Action cutSelectedPartAction = new LocalizableAction("Cut", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			editSelectedAction(true, true, true, false);
		}
	};
	/**
	 * The action responsible for copying text content.
	 */
	private Action copySelectedPartAction = new LocalizableAction("Copy", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			editSelectedAction(true, false, true, false);
		}
	};
	/**
	 * The action responsible for pasting text content.
	 */
	private Action pasteSelectedPartAction = new LocalizableAction("Paste", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			editSelectedAction(false, true, false, true);
		}
	};
	/**
	 * The action responsible for inverting the case of text content.
	 */
	private Action invertCaseAction = new LocalizableAction("InvertCase", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			changeCaseAction(txt -> invertCase(txt));
		}

	};
	/**
	 * The action responsible for switching the case of text content to uppercase.
	 */
	private Action upperCaseAction = new LocalizableAction("UpperCase", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			changeCaseAction(txt -> txt.toUpperCase());
		}

	};
	/**
	 * The action responsible for switching the case of text content to lowercase.
	 */
	private Action lowerCaseAction = new LocalizableAction("LowerCase", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			changeCaseAction(txt -> txt.toLowerCase());
		}

	};
	/**
	 * The action responsible for exitting the application.
	 */
	private Action exitAction = new LocalizableAction("Exit", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	};
	/**
	 * The action responsible for closing the current document.
	 */
	private Action closeDocumentAction = new LocalizableAction("Close", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (docTabs.getCurrentDocument().isModified()) {
				showUnsavedChangesExit(docTabs.getCurrentDocument(), false);
			}

			docTabs.closeDocument(docTabs.getCurrentDocument());
		}
	};
	/**
	 * The action responsible for closing a particular document.
	 */
	private Action closeTabAction = new LocalizableAction("Close", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			
			Component src = (Component)e.getSource();
			while(!(src instanceof IDocumentTab)) {
				src = src.getParent();
			}
			IDocumentTab tab = (IDocumentTab) src;
			SingleDocumentModel document = tab.getDocument();

			if (tab.getDocument().isModified()) {
				showUnsavedChangesExit(tab.getDocument(), false);
			}

			docTabs.closeDocument(document);
		}
	};
	/**
	 * The action responsible for displaying document statistics for the current document.
	 */
	private Action statsAction = new LocalizableAction("Statistics", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			long[] stats = calculateStats();
			String message = String.format(provider.getString("StatsMessage"), stats[0], stats[1], stats[2]);

			showInfo(message, provider.getString("Statistics"));
		}
	};
	/**
	 * The action responsible for sorting text content ascending.
	 */
	private Action sortAscendingAction = new LocalizableAction("Ascending", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			sortAction(true, false);

		}
	};
	/**
	 * The action responsible for sorting text content descending.
	 */
	private Action sortDescendingAction = new LocalizableAction("Descending", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			sortAction(false, false);

		}

	};
	/**
	 * The action responsible for removing duplicate lines from text content.
	 */
	private Action UniqueAction = new LocalizableAction("Unique", provider) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			sortAction(null, true);

		}

	};
	
	/**
	 * Initialises the action shortcut keys.
	 */
	private void createActions() {

		createDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
		openDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
		saveDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
		saveAsDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F12"));
		cutSelectedPartAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
		copySelectedPartAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
		pasteSelectedPartAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
		invertCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F3"));
		upperCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift F3"));
		lowerCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt F3"));
		sortAscendingAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt UP"));
		sortDescendingAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt DOWN"));
		UniqueAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control U"));
		statsAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control I"));
		exitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Q"));
		
		closeTabAction.putValue(Action.LARGE_ICON_KEY, ImageIcons.getIcon("close"));
		closeTabAction.putValue(Action.NAME, null);
	}
	/**
	 * Creates the menus.
	 */
	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new LocalizableMenu("File", provider);
		menuBar.add(fileMenu);

		fileMenu.add(new JMenuItem(createDocumentAction));
		fileMenu.add(new JMenuItem(openDocumentAction));
		fileMenu.add(new JMenuItem(saveDocumentAction));
		fileMenu.add(new JMenuItem(saveAsDocumentAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(statsAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(closeDocumentAction));
		fileMenu.add(new JMenuItem(exitAction));

		JMenu editMenu = new LocalizableMenu("Edit", provider);
		menuBar.add(editMenu);

		editMenu.add(new JMenuItem(cutSelectedPartAction));
		editMenu.add(new JMenuItem(copySelectedPartAction));
		editMenu.add(new JMenuItem(pasteSelectedPartAction));

		JMenu toolsMenu = new LocalizableMenu("Tools", provider);
		menuBar.add(toolsMenu);

		JMenu changeCaseMenu = new LocalizableMenu("ChangeCase", provider);
		toolsMenu.add(changeCaseMenu);

		changeCaseMenu.add(new JMenuItem(upperCaseAction));
		changeCaseMenu.add(new JMenuItem(lowerCaseAction));
		changeCaseMenu.add(new JMenuItem(invertCaseAction));

		JMenu sortMenu = new LocalizableMenu("Sort", provider);
		toolsMenu.add(sortMenu);

		sortMenu.add(new JMenuItem(sortAscendingAction));
		sortMenu.add(new JMenuItem(sortDescendingAction));
		
		toolsMenu.add(new JMenuItem(UniqueAction));

		JMenu languageMenu = new LocalizableMenu("Language", provider);
		menuBar.add(languageMenu);

		languageMenu.add(new JMenuItem(switchLang("hr")));
		languageMenu.add(new JMenuItem(switchLang("en")));
		languageMenu.add(new JMenuItem(switchLang("de")));

		this.setJMenuBar(menuBar);
	}
	/**
	 * Creates the toolbars and puts them on the content pane that is intended for toolbar docking.
	 * @param contentPane
	 */
	private void createToolbars(JPanel contentPane) {
		JToolBar toolBar = new JToolBar(provider.getString("Tools"));
		toolBar.setFloatable(true);

		toolBar.add(new JButton(createDocumentAction));
		toolBar.add(new JButton(openDocumentAction));
		toolBar.add(new JButton(saveDocumentAction));
		toolBar.add(new JButton(saveAsDocumentAction));
		toolBar.add(new JButton(statsAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(cutSelectedPartAction));
		toolBar.add(new JButton(copySelectedPartAction));
		toolBar.add(new JButton(pasteSelectedPartAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(closeDocumentAction));
		toolBar.add(new JButton(exitAction));

		contentPane.add(toolBar, BorderLayout.PAGE_START);
	}
	/**
	 * 
	 * @return True if there are unsaved documents.
	 */
	private boolean hasUnsavedChanges() {
		for (var doc : docTabs) {
			if (doc.isModified())
				return true;
		}
		return false;
	}
	/**
	 * Saves the specified document to the specified path. If path is null the document is saved to its default path.
	 * Displays success message afterwards.
	 * @param document The document to be saved.
	 * @param documentSavedPath The path to save it to.
	 */
	private void saveSingleAction(SingleDocumentModel document, Path documentSavedPath) {
		saveAction(document, documentSavedPath);
		showInfo(provider.getString("SaveMessage"), provider.getString("Information"));
	}
	/**
	 * Saves all open documents.
	 * Displays success message afterwards.
	 */
	private void saveAllAction() {
		for (var doc : docTabs) {
			if(doc.isModified())
			saveAction(doc, doc.getFilePath());
		}
		showInfo(provider.getString("SaveMessage"), provider.getString("Information"));
	}
	/**
	 * Saves the specified document to the specified path. If path is null the document is saved to its default path.
	 * @param document The document to be saved.
	 * @param documentSavedPath The path to save it to.
	 */
	private void saveAction(SingleDocumentModel document, Path documentSavedPath) {
		Path newSavePath = null;
		while (documentSavedPath == null) {
			newSavePath = chooseFile();

			if (newSavePath == null)
				return;
			if (Files.exists(newSavePath)) {
				String[] options = new String[] { provider.getString("Yes"), provider.getString("No"),
						provider.getString("Cancel") };
				int res = JOptionPane.showOptionDialog(JNotepadPP.this, provider.getString("OverwriteMessage"),
						provider.getString("Warning"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
						options, options[0]);

				switch (res) {
				case JOptionPane.CLOSED_OPTION -> {
					return;
				}
				case 0 -> {
				}
				case 1 -> {
					continue;
				}
				case 2 -> {
					return;
				}
				}
			}
			documentSavedPath = newSavePath;
		}
		try {
			docTabs.saveDocument(document, newSavePath);
		} catch (IllegalArgumentException e1) {
			showError(provider.getString("ErrorWritingFileMessage"), provider.getString("ErrorWritingFile"));

			return;
		} catch (FileAlreadyOpenException e2) {
			showError(provider.getString("FileAlreadyOpenMessage") + " " + documentSavedPath.toFile().getAbsolutePath(),
					provider.getString("ErrorWritingFile"));

			return;
		} catch (IllegalStateException e4) {
			showError(provider.getString("FilePathNullMessage"), provider.getString("ErrorWritingFile"));

			return;
		}

	}
	/**
	 * Displays a dialog for choosing a file.
	 * @return The path of the chosen file.
	 */
	private Path chooseFile() {
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Save document");
		if (jfc.showSaveDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
			showInfo(provider.getString("SaveAbortedMessage"), provider.getString("OperationAborted"));

			return null;
		}
		return jfc.getSelectedFile().toPath();
	}
	/**
	 * Edits the selected text according to the boolean flags.
	 * @param needsSelection A selection is needed to apply the action.
	 * @param deletesSelection The selected part will be deleted.
	 * @param copiesToClipboard The selected part will be copied to clipboard first.
	 * @param pastesFromClipboard Clipboard content will be pasted to the caret position.
	 */
	private void editSelectedAction(boolean needsSelection, boolean deletesSelection, boolean copiesToClipboard,
			boolean pastesFromClipboard) {
		JTextComponent editor = docTabs.getCurrentDocument().getTextComponent();
		Document doc = editor.getDocument();

		int len = Math.abs(editor.getCaret().getDot() - editor.getCaret().getMark());
		if (len == 0 && needsSelection) {
			return;
		}

		int offset = Math.min(editor.getCaret().getDot(), editor.getCaret().getMark());
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			String paste = "";
			if (pastesFromClipboard) {
				paste = clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor).toString();
			}
			if (copiesToClipboard) {
				Transferable transferable = new StringSelection(doc.getText(offset, len));
				clipboard.setContents(transferable, null);
			}
			if (deletesSelection) {
				doc.remove(offset, len);
			}
			if (pastesFromClipboard) {
				doc.insertString(offset, paste, null);
			}
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		} catch (UnsupportedFlavorException e) {
			showError(provider.getString("ErrorPastingContentInvalidMessage"),
					provider.getString("ErrorPastingContent"));

			return;
		} catch (IOException e) {
			showError(provider.getString("ErrorPastingContentMessage"), provider.getString("ErrorPastingContent"));

			return;
		}
	}
	
	/**
	 * Sorts the selected lines according to the provided sorted Boolean. If it is null the sorting is not applied. If distinct is true, duplicate lines will be removed from the selection.
	 * @param sorted The sorting Boolean.
	 * @param distinct The distinct boolean.
	 */
	private void sortAction(Boolean sorted, boolean distinct) {

		JTextComponent editor = docTabs.getCurrentDocument().getTextComponent();
		Caret caret = editor.getCaret();
		int caretPosition = caret.getDot();
		int markPosition = caret.getMark();

		Document doc = editor.getDocument();
		Element root = doc.getDefaultRootElement();
		int rowStart = root.getElementIndex(Math.min(caretPosition, markPosition));
		int rowEnd = root.getElementIndex(Math.max(caretPosition, markPosition));

		int startOffset = root.getElement(rowStart).getStartOffset();
		int endOffset = root.getElement(rowEnd).getEndOffset();
		int len = Math.abs(endOffset - startOffset);
		
		String selected = "";
		try {
			selected = doc.getText(startOffset, len - 1);

			String[] lines = selected.split("\n", -1);
			
			if (sorted != null) {
				Locale locale = new Locale(provider.getCurrentLanguage());
				Collator collator = Collator.getInstance(locale);
				Comparator<Object> comparator;
				
				if (!sorted.booleanValue())
					comparator = collator.reversed();
				else
					comparator = collator;
				
				System.out.println(selected);
				lines = Arrays.stream(lines).sorted(comparator).collect(Collectors.toList()).toArray(new String[0]);
			}
			if (distinct)
				lines = Arrays.stream(lines).distinct().collect(Collectors.toList()).toArray(new String[0]);

			doc.remove(startOffset, len - 1);
			doc.insertString(startOffset, String.join("\n", lines), null);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Adds a default caret listener to the specified document.
	 * The listener will update status of the statusBar an toggle actions pertaining to selected text.
	 * @param document The specified document.
	 */
	private void addDefaultCaretListener(SingleDocumentModel document) {
		JTextComponent editor = document.getTextComponent();
		editor.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				if (docTabs.getCurrentDocument().equals(document)) {
					int len = Math.abs(editor.getCaret().getDot() - editor.getCaret().getMark());

					toggleSelectionActions(len != 0);

					updateStatus(editor);
				}
			}
		});
	}
	/**
	 * Toggles actions pertaining to selected text on or off according to state boolean.
	 * @param state The desired state of the action.
	 */
	private void toggleSelectionActions(boolean state) {
		cutSelectedPartAction.setEnabled(state);
		copySelectedPartAction.setEnabled(state);
		invertCaseAction.setEnabled(state);
		upperCaseAction.setEnabled(state);
		lowerCaseAction.setEnabled(state);
		sortAscendingAction.setEnabled(state);
		sortDescendingAction.setEnabled(state);
		UniqueAction.setEnabled(state);
	}
	/**
	 * Inverts the case of the characters in the text.
	 * @param text The text.
	 * @return The inverted case text.
	 */
	private String invertCase(String text) {
		char[] znakovi = text.toCharArray();
		for (int i = 0; i < znakovi.length; i++) {
			char c = znakovi[i];
			if (Character.isLowerCase(c)) {
				znakovi[i] = Character.toUpperCase(c);
			} else if (Character.isUpperCase(c)) {
				znakovi[i] = Character.toLowerCase(c);
			}
		}
		return new String(znakovi);
	}
	/**
	 * Changes the case of the selected text according to the modifier function.
	 * @param modifier The modifier function.
	 */
	private void changeCaseAction(Function<String, String> modifier) {
		JTextComponent editor = docTabs.getCurrentDocument().getTextComponent();
		Document doc = editor.getDocument();

		int len = Math.abs(editor.getCaret().getDot() - editor.getCaret().getMark());
		int offset = 0;
		if (len != 0) {
			offset = Math.min(editor.getCaret().getDot(), editor.getCaret().getMark());
		} else {
			return;
		}

		try {
			String text = doc.getText(offset, len);
			text = modifier.apply(text);
			doc.remove(offset, len);
			doc.insertString(offset, text, null);
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * Creates an action that switches the app language to the provided key.
	 * @param key The language key
	 * @return The action that will change the language according to the key.
	 */
	private LocalizableAction switchLang(String key) {
		return new LocalizableAction(key, provider) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				LocalizationProvider.getInstance().setLanguage(key);

			}
		};
	}
	/**
	 * Updates the status bar according to the provided text component.
	 * @param editor The provided text component.
	 */
	private void updateStatus(JTextComponent editor) {
		Caret caret = editor.getCaret();

		int caretPosition = editor.getCaretPosition();
		Document doc = editor.getDocument();
		Element root = doc.getDefaultRootElement();

		int len = (int) calculateStats()[0];
		int line = root.getElementIndex(caretPosition);
		int column = caretPosition - root.getElement(line).getStartOffset();
		int selection = Math.abs(caret.getDot() - caret.getMark());

		statusBar.setStatus(len, line + 1, column + 1, selection);
	}
	/**
	 * Calculates the statistics of the currently open document.
	 * @return A long array of 3 elements - { numberOfCharacters, numberOfNBcharacters, numberOfLines }
	 */
	private long[] calculateStats() {
		String text = docTabs.getCurrentDocument().getTextComponent().getText();
		long numberOfCharacters = 0;
		long numberOfNBcharacters = 0;
		long numberOfLines = 1;

		numberOfCharacters = text.chars().count();
		numberOfNBcharacters = text.chars().filter((c) -> !Character.isWhitespace(c)).count();
		numberOfLines = docTabs.getCurrentDocument().getTextComponent().getLineCount();

		return new long[] { numberOfCharacters, numberOfNBcharacters, numberOfLines };
	}
	/**
	 * Displays an error dialog with the provided message and title.
	 * @param message The provided message.
	 * @param title The provided title.
	 */
	private void showError(String message, String title) {
		String[] options = new String[] { provider.getString("Ok") };
		JOptionPane.showOptionDialog(JNotepadPP.this, message, title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE, null, options, options[0]);
	}

	/*private void showWarning(String message, String title) {
		String[] options = new String[] { provider.getString("Ok") };
		JOptionPane.showOptionDialog(JNotepadPP.this, message,
				title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
				null, options, options[0]);
	}*/
	
	/**
	 * Displays an info dialog with the provided message and title.
	 * @param message The provided message.
	 * @param title The provided title.
	 */
	private void showInfo(String message, String title) {
		String[] options = new String[] { provider.getString("Ok") };
		JOptionPane.showOptionDialog(JNotepadPP.this, message, title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
	}
	/**
	 * Displays a warning unsaved changes dialog.
	 * @param document The document that needs saving. Null if all documents are to be saved.
	 * @param exit Whether the application should exit after saving.
	 */
	private void showUnsavedChangesExit(SingleDocumentModel document, boolean exit) {
		String[] options = new String[] { provider.getString("Yes"), provider.getString("No"),
				provider.getString("Cancel") };
		int res = JOptionPane.showOptionDialog(JNotepadPP.this, provider.getString("UnsavedChangesMessage"),
				provider.getString("Warning"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
				options[0]);

		switch (res) {
		case JOptionPane.CLOSED_OPTION -> {
			return;
		}
		case 0 -> {
			if (document == null) {
				saveAllAction();
			} else {
				saveSingleAction(document, document.getFilePath());
			}
			if (exit)
				dispose();
			return;
		}
		case 1 -> {
			if (exit)
				dispose();
			return;
		}
		case 2 -> {
			return;
		}
		}
	}
	/**
	 * The main function to start our app frame.
	 * @param args Irrelevant for now.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new JNotepadPP().setVisible(true);
			}
		});

	}

}
