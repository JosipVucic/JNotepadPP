package hr.fer.oprpp1.hw08.jnotepadpp;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.Icon;

import hr.fer.oprpp1.hw08.jnotepadpp.models.SingleDocumentModel;
/**
 * The class responsible for implementing a simple document tab factory for our application needs.
 * @author Josip Vucić
 *
 */
public class DocumentTabFactory implements IDocumentTabFactory {
	/**
	 * The default tab icon to use.
	 */
	private Icon defaultTabIcon;
	/**
	 * The default tab close action to use.
	 */
	private Action defaultTabCloseAction;
	/**
	 * Creates a new document tab factory.
	 */
	public DocumentTabFactory() {
		this(null, null);
	}
	/**
	 * Creates a new document tab factory with the given default arguments.
	 * @param tabIcon The default tab icon to use.
	 * @param closeAction The default tab close action to use.
	 */
	public DocumentTabFactory(Icon tabIcon, Action closeAction) {
		this.defaultTabIcon = tabIcon;
		this.defaultTabCloseAction = closeAction;
	}
	/**
	 * Creates a new document tab with the given arguments.
	 */
	@Override
	public Component createTab(String title, Icon icon, Action tabClose, SingleDocumentModel document) {
		return new DocumentTab(title, icon, tabClose, document);
	}
	/**
	 * Creates a new document tab with the given arguments using the default tab icon and default tab close action (These should be set in the factory constructor).
	 */
	@Override
	public Component createTab(String title, SingleDocumentModel document) {
		// TODO Auto-generated method stub
		return new DocumentTab(title, defaultTabIcon, defaultTabCloseAction, document);
	}

}
