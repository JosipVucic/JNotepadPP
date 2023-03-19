package hr.fer.oprpp1.hw08.jnotepadpp;

import javax.swing.Action;
import javax.swing.Icon;

import hr.fer.oprpp1.hw08.jnotepadpp.models.SingleDocumentModel;
/**
 * The interface responsible for defining document tabs - The part of a tabbed pane that contains the title, icon, close button etc.
 * This interface is intended to be implemented by classes that extend the java.awt.Component class.
 * @author Josip Vucić
 *
 */
public interface IDocumentTab {
	/**
	 * Sets the title of the tab.
	 * @param title
	 */
	void setTitle(String title);
	/**
	 * Sets the icon of the tab.
	 * @param icon
	 */
	void setIcon(Icon icon);
	/**
	 * Gets the document the tab pertains to.
	 * @return
	 */
	SingleDocumentModel getDocument();
	/**
	 * Sets the action to use for the close button.
	 * @param action
	 */
	void setCloseAction(Action action);
	
}
