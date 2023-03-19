package hr.fer.oprpp1.hw08.jnotepadpp;

import javax.swing.JMenu;

import hr.fer.oprpp1.hw08.jnotepadpp.local.ILocalizationListener;
import hr.fer.oprpp1.hw08.jnotepadpp.local.ILocalizationProvider;
/**
 * The class responsible for defining localizable JMenus.
 * @author Josip Vucić
 *
 */
public class LocalizableMenu extends JMenu {
	
	private static final long serialVersionUID = 1L;
	/**
	 * The key for the requested string we'll get from the language provider.
	 */
	String key;
	
	/**
	 * The localisation provider.
	 */
	private ILocalizationProvider provider;
	/**
	 * The listener for our localisation provider.
	 */
	private ILocalizationListener listener = () -> {
		this.setText(provider.getString(key));
	};
	/**
	 * Creates a new LocalizableMenu with the given localisation provider and key.
	 * @param key
	 * @param provider
	 */
	public LocalizableMenu(String key, ILocalizationProvider provider) {
		this.provider = provider;
		this.key = key;
		provider.addLocalizationListener(listener);
	}

}
