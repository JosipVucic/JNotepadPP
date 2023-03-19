package hr.fer.oprpp1.hw08.jnotepadpp.local;
/**
 * The interface responsible for defining listeners for Localisation providers.
 * @author Josip Vucić
 *
 */
public interface ILocalizationListener {
	/**
	 * Execute this when the localisation has changed.
	 */
	void localizationChanged();
}
