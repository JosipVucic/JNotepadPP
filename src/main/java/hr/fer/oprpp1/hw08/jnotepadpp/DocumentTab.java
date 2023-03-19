package hr.fer.oprpp1.hw08.jnotepadpp;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import hr.fer.oprpp1.hw08.jnotepadpp.models.SingleDocumentModel;
/**
 * The class responsible for implementing a simple document tab with a tab icon, title and close button.
 * @author Josip Vucić
 *
 */
public class DocumentTab extends JPanel implements IDocumentTab {
	
	private static final long serialVersionUID = 1L;
	/**
	 * The JLabel to store our icon in.
	 */
	private JLabel tabIcon;
	/**
	 * The JLabel for our title.
	 */
	private JLabel title;
	/**
	 * The close button.
	 */
	private JButton close;
	/**
	 * The document this tab pertains to.
	 */
	private SingleDocumentModel doc;
	/**
	 * Creates a new DocumentTab with the given arguments.
	 * @param title The tab title.
	 * @param icon The tab icon.
	 * @param tabClose The close action.
	 * @param doc The document the tab pertains to.
	 */
	public DocumentTab(String title, Icon icon, Action tabClose, SingleDocumentModel doc) {
		this.title = new JLabel(title);
		this.tabIcon = new JLabel(icon);
		this.doc = doc;
		
		close = new JButton(tabClose);	
		close.setBorder(null);
		close.setContentAreaFilled(false);
		
		this.add(tabIcon);
		this.add(this.title);
		this.add(close);
		this.setOpaque(false);
		
	}
	/**
	 * Sets the title of the tab.
	 */
	public void setTitle(String title) {
		this.title.setText(title);
	}
	/**
	 * Sets the icon of the tab.
	 */
	public void setIcon(Icon icon) {
		this.tabIcon.setIcon(icon);
	}
	/**
	 * Sets the close action of the tab.
	 */
	public void setCloseAction(Action action) {
		close.setAction(action);
	}
	/**
	 * Returns the document the tab pertains to.
	 */
	public SingleDocumentModel getDocument() {
		return doc;
	}
}
