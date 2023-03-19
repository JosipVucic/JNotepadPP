package hr.fer.oprpp1.hw08.jnotepadpp;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.Icon;

import hr.fer.oprpp1.hw08.jnotepadpp.models.SingleDocumentModel;
/**
 * The interface responsible for defining Document tab factories.
 * The components returned by methods of this factory should implement the IDocumentTab interface.
 * @author Josip Vucić
 *
 */
public interface IDocumentTabFactory {
	/**
	 * Creates a document tab with the given arguments.
	 * @param title The tab title
	 * @param icon The tab icon.
	 * @param tabClose The action for closing the tab.
	 * @param document The document the tab pertains to.
	 * @return The tab component.
	 */
	Component createTab(String title, Icon icon, Action tabClose, SingleDocumentModel document);
	/**
	 * Creates a document tab with the given arguments.
	 * @param title The tab title
	 * @param document The document the tab pertains to.
	 * @return The tab component.
	 */
	Component createTab(String title, SingleDocumentModel document);

}
