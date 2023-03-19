package hr.fer.oprpp1.hw08.jnotepadpp.local;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.util.MissingResourceException;

/**
 * The class responsible for defining localisable actions.
 * 
 * @author Josip Vucić
 *
 */
public abstract class LocalizableAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	/**
	 * The key for the requested action string.
	 */
	private String key;
	/**
	 * The provider for the action string.
	 */
	private ILocalizationProvider provider;
	/**
	 * The localisation listener for the provider. Fetches the action name and
	 * description from the provider.
	 */
	private ILocalizationListener listener = () -> {
		if (this.getValue(Action.LARGE_ICON_KEY) == null)
			this.putValue(Action.NAME, provider.getString(key));
		try {
			this.putValue(Action.SHORT_DESCRIPTION, provider.getString(key + "ShortDescription"));
		} catch (MissingResourceException e) {

		}
		this.putValue(Action.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(provider.getString(key).charAt(0)));
	};

	/**
	 * Creates a new LocalisableAction with the given string key and localisation
	 * provider.
	 * 
	 * @param key The string key.
	 * @param lp  The localisation provider.
	 */
	public LocalizableAction(String key, ILocalizationProvider lp) {
		this.key = key;
		this.provider = lp;

		listener.localizationChanged();
		provider.addLocalizationListener(listener);

	}

}
