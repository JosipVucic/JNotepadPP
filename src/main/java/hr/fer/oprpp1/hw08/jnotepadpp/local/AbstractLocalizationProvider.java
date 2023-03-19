package hr.fer.oprpp1.hw08.jnotepadpp.local;

import java.util.ArrayList;
import java.util.List;
/**
 * The class responsible for implementing basic localisation provider listener functionality.
 * @author Josip Vucić
 *
 */
public abstract class AbstractLocalizationProvider implements ILocalizationProvider {
	/**
	 * The list of listeners.
	 */
	List<ILocalizationListener> listeners = new ArrayList<>();
	/**
	 * Returns the string for the given key.
	 */
	@Override
	public abstract String getString(String key);
	/**
	 * Adds the listener to the provider. Throws a NullPointerException if the listener is null.
	 */
	@Override
	public void addLocalizationListener(ILocalizationListener listener) {
		if(listener == null) throw new NullPointerException();
		
		listeners.add(listener);
	}
	/**
	 * Removes the listener from the provider.
	 */
	@Override
	public void removeLocalizationListener(ILocalizationListener listener) {
		if(listener == null) return;

		listeners.remove(listener);
	}
	/**
	 * Alerts the listeners that a change of language has occurred.
	 */
	public void fire() {
		listeners.forEach((l) -> l.localizationChanged());
	}

}
