package hr.fer.oprpp1.hw08.jnotepadpp.local;
/**
 * The interface responsible for defining localisation providers.
 * @author Josip Vucić
 *
 */
public interface ILocalizationProvider {
	/**
	 * 
	 * @param key The key of the requested string.
	 * @return The requested string.
	 */
	String getString(String key);
	/**
	 * Adds the provided listener to the localisation provider.
	 * @param listener
	 */
	void addLocalizationListener(ILocalizationListener listener);
	/**
	 * Removes the provided listener from the localisation provider.
	 * @param listener
	 */
	void removeLocalizationListener(ILocalizationListener listener);
	/**
	 * 
	 * @return The current language string.
	 */
	String getCurrentLanguage();
}
